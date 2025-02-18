package me.barnaby.milestones.object.reward.rewardType;

import me.barnaby.milestones.object.reward.MilestoneReward;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class GiveItem extends MilestoneReward {


    @Override
    public ItemStack itemStack() {
        return new ItemStack(Material.DIAMOND);
    }

    @Override
    public void execute(Player player, String execution) {
        String[] splitted = execution.split(":");

        Material material = Material.getMaterial(splitted[0]);
        int amount = Integer.parseInt(splitted[1]);

        player.getInventory().addItem(new ItemStack(material, amount));
    }
}
