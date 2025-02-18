package me.barnaby.milestones.gui.guis;

import me.barnaby.milestones.Milestones;
import me.barnaby.milestones.gui.GUI;
import me.barnaby.milestones.gui.GUIItem;
import me.barnaby.milestones.util.ItemBuilder;
import me.barnaby.milestones.util.StringUtil;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Statistic;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class MilestonesGUI extends GUI {

    public MilestonesGUI(Milestones milestones, Player player) {
        super("Milestones", 6);

        // Outline the GUI with gray glass panes
        outline(new GUIItem(new ItemStack(Material.GRAY_STAINED_GLASS_PANE)));

        // Loop through all milestones and add them to the GUI
        milestones.getConfigManager().getMilestones().forEach(milestone -> {
            String progress = "" + milestone.getStatistic(player);
            if (milestone.getStatistic() == Statistic.PLAY_ONE_MINUTE ||
            milestone.getStatistic() == Statistic.TIME_SINCE_DEATH
            || milestone.getStatistic() == Statistic.TIME_SINCE_REST) {
                progress = StringUtil.formatMinutes(
                        player.getStatistic(Statistic.PLAY_ONE_MINUTE));
            }

            // Create an item to represent the milestone
            ItemStack milestoneItem = new ItemStack(milestone.getMaterial());
            ItemMeta meta = milestoneItem.getItemMeta();
            if (meta != null) {
                meta.setDisplayName(
                        StringUtil.format(milestone.getName()));

                List<String> newLore = new ArrayList<>();

                String finalProgress = progress;
                milestone.getLore().forEach(line -> newLore.add(StringUtil.format(line
                        .replace("%value%", finalProgress))));

                meta.setLore(newLore);

                milestoneItem.setItemMeta(meta);
            }

            // Add the item to the GUI and open rewards GUI on click
            addItem(new GUIItem(milestoneItem, event -> {
                event.setCancelled(true);
                new MilestoneRewardsGUI("Rewards: " + StringUtil.format(milestone.getName()), milestone, milestones, player).open(player);
            }));
        });

        if (player.hasPermission("milestones.admin")) {
            setItem(53, new GUIItem(
                    new ItemBuilder(Material.EMERALD_BLOCK)
                            .name(ChatColor.GREEN + "Admin Control")
                            .lore(ChatColor.WHITE + "Click to add a new milestone")
                            .build(),
                    event -> {
                        event.setCancelled(true);
                        // Assuming your main plugin class provides access to the SessionManager:
                        milestones.getSessionManager().addStatisticAdderConversation(player);
                    }
            ));
        }
    }
}
