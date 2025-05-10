package com.duperknight.client.Character;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import net.fabricmc.loader.api.FabricLoader;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

public class CharacterManager {
    private static final Path CHARACTERS_FILE =
        FabricLoader.getInstance().getConfigDir().resolve("dect_characters.json");
    private static final Path SETTINGS_FILE =
        FabricLoader.getInstance().getConfigDir().resolve("dect_settings.json");
    private static final Gson GSON = new Gson();
    private static final Map<String, CharacterData> characters = new HashMap<>();
    private static String currentCharacter = null;
    private static int broadcastDistance = 20;

    public static void init() {
        loadCharacters();
        loadSettings();
    }

    private static void loadCharacters() {
        if (Files.exists(CHARACTERS_FILE)) {
            try (Reader r = Files.newBufferedReader(CHARACTERS_FILE)) {
                Map<String, CharacterData> map = GSON.fromJson(r,
                    new TypeToken<Map<String, CharacterData>>(){}.getType());
                if (map != null) characters.putAll(map);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private static void saveCharacters() {
        try {
            Files.createDirectories(CHARACTERS_FILE.getParent());
            try (Writer w = Files.newBufferedWriter(CHARACTERS_FILE)) {
                GSON.toJson(characters, w);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void loadSettings() {
        if (Files.exists(SETTINGS_FILE)) {
            try (Reader r = Files.newBufferedReader(SETTINGS_FILE)) {
                JsonObject settings = GSON.fromJson(r, JsonObject.class);
                if (settings != null && settings.has("broadcastDistance")) {
                    broadcastDistance = settings.get("broadcastDistance").getAsInt();
                }
            } catch (IOException | IllegalStateException | ClassCastException e) {
                System.err.println("Error loading DECT settings, using defaults: " + e.getMessage());
            }
        }
    }

    private static void saveSettings() {
        try {
            Files.createDirectories(SETTINGS_FILE.getParent());
            try (Writer w = Files.newBufferedWriter(SETTINGS_FILE)) {
                JsonObject settings = new JsonObject();
                settings.addProperty("broadcastDistance", broadcastDistance);
                GSON.toJson(settings, w);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static boolean createCharacter(String name, String style) {
        if (characters.containsKey(name)) return false;
        characters.put(name, new CharacterData(name, style));
        saveCharacters();
        return true;
    }

    public static CharacterData getCharacter(String name) {
        return characters.get(name);
    }

    public static boolean removeCharacter(String name) {
        if (characters.remove(name) != null) {
            if (name.equals(currentCharacter)) currentCharacter = null;
            saveCharacters();
            return true;
        }
        return false;
    }

    public static List<String> listNames() {
        return new ArrayList<>(characters.keySet());
    }

    public static String getCurrent() {
        return currentCharacter;
    }

    public static boolean selectCharacter(String name) {
        if (characters.containsKey(name)) {
            currentCharacter = name;
            return true;
        }
        return false;
    }

    public static void resetCurrent() {
        currentCharacter = null;
    }

    public static int getBroadcastDistance() {
        return broadcastDistance;
    }

    public static void setBroadcastDistance(int distance) {
        if (distance > 0) {
            broadcastDistance = distance;
            saveSettings();
        }
    }
}
