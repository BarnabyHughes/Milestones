package me.barnaby.milestones.object;

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
    
    private final Statistic statistic;
    private final Material material;
    private final String name;
    private final List<String> lore;
    private final List<MilestoneReward> rewards;
    private final ConfigurationSection section;
    private final Material blockType;
    private long nextReset;

    public Milestone(Statistic statistic, Material material, String name, List<String> lore, List<MilestoneReward> rewards, ConfigurationSection section, Material blockType) {
        this.statistic = statistic;
        this.material = material;
        this.name = name;
        this.lore = lore;
        this.rewards = rewards;
        this.section = section;
        this.blockType = blockType;

        if (section.contains("reset-every")) {
            reset();
        }
    }

    public void reset() {
        nextReset = System.currentTimeMillis() + (section.getInt("reset-every") * 1000L);

        if (statistic == Statistic.MINE_BLOCK) {
            Bukkit.getOnlinePlayers().forEach(player -> {
                player.setStatistic(statistic, blockType, 0);
            });
            Arrays.stream(Bukkit.getOfflinePlayers()).forEach(offlinePlayer -> {
                offlinePlayer.setStatistic(statistic, blockType,0);
            });
        }
        else {
            Bukkit.getOnlinePlayers().forEach(player -> {
                player.setStatistic(statistic, 0);
            });
            Arrays.stream(Bukkit.getOfflinePlayers()).forEach(offlinePlayer -> {
                offlinePlayer.setStatistic(statistic, 0);
            });
        }

        System.out.println("Reset statistic " + statistic);

    }

    public Statistic getStatistic() {
        return statistic;
    }

    public int getStatistic(Player player) {
        if (statistic == Statistic.MINE_BLOCK) {
            if (blockType != null) return player.getStatistic(Statistic.MINE_BLOCK, blockType);

        }
        return player.getStatistic(statistic);
    }

    public Material getMaterial() {
        return material;
    }

    public String getName() {
        return name;
    }

    public List<String> getLore() {
        return lore;
    }

    public List<MilestoneReward> getRewards() {
        return rewards;
    }

    public ConfigurationSection getSection() {
        return section;
    }

    public long getNextReset() {
        return nextReset;
    }
}
