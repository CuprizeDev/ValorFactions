package com.massivecraft.factions.util;

import com.massivecraft.factions.FactionsPlugin;
import com.massivecraft.factions.zcore.util.TextUtil;
import org.bukkit.ChatColor;

public class Logger {

    public static void print(String message) {
        print(message, PrefixType.DEFAULT);
    }

    public static void print(String message, PrefixType type) {
        FactionsPlugin.getInstance().getServer().getConsoleSender().sendMessage(type.getPrefix() + message);
    }

    public static void printArgs(String message, PrefixType type, Object... args) {
        FactionsPlugin.getInstance().getServer().getConsoleSender().sendMessage(type.getPrefix() + TextUtil.parse(message, args));
    }

    public enum PrefixType {

        DEBUG(ChatColor.YELLOW + "DEBUG: "),
        WARNING(ChatColor.RED + "WARNING: "),
        NONE(""),
        DEFAULT(ChatColor.RED.toString()),
        HEADLINE(ChatColor.RED + ""),
        FAILED(ChatColor.RED + "FAILED: ");

        private final String prefix;

        PrefixType(String prefix) {
            this.prefix = ChatColor.DARK_RED + "[ValorFactions] " + prefix;
        }

        public String getPrefix() {
            return this.prefix;
        }
    }
}
