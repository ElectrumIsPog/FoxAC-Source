

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

@CommandInfo(name = "logs", purpose = "Shows all violations a player has made.", syntax = "<player>")
public final class Logs extends FoxCommand {
    @Override
    protected boolean handle(final CommandSender sender, final Command command, final String label, final String[] args) {
        if (args.length == 2) {
            final Player player = Bukkit.getPlayer(args[1]);

            if (player != null) {
                final PlayerData playerData = PlayerDataManager.getInstance().getPlayerData(player);

                if (playerData != null) {
                    sendLineBreak(sender);
                    sendMessage(sender, ColorUtil.translate("&cViolations for &c" + playerData.getPlayer().getName() + "&a."));

                    for (final Check check : playerData.getChecks()) {
                        if (check.getVl() > 0) {
                            sendMessage(sender, String.format("&c %s &8(&c%s&8) VL:&c %s", check.getCheckInfo().name(), check.getCheckInfo().type(), check.getVl()));
                        }
                    }

                    sendLineBreak(sender);

                    return true;
                }
            }
        }

        return false;
    }
}
