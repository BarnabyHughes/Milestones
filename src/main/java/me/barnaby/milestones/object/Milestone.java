package me.barnaby.milestones.object;

// Import necessary classes for milestone rewards, Bukkit API functionalities, statistics, configuration sections, and players.
import me.barnaby.milestones.object.reward.MilestoneReward;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Statistic;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class Milestone {

    // Statistic to track for this milestone (e.g., blocks mined, items crafted).
    private final Statistic statistic;
    // Material used to represent the milestone.
    private final Material material;
    // Name of the milestone.
    private final String name;
    // List of descriptive lore strings for display.
    private final List<String> lore;
    // List of rewards associated with this milestone.
    private final List<MilestoneReward> rewards;
    // Configuration section containing extra settings for this milestone.
    private final ConfigurationSection section;
    // Specific block type relevant when the statistic relates to block mining.
    private final Material blockType;
    // The next timestamp when this milestone will be reset.
    private long nextReset;

    // Constructor: initializes all the fields for the milestone.
    public Milestone(Statistic statistic, Material material, String name, List<String> lore, List<MilestoneReward> rewards, ConfigurationSection section, Material blockType) {
        this.statistic = statistic;
        this.material = material;
        this.name = name;
        this.lore = lore;
        this.rewards = rewards;
        this.section = section;
        this.blockType = blockType;

        // If the configuration includes a "reset-every" option, automatically trigger a reset.
        if (section.contains("reset-every")) {
            reset();
        }
    }

    /**
     * Resets the milestone's tracking and schedules the next reset time.
     * For block-related statistics, resets the counter for each player for the specified block type.
     * Otherwise, resets the counter for the statistic.
     */
    public void reset() {
        // Calculate the next reset time based on the "reset-every" value from the config (in seconds).
        nextReset = System.currentTimeMillis() + (section.getInt("reset-every") * 1000L);

        // If the statistic deals with mining blocks...
        if (statistic == Statistic.MINE_BLOCK) {
            // Reset the statistic for all online players for the specified block type.
            Bukkit.getOnlinePlayers().forEach(player -> {
                player.setStatistic(statistic, blockType, 0);
            });
            // Also reset the statistic for all offline players.
            Arrays.stream(Bukkit.getOfflinePlayers()).forEach(offlinePlayer -> {
                offlinePlayer.setStatistic(statistic, blockType, 0);
            });
        } else {
            // For other types of statistics, reset for all online and offline players.
            Bukkit.getOnlinePlayers().forEach(player -> {
                player.setStatistic(statistic, 0);
            });
            Arrays.stream(Bukkit.getOfflinePlayers()).forEach(offlinePlayer -> {
                offlinePlayer.setStatistic(statistic, 0);
            });
        }

        // Log the reset action to the console.
        System.out.println("Reset statistic " + statistic);
    }

    // Getter for the statistic tracked.
    public Statistic getStatistic() {
        return statistic;
    }

    /**
     * Retrieves the player's current value for the tracked statistic.
     * For MINE_BLOCK, checks the statistic for the specific block type if available.
     *
     * @param player The player whose statistic is being retrieved.
     * @return The statistic counter for the player.
     */
    public int getStatistic(Player player) {
        if (statistic == Statistic.MINE_BLOCK) {
            // Check that blockType is defined before retrieving the value.
            if (blockType == Material.CLOCK) {
                int total = 0;
                for (Material material1 : Material.values()) {
                    total+=player.getStatistic(Statistic.MINE_BLOCK, material1);
                }
                return total;
            }
            if (blockType != null)
                return player.getStatistic(Statistic.MINE_BLOCK, blockType);
        }
        // For other statistics, simply return the tracked value.
        return player.getStatistic(statistic);
    }

    // Getter for the material representing the milestone.
    public Material getMaterial() {
        return material;
    }

    // Getter for the milestone's name.
    public String getName() {
        return name;
    }

    // Getter for the lore description.
    public List<String> getLore() {
        return lore;
    }

    // Getter for the list of rewards associated with this milestone.
    public List<MilestoneReward> getRewards() {
        return rewards;
    }

    // Getter for the configuration section containing detailed milestone settings.
    public ConfigurationSection getSection() {
        return section;
    }

    // Getter for the next reset timestamp.
    public long getNextReset() {
        return nextReset;
    }
}
