package com.mcconquest.conquest;

import java.util.ArrayList;
import java.util.HashMap;

import net.md_5.bungee.api.ChatColor;

import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.kfalk.conquesttowns.data.TownMaterial;


public class CommandHandler implements CommandExecutor{
    ArrayList<Player> conqPointCreators = new ArrayList<Player>();
    ArrayList<Player> conqPointRemovers = new ArrayList<Player>();

    Conquest plugin;
    
    
    public CommandHandler(Conquest instance)
    {
    	this.plugin = instance;
    }
    
    private boolean sendHelpMessage(Player player)
    {
    	//for players
      player.sendMessage(ChatColor.DARK_GRAY + "Conquest // ---" + ChatColor.YELLOW + " Conquest-related help " + ChatColor.DARK_GRAY + "---");
      player.sendMessage("  " + ChatColor.YELLOW + "/cq, /cq help, /cq ?" + ChatColor.DARK_GRAY + " - Displays this message.");
      player.sendMessage("  " + ChatColor.YELLOW + "/cq info" + ChatColor.DARK_GRAY + " - Displays plugin information.");
      player.sendMessage("  " + ChatColor.YELLOW + "/cq getPoints" + ChatColor.DARK_GRAY + " - Displays ConquestPoint information.");

      if (player.hasPermission("conquest.admin"))
      {
        player.sendMessage(ChatColor.DARK_GRAY + "Conquest // ---" + ChatColor.YELLOW + " For Staff " + ChatColor.DARK_GRAY + "---");
        player.sendMessage("  " + ChatColor.YELLOW + "/cq create [name] [radius] [mininumPlayers]" + ChatColor.DARK_GRAY + " - Starts creation of beacon. Left click to complete");
        player.sendMessage("  " + ChatColor.YELLOW + "/cq cancel" + ChatColor.DARK_GRAY + " - Cancels conquest point creation process");
        player.sendMessage("  " + ChatColor.YELLOW + "/cq remove" + ChatColor.DARK_GRAY + " - Removes conquest point. Click beacon to remove. ");
        player.sendMessage("  " + ChatColor.YELLOW + "/cq loadPoints" + ChatColor.DARK_GRAY + " - Loads the points in the plugin and spits them out. ");
      }
      return true;
    }
    private boolean sendPluginInfo(Player player)
    {
      player.sendMessage(ChatColor.DARK_GRAY + "Conquest // ---" + ChatColor.YELLOW + " Info " + ChatColor.DARK_GRAY + "---");
      player.sendMessage("  " + ChatColor.YELLOW + "Developed by Falker (Kleenx)");
      player.sendMessage("  " + ChatColor.GRAY + "Current state: Enabled");
      return true;
    }
    
    
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
    	if(sender instanceof Player){
    	if (cmd.getName().equalsIgnoreCase("cq")) {
    		try{
    			Player player = (Player)sender;    		
    		if(args.length >= 1){
    			//admin commands
    		if(player.hasPermission("conquest.admin") || player.isOp() ){
    			
    		if(args[0].equalsIgnoreCase("create")){
    			conqPointCreators.add(player);
    			if(args.length == 4){
    			if(args[1] != null)
    				plugin.getConquestHandler().name = args[1];
    			if(args[2] != null)
    				plugin.getConquestHandler().radius = Integer.parseInt(args[2]);
    			if(args[3] != null)
    				plugin.getConquestHandler().minPlayers = Integer.parseInt(args[3]);
    			
    			plugin.Messaging(player, "Left click on a beacon to create a conquest point!");
    			}
    			else{
    				plugin.Messaging(player, "You need a name, radius and minPlayers for this command. ");
    				return true;
    			}
    			

    		}
    		
    		
    		
    		if(args[0].equalsIgnoreCase("cancel")){
    		if(conqPointCreators.contains(player)){
    				conqPointCreators.remove(player);
				plugin.Messaging(player, "Conquest Point creation canceled!");
    		}else if(conqPointRemovers.contains(player)){
				conqPointRemovers.remove(player);
			plugin.Messaging(player, "Conquest Point removal canceled!");
		}else{
    				plugin.Messaging(player, "You are not creating or removing a conquest point!");
    			}
    		}
    		}
    		if(args[0].equalsIgnoreCase("loadPoints")){
    			plugin.Messaging(player, "Loading all points");
    			String msg = "Loaded Conquest Points: ";
    			 plugin.getConquestHandler().loadAllPoints(null);
    			 for(ConquestPoint cqP: plugin.getConquestHandler().getConquestPoints()){
    				msg+= cqP.getName() + ", ";
    			}
    			 plugin.Messaging(player, msg);
    			}
    			
    		
    		if(args[0].equalsIgnoreCase("remove")){
    			if(player.hasPermission("conquest.admin")){
    				plugin.Messaging(player, "Left click a conquest point to remove it");
    				conqPointRemovers.add(player);
    			}
    		}
    		
    		
    			//everyone commands
    			if(args[0].equalsIgnoreCase("getPoints")){
    				player.sendMessage(ChatColor.DARK_GRAY + "---  [" + ChatColor.DARK_RED + "Conquest Points" + ChatColor.DARK_GRAY + "]  ---");
    				player.sendMessage(ChatColor.DARK_GRAY + "Name          " + ChatColor.DARK_AQUA + "Location             " + ChatColor.DARK_RED + "Holder");
    				player.sendMessage(ChatColor.DARK_GRAY + "----        " + ChatColor.DARK_AQUA + "--------             " + ChatColor.DARK_RED + "------");


    				for(ConquestPoint cqP : plugin.getConquestHandler().getConquestPoints()){
    					player.sendMessage(cqP.getName() +  "-   "  + ConquestPoint.serialize(cqP.getLocation()) + ",   " + cqP.getHolder());
    				}
    			}
    	        if ((args == null) || (args.length == 0) || (args[0].equalsIgnoreCase("help")) || (args[0].equalsIgnoreCase("?"))) {
    	            return sendHelpMessage(player);
    	          }
    	        if (args[0].equalsIgnoreCase("info")) {
    	            return sendPluginInfo(player);
    	          }
    		/*if(args[0].equalsIgnoreCase("rewards")){
    			ItemStack item = new ItemStack(Material.DIAMOND);
    			double percent = 0.15;
    			HashMap<ItemStack, Double> rewards = new HashMap<ItemStack,Double>();
    			rewards.put(item, percent);
    			String cereal = ConquestPoint.serializeRewards(rewards);
    			System.out.println(cereal);
    			HashMap<ItemStack, Double> nocereal = ConquestPoint.deserializeRewards(cereal);
    			System.out.println(("DESERIALIZED: " + nocereal.toString()));
    			System.out.println(("ORIGINAL: " + rewards.toString()));

    		}*/
    		
    		}
    		}catch(Exception e){
    		plugin.Messaging((Player) sender, "Oh no! Something went wrong!!");;
    		plugin.log.severe(e.getMessage());
    		e.printStackTrace();
    		return false;
    		
    		}
    	
    	
    	
    	}
    	
    	}else{
    		sender.sendMessage("ERROR: CommandSender must be a player!");
    	}
    	return true; 
    	
    	}
}