package me.barnaby.milestones;

import me.barnaby.milestones.commands.MilestoneCommand;
import me.barnaby.milestones.config.ConfigManager;
import me.barnaby.milestones.listener.PlayerListeners;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.plugin.java.JavaPlugin;

public class Milestones extends JavaPlugin {

    private final ConfigManager configManager = new ConfigManager(this);

    @Override
    public void onEnable() {
        sendEnableMessage();

        Bukkit.getPluginManager().registerEvents(new PlayerListeners(), this);
        this.getCommand("milestones").setExecutor(new MilestoneCommand(this));
    }



    @Override
    public void onDisable() {
        sendDisableMessage();
    }

    public ConfigManager getConfigManager() {
        return configManager;
    }

    private void sendEnableMessage() {
        String pluginName = getDescription().getName();
        String version = getDescription().getVersion();
        String author = String.join(", ", getDescription().getAuthors());

        Bukkit.getConsoleSender().sendMessage(ChatColor.DARK_AQUA + "============================================");
        Bukkit.getConsoleSender().sendMessage(ChatColor.GOLD + "  " + pluginName + " v" + version);
        Bukkit.getConsoleSender().sendMessage(ChatColor.YELLOW + "  Developed by: " + ChatColor.AQUA + author);
        Bukkit.getConsoleSender().sendMessage(ChatColor.GREEN + "  Status: " + ChatColor.BOLD + "ENABLED!");
        Bukkit.getConsoleSender().sendMessage(ChatColor.DARK_AQUA + "============================================");
    }

    private void sendDisableMessage() {
        Bukkit.getConsoleSender().sendMessage(ChatColor.DARK_RED + "============================================");
        Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "  " + getDescription().getName() + " is now disabled.");
        Bukkit.getConsoleSender().sendMessage(ChatColor.DARK_RED + "============================================");
    }
}

