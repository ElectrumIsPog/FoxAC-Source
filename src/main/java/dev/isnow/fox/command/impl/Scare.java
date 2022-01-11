package dev.isnow.fox.command.impl;

import dev.isnow.fox.command.CommandInfo;
import dev.isnow.fox.command.FoxCommand;
import dev.isnow.fox.config.Config;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@CommandInfo(name = "scare", syntax = "<player>", purpose = "Make player shit themselfs")
public class Scare extends FoxCommand {
    protected boolean handle(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 2) {
            Player p = Bukkit.getPlayer(args[1]);
            final Player player = Bukkit.getPlayer(args[1]);
            if (p == null) {
                sendMessage(sender, "&cFailed To send!");
                return true;
            }
            sendGameState(p, 10, 0.0F);
            sendMessage(sender, Config.SCARE.replaceAll("%player%", player.getName()));
            return true;
        }
        return false;
    }

    public void sendGameState(Player player, int type, float state) {
        try {
            Object entityPlayer = player.getClass().getMethod("getHandle", new Class[0]).invoke(player);
            Object playerConnection = entityPlayer.getClass().getField("playerConnection").get(entityPlayer);
            Object packet = getNMSClass("PacketPlayOutGameStateChange").getConstructor(new Class[] { int.class, float.class }).newInstance(Integer.valueOf(type), Float.valueOf(state));
            playerConnection.getClass().getMethod("sendPacket", new Class[] { getNMSClass("Packet") }).invoke(playerConnection, packet);
        } catch (IllegalAccessException|IllegalArgumentException|java.lang.reflect.InvocationTargetException|NoSuchMethodException|SecurityException|NoSuchFieldException|InstantiationException e) {
            e.printStackTrace();
        }
    }

    public Class<?> getNMSClass(String name) {
        try {
            return Class.forName("net.minecraft.server." + getVersion() + "." + name);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }

    public String getVersion() {
        return Bukkit.getServer().getClass().getPackage().getName().split("\\.")[3];
    }
}