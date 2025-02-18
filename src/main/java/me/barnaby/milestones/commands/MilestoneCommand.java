package me.barnaby.milestones.commands;

import me.barnaby.milestones.Milestones;
import me.barnaby.milestones.gui.guis.MilestonesGUI;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class MilestoneCommand implements CommandExecutor {

    private final Milestones milestones;
    public MilestoneCommand(Milestones milestones) {
        this.milestones = milestones;
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if (!(commandSender instanceof Player player)) return true;

        if (strings.length == 0) new MilestonesGUI(milestones, player).open(player);
        else if (strings.length == 1) {
            switch (strings[0]) {
                case "reload":
                    milestones.getConfigManager().reloadConfig();
                    player.sendMessage(ChatColor.GREEN + "Milestones Reloaded!");
                    break;
                case "open":
                    new MilestonesGUI(milestones, player).open(player);
                    break;
                case "help":
                default:
                    // print help message
                    player.sendMessage(ChatColor.AQUA + "Milestones Plugin v" + milestones.getDescription().getVersion());
                    player.sendMessage(ChatColor.AQUA + "Coded by: " + String.join(", ", milestones.getDescription().getAuthors()));
                    player.sendMessage(ChatColor.GREEN + "Use " + ChatColor.YELLOW + "/milestones open" + ChatColor.GREEN + " to view your milestones.");
                    break;
            }


        }

        return false;
    }
}
