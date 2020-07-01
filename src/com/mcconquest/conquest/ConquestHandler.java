package com.mcconquest.conquest;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;

import net.md_5.bungee.api.ChatColor;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import com.kfalk.conquesttowns.api.ConquestTownsAPI;
import com.kfalk.conquesttowns.data.Town;
import com.kfalk.conquesttowns.data.TownMaterial;
import com.kfalk.conquesttowns.database.TownManager;

public class ConquestHandler {
	
	Conquest plugin;
	String name;
	int radius;
	int minPlayers;
	ArrayList<ConquestPoint> conquestPoints = new ArrayList<ConquestPoint>();
	public ConquestHandler(Conquest instance){
		this.plugin = instance;
	}
	
	public void registerPoint(Player player, Location location){
		for(ConquestPoint cqP: conquestPoints){
			if(cqP.getLocation().equals(location)){
				plugin.Messaging(player, "ERROR: There is already a conquest point here!");
				return;
			}
		}
		String cqName = this.name;
		int cqRadius = this.radius;
		int cqMinPlayers = this.minPlayers;
		Location cqLoc = location;

		this.name = null;
		this.radius = 0;
		this.minPlayers = 0;
		plugin.Messaging(player,"Registering Point: " + cqName + " " + cqRadius + " " + cqMinPlayers);
		ConquestPoint newCPInstance = new ConquestPoint(plugin);
		newCPInstance.setName(cqName);
		newCPInstance.setRadius(cqRadius);
		newCPInstance.setMinPlayers(cqMinPlayers);
		newCPInstance.setLocation(cqLoc);
		newCPInstance.save(true);
		newCPInstance.load(null);
		conquestPoints.add(newCPInstance);
		
	}
	public void removePoint(Player player, Location location){
		boolean foundPoint= false;
		player.sendMessage(location.toString());
		
		for(ConquestPoint cqP: conquestPoints){
			if(location.equals(cqP.getLocation())){
				plugin.Messaging(player, "Removing ConquestPoint: " + cqP.getName());
				cqP.remove(null, cqP.getName());
				conquestPoints.remove(cqP);
				cqP = null;
				foundPoint = true;
				break;
			}
		}
		if(foundPoint == false)
			plugin.Messaging(player, "There is no conquest point at this location.");
	}
	public void loadAllPoints(final Runnable callback)
	{
		  
	        final ConquestHandler cqHandler = this;
	        Bukkit.getScheduler().runTaskAsynchronously(plugin, new Runnable() {
	            public void run() {

	                Database database = plugin.getConquestDatabase();
	                Connection conn = database.getConnection();
	                try {
	                    PreparedStatement statement = conn.prepareStatement("SELECT * FROM ConquestPoints");
	                    ResultSet result = statement.executeQuery();

	                    while(result.next()) {
	                        String name         = result.getString(1);
	                        String stringLocation = result.getString(2);
	                        int id              = result.getInt(3);
	                        int radius          = result.getInt(4);
	                        int minPlayers      = result.getInt(5);
	                        String holder       = result.getString(6);
	                        String rewards      = result.getString(7);

	                        ConquestPoint instance = new ConquestPoint(plugin);
	                        if(!instance.isLoaded()){
	                        instance.setId(id);
	                        instance.setLocation(ConquestPoint.deserialize(stringLocation));
	                        instance.setRewards(rewards);
	                        instance.setHolder(holder);
	                        instance.setName(name);
	                        instance.setRadius(radius);
	                        instance.setMinPlayers(minPlayers);
		                    instance.setLoaded(true);
		                    conquestPoints.add(instance);

	                        }
		                    
	                    }
                        result.close();

	                    statement.close();
	                    conn.close();
	                }catch(Exception e) {
	                    e.printStackTrace();
	                }

	                if(callback != null)
	                {
	                    Bukkit.getScheduler().runTask(plugin , callback);
	                }
	            }
	        });
	    }
	

	public void conquerPoint(ConquestPoint cp, Player p){
		  //Conquering!
		final int conquestTime = plugin.getSettings().getConquestTime();
			
		  if(TownManager.getPlayerTown(p.getUniqueId()) == null)
			  return;
		  Town cTown = TownManager.getPlayerTown(p.getUniqueId());
		  if(cp.getHolder() != null){
		  if(cp.getHolderTown().equals(cTown)){
			  plugin.Messaging(p, "Your town already owns " + cp.getName());
			  return;
		  }
		  }
		  sendMessageToTown(cTown, p.getName() + " has begun conquering " + cp.getName());
		  
		  if(cp.getHolder() != null){
			  if(!getOnlineMembers(cp.getHolderTown()).isEmpty()){
				  sendMessageToTown(cp.getHolderTown(),  cTown.getTownName() + " has begun conquering " + cp.getName() + " !");
			  }else{
				  //No one is there to defend!
			  }
		  }
		  
		  cp.setConquerers(cTown);
		  final Town A_TOWN = cTown;
		  final ConquestPoint CP = cp;
		 BukkitTask conquerTimer = new BukkitRunnable(){
			  int counter = 0;
			  ArrayList<Player> conqueringPlayers;
			  double percentConquered = 0;
			  int percentTen = 0;
			  int minPlayers = 0;
			  public void run(){
				 conqueringPlayers =  CP.getConqueringPlayers();
				 counter++;
				 minPlayers = 0;

				 if(CP.getHolderTown() == null){
					 CP.setHolder(null);
				 }
				 	//send percentage conquered to players
				 System.out.println(minPlayers);
				 	for(Player p: getOnlineMembers(A_TOWN)){
				 		if(p.getLocation().distance(CP.getLocation())<=CP.getRadius()){
				 				if(!p.isDead()){
				 					minPlayers++;
				 				}
				 			
				 		}
				 	}
					 System.out.println(minPlayers);

				 	percentConquered = (counter/(double)conquestTime)*100;
				 	System.out.println(percentConquered + " ");
				 	if(percentConquered>percentTen){
				 		percentTen+=10;
					 	sendMessageToTown(A_TOWN, "Conquered " + percentTen + "% of " + CP.getName());

				 	}
					//check to make sure players are within range
				 	if(minPlayers < CP.getMinPlayers()){

					 sendMessageToTown(A_TOWN,"Your town has stopped conquering " + CP.getName() + " due to lack of players near!");
					 if(CP.getHolderTown() != null)
					 sendMessageToTown(CP.getHolderTown(),A_TOWN.getTownName() +" has stopped conquering " + CP.getName() + " due to lack of players near!");
					 CP.getConqueringPlayers().clear();
					 CP.getDeadPlayers().clear();

					 CP.setConquerers(null);
					 this.cancel();
				 }
				  
				 //congrats you conquered it!
				 if(counter >= conquestTime){
					 sendMessageToTown(A_TOWN, "Congrats! Your town has conquered " + CP.getName());
					 
					// takeAwayRewards(CP.getHolderTown());
					 getConquestPoints().get(getConquestPoints().indexOf(CP)).setHolder(A_TOWN.getTownName());
					 CP.setHolder(A_TOWN.getTownName());
					 CP.getConqueringPlayers().clear();
					 CP.getDeadPlayers().clear();
					 CP.setConquerers(null);
					 CP.save(false);
					 this.cancel();
				  }
			  }
			  
			  
		  }.runTaskTimer(plugin, 0 ,20);
		  
		  
	  }
	
	public void rewardsHandler(){
		 BukkitTask rewardsRunnable = new BukkitRunnable(){

			public void run() {
				for(ConquestPoint cqP: getConquestPoints()){
				
					
					if(cqP.getHolder()!=null){
						Town holderTown = cqP.getHolderTown();
						if(holderTown != null){
						//check to make sure min players are online
						if(holderTown.getOnlinePlayers() < plugin.getSettings().getMinOnlinePLayers()){
							getConquestPoints().get(getConquestPoints().indexOf(cqP)).setHolder(null);
							cqP.setHolder(null);
							cqP.save(true);
							plugin.getServer().broadcastMessage(ChatColor.DARK_GRAY + "[" + ChatColor.DARK_RED + "Conquest" + ChatColor.DARK_GRAY+ "] " + ChatColor.WHITE + holderTown.getTownName() + " has lost " + cqP.getName() + " due to inactivity!" );
						}
						
						
						
						//doll out rewards

						if(holderTown.getTownChestLocation().getBukkitLocation().getBlock()!= null){
						Block townBlock = holderTown.getTownChestLocation().getBukkitLocation().getBlock();
						if(townBlock.getState() instanceof Chest){
						Chest chest = (Chest) townBlock.getState();
						int amount = 0;
						for(ItemStack item: cqP.getRewards().keySet()){	
						amount = (int) (cqP.getRewards().get(item)*64);
						ItemStack reward = new ItemStack(item.getType(), amount);
						ItemMeta im =reward.getItemMeta();
						im.setDisplayName(TownMaterial.fromMaterial(reward.getType()).getAlias());
						reward.setItemMeta(im);
						chest.getInventory().addItem(reward);
									}
								}
							}	
						}	
						
					}
				}
			}
			 
			  
			  
		  }.runTaskTimer(plugin, 0 , plugin.getSettings().getRewardsTimer());
		  
	}
	/*public void takeAwayRewards(Town town){
		Chest c = TownManager.getTownChest(town);
		ItemStack[] is = c.getInventory().getContents();
		ArrayList<Material> mats =  new ArrayList<Material>();
		for(ItemStack item : is){
			Material m = item.getType();
			if(!mats.contains(m))
			mats.add(m);
		}
		for(Material type: mats){
			c.getInventory().remove(new ItemStack(type, 300));
		}
		c.update(true);
	}*/
	
	public void sendMessageToTown(Town town, String message){
		for(Player player : getOnlineMembers(town)){
			  plugin.Messaging(player, message);

		  }
	}
	
	/* public ConquestPoint isInside(Location loc){		      		
		  
		 for(ConquestPoint cp : getConquestPoints()){
			 if(loc.distance(cp.getLocation())<=cp.getRadius()){
				 return cp;
			 }
		 }
		 return null;
	 }*/
	 public ArrayList<Player> getOnlineMembers(Town town){
		 ArrayList<Player> onlineMembers = new ArrayList<Player>();
		 for (Player p : Bukkit.getOnlinePlayers()) {
		      if (town.isMember(p.getUniqueId())) {
		    	  onlineMembers.add(p);
		      }
		    }
		 return onlineMembers;

		  }
	 
	public ArrayList<ConquestPoint> getConquestPoints(){
		return conquestPoints;
	}
	

}
