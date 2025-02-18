package me.barnaby.milestones.gui.guis;

import me.barnaby.milestones.Milestones;
import me.barnaby.milestones.data.ConfigManager;
import me.barnaby.milestones.gui.GUI;
import me.barnaby.milestones.gui.GUIItem;
import me.barnaby.milestones.object.Milestone;
import me.barnaby.milestones.util.ItemBuilder;
import me.barnaby.milestones.util.StringUtil;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Statistic;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;
import java.util.concurrent.TimeUnit;

public class MilestoneRewardsGUI extends GUI {

    public MilestoneRewardsGUI(String name, Milestone milestone, Milestones milestones, Player player) {
        super(name, 5);

        outline(new GUIItem(new ItemStack(Material.GRAY_STAINED_GLASS_PANE)));

        int progress = milestone.getStatistic(player);

        // Loop through milestone rewards and display them
        milestone.getRewards().forEach(reward -> {
            ItemStack rewardItem = reward.getItemStack() != null ? reward.getItemStack().clone() : new ItemStack(Material.CHEST);
            ItemMeta meta = rewardItem.getItemMeta();
            if (meta != null) {
                List<String> lore = meta.getLore();
                List<String> newLore;

                if (progress >= reward.getThreshold()) {
                    rewardItem.setType(Material.LIME_STAINED_GLASS_PANE);
                    newLore = milestones.getConfigManager().getConfig().getStringList(
                            "messages.unlocked-lore");
                } else
                    newLore = milestones.getConfigManager().getConfig().getStringList(
                            "messages.locked-lore"
                    );

                newLore.forEach(line -> {
                    if (milestone.getStatistic() == Statistic.PLAY_ONE_MINUTE) {
                        lore.add(StringUtil.format(line
                                .replace("%progress%", StringUtil.formatTicks(progress))
                                .replace("%threshold%", StringUtil.formatTicks(reward.getThreshold()))));
                    }
                    else {
                        lore.add(StringUtil.format(line
                                .replace("%progress%", progress + "")
                                .replace("%threshold%", reward.getThreshold() + "")));
                    }
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
                        new MilestonesGUI(milestones, player).open(player)));

        if (player.hasPermission("milestones.admin")) {
            setItem(44, new GUIItem(
                    new ItemBuilder(Material.EMERALD_BLOCK)
                            .name(ChatColor.GREEN + "Admin Control")
                            .lore(ChatColor.WHITE + "Click to add a new milestone reward")
                            .build(),
                    event -> {
                        event.setCancelled(true);
                        // Assuming your main plugin class provides access to the SessionManager:
                        milestones.getSessionManager().addStatisticRewardAdderConversation(player, milestone);
                    }
            ));
        }
    }
}
