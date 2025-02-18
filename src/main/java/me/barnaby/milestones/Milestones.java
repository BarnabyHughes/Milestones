package me.barnaby.milestones;

// Import necessary classes for commands, data management, event listening, scheduling tasks, and Bukkit functionalities.
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

    // Instantiate managers for configurations, data, and sessions.
    private final ConfigManager configManager = new ConfigManager(this);
    private final DataManager dataManager = new DataManager(this);
    private final SessionManager sessionManager = new SessionManager(this);

    // The onEnable method is called when the plugin is starting up.
    @Override
    public void onEnable() {
        // Send an enable message to the console.
        sendEnableMessage();

        // Register the event listeners to handle player-related events.
        Bukkit.getPluginManager().registerEvents(new PlayerListeners(), this);

        // Schedule a repeating task to periodically check statistics.
        new StatisticChecker(this).runTaskTimer(this, 0, 100);

        // Set the command executor for the 'milestones' command.
        this.getCommand("milestones").setExecutor(new MilestoneCommand(this));
    }

    // The onDisable method is called when the plugin is being disabled.
    @Override
    public void onDisable() {
        // Send a disable message to the console.
        sendDisableMessage();
    }

    // Getter method for the config manager.
    public ConfigManager getConfigManager() {
        return configManager;
    }

    // Getter method for the data manager.
    public DataManager getDataManager() {
        return dataManager;
    }

    // Getter method for the session manager.
    public SessionManager getSessionManager() {
        return sessionManager;
    }

    // Private method to send an enable message to the console.
    private void sendEnableMessage() {
        // Retrieve plugin details from the plugin's description.
        String pluginName = getDescription().getName();
        String version = getDescription().getVersion();
        String author = String.join(", ", getDescription().getAuthors());

        // Format and send the message showing the plugin name, version, author, and enabled status.
        Bukkit.getConsoleSender().sendMessage(ChatColor.DARK_AQUA + "============================================");
        Bukkit.getConsoleSender().sendMessage(ChatColor.GOLD + "  " + pluginName + " v" + version);
        Bukkit.getConsoleSender().sendMessage(ChatColor.YELLOW + "  Developed by: " + ChatColor.AQUA + author);
        Bukkit.getConsoleSender().sendMessage(ChatColor.GREEN + "  Status: " + ChatColor.BOLD + "ENABLED!");
        Bukkit.getConsoleSender().sendMessage(ChatColor.DARK_AQUA + "============================================");
    }

    // Private method to send a disable message to the console.
    private void sendDisableMessage() {
        // Log a formatted message to the console indicating the plugin is now disabled.
        Bukkit.getConsoleSender().sendMessage(ChatColor.DARK_RED + "============================================");
        Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "  " + getDescription().getName() + " is now disabled.");
        Bukkit.getConsoleSender().sendMessage(ChatColor.DARK_RED + "============================================");
    }
}