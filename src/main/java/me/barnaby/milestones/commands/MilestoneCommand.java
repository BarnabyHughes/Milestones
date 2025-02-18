package me.barnaby.milestones.commands;

import me.barnaby.milestones.Milestones;
import me.barnaby.milestones.gui.guis.MilestonesGUI;
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

        if (strings.length == 0) new MilestonesGUI(milestones.getConfigManager(), player).open(player);
        else if (strings.length == 1) {
            switch (strings[0]) {
                case "reload":
                    milestones.getConfigManager().reloadConfig();
                    player.sendMessage(
                            milestones.getConfigManager().getConfig().getString("test", "111"));
                    break;
                    // later
                case "edit":
                    // later
                case "open":
                    new MilestonesGUI(milestones.getConfigManager(), player).open(player);
                    break;
                case "help":
                default:
                    // print help message
                    break;
            }


        }

        return false;
    }
}
