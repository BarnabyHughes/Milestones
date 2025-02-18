package me.barnaby.milestones.gui.guis;

import me.barnaby.milestones.config.ConfigManager;
import me.barnaby.milestones.gui.GUI;
import me.barnaby.milestones.gui.GUIItem;
import me.barnaby.milestones.object.Milestone;
import me.barnaby.milestones.object.reward.MilestoneReward;
import me.barnaby.milestones.util.ItemBuilder;
import me.barnaby.milestones.util.StringUtil;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class MilestoneRewardsGUI extends GUI {

    public MilestoneRewardsGUI(String name, Milestone milestone, ConfigManager configManager, Player player) {
        super(name, 5);

        outline(new GUIItem(new ItemStack(Material.GRAY_STAINED_GLASS_PANE)));

        int progress = player.getStatistic(milestone.getStatistic()); // Get the player's progress

        // Loop through milestone rewards and display them
        milestone.getRewards().forEach(reward -> {
            ItemStack rewardItem = reward.getItemStack() != null ? reward.getItemStack() : new ItemStack(Material.CHEST);
            ItemMeta meta = rewardItem.getItemMeta();
            if (meta != null) {
                List<String> lore = meta.getLore();
                List<String> newLore;

                if (progress >= reward.getThreshold())
                     newLore = configManager.getConfig().getStringList(
                            "messages.unlocked-lore");
                else
                    newLore = configManager.getConfig().getStringList(
                            "messages.locked-lore"
                    );

                newLore.forEach(line -> {
                    lore.add(StringUtil.format(line
                            .replace("%progress%", progress + "")
                            .replace("%threshold%", reward.getThreshold() + "")));
                });
                meta.setLore(lore);

                rewardItem.setItemMeta(meta);
            }

            // Add the reward item to the GUI
            addItem(new GUIItem(rewardItem));
        });

        setItem(40, new GUIItem(new ItemBuilder(
                Material.REDSTONE)
                .name(ChatColor.RED + "" + ChatColor.BOLD + "Back").build(),
                inventoryClickEvent ->
                new MilestonesGUI(configManager, player).open(player)));
    }
}
