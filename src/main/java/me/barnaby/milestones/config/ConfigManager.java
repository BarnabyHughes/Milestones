package me.barnaby.milestones.config;

import me.barnaby.milestones.object.Milestone;
import me.barnaby.milestones.object.reward.MilestoneReward;
import me.barnaby.milestones.object.reward.MilestoneRewardType;
import org.bukkit.Material;
import org.bukkit.Statistic;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

public class ConfigManager {

    private final JavaPlugin plugin;
    private FileConfiguration config;
    private final List<Milestone> milestones = new ArrayList<>();

    public ConfigManager(JavaPlugin plugin) {
        this.plugin = plugin;
        initialize();
    }

    /**
     * Initializes the configuration by loading defaults and copying them if necessary.
     */
    private void initialize() {
        // Save the default config.yml if it doesn't exist
        plugin.saveDefaultConfig();

        // Load the configuration
        config = plugin.getConfig();
        config.options().copyDefaults(true);
        saveConfig();

        // Load all milestones from config
        initializeMilestones();
    }

    /**
     * Loads all milestone trackers from config.yml
     */
    private void initializeMilestones() {
        milestones.clear(); // Clear old milestones

        ConfigurationSection milestoneSection = config.getConfigurationSection("milestone-trackers");
        if (milestoneSection == null) {
            plugin.getLogger().warning("No milestone-trackers found in config.yml!");
            return;
        }

        for (String key : milestoneSection.getKeys(false)) {
            ConfigurationSection section = milestoneSection.getConfigurationSection(key);
            if (section == null) continue;

            try {
                Statistic statistic = Statistic.valueOf(key.toUpperCase());
                Material material = Material.valueOf(section.getString("material", "STONE").toUpperCase());
                String name = section.getString("name", "&7Unknown Milestone");
                List<String> lore = section.getStringList("lore");

                // Load rewards
                Map<Integer, MilestoneReward> rewards = new HashMap<>();
                ConfigurationSection rewardsSection = section.getConfigurationSection("milestone-rewards");
                if (rewardsSection != null) {
                    for (String milestoneValue : rewardsSection.getKeys(false)) {
                        int milestoneThreshold = Integer.parseInt(milestoneValue);
                        ConfigurationSection rewardSection = rewardsSection.getConfigurationSection(milestoneValue);

                        if (rewardSection == null) continue;
                        MilestoneRewardType rewardType = MilestoneRewardType.valueOf(rewardSection.getString("type"));
                        if (rewardType == null) {
                            plugin.getLogger().log(Level.SEVERE, "Reward type in " + rewardSection.getCurrentPath() + " not found!");
                            continue;
                        }

                        switch ()
                    }
                }

                milestones.add(new Milestone(statistic, material, name, lore, new ArrayList<>(rewards.values())));
                plugin.getLogger().info("Loaded milestone: " + name + " [" + statistic + "]");

            } catch (IllegalArgumentException e) {
                plugin.getLogger().warning("Invalid milestone in config: " + key);
            }
        }
    }

    /**
     * Returns the list of all milestones.
     */
    public List<Milestone> getMilestones() {
        return milestones;
    }

    /**
     * Reloads the configuration from the file.
     */
    public void reloadConfig() {
        plugin.reloadConfig();
        config = plugin.getConfig();
        initializeMilestones();
    }

    /**
     * Saves the current configuration to the file.
     */
    public void saveConfig() {
        plugin.saveConfig();
    }

    /**
     * Returns the raw config object if needed.
     */
    public FileConfiguration getConfig() {
        return config;
    }
}
