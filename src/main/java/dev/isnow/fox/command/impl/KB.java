package dev.isnow.fox.command.impl;

import dev.isnow.fox.command.CommandInfo;
import dev.isnow.fox.command.FoxCommand;
import dev.isnow.fox.config.Config;
import dev.isnow.fox.data.PlayerData;
import dev.isnow.fox.manager.PlayerDataManager;
import dev.isnow.fox.util.ColorUtil;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

@CommandInfo(name = "kb", syntax = "<player>", purpose = "Test player for anti-kb")
public final class KB extends FoxCommand
{
    @Override
    protected boolean handle(final CommandSender sender, final Command command, final String label, final String[] args) {
        if (args.length == 2) {
            final Player player = Bukkit.getPlayer(args[1]);
            if (player != null) {
                final PlayerData playerData = PlayerDataManager.getInstance().getPlayerData(player);
                if (playerData != null) {
                    player.setVelocity(new Vector(4, 8, 1));
                    this.sendMessage(sender, ColorUtil.translate(Config.KBTEST.replaceAll("%player%", playerData.getPlayer().getName())));
                    return true;
                }
            }
        }
        return false;
    }
}