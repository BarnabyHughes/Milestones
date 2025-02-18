package me.barnaby.milestones.object.reward;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public abstract class MilestoneReward {

    private final int threshold;
    private final ItemStack itemStack;
    private final List<String> executions;
    public MilestoneReward(int threshold, ItemStack itemStack, List<String> executions) {
        this.threshold = threshold;
        this.itemStack = itemStack;
        this.executions = executions;
    }

    public abstract void execute(Player player, String execution);

    public int getThreshold() {
        return threshold;
    }

    public ItemStack getItemStack() {
        return itemStack;
    }

    public List<String> getExecutions() {
        return executions;
    }
}
