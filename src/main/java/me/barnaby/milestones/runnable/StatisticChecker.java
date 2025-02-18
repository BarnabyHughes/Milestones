package me.barnaby.milestones.runnable;

// Import necessary classes from the project and Bukkit API.
import me.barnaby.milestones.Milestones;
import me.barnaby.milestones.data.ConfigManager;
import me.barnaby.milestones.object.Milestone;
import me.barnaby.milestones.util.StringUtil;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Objects;

/**
 * StatisticChecker is a recurring task that checks and updates milestone statistics.
 */
public class StatisticChecker extends BukkitRunnable {

    // Holds a reference to the main plugin instance.
    private final Milestones milestones;

    /**
     * The constructor initializes the StatisticChecker with the main plugin instance.
     *
     * @param plugin The main Milestones plugin instance.
     */
    public StatisticChecker(Milestones plugin) {
        this.milestones = plugin;
    }

    /**
     * The run method is called every time the task executes.
     */
    @Override
    public void run() {
        // Loop through each milestone from the configuration.
        for (Milestone milestone : milestones.getConfigManager().getMilestones()) {
            // Check if the milestone's configuration contains a "next-reset" field.
            if (milestone.getSection().contains("next-reset"))
                // If the current system time has passed the next reset timestamp, reset the milestone.
                if (System.currentTimeMillis() > milestone.getNextReset())
                    milestone.reset();
        }

        // Process each online player.
        Bukkit.getOnlinePlayers().forEach(player -> {
            // For each milestone configuration, check for rewards.
            milestones.getConfigManager().getMilestones().forEach(milestone -> {
                // For each reward in the current milestone:
                milestone.getRewards().forEach(milestoneReward -> {
                    // Skip this reward if the player hasn't unlocked it.
                    if (!milestoneReward.isUnlocked(player))
                        return;

                    // Skip if the player has already claimed the reward.
                    if (milestones.getDataManager().hasClaimedReward(
                            milestone.getName(), milestoneReward.getThreshold(), player.getUniqueId()))
                        return;

                    // Send the configured milestone unlocked messages to the player.
                    milestones.getConfigManager().getConfig()
                            .getStringList("messages.milestone-unlocked-message")
                            .forEach(msg ->
                                    player.sendMessage(StringUtil.format(
                                            // Replace the placeholder with the actual reward message.
                                            msg.replace("%message%",
                                                    Objects.requireNonNull(milestone.getSection().getString(
                                                            "milestone-rewards." +
                                                                    milestoneReward.getThreshold() + ".message"))))));

                    // Play a level-up sound effect at the player's location.
                    player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1, 1);

                    // Execute any commands or actions associated with the reward.
                    milestoneReward.execute(player);

                    // Mark the reward as claimed in the data manager.
                    milestones.getDataManager().claimReward(
                            milestone.getName(), milestoneReward.getThreshold(), player.getUniqueId());
                });
            });
        });
    }
}