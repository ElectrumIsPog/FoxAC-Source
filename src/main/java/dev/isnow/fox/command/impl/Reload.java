package dev.isnow.fox.command.impl;

import dev.isnow.fox.Fox;
import dev.isnow.fox.command.CommandInfo;
import dev.isnow.fox.command.FoxCommand;
import dev.isnow.fox.config.Config;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
@CommandInfo(name = "reload", purpose = "Reloads the configs.")

public class Reload extends FoxCommand {
    @Override
    protected boolean handle(CommandSender sender, Command command, String label, String[] args) {
        Player p = (Player) sender;
        p.sendMessage(ChatColor.GRAY + "Reloading Config");
        Config.MAX_VIOLATIONS.clear();
        Config.ENABLED_CHECKS.clear();
        Fox.INSTANCE.getPlugin().reloadConfig();
        Fox.INSTANCE.reloadConfig();
        p.sendMessage(ChatColor.RED + "Successfully reloaded configs! (Experimental)");

        return true;
    }
}