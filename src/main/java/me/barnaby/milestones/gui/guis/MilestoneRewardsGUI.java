package me.barnaby.milestones.gui.guis;

// Import necessary classes including project-specific classes and Bukkit APIs.
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

    /**
     * Constructs the GUI for displaying milestone rewards.
     *
     * @param name      The title/name for the GUI.
     * @param milestone The milestone whose rewards are to be displayed.
     * @param milestones The main plugin instance.
     * @param player    The player viewing the GUI.
     */
    public MilestoneRewardsGUI(String name, Milestone milestone, Milestones milestones, Player player) {
        // Initialize the GUI with a specified number of rows (5 in this case).
        super(name, 5);

        // Outline the GUI with a default item (using gray stained glass pane for empty slots).
        outline(new GUIItem(new ItemStack(Material.GRAY_STAINED_GLASS_PANE)));

        // Get the player's current progress for the milestone.
        int progress = milestone.getStatistic(player);

        // Loop through each reward associated with the milestone.
        milestone.getRewards().forEach(reward -> {
            // Clone the reward's item, or default to a chest if null.
            ItemStack rewardItem = reward.getItemStack() != null ? reward.getItemStack().clone() : new ItemStack(Material.CHEST);
            ItemMeta meta = rewardItem.getItemMeta();
            if (meta != null) {
                // Retrieve the current lore (descriptive text) or create a new list.
                List<String> lore = meta.getLore();
                List<String> newLore;

                // Decide which lore to use based on whether the player has reached the reward threshold.
                if (progress >= reward.getThreshold()) {
                    // Change the item type to indicate an unlocked reward.
                    rewardItem.setType(Material.LIME_STAINED_GLASS_PANE);
                    // Fetch unlocked lore configuration.
                    newLore = milestones.getConfigManager().getConfig().getStringList("messages.unlocked-lore");
                } else {
                    // Fetch locked lore configuration otherwise.
                    newLore = milestones.getConfigManager().getConfig().getStringList("messages.locked-lore");
                }

                // Process each line of lore, replacing placeholders with actual progress and threshold values.
                newLore.forEach(line -> {
                    // Special formatting for a specific statistic (e.g., time played).
                    if (milestone.getStatistic() == Statistic.PLAY_ONE_MINUTE) {
                        lore.add(StringUtil.format(line
                                .replace("%progress%", StringUtil.formatTicks(progress))
                                .replace("%threshold%", StringUtil.formatTicks(reward.getThreshold()))));
                    } else {
                        lore.add(StringUtil.format(line
                                .replace("%progress%", Integer.toString(progress))
                                .replace("%threshold%", Integer.toString(reward.getThreshold()))));
                    }
                });
                // Set the updated lore back to the item meta.
                meta.setLore(lore);
                rewardItem.setItemMeta(meta);
            }

            // Add the reward item to the GUI.
            addItem(new GUIItem(rewardItem));
        });

        // Set a back button at a specific slot (40) that opens the previous GUI when clicked.
        setItem(40, new GUIItem(new ItemBuilder(Material.REDSTONE)
                .name(ChatColor.RED + "" + ChatColor.BOLD + "Back")
                .build(), inventoryClickEvent ->
                new MilestonesGUI(milestones, player).open(player)));

        // If the player has admin permission, add an admin control button.
        if (player.hasPermission("milestones.admin")) {
            setItem(44, new GUIItem(
                    new ItemBuilder(Material.EMERALD_BLOCK)
                            .name(ChatColor.GREEN + "Admin Control")
                            .lore(ChatColor.WHITE + "Click to add a new milestone reward")
                            .build(),
                    // When the admin control button is clicked, cancel the click event and open the reward adder conversation.
                    event -> {
                        event.setCancelled(true);
                        milestones.getSessionManager().addStatisticRewardAdderConversation(player, milestone);
                    }
            ));
        }
    }
}