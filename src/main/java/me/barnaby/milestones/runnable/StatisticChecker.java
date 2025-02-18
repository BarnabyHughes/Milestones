package me.barnaby.milestones.runnable;

import me.barnaby.milestones.Milestones;
import me.barnaby.milestones.data.ConfigManager;
import me.barnaby.milestones.util.StringUtil;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitRunnable;

public class StatisticChecker extends BukkitRunnable {

    private final Milestones milestones;


    public StatisticChecker(Milestones plugin) {
        this.milestones = plugin;
    }


    @Override
    public void run() {
        Bukkit.getOnlinePlayers().forEach(player -> {
            milestones.getConfigManager().getMilestones().forEach(milestone -> {
                milestone.getRewards().forEach(milestoneReward -> {
                    if (!milestoneReward.isUnlocked(player)) return;
                    if (milestones.getDataManager().hasClaimedReward(
                            milestone.getName(), milestoneReward.getThreshold(), player.getUniqueId()
                    )) return;

                    milestones.getConfigManager().getConfig().getStringList("messages.milestone-unlocked-message")
                            .forEach(msg ->
                                    player.sendMessage(StringUtil.format(
                                            msg.replace("%message%",
                                                    milestone.getSection().getString("message")))));
                });
            });
        });
    }
}
