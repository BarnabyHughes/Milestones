package me.barnaby.milestones.data;

import me.barnaby.milestones.object.Milestone;
import me.barnaby.milestones.object.reward.MilestoneReward;
import me.barnaby.milestones.object.reward.MilestoneRewardType;
import me.barnaby.milestones.util.ItemBuilder;
import me.barnaby.milestones.util.StringUtil;
import org.bukkit.Material;
import org.bukkit.Statistic;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;

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
        config.options().copyDefaults(false);
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
                String finalKey = key;
                String[] strings = key.split(":");
                Material block = null;
                if (strings.length > 1) {
                    finalKey = strings[0];
                    if (strings[1].equalsIgnoreCase("ALL")) block = Material.CLOCK;
                    else block = Material.valueOf(strings[1].toUpperCase());
                }
                Statistic statistic = Statistic.valueOf(finalKey.toUpperCase());
                Material material = Material.valueOf(section.getString("material", "STONE").toUpperCase());
                String name = section.getString("name", "&7Unknown Milestone");
                List<String> lore = section.getStringList("lore");

                Milestone milestone = new Milestone(statistic, material, name, lore, new ArrayList<>(), section, block);
                milestones.add(milestone);


                List<MilestoneReward> rewards = new ArrayList<>();
                ConfigurationSection rewardsSection = section.getConfigurationSection("milestone-rewards");
                if (rewardsSection != null) {
                    for (String milestoneValue : rewardsSection.getKeys(false)) {
                        int milestoneThreshold = Integer.parseInt(milestoneValue);
                        ConfigurationSection rewardSection = rewardsSection.getConfigurationSection(milestoneValue);

                        if (rewardSection == null) continue;
                        MilestoneRewardType rewardType = MilestoneRewardType.valueOf(rewardSection.getString("type"));

                        ItemBuilder itemBuilder = new ItemBuilder(
                                Material.RED_STAINED_GLASS_PANE).name(
                                        StringUtil.format(rewardSection.getString("name")))
                                .lore(rewardSection.getStringList("lore"));
                        rewards.add(rewardType.
                                createReward(milestoneThreshold, itemBuilder.build(),
                                        rewardSection.getStringList("values"), milestone));
                    }
                }

                milestone.getRewards().addAll(rewards);

                plugin.getLogger().info("Loaded milestone: " + name + " [" + statistic + "]");

            } catch (IllegalArgumentException e) {
                e.printStackTrace();
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

        new BukkitRunnable() {
            @Override
            public void run() {
                initializeMilestones();
            }
        }.runTaskLater(plugin, 20);

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
