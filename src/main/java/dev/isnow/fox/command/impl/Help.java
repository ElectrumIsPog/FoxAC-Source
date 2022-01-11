package dev.isnow.fox.command.impl;

import dev.isnow.fox.command.CommandInfo;
import dev.isnow.fox.command.CommandManager;
import dev.isnow.fox.command.FoxCommand;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

@CommandInfo(name = "help", purpose = "Prints every command.")
public class Help extends FoxCommand {
    @Override
    protected boolean handle(CommandSender sender, Command command, String label, String[] args) {
        for (final FoxCommand Foxcommand : CommandManager.getInstance().commands) {
            final String commandName = Foxcommand.getCommandInfo().name();
            if (commandName.equals(args[0])) {
                    sender.sendMessage(ChatColor.GRAY + "[" + ChatColor.RED + "Fox" + ChatColor.GRAY + "]" + " Usage: /fox " +
                            Foxcommand.getCommandInfo().name() + " " +
                            Foxcommand.getCommandInfo().syntax());
                return true;
            }
        }
        return false;
    }
}
