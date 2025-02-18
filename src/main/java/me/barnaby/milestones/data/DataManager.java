package me.barnaby.milestones.data;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.*;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.*;

public class DataManager {

    private final JavaPlugin plugin;
    private final File dataFile;
    private final Gson gson;
    private Map<String, List<UUID>> claimedRewards;

    public DataManager(JavaPlugin plugin) {
        this.plugin = plugin;
        this.dataFile = new File(plugin.getDataFolder(), "data.json");
        this.gson = new GsonBuilder().setPrettyPrinting().create();
        this.claimedRewards = new HashMap<>();

        loadData();
    }

    /**
     * Loads the claimed rewards from data.json.
     */
    private void loadData() {
        if (!dataFile.exists()) {
            plugin.getLogger().info("Creating new data.json...");
            saveData(); // Save empty data
            return;
        }

        try (Reader reader = new FileReader(dataFile)) {
            Type type = new TypeToken<Map<String, List<UUID>>>() {}.getType();
            claimedRewards = gson.fromJson(reader, type);
            if (claimedRewards == null) {
                claimedRewards = new HashMap<>();
            }
        } catch (Exception e) {
            plugin.getLogger().severe("Failed to load data.json!");
            e.printStackTrace();
        }
    }

    /**
     * Saves the claimed rewards to data.json.
     */
    public void saveData() {
        try (Writer writer = new FileWriter(dataFile)) {
            gson.toJson(claimedRewards, writer);
        } catch (IOException e) {
            plugin.getLogger().severe("Failed to save data.json!");
            e.printStackTrace();
        }
    }

    /**
     * Checks if a player has claimed a specific reward.
     *
     * @param statistic The statistic (e.g., DAMAGE_TAKEN)
     * @param threshold The milestone threshold (e.g., 1000)
     * @param playerUUID The player's UUID
     * @return true if the player has already claimed it
     */
    public boolean hasClaimedReward(String statistic, int threshold, UUID playerUUID) {
        String key = statistic + "." + threshold;
        return claimedRewards.containsKey(key) && claimedRewards.get(key).contains(playerUUID);
    }

    /**
     * Marks a reward as claimed for a player.
     *
     * @param statistic The statistic (e.g., DAMAGE_TAKEN)
     * @param threshold The milestone threshold (e.g., 1000)
     * @param playerUUID The player's UUID
     */
    public void claimReward(String statistic, int threshold, UUID playerUUID) {
        String key = statistic + "." + threshold;

        claimedRewards.computeIfAbsent(key, k -> new ArrayList<>());
        if (!claimedRewards.get(key).contains(playerUUID)) {
            claimedRewards.get(key).add(playerUUID);
            saveData(); // Persist the changes
        }
    }

    /**
     * Resets a player's claimed rewards for a specific milestone.
     *
     * @param statistic The statistic (e.g., DAMAGE_TAKEN)
     * @param threshold The milestone threshold (e.g., 1000)
     * @param playerUUID The player's UUID
     */
    public void resetClaimedReward(String statistic, int threshold, UUID playerUUID) {
        String key = statistic + "." + threshold;
        if (claimedRewards.containsKey(key)) {
            claimedRewards.get(key).remove(playerUUID);
            saveData();
        }
    }

    /**
     * Resets all claimed rewards for a player.
     *
     * @param playerUUID The player's UUID
     */
    public void resetAllClaims(UUID playerUUID) {
        claimedRewards.forEach((key, players) -> players.remove(playerUUID));
        saveData();
    }
}
