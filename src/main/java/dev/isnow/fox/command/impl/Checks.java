

package dev.isnow.fox.command.impl;

import dev.isnow.fox.check.Check;
import dev.isnow.fox.command.CommandInfo;
import dev.isnow.fox.command.FoxCommand;
import dev.isnow.fox.data.PlayerData;
import dev.isnow.fox.manager.PlayerDataManager;
import dev.isnow.fox.util.ColorUtil;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@CommandInfo(name = "checks", syntax = "<player>", purpose = "Get the players registered checks.")
public final class Checks extends FoxCommand {

    @Override
    protected boolean handle(final CommandSender sender, final Command command, final String label, final String[] args) {
        if (args.length == 2) {
            final Player player = Bukkit.getPlayer(args[1]);

            if (player != null) {
                final PlayerData playerData = PlayerDataManager.getInstance().getPlayerData(player);

                if (playerData != null) {
                    sendLineBreak(sender);
                    sendMessage(sender, ColorUtil.translate("&cThere are " + playerData.getChecks().size() + " registered checks for " + player.getName() + "\n" + " \n"));
                    for (final Check check : playerData.getChecks()) {
                        sendMessage(sender, ColorUtil.translate("&c" + check.getCheckInfo().name() + " (" + check.getCheckInfo().type() + ")"));
                    }
                    sendLineBreak(sender);
                    return true;
                }
            }
        }
        return false;
    }
}
