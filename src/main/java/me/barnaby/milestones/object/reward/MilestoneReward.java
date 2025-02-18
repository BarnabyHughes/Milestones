package me.barnaby.milestones.object.reward;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public abstract class MilestoneReward {

    public abstract ItemStack itemStack();

    public abstract void execute(Player player, String execution);

}
