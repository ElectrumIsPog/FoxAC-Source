package dev.isnow.fox.command;

import dev.isnow.fox.config.Config;
import dev.isnow.fox.util.ColorUtil;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerJoinEvent;

import java.util.ArrayList;
import java.util.Collection;

public class VanishCommand implements CommandExecutor {

    public ArrayList<Player> inVanish = new ArrayList<Player>();

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String s, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;

            if (cmd.getName().equalsIgnoreCase("vanish")) {
                Collection<? extends Player> onlinePlayers = Bukkit.getServer().getOnlinePlayers();
                if (!inVanish.contains(player)) {
                    if (player.hasPermission("fox.vanish")) {

                        for (Player p : onlinePlayers) {
                            if (p.hasPermission("fox.vanish")) {
                                p.showPlayer(player);

                            } else {
                                p.hidePlayer(player);
                            }

                        }
                        player.setAllowFlight(true);
                        player.setFlying(true);
                        player.sendMessage(ColorUtil.translate(Config.VANISHON));
                        inVanish.add(player);
                    } else {
                        player.sendMessage(ColorUtil.translate(""));
                    }
                } else {
                    for (Player p : onlinePlayers) {
                        p.showPlayer(player);
                    }
                    player.setAllowFlight(false);
                    player.setFlying(false);
                    player.sendMessage(ColorUtil.translate(Config.VANISHOFF));
                    inVanish.remove(player);

                }
            }

        } else {
            sender.sendMessage("You are not a player!");
        }
        return true;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e) {
        for (Player p : inVanish) {
            e.getPlayer().hidePlayer(p);

            Collection<? extends Player> onlinePlayers = Bukkit.getServer().getOnlinePlayers();
            for (Player player : onlinePlayers) {
                if (player.hasPermission("fox.vanish")) {
                    player.canSee(p);
                }
            }
        }

    }
}