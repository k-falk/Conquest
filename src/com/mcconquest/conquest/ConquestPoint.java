package com.mcconquest.conquest;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;








import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import com.kfalk.conquesttowns.api.ConquestTownsAPI;
import com.kfalk.conquesttowns.data.Town;
import com.kfalk.conquesttowns.data.TownMaterial;
import com.kfalk.conquesttowns.database.TownManager;



public class ConquestPoint {
		String name;
		int id = -1;
		int radius = 0;
		int minPlayers = 0;
		Location loc;
		static Conquest plugin;
		boolean loaded;
		String holder;
		Town conquerers = null;
		ArrayList<Player> conqueringPlayers = new ArrayList<Player>();
		ArrayList<Player> deadPlayers = new ArrayList<Player>();
		HashMap<ItemStack, Double> rewards= new HashMap<ItemStack, Double>();
		public ConquestPoint(Conquest instance){
		this.plugin = instance;
		this.loaded = false;
		}
		
	   /* private void ensureLoaded()
	    {
	        if(!this.isLoaded()) {
	            throw new IllegalStateException("ConquestPoint " + this.getName() + " is not loaded");
	        }
	    }*/

	    public void load(final Runnable callback)
	    {
	        if(this.loaded) {
	            return;
	        }

	        final ConquestPoint instance = this;
	        Bukkit.getScheduler().runTaskAsynchronously(plugin, new Runnable() {
	            public void run() {
	                Database database = plugin.getConquestDatabase();
	                Connection conn = database.getConnection();
	                try {
	                    PreparedStatement statement = conn.prepareStatement("SELECT name,location,id,radius,minPlayers,holder,rewards FROM ConquestPoints where name=?");
	                    statement.setString(1, name);
	                    ResultSet result = statement.executeQuery();
	                    if(result.next()) {
	                        String name         = result.getString(1);
	                        Location location   = deserialize(result.getString(2));
	                        int id              = result.getInt(3);
	                        int radius          = result.getInt(4);
	                        int minPlayers      = result.getInt(5);
	                        String holder       = result.getString(6);
	                        String rewards      = result.getString(7);
	                        
	                        instance.setLocation(location);
	                        instance.setName(name);
	                        instance.setId(id);
	                        instance.setHolder(holder);
	                        instance.setRadius(radius);
	                        instance.setMinPlayers(minPlayers);
	                        instance.setRewards(rewards);
	                        if(plugin.getConquestHandler().getConquestPoints().contains(instance)){
	                        for(ConquestPoint cp: plugin.getConquestHandler().getConquestPoints()){
	                        
	                        	plugin.getConquestHandler().getConquestPoints().remove(cp);
	                        }
	                        }
	                        plugin.getConquestHandler().getConquestPoints().add(instance);
	                        
	                        result.close();
	                    }
	                    statement.close();
	                    conn.close();
	                    instance.setLoaded(true);
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
	
	//Saves to server SQL database
	    public void save(boolean async)
	    {
	      //  this.ensureLoaded();
	        if(this.id == -1) {
	            final ConquestPoint instance = this;
	            Runnable runnable = new Runnable() {
	                public void run() {
	                    String query = "INSERT into ConquestPoints (name,location,radius,minPlayers,holder,rewards) values (?,?,?,?,?,?)";
	                    Conquest plugin = Conquest.getInstance();
	                    Database database = plugin.getConquestDatabase();
	                    Connection conn = database.getConnection();
	                    try {
	                        PreparedStatement statement = conn.prepareStatement(query);
	                        statement.setString(1, instance.getName());
	                        statement.setString(2, serialize(instance.getLocation()));
	                        statement.setInt(3, instance.getRadius());
	                        statement.setInt(4, instance.getMinPlayers());
	                        statement.setString(5, instance.getHolder());
	                        statement.setString(6, serializeRewards(instance.getRewards()));
	                        
	                        statement.executeUpdate();
	                        statement.close();

	                        query = "SELECT id FROM ConquestPoints where name=?";
	                        statement = conn.prepareStatement(query);
	                        statement.setString(1, name);
	                        ResultSet result = statement.executeQuery();
	                        
	                        if(result.next()) {
		                       
	                            result.close();
	                        } else {
	                            throw new Exception("Could not fetch ConquestPoint id from database for ConquestPoint " + instance.getName());
	                        }
	                        statement.close();
	                        conn.close();
	                    }catch(Exception e) {
	                        e.printStackTrace();
	                    }
	                }
	            };
	            if(async) {
	                Bukkit.getScheduler().runTaskAsynchronously(Conquest.getInstance(), runnable);
	            }else {
	                runnable.run();
	            }
	        } else {
	            final ConquestPoint instance = this;
	            Runnable runnable = new Runnable() {
	                public void run() {
	                   Conquest plugin = Conquest.getInstance();
	                    Database database = plugin.getConquestDatabase();
	                    Connection conn = database.getConnection();
	                    try {	  
		    	        	PreparedStatement statement = conn.prepareStatement("UPDATE ConquestPoints SET name=?,location=?,radius=?, minPlayers=?, holder=?, rewards=? where name=?");
		                    statement.setString(7, name);
		    	        	statement.setString(1, instance.getName());
	                        statement.setString(2, serialize(instance.getLocation()));
	                        statement.setInt(3, instance.getRadius());
	                        statement.setInt(4, instance.getMinPlayers());
	                        statement.setString(5, instance.getHolder());
	                        statement.setString(6, serializeRewards(instance.getRewards()));
	                        statement.executeUpdate();
	                        
	                        statement.close();
	                        conn.close();
	                    }catch(Exception e) {
	                        e.printStackTrace();
	                    }
	                }
	            };
	            if(async) {
	                Bukkit.getScheduler().runTaskAsynchronously(Conquest.getInstance(), runnable);
	            } else {
	                runnable.run();
	            }
	        }

	    }
	    public void remove(final Runnable callback, final String cqName)
	    {
	        

	        final ConquestPoint instance = this;
	        Bukkit.getScheduler().runTaskAsynchronously(plugin, new Runnable() {
	            public void run() {
	                Database database = plugin.getConquestDatabase();
	                Connection conn = database.getConnection();
	                try {
	                    PreparedStatement statement = conn.prepareStatement("DELETE FROM `ConquestPoints` WHERE name=?");
	                    statement.setString(1, cqName);
	                    statement.executeUpdate();
	                   

	                    statement.close();
	                    conn.close();
	                    instance.setLoaded(true);
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
	    public synchronized boolean isLoaded()
	    {
	        return this.loaded;
	    }
	    
	    //Cereal
	    public static String serialize (Location location) {
	        String result = location.getWorld().getName() + "," + (int)location.getX() + "," + (int)location.getY() + "," + (int)location.getZ();	             
	        return result;
		    }
	    public static Location deserialize (String serialized) {
		     
            String[] split = serialized.split(",");
            Location result = new Location(Bukkit.getWorld(split[0]), Double.parseDouble(split[1]), Double.parseDouble(split[2]),
                                                Double.parseDouble(split[3]));
        
            System.out.println(result.toString());

        return result;
	    }
	    
	    public static String serializeRewards (HashMap<ItemStack, Double> rewards) {
	        if (rewards.isEmpty())
	            return null;
	      ItemStack[] items  = rewards.keySet().toArray(new ItemStack[rewards.size()]);
	      Double[] percentages = rewards.values().toArray(new Double[rewards.size()]);
	      TownMaterial townMat = TownMaterial.fromMaterial(items[0].getType());
	        String iResult = townMat.toString();
	        String pResult = percentages[0].toString();
	        String result = (iResult + ", " + pResult);
	        for (int i = 1; i < rewards.size(); ++i) {
	            result += "; " + townMat.toString() + ", " + percentages[i].toString();
	        }
	        return result;
	    }
	     
	  public static HashMap<ItemStack, Double> deserializeRewards (String serialized) {
	        String[] split = serialized.split(";");
	        //WAITING FOR REWARDS
            HashMap<ItemStack, Double> result = new HashMap<ItemStack, Double>();
	        for (int i = 0; i < split.length; i++) {	    	
	            String[] split2 = split[i].split(",");
	            String townMatString = split2[0].toUpperCase();
	            TownMaterial townMat = TownMaterial.valueOf(split2[0]);
	            Material material = townMat.getMaterial();
	            ItemStack items = new ItemStack(material);
	            
	            double percentages = Double.parseDouble(split2[1]);

	            result.put(items, percentages);
	        }

	        return result;
	    }
	  

	  
	    
	    
	    
	    
	    //Getters
	    public String getName(){
	    	return this.name;
	    }
	    public Integer getRadius(){
	    	return this.radius;
	    }
	    public Integer getMinPlayers(){
	    	return this.minPlayers;
	    }
	    public Integer getId(){
	    	return this.id;
	    }
	    public String getHolder(){
	    	return this.holder;
	    }
	    public Location getLocation(){
	    
	    	return this.loc;
	    }
	    public HashMap<ItemStack, Double> getRewards(){
	    	return this.rewards;
	    }
	    
	    public Town getHolderTown(){
	    	for(Town t : TownManager.towns.values()){
	    		if(t.getTownName().equals(getHolder())){
	    			return t;
	    		}
	    		
	    	}
	    	return null;
	    }
	    
	    public Town getConquerers(){
	    	return this.conquerers;
	    }

	    public ArrayList<Player> getConqueringPlayers()
	    {	
	    	return this.conqueringPlayers;
	    }
	    public ArrayList<Player> getDeadPlayers()
	    {	
	    	return this.deadPlayers;
	    }
	    
	    
	    //Setters
	    public synchronized void setLoaded(boolean loaded)
	    {
	        this.loaded = loaded;
	    }
	    public synchronized void setConquerers(Town t){
	    	this.conquerers = t;
	    }
	    public synchronized void addConqueringPlayer(Player cqPlayer){
	    	getConqueringPlayers().add(cqPlayer);
	    }
	    public synchronized void removeConqueringPlayer(Player cqPlayer){
	    	getConqueringPlayers().remove(cqPlayer);
	    }
	    public synchronized void addDeadPlayer(Player cqPlayer){
	    	getConqueringPlayers().add(cqPlayer);
	    }
	    public synchronized void removeDeadPlayer(Player cqPlayer){
	    	getConqueringPlayers().remove(cqPlayer);
	    }
	    
	    public synchronized void setHolder(String holderName)
	    {
	        this.holder = holderName;
	    }
	    
	    public synchronized void setName(String name)
	    {
	        this.name = name;
	    }
	    
	    public synchronized void setId(int id)
	    {
	        this.id = id;
	    }
	    
	    public synchronized void setMinPlayers(int minPlayers)
	    {
	        this.minPlayers = minPlayers;
	    }
	    public synchronized void setLocation(Location loc)
	    {
	        this.loc = loc;
	    }
	    
	    public synchronized void setRadius(int radius)
	    {
	        this.radius = radius;
	    }
	    public synchronized void setRewards(String rewards)
	    {
	        this.rewards = deserializeRewards(rewards);
	    }
	       
	   
	        
}
