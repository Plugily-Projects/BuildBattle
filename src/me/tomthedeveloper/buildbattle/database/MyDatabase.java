package me.tomthedeveloper.buildbattle.database;

import com.mongodb.*;
import me.tomthedeveloper.buildbattle.handlers.ConfigurationManager;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;

import java.io.IOException;
import java.net.UnknownHostException;

/**
 * Created by Tom on 7/09/2014.
 */
public class MyDatabase {

    private static String IP, DATABASENAME, USER, PASS, COLLECTION;
    private FileConfiguration config;
    private int PORT;
    private DB db;
    private DBCollection dbCollection = null;


    private MongoClient mongoClient;

    public MyDatabase() {
        config = ConfigurationManager.getConfig("database");

        if(!config.contains("IP")) config.set("IP", "<ip from database>");
        if(!config.contains("PORT")) config.set("PORT", 0000);
        if(!config.contains("DatabaseName")) config.set("DatabaseName", "FM");
        if(!config.contains("USER")) config.set("USER", "<Username>");
        if(!config.contains("PASSWORD")) config.set("PASSWORD", "<You're awesome password goes here>");
        if(!config.contains("Collection")) config.set("Collection", "FM");
        try {
            config.save(ConfigurationManager.getFile("database"));
        } catch(IOException e) {
            e.printStackTrace();
        }
        if(!config.contains("IP")) {
            System.out.print("SHUTTING DOWN SERVER. FILL IN THE DATABASE.YML IN THE MAP FIRMASTER!");
            Bukkit.getServer().shutdown();
        }

        IP = config.getString("IP");
        PORT = config.getInt("PORT");
        DATABASENAME = config.getString("DatabaseName");
        USER = config.getString("USER");
        PASS = config.getString("PASSWORD");
        COLLECTION = config.getString("Collection");
        try {
            mongoClient = new MongoClient(IP, PORT);
            System.out.print("CONNECTED TO DATABASE!");

        } catch(UnknownHostException e) {
            System.out.print("Couldn't connect to database! Is the IP and PORT right?");
            e.printStackTrace();
        }
        db = mongoClient.getDB(DATABASENAME);
        if(!db.authenticate(USER, PASS.toCharArray())) {
            System.out.print("FAILED TO AUTHENTICATE DATABASE!!!");
            return;
        }
        dbCollection = db.getCollection(COLLECTION);


    }



   /* public Object getStat(Player player, String name){
        DBCollection dbCollection = db.getCollection(COLLECTION);
        BasicDBObject search = new BasicDBObject();
        search.put("UUID",player.getUniqueId().toString() );
        DBCursor dbCursor = dbCollection.find(search);
        if(!dbCursor.hasNext()){
            BasicDBObject newObject = new BasicDBObject();
            newObject.put("UUID", player.getUniqueId().toString());
            dbCollection.insert(newObject);
        }
        dbCursor = dbCollection.find(search);
        if(dbCursor.next().get(name) == null) {
            setStat(player, name, 1);
            System.out.print("resturning null");
            return 0;
        }
        System.out.print("Gettings stat from" + player.getName());
        return dbCursor.next().get(name);
    }


    public void setStat(Player player, String name, Object stat){
        DBCollection dbCollection = db.getCollection("user");
        BasicDBObject search = new BasicDBObject();
        search.put("UUID", player.getUniqueId().toString());

        BasicDBObject updateObject = new BasicDBObject();
        updateObject.append("$set", new BasicDBObject().append(name, stat));

        dbCollection.update(search, updateObject);
    }

    public void addStat(Player player, String name, Object stat){
        DBCollection dbCollection = db.getCollection("user");
        BasicDBObject search = new BasicDBObject();
        search.put("UUID", player.getUniqueId().toString());

        BasicDBObject updateObject = new BasicDBObject();
        updateObject.append("$inc", new BasicDBObject().append(name, stat));

        dbCollection.update(search, updateObject);

    } */

    public DBObject getSingle(BasicDBObject query) {
        DBCursor cursor = dbCollection.find(query);
        if(cursor == null) return null;
        if(cursor.hasNext()) {
            DBObject retrieve = cursor.next();
            cursor.close();
            return retrieve;
        } else {
            return null;
        }
    }

    public void insertDocument(String[] fields, Object[] data) {
        BasicDBObject document = new BasicDBObject();
        for(int i = 0; i < fields.length; i++) {
            document.put(fields[i], data[i]);
        }
        dbCollection.insert(document);
    }

    public void updateDocument(BasicDBObject query, BasicDBObject update) {
        dbCollection.update(query, new BasicDBObject().append("$set", update));
    }

    public void incrementFieldValue(BasicDBObject query, String field, int value) {
        dbCollection.update(query, new BasicDBObject().append("$inc", new BasicDBObject(field, value)));
    }

    public void decrementFieldValue(BasicDBObject query, String field, int value) {
        dbCollection.update(query, new BasicDBObject().append("$dec", new BasicDBObject(field, value)));
    }

    public void removeOne(BasicDBObject query) {
        dbCollection.remove(query);
    }


}
