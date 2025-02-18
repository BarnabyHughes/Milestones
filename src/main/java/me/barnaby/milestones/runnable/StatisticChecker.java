package me.barnaby.milestones.runnable;

import me.barnaby.milestones.Milestones;
import me.barnaby.milestones.data.ConfigManager;
import me.barnaby.milestones.object.Milestone;
import me.barnaby.milestones.util.StringUtil;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Objects;

public class StatisticChecker extends BukkitRunnable {

    private final Milestones milestones;


    public StatisticChecker(Milestones plugin) {
        this.milestones = plugin;
    }


    @Override
    public void run() {
        for (Milestone milestone : milestones.getConfigManager().getMilestones()) {
            if (System.currentTimeMillis() > milestone.getNextReset()) milestone.reset();
        }

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
                                                    Objects.requireNonNull(milestone.getSection().getString(
                                                            "milestone-rewards." +
                                                            milestoneReward.getThreshold() + ".message"))))));
                    player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1, 1);
                    milestoneReward.execute(player);
                    milestones.getDataManager().claimReward(milestone.getName(), milestoneReward.getThreshold(), player.getUniqueId());
                });
            });
        });
    }
}
