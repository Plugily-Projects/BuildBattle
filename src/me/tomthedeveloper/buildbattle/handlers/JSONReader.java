package me.tomthedeveloper.buildbattle.handlers;

import org.bukkit.plugin.java.JavaPlugin;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;

/**
 * Created by Tom on 5/08/2014.
 */
public class JSONReader {


    public static JavaPlugin plugin;
    private JSONObject jsonObject;
    private JSONParser parser;
    private File file;
    private String filename;


    public JSONReader(String filename) {
        this.filename = filename;
        File file = new File(plugin.getDataFolder() + File.separator + filename + ".json");
        if(!file.exists()) {
            try {
                file.createNewFile();
            } catch(IOException e) {
                e.printStackTrace();
            }
        }
        this.file = file;
        try {
            System.out.print(plugin.getDataFolder().toString());
            jsonObject = (JSONObject) parser.parse(new FileReader(file));
        } catch(IOException e) {
            e.printStackTrace();
        } catch(ParseException e) {
            e.printStackTrace();
        }
    }

    public <T> T get(String s) {
        return (T) jsonObject.get(s);
    }


}
