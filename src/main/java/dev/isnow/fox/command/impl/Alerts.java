

package dev.isnow.fox.command.impl;

import dev.isnow.fox.command.CommandInfo;
import dev.isnow.fox.command.FoxCommand;
import dev.isnow.fox.config.Config;
import dev.isnow.fox.data.PlayerData;
import dev.isnow.fox.manager.AlertManager;
import dev.isnow.fox.manager.PlayerDataManager;
import dev.isnow.fox.util.ColorUtil;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@CommandInfo(name = "alerts", purpose = "Toggles cheat alerts.")
public final class Alerts extends FoxCommand implements CommandExecutor {

    @Override
    protected boolean handle(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player && sender.hasPermission("fox.alerts")) {
            final Player player = (Player) sender;
                    final PlayerData data = PlayerDataManager.getInstance().getPlayerData(player);

                    if (data != null) {
                        if (AlertManager.toggleAlerts(data) == AlertManager.ToggleAlertType.ADD) {
                            sendMessage(sender, ColorUtil.translate(Config.ALERTSON));
                        } else {
                            sendMessage(sender, ColorUtil.translate(Config.ALERTSOFF));
                        }
                        return true;
                    }

                } else {
                    sendMessage(sender, "Only players can execute this command.");
                }
        return false;
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if(commandSender instanceof Player) {
            handle(commandSender, command, s, strings);
        }
        return true;
    }
}