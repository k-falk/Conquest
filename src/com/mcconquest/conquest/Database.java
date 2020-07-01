package com.mcconquest.conquest;

import java.sql.Connection;

import javax.sql.DataSource;

import org.apache.commons.dbcp2.BasicDataSource;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;

public class Database
{
    private Conquest plugin;
    private DataSource   dataSource;

    public Database(Conquest instance)
    {
        this.plugin = instance;
        this.setupDataSource();
    }

    private void setupDataSource()
    {
        FileConfiguration config = plugin.getConfig();
        ConfigurationSection section = config.getConfigurationSection("mysql");
        String url = section.getString("url");
        String user = section.getString("user");
        String pass = section.getString("pass");

        BasicDataSource connectionPool = new BasicDataSource();
        connectionPool.setDriverClassName("com.mysql.jdbc.Driver");
        connectionPool.setUrl(url);
        connectionPool.setUsername(user);
        connectionPool.setPassword(pass);
        connectionPool.setInitialSize(3);
        this.dataSource = connectionPool;
    }

    /**
     * Get a connection to use for database queries.
     * @return Connection
     */
    public Connection getConnection()
    {
        try {
            return dataSource.getConnection();
        } catch(Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
