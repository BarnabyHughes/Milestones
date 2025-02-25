package me.barnaby.milestones.session;

import me.barnaby.milestones.Milestones;
import me.barnaby.milestones.data.ConfigManager;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Statistic;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.conversations.*;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class StatisticAdderConversation implements ConversationAbandonedListener {

    private final Milestones plugin;
    private final ConfigManager configManager;

    public StatisticAdderConversation(Milestones plugin, ConfigManager configManager) {
        this.plugin = plugin;
        this.configManager = configManager;
    }

    /**
     * Starts the conversation with the given player.
     */
    public void start(Player player) {
        ConversationFactory factory = new ConversationFactory(plugin)
                .withModality(true)
                .withLocalEcho(false)
                .withFirstPrompt(new StatisticPrompt())
                .addConversationAbandonedListener(this);
        factory.buildConversation(player).begin();
    }

    /**
     * Called when the conversation ends.
     * If ended gracefully, we update the config with the new milestone.
     */
    @Override
    public void conversationAbandoned(ConversationAbandonedEvent event) {
        if (event.gracefulExit()) {
            conversationFinished(event.getContext());
        } else {
            event.getContext().getForWhom().sendRawMessage(ChatColor.RED + "Milestone addition canceled.");
        }
    }

    private void conversationFinished(ConversationContext context) {
        System.out.println("conversation finished");
        Statistic stat = (Statistic) context.getSessionData("statistic");
        String displayName = (String) context.getSessionData("name");
        Material material = (Material) context.getSessionData("material");
        List<String> lore = (List<String>) context.getSessionData("lore");
        String blockType = (String) context.getSessionData("block");
        if (stat == null || displayName == null || material == null || lore == null) {
            context.getForWhom().sendRawMessage(ChatColor.RED + "Milestone addition incomplete. Operation canceled.");
            return;
        }
        // Update the config file
        FileConfiguration config = configManager.getConfig();
        if (!config.isConfigurationSection("milestone-trackers")) {
            config.createSection("milestone-trackers");
        }
        ConfigurationSection trackers = config.getConfigurationSection("milestone-trackers");
        String statKey = stat.toString();
        if (trackers.isConfigurationSection(statKey)) {
            context.getForWhom().sendRawMessage(ChatColor.RED + "A milestone for " + statKey + " already exists.");
            return;
        }
        if (blockType != null) {
            System.out.println("adding block");
            statKey+=":" + blockType.toUpperCase();
        }

        ConfigurationSection newMilestone = trackers.createSection(statKey);
        newMilestone.set("material", material.toString());
        newMilestone.set("name", displayName);
        newMilestone.set("lore", lore);

        // Create an empty section for rewards (to be filled in later)
        newMilestone.createSection("milestone-rewards");
        configManager.saveConfig();
        context.getForWhom().sendRawMessage(ChatColor.GREEN + "Milestone added successfully!");
        configManager.reloadConfig();
    }

    // --- Conversation Prompts ---

    private class StatisticPrompt extends StringPrompt {
        @Override
        public String getPromptText(ConversationContext context) {
            return ChatColor.YELLOW + "Enter the statistic type (e.g. DAMAGE_TAKEN, MINE_BLOCK) or type 'cancel' to abort:";
        }

        @Override
        public Prompt acceptInput(ConversationContext context, String input) {
            if (input.equalsIgnoreCase("cancel")) {
                return Prompt.END_OF_CONVERSATION;
            }
            try {
                Statistic stat = Statistic.valueOf(input.toUpperCase());
                context.setSessionData("statistic", stat);
                // If it's MINE_BLOCK or PLACE_BLOCK, prompt for block type
                if (stat == Statistic.MINE_BLOCK) {
                    return new BlockTypePrompt();
                }
                return new NamePrompt();
            } catch (IllegalArgumentException e) {
                context.getForWhom().sendRawMessage(ChatColor.RED + "Invalid statistic type. Try again or type 'cancel'.");
                return this;
            }
        }
    }

    private class BlockTypePrompt extends StringPrompt {
        @Override
        public String getPromptText(ConversationContext context) {
            return ChatColor.YELLOW + "Enter the block type to track (e.g., STONE) or type 'all' for all blocks:";
        }

        @Override
        public Prompt acceptInput(ConversationContext context, String input) {
            if (input.equalsIgnoreCase("cancel")) {
                return Prompt.END_OF_CONVERSATION;
            }
            if (input.equalsIgnoreCase("all")) {
                context.setSessionData("block", "all");
                return new NamePrompt();
            }
            Material blockMaterial = Material.matchMaterial(input.toUpperCase());
            if (blockMaterial == null || !blockMaterial.isBlock()) {
                context.getForWhom().sendRawMessage(ChatColor.RED + "Invalid block type. Please enter a valid block type or 'all'.");
                return this;
            }
            context.setSessionData("block", blockMaterial.toString());
            return new NamePrompt();
        }
    }

    private class NamePrompt extends StringPrompt {
        @Override
        public String getPromptText(ConversationContext context) {
            return ChatColor.YELLOW + "Enter the display name for the milestone (use color codes like &a, &c) or type 'cancel' to abort:";
        }

        @Override
        public Prompt acceptInput(ConversationContext context, String input) {
            if (input.equalsIgnoreCase("cancel")) {
                return Prompt.END_OF_CONVERSATION;
            }
            context.setSessionData("name", input);
            return new MaterialPrompt();
        }
    }

    private class MaterialPrompt extends StringPrompt {
        @Override
        public String getPromptText(ConversationContext context) {
            return ChatColor.YELLOW + "Enter the material for the milestone icon (e.g. IRON_SWORD) or type 'cancel' to abort:";
        }

        @Override
        public Prompt acceptInput(ConversationContext context, String input) {
            if (input.equalsIgnoreCase("cancel")) {
                return Prompt.END_OF_CONVERSATION;
            }
            Material material = Material.matchMaterial(input.toUpperCase());
            if (material == null) {
                context.getForWhom().sendRawMessage(ChatColor.RED + "Invalid material. Try again or type 'cancel'.");
                return this;
            }
            context.setSessionData("material", material);
            return new LorePrompt();
        }
    }

    private class LorePrompt extends StringPrompt {
        @Override
        public String getPromptText(ConversationContext context) {
            return ChatColor.YELLOW + "Enter a lore line for the milestone. Type 'done' when finished, or 'cancel' to abort:";
        }

        @Override
        public Prompt acceptInput(ConversationContext context, String input) {
            if (input.equalsIgnoreCase("cancel")) {
                return Prompt.END_OF_CONVERSATION;
            }
            List<String> lore = (List<String>) context.getSessionData("lore");
            if (lore == null) {
                lore = new ArrayList<>();
            }
            if (input.equalsIgnoreCase("done")) {
                context.setSessionData("lore", lore);
                return new ConfirmationPrompt();
            }
            lore.add(input);
            context.setSessionData("lore", lore);
            return this; // Continue asking for more lore lines
        }
    }

    private class ConfirmationPrompt extends StringPrompt {
        @Override
        public String getPromptText(ConversationContext context) {
            Statistic stat = (Statistic) context.getSessionData("statistic");
            String name = (String) context.getSessionData("name");
            Material material = (Material) context.getSessionData("material");
            List<String> lore = (List<String>) context.getSessionData("lore");
            String block = (String) context.getSessionData("block");
            String confirmationMessage = ChatColor.AQUA + "Please confirm the milestone details:\n" +
                    ChatColor.GRAY + "Statistic: " + stat + "\n" +
                    "Name: " + name + "\n" +
                    "Material: " + material + "\n";
            if (block != null) {
                confirmationMessage += "Block Type: " + block + "\n";
            }
            confirmationMessage += "Lore: " + lore.toString() + "\n" +
                    "Type 'yes' to confirm or 'no' to cancel:";
            return confirmationMessage;
        }

        @Override
        public Prompt acceptInput(ConversationContext context, String input) {
            if (input.equalsIgnoreCase("yes")) {
                // The milestone will be added in conversationFinished
                return Prompt.END_OF_CONVERSATION;
            } else {
                context.getForWhom().sendRawMessage(ChatColor.RED + "Milestone addition canceled.");
                return Prompt.END_OF_CONVERSATION;
            }
        }
    }
}
