package org.sharkk2.mcord;

import com.google.gson.JsonObject;
import org.json.JSONObject;

import java.io.*;
import java.util.stream.Collectors;

public class MCordDatabox {
    private final MCord mcord;
    private JSONObject database;
    private File databaseFile;
    public MCordDatabox(MCord mcord) {
        this.mcord = mcord;
    }

    public void loadData() {
        if (databaseFile == null) {
            databaseFile = new File(mcord.getDataFolder(), "mcord.json");

            if (!databaseFile.exists()) {
                createData(databaseFile);
                this.database = new JSONObject();
            }
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(databaseFile))) {
            String content = reader.lines().collect(Collectors.joining("\n"));
            this.database = new JSONObject(content);
        } catch (IOException e) {
            mcord.getLogger().warning("Failed to load mcord.json! (" + e.getMessage() + ")");
            this.database = new JSONObject();
        }
    }

    public long getChatChannelId() {
        if (databaseFile == null) {return -1;}
        return database.getLong("chatChannel");
    }

    public long getConsoleChannelId() {
        if (databaseFile == null) {return -1;}
        return database.getLong("consoleChannel");
    }

    public void setChatChannelId(long id) {
        database.put("chatChannel", id);
        saveData(database);
    }

    public void setConsoleChannelId(long id) {
        database.put("consoleChannel", id);
        saveData(database);
    }

    public void saveData(JSONObject data) {
        try (FileWriter writer = new FileWriter(databaseFile)) {
           writer.write(data.toString(3));
        } catch (IOException e) {
            mcord.getLogger().warning("Failed to save mcord data (" + e.getMessage() + ")");
        }
        loadData();
    }

    private void createData(File file) {
        try {
            if (!file.getParentFile().exists()) file.getParentFile().mkdirs();
            if (!file.exists()) file.createNewFile();

            try (FileWriter writer = new FileWriter(file)) {
                JSONObject obj = new JSONObject();
                obj.put("chatChannel", -1);
                obj.put("consoleChannel", -1);
                obj.put("players", new JSONObject());
                writer.write(obj.toString(3));
            }
        } catch (IOException e) {
            mcord.getLogger().severe("Failed to create mcord data file! (" + e.getMessage() + ")");
        }
    }
}
