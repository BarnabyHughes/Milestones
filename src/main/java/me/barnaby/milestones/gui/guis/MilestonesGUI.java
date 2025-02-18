package me.barnaby.milestones.gui.guis;

import me.barnaby.milestones.config.ConfigManager;
import me.barnaby.milestones.gui.GUI;
import me.barnaby.milestones.gui.GUIItem;
import me.barnaby.milestones.util.StringUtil;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class MilestonesGUI extends GUI {

    public MilestonesGUI(ConfigManager configManager, Player player) {
        super("Milestones", 6);

        // Outline the GUI with gray glass panes
        outline(new GUIItem(new ItemStack(Material.GRAY_STAINED_GLASS_PANE)));

        // Loop through all milestones and add them to the GUI
        configManager.getMilestones().forEach(milestone -> {
            int progress = player.getStatistic(milestone.getStatistic()); // Get player's progress

            // Create an item to represent the milestone
            ItemStack milestoneItem = new ItemStack(milestone.getMaterial());
            ItemMeta meta = milestoneItem.getItemMeta();
            if (meta != null) {
                meta.setDisplayName(
                        StringUtil.format(milestone.getName()));

                List<String> newLore = new ArrayList<>();

                milestone.getLore().forEach(line -> newLore.add(StringUtil.format(line
                        .replace("%value%", progress + ""))));

                meta.setLore(newLore);

                milestoneItem.setItemMeta(meta);
            }

            // Add the item to the GUI and open rewards GUI on click
            addItem(new GUIItem(milestoneItem, event -> {
                event.setCancelled(true);
                new MilestoneRewardsGUI("Rewards: " + StringUtil.format(milestone.getName()), milestone, configManager, player).open(player);
            }));
        });
    }
}
