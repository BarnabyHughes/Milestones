package me.barnaby.milestones.object.reward;

import me.barnaby.milestones.object.reward.rewardType.GiveItem;
import me.barnaby.milestones.object.reward.rewardType.RunCommand;

public enum MilestoneRewardType {
    GIVE_ITEM(new GiveItem()),
    RUN_COMMAND(new RunCommand());

    private final MilestoneReward reward;

    MilestoneRewardType(MilestoneReward reward) {
        this.reward = reward;
    }

    public MilestoneReward getReward() {
        return reward;
    }
}
