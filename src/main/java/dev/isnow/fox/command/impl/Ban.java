package dev.isnow.fox.command.impl;

import dev.isnow.fox.Fox;
import dev.isnow.fox.command.CommandInfo;
import dev.isnow.fox.command.FoxCommand;
import dev.isnow.fox.config.Config;
import dev.isnow.fox.util.ColorUtil;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

@CommandInfo(name = "ban", syntax = "<player>", purpose = "Bans player with anticheat.")
public class Ban extends FoxCommand {

    @Override
    protected boolean handle(CommandSender sender, Command command, String label, String[] args) {
        if(args.length >= 2) {
            Player p = Bukkit.getPlayer(args[1]);
            if(p == null) {
                sender.sendMessage(ColorUtil.translate("&cThis player does not exist!"));
                return true;
            }
            if(!Config.GLOBALCMD) {
                sender.sendMessage(ColorUtil.translate("&cGlobal commands aren't enabled!"));
                return true;
            }

            for(String s : Config.GLOBAL_COMMANDS) {
                if(!s.equals("")) {
                    s = ChatColor.translateAlternateColorCodes('&', s);
                    s = s.replaceAll("%player%", p.getName())
                            .replaceAll("%prefix%", ColorUtil.translate(Config.PREFIX))
                            .replaceAll("%check%", "")
                            .replaceAll("%type%", "")
                            .replaceAll("%vl%", "")
                            .replaceAll("Vl:", "")
                            .replaceAll("VL:", "")
                            .replaceAll("vl:", "")
                            .replaceAll("vl", "")
                            .replaceAll("MAXVl:", "")
                            .replaceAll("MAXVL:", "")
                            .replaceAll("MAXVL", "")
                            .replaceAll("maxvl", "")
                            .replaceAll("MaxVl", "")
                            .replaceAll("MaxVl:", "")
                            .replaceAll("%maxvl%", "");
                    String finalS = s;
                    Bukkit.getScheduler().runTask(Fox.INSTANCE.getPlugin(), () -> Bukkit.getServer().dispatchCommand(Bukkit.getServer().getConsoleSender(), finalS));
                }
            }
            if(Config.BANTIMER) {
                new BukkitRunnable() {
                    @Override
                    public void run() {
                        for(String s : Config.TIMER_COMMANDS) {
                            if(!s.equals("")) {
                                s = ChatColor.translateAlternateColorCodes('&', s);
                                s = s.replaceAll("%player%", p.getName())
                                        .replaceAll("%prefix%", ColorUtil.translate(Config.PREFIX))
                                        .replaceAll("%check%", "")
                                        .replaceAll("%type%", "")
                                        .replaceAll("%vl%", "")
                                        .replaceAll("%maxvl%", "");
                                String finalS = s;
                                Bukkit.getScheduler().runTask(Fox.INSTANCE.getPlugin(), () -> Bukkit.getServer().dispatchCommand(Bukkit.getServer().getConsoleSender(), finalS));
                            }
                        }
                    }
                }.runTaskLater(Fox.INSTANCE.getPlugin(), Config.BANTIMERTIME * 20L);
            }
            sender.sendMessage(ColorUtil.translate("&cBannin."));
        }
        return false;
    }
}
