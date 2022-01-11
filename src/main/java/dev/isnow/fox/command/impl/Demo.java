package dev.isnow.fox.command.impl;

import dev.isnow.fox.command.CommandInfo;
import dev.isnow.fox.command.FoxCommand;
import dev.isnow.fox.data.PlayerData;
import dev.isnow.fox.manager.PlayerDataManager;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@CommandInfo(name = "demo", purpose = "Make a demo screen popup for a player", syntax = "<player>")
public class Demo extends FoxCommand {
    @Override
    protected boolean handle(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 2) {
            final Player player = Bukkit.getPlayer(args[1]);
            assert player != null;
            final PlayerData data = PlayerDataManager.getInstance().getPlayerData(player);
            data.sendDemo(player);
            return true;
        }
        return false;
    }
}