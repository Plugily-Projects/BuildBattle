package me.tomthedeveloper.buildbattle.handlers;

import me.tomthedeveloper.buildbattle.GameAPI;
import org.json.simple.JSONObject;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

/**
 * Created by Tom on 5/08/2014.
 */
public class JSONWriter {

    public static GameAPI plugin;
    private String filename;
    private File file;

    private JSONObject jsonObject;


    public JSONWriter(String filename) {
        this.filename = filename;

        File file = new File(plugin.getPlugin().getDataFolder() + File.separator + filename + ".json");
        if(!file.exists()) {
            try {
                file.createNewFile();
            } catch(IOException e) {
                e.printStackTrace();
            }
        }
        this.file = file;
        jsonObject = new JSONObject();
    }

    public File getFile() {
        return file;
    }

    public String getFileName() {
        return filename;
    }


    public <T> void put(String s, T t) {
        jsonObject.put(s, t);
    }

    public void save() {
        FileWriter fileWriter;

        try {
            fileWriter = new FileWriter(plugin.getPlugin().getDataFolder() + File.separator + filename + ".json");
            fileWriter.write(jsonObject.toJSONString());
            fileWriter.flush();
            fileWriter.close();

        } catch(IOException e) {
            e.printStackTrace();
        }


    }


}
