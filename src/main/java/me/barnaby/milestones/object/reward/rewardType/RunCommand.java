package me.barnaby.milestones.object.reward.rewardType;

import me.barnaby.milestones.object.Milestone;
import me.barnaby.milestones.object.reward.MilestoneReward;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class RunCommand extends MilestoneReward {


    public RunCommand(int threshold, ItemStack itemStack, List<String> executions, Milestone milestones) {
        super(threshold, itemStack, executions, milestones);
    }

    @Override
    public void execute(Player player, String execution) {

    }
}
