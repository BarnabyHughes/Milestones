package me.barnaby.milestones.session;

import me.barnaby.milestones.Milestones;
import me.barnaby.milestones.data.ConfigManager;
import me.barnaby.milestones.object.Milestone;
import me.barnaby.milestones.object.reward.MilestoneRewardType;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Statistic;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.conversations.*;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class StatisticRewardAdderConversation implements ConversationAbandonedListener {

    private final Milestones plugin;
    private final ConfigManager configManager;
    private final ConfigurationSection milestoneSection; // The config section for the existing milestone
    private Player player;
    private Milestone milestone;

    public StatisticRewardAdderConversation(Milestones plugin, ConfigManager configManager, Milestone milestone) {
        this.plugin = plugin;
        this.configManager = configManager;
        this.milestoneSection = milestone.getSection();
        this.milestone = milestone;
    }

    /**
     * Starts the conversation with the given player.
     */
    public void start(Player player) {
        this.player = player;
        ConversationFactory factory = new ConversationFactory(plugin)
                .withModality(true)
                .withLocalEcho(false)
                .withFirstPrompt(new ThresholdPrompt())
                .addConversationAbandonedListener(this);
        factory.buildConversation(player).begin();
    }

    /**
     * Called when the conversation ends.
     * If it ended gracefully and the reward was confirmed, finish by updating the config.
     */
    @Override
    public void conversationAbandoned(ConversationAbandonedEvent event) {
        if (event.gracefulExit() && Boolean.TRUE.equals(event.getContext().getSessionData("confirmed"))) {
            conversationFinished(event.getContext());
        } else {
            player.sendMessage(ChatColor.RED + "Reward addition canceled.");
        }
    }

    /**
     * Called when the conversation ends gracefully (with confirmation)
     * to update the config with the new reward.
     */
    private void conversationFinished(ConversationContext context) {
        ConfigurationSection rewardsSection;
        if (milestoneSection.isConfigurationSection("milestone-rewards")) {
            rewardsSection = milestoneSection.getConfigurationSection("milestone-rewards");
        } else {
            rewardsSection = milestoneSection.createSection("milestone-rewards");
        }
        int threshold = (int) context.getSessionData("threshold");
        // Use threshold as key (as a String)
        ConfigurationSection rewardSection = rewardsSection.createSection(String.valueOf(threshold));
        rewardSection.set("name", context.getSessionData("rewardName"));
        rewardSection.set("lore", context.getSessionData("rewardLore"));
        rewardSection.set("message", context.getSessionData("rewardMessage"));
        rewardSection.set("type", context.getSessionData("rewardType").toString());
        rewardSection.set("values", context.getSessionData("rewardValues"));
        configManager.saveConfig();

        plugin.getConfigManager().reloadConfig();
        context.getForWhom().sendRawMessage(ChatColor.GREEN + "Reward added successfully!");
    }

    // ----- Prompts -----

    private class ThresholdPrompt extends StringPrompt {
        @Override
        public String getPromptText(ConversationContext context) {
            return ChatColor.YELLOW + "Enter the reward threshold (numeric value) or type 'cancel':";
        }

        @Override
        public Prompt acceptInput(ConversationContext context, String input) {
            if (input.equalsIgnoreCase("cancel")) {
                return Prompt.END_OF_CONVERSATION;
            }
            try {
                int threshold = Integer.parseInt(input);
                context.setSessionData("threshold", threshold);
                return new RewardNamePrompt();
            } catch (NumberFormatException e) {
                context.getForWhom().sendRawMessage(ChatColor.RED + "Invalid number. Try again or type 'cancel'.");
                return this;
            }
        }
    }

    private class RewardNamePrompt extends StringPrompt {
        @Override
        public String getPromptText(ConversationContext context) {
            return ChatColor.YELLOW + "Enter the reward name (use color codes like &a) or type 'cancel':";
        }

        @Override
        public Prompt acceptInput(ConversationContext context, String input) {
            if (input.equalsIgnoreCase("cancel")) {
                return Prompt.END_OF_CONVERSATION;
            }
            context.setSessionData("rewardName", input);
            return new RewardLorePrompt();
        }
    }

    private class RewardLorePrompt extends StringPrompt {
        @Override
        public String getPromptText(ConversationContext context) {
            return ChatColor.YELLOW + "Enter a reward lore line. Type 'done' when finished, or 'cancel' to abort:";
        }

        @Override
        public Prompt acceptInput(ConversationContext context, String input) {
            if (input.equalsIgnoreCase("cancel")) {
                return Prompt.END_OF_CONVERSATION;
            }
            List<String> lore = (List<String>) context.getSessionData("rewardLore");
            if (lore == null) {
                lore = new ArrayList<>();
            }
            if (input.equalsIgnoreCase("done")) {
                context.setSessionData("rewardLore", lore);
                return new RewardMessagePrompt();
            }
            lore.add(input);
            context.setSessionData("rewardLore", lore);
            return this; // Continue asking for lore lines
        }
    }

    private class RewardMessagePrompt extends StringPrompt {
        @Override
        public String getPromptText(ConversationContext context) {
            return ChatColor.YELLOW + "Enter the reward message (displayed when claimed) or type 'cancel':";
        }

        @Override
        public Prompt acceptInput(ConversationContext context, String input) {
            if (input.equalsIgnoreCase("cancel")) {
                return Prompt.END_OF_CONVERSATION;
            }
            context.setSessionData("rewardMessage", input);
            return new RewardTypePrompt();
        }
    }

    private class RewardTypePrompt extends StringPrompt {
        @Override
        public String getPromptText(ConversationContext context) {
            return ChatColor.YELLOW + "Enter the reward type (GIVE_ITEM or RUN_COMMAND) or type 'cancel':";
        }

        @Override
        public Prompt acceptInput(ConversationContext context, String input) {
            if (input.equalsIgnoreCase("cancel")) {
                return Prompt.END_OF_CONVERSATION;
            }
            try {
                MilestoneRewardType rewardType = MilestoneRewardType.valueOf(input.toUpperCase());
                context.setSessionData("rewardType", rewardType);
                return new RewardValuesPrompt();
            } catch (IllegalArgumentException e) {
                context.getForWhom().sendRawMessage(ChatColor.RED + "Invalid reward type. Try again or type 'cancel'.");
                return this;
            }
        }
    }

    private class RewardValuesPrompt extends StringPrompt {
        @Override
        public String getPromptText(ConversationContext context) {
            return ChatColor.YELLOW + "Enter a reward value (e.g., IRON_INGOT:2 or gamemode creative %player%). Type 'done' when finished, or 'cancel' to abort:";
        }

        @Override
        public Prompt acceptInput(ConversationContext context, String input) {
            if (input.equalsIgnoreCase("cancel")) {
                return Prompt.END_OF_CONVERSATION;
            }
            List<String> values = (List<String>) context.getSessionData("rewardValues");
            if (values == null) {
                values = new ArrayList<>();
            }
            if (input.equalsIgnoreCase("done")) {
                context.setSessionData("rewardValues", values);
                return new RewardConfirmationPrompt();
            }
            values.add(input);
            context.setSessionData("rewardValues", values);
            return this;
        }
    }

    private class RewardConfirmationPrompt extends StringPrompt {
        @Override
        public String getPromptText(ConversationContext context) {
            Statistic stat = milestone.getStatistic(); // assuming milestoneSection has the statistic stored
            int threshold = (int) context.getSessionData("threshold");
            String rewardName = (String) context.getSessionData("rewardName");
            List<String> rewardLore = (List<String>) context.getSessionData("rewardLore");
            String rewardMessage = (String) context.getSessionData("rewardMessage");
            MilestoneRewardType rewardType = (MilestoneRewardType) context.getSessionData("rewardType");
            List<String> rewardValues = (List<String>) context.getSessionData("rewardValues");

            return ChatColor.AQUA + "Please confirm the reward details:\n" +
                    ChatColor.GRAY + "Threshold: " + threshold + "\n" +
                    "Reward Name: " + rewardName + "\n" +
                    "Reward Lore: " + rewardLore + "\n" +
                    "Reward Message: " + rewardMessage + "\n" +
                    "Reward Type: " + rewardType + "\n" +
                    "Reward Values: " + rewardValues + "\n" +
                    "Type 'yes' to confirm, or 'no' to cancel:";
        }

        @Override
        public Prompt acceptInput(ConversationContext context, String input) {
            if (input.equalsIgnoreCase("yes")) {
                context.setSessionData("confirmed", true);
                return Prompt.END_OF_CONVERSATION;
            } else {
                context.getForWhom().sendRawMessage(ChatColor.RED + "Reward addition canceled.");
                return Prompt.END_OF_CONVERSATION;
            }
        }
    }
}
