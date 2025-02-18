package me.barnaby.milestones.object.reward.rewardType;

import me.barnaby.milestones.object.Milestone;
import me.barnaby.milestones.object.reward.MilestoneReward;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class GiveItem extends MilestoneReward {


    public GiveItem(int threshold, ItemStack itemStack, List<String> executions, Milestone milestone) {
        super(threshold, itemStack, executions, milestone);
    }

    @Override
    public void execute(Player player, String execution) {
        String[] splitted = execution.split(":");

        Material material = Material.getMaterial(splitted[0]);
        int amount = Integer.parseInt(splitted[1]);

        player.getInventory().addItem(new ItemStack(material, amount));
    }
}
