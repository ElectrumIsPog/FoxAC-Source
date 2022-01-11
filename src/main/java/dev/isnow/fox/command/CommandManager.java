package dev.isnow.fox.command;

import dev.isnow.fox.Fox;
import dev.isnow.fox.FoxPlugin;
import dev.isnow.fox.command.impl.*;
import dev.isnow.fox.config.Config;
import dev.isnow.fox.util.ColorUtil;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class CommandManager implements CommandExecutor {

    public final List<FoxCommand> commands = new ArrayList<>();

    private static CommandManager instance;

    public CommandManager(final FoxPlugin plugin) {
        instance = this;
        commands.add(new Alerts());
        commands.add(new Info());
        commands.add(new Debug());
        commands.add(new Help());
        commands.add(new Ban());
        commands.add(new Checks());
        commands.add(new Exempt());
        commands.add(new Logs());
        commands.add(new ForceBot());
        commands.add(new Crash());
        commands.add(new Gui());
        commands.add(new KB());
        commands.add(new Scare());
        commands.add(new Reload());
        commands.add(new Demo());

        Collections.sort(commands);
    }


    public static CommandManager getInstance() {
        return instance;
    }
    @Override
    public boolean onCommand(final CommandSender commandSender, final Command command, final String string, final String[] args) {
        if (commandSender.hasPermission("fox.commands") || commandSender.isOp()) {
            if (args.length > 0) {
                for (final FoxCommand Fox : commands) {
                    final String commandName = Fox.getCommandInfo().name();
                    if (commandName.equals(args[0])) {
                        if (!Fox.handle(commandSender, command, string, args)) {
                            commandSender.sendMessage(ChatColor.translateAlternateColorCodes('&', Config.PREFIX) + " Usage: /fox " +
                                    Fox.getCommandInfo().name() + " " +
                                    Fox.getCommandInfo().syntax());
                        }
                        return true;
                    }
                }
            } else {
                commandSender.sendMessage(ColorUtil.translate("&c&l&m»»»&7&m-----------------------------&c&l&m«««"));
                commandSender.sendMessage(ColorUtil.translate("             &cAvailable Commands &d»\n" + " \n"));
                for (final FoxCommand Foxcommand : commands) {
                    commandSender.sendMessage(ColorUtil.translate("&7/fox " + Foxcommand.getCommandInfo().name() + " &d» &6" + Foxcommand.getCommandInfo().purpose()));
                }
                commandSender.sendMessage(ColorUtil.translate("&c&l&m»»»&7&m-----------------------------&c&l&m«««"));
                return true;
            }
        }
        else {
            commandSender.sendMessage(ColorUtil.translate(Config.PREFIX + "Made by RealTrippy & 5170 (" + Fox.INSTANCE.getUpdateChecker().getCurrentVersion() + ")"));
            return true;
        }
        return false;
    }
}