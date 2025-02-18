package me.barnaby.milestones.object;

import me.barnaby.milestones.object.reward.MilestoneReward;
import org.bukkit.Material;
import org.bukkit.Statistic;

import java.util.List;

public class Milestone {
    
    private final Statistic statistic;
    private final Material material;
    private final String name;
    private final List<String> lore;
    private final List<MilestoneReward> rewards;

    public Milestone(Statistic statistic, Material material, String name, List<String> lore, List<MilestoneReward> rewards) {
        this.statistic = statistic;
        this.material = material;
        this.name = name;
        this.lore = lore;
        this.rewards = rewards;
    }

    public Statistic getStatistic() {
        return statistic;
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
}
