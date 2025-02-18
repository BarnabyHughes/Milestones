package me.barnaby.milestones;

import me.barnaby.milestones.commands.MilestoneCommand;
import me.barnaby.milestones.data.ConfigManager;
import me.barnaby.milestones.data.DataManager;
import me.barnaby.milestones.listener.PlayerListeners;
import me.barnaby.milestones.runnable.StatisticChecker;
import me.barnaby.milestones.session.SessionManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.plugin.java.JavaPlugin;

public class Milestones extends JavaPlugin {

    private final ConfigManager configManager = new ConfigManager(this);
    private final DataManager dataManager = new DataManager(this);
    private final SessionManager sessionManager = new SessionManager(this);

    @Override
    public void onEnable() {
        sendEnableMessage();

        Bukkit.getPluginManager().registerEvents(new PlayerListeners(), this);

        new StatisticChecker(this).runTaskTimer(this,0,
                100);
        this.getCommand("milestones").setExecutor(new MilestoneCommand(this));
    }



    @Override
    public void onDisable() {
        sendDisableMessage();
    }

    public ConfigManager getConfigManager() {
        return configManager;
    }

    public DataManager getDataManager() {
        return dataManager;
    }

    public SessionManager getSessionManager() {
        return sessionManager;
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

