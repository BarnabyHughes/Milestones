package me.barnaby.milestones.object.reward.rewardType;

import me.barnaby.milestones.object.reward.MilestoneReward;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class RunCommand extends MilestoneReward {


    public RunCommand(int threshold, ItemStack itemStack, List<String> executions) {
        super(threshold, itemStack, executions);
    }

    @Override
    public void execute(Player player, String execution) {

    }
}
