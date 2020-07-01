package com.mcconquest.conquest;

import java.util.ArrayList;

import net.md_5.bungee.api.ChatColor;

import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import com.kfalk.conquesttowns.data.Town;
import com.kfalk.conquesttowns.database.TownManager;


public class PlayerListener implements Listener {

    private Conquest plugin;
    private CommandHandler ch;
    private ConquestHandler cpH;
    public PlayerListener(Conquest instance)
    {
        this.plugin = instance;
        this.ch = plugin.getCommandHandler();
        this.cpH = plugin.getConquestHandler();
    }
    

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event){
    	if(event.isCancelled())return;
    	if(!(event.getAction().equals(Action.LEFT_CLICK_BLOCK))) return;
    	Player player = event.getPlayer();

    	if(ch.conqPointCreators.contains(player)){
        	if(!(event.getClickedBlock().getType().equals(Material.BEACON))){
        		plugin.Messaging(player, "That is not a beacon.");
        		return;
        	}
		plugin.getConquestHandler().registerPoint(player, event.getClickedBlock().getLocation());
    	event.setCancelled(true);
    	
    	ch.conqPointCreators.remove(event.getPlayer());

    	}else if(ch.conqPointRemovers.contains(player)){
    		if(!(event.getClickedBlock().getType().equals(Material.BEACON))){
        		plugin.Messaging(player, "That is not a beacon.");
        		return;
        	}
    		plugin.getConquestHandler().removePoint(player, event.getClickedBlock().getLocation());
    		event.setCancelled(true);
    	}
    	
    }
    @EventHandler
    public void onPlayerConquer(PlayerInteractEvent event){
    	if(event.isCancelled())return;
    	if(!(event.getAction().equals(Action.LEFT_CLICK_BLOCK))) return;
    	Player player = event.getPlayer();
    	if(ch.conqPointCreators.contains(player) || ch.conqPointRemovers.contains(player))
    		return;
    	if(!event.getClickedBlock().getType().equals(Material.BEACON))
    		return;
    	for(ConquestPoint cp : cpH.getConquestPoints()){
    		if(cp.getLocation().equals(event.getClickedBlock().getLocation())){
    			event.setCancelled(true);
    			cpH.conquerPoint(cp, player);
    			
    		}
    	}

    	}
    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event){
    	Player p = event.getEntity();
    	ConquestPoint cp = null;
    	if(TownManager.getPlayerTown(p.getUniqueId()) != null){
    	Town town = TownManager.getPlayerTown(p.getUniqueId());
    	for(ConquestPoint cqP : plugin.getConquestHandler().getConquestPoints()){
    		if(cqP.getConquerers() != null){
    			Town conquerers = cqP.getConquerers();
    			if(conquerers.equals(town)){
    				cp = cqP;
    			}
    			
    		}
    	}
    	if(cp == null){
    		return;
    	}
    	 cp.addDeadPlayer(p);
    	 cp.removeConqueringPlayer(p);
    	}
    
    }
   
    
    	
    }
    

    
    //not doing a prompt ignore this. 
    /*@EventHandler
    public void onPlayerRegister(AsyncPlayerChatEvent event){
    	if(event.isCancelled())return;
    	Player player = event.getPlayer();
    	if(!(cpH.registration.containsKey(player)))
    		return;
    	if(cpH.registration.get(player).equals(0)){
    		this.name = event.getMessage();
    	}
    	if(cpH.registration.get(player).equals(1)){
    		this.radius = Integer.parseInt(event.getMessage());
    	}
    	if(cpH.registration.get(player).equals(2)){
    		this.minPlayers = Integer.parseInt(event.getMessage());
    	}
    	if(cpH.registration.get(player).equals(3)){
    		if(event.getMessage().equalsIgnoreCase("Yes") || event.getMessage().equalsIgnoreCase("Y")){
    			cpH.registration.remove(player);
    			this.name = null;
    			this.radius = 0;
    			this.minPlayers = 0;
    		}else{
    			plugin.Messaging(player, "Restarting Conquest Point Registration");
    			cpH.registration.remove(player)
    			cpH.re
    			this.name = null;
    			this.radius = 0;
    			this.minPlayers = 0;
    		}
    	}
    }*/





