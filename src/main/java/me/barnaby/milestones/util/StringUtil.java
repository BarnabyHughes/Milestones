package me.barnaby.milestones.util;

import org.bukkit.ChatColor;

public class StringUtil {

    public static String format(String s) {
        return ChatColor.translateAlternateColorCodes('&', s);
    }


    /**
     * Converts a given time in minutes to a human-readable string.
     * For example, 135 minutes becomes "2h 15m".
     *
     * @param minutes the time in minutes
     * @return a formatted string representing the time
     */
    public static String formatMinutes(int minutes) {
        int hours = minutes / 60;
        int remainingMinutes = minutes % 60;
        StringBuilder sb = new StringBuilder();
        if (hours > 0) {
            sb.append(hours).append("h");
        }
        if (remainingMinutes > 0) {
            if (sb.length() > 0) {
                sb.append(" ");
            }
            sb.append(remainingMinutes).append("m");
        }
        if (sb.length() == 0) {
            sb.append("0m");
        }
        return sb.toString();
    }

    /**
     * Converts a given number of ticks to a human-readable string.
     * Minecraft has 20 ticks per second, so this method converts ticks to minutes first.
     *
     * @param ticks the time in ticks
     * @return a formatted string representing the time in hours and minutes
     */
    public static String formatTicks(long ticks) {
        // Convert ticks to total seconds, then to minutes.
        int minutes = (int) ((ticks / 20) / 60);
        return formatMinutes(minutes);
    }

    public static String formatDistance(int cm) {
        return cm / 100 + "m";
    }
}
