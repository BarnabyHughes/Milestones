package me.barnaby.milestones.object.reward;

import me.barnaby.milestones.object.Milestone;
import me.barnaby.milestones.object.reward.rewardType.GiveItem;
import me.barnaby.milestones.object.reward.rewardType.RunCommand;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public enum MilestoneRewardType {

    GIVE_ITEM(GiveItem.class),
    RUN_COMMAND(RunCommand.class);

    private final Class<? extends MilestoneReward> rewardClass;

    MilestoneRewardType(Class<? extends MilestoneReward> rewardClass) {
        this.rewardClass = rewardClass;
    }

    /**
     * Uses reflection to instantiate the reward class dynamically.
     */
    public MilestoneReward createReward(int threshold, ItemStack itemStack, List<String> executions, Milestone milestone) {
        try {
            return rewardClass.getConstructor(int.class, ItemStack.class, List.class, Milestone.class)
                    .newInstance(threshold, itemStack, executions, milestone);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to instantiate reward: " + rewardClass.getSimpleName());
        }
    }
}


