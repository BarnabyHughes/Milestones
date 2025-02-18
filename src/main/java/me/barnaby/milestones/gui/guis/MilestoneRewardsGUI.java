package me.barnaby.milestones.gui.guis;

import me.barnaby.milestones.gui.GUI;
import me.barnaby.milestones.gui.GUIItem;
import me.barnaby.milestones.object.Milestone;
import me.barnaby.milestones.object.reward.MilestoneReward;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class MilestoneRewardsGUI extends GUI {

    public MilestoneRewardsGUI(String name, Milestone milestone, Player player) {
        super(name, 5);

        outline(new GUIItem(new ItemStack(Material.GRAY_STAINED_GLASS_PANE)));

        int progress = player.getStatistic(milestone.getStatistic()); // Get the player's progress

        // Loop through milestone rewards and display them
        milestone.getRewards().forEach(reward -> {
            ItemStack rewardItem = reward.getItemStack() != null ? reward.getItemStack() : new ItemStack(Material.CHEST);
            ItemMeta meta = rewardItem.getItemMeta();
            if (meta != null) {
                meta.setDisplayName("§aReward: " + reward.getItemStack().getType().name());

                List<String> lore = new ArrayList<>();
                lore.add("§eMilestone: " + milestone.getName());
                lore.add("§7Threshold: " + reward.getThreshold());
                lore.add("§7Your Progress: §f" + progress);
                if (progress >= reward.getThreshold()) {
                    lore.add("§a✔ Unlocked!");
                } else {
                    lore.add("§c✖ Locked! (" + (reward.getThreshold() - progress) + " left)");
                }
                meta.setLore(lore);

                rewardItem.setItemMeta(meta);
            }

            // Add the reward item to the GUI
            addItem(new GUIItem(rewardItem));
        });
    }
}
