package me.barnaby.milestones.gui.guis;

import me.barnaby.milestones.config.ConfigManager;
import me.barnaby.milestones.gui.GUI;
import me.barnaby.milestones.gui.GUIItem;
import org.bukkit.Material;
import org.bukkit.Statistic;
import org.bukkit.inventory.ItemStack;

public class MilestonesGUI extends GUI {

    public MilestonesGUI(ConfigManager configManager) {
        super("Milestones", 6);


        outline(new GUIItem(new ItemStack(Material.GRAY_STAINED_GLASS_PANE)));
        getOwner().getStatistic(Statistic.DAMAGE_TAKEN);
    }
}
