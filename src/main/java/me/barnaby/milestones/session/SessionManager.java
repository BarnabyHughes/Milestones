package me.barnaby.milestones.session;

import me.barnaby.milestones.Milestones;
import me.barnaby.milestones.object.Milestone;
import org.bukkit.entity.Player;

public class SessionManager {

    private final Milestones plugin;

    public SessionManager(Milestones milestones) {
        this.plugin = milestones;
    }

    public void addStatisticAdderConversation(Player player) {
        player.closeInventory();
        new StatisticAdderConversation(plugin, plugin.getConfigManager()).start(player);
    }

    public void addStatisticRewardAdderConversation(Player player, Milestone milestone) {
        player.closeInventory();
        new StatisticRewardAdderConversation(plugin, plugin.getConfigManager(), milestone).start(player);
    }
}
