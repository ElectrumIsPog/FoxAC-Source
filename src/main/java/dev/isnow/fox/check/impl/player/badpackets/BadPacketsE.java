package dev.isnow.fox.check.impl.player.badpackets;

import dev.isnow.fox.check.Check;
import dev.isnow.fox.check.api.CheckInfo;
import dev.isnow.fox.data.PlayerData;
import dev.isnow.fox.packet.Packet;
import io.github.retrooper.packetevents.PacketEvents;
import io.github.retrooper.packetevents.packetwrappers.play.in.useentity.WrappedPacketInUseEntity;
import io.github.retrooper.packetevents.utils.player.ClientVersion;

@CheckInfo(name = "BadPackets", type = "E", description = "Checks for attacking and digging.")
public class BadPacketsE extends Check {

    public BadPacketsE(PlayerData data) {
        super(data);
    }

    @Override
    public void handle(Packet packet) {
        if (packet.isUseEntity()) {
            if(PacketEvents.get().getPlayerUtils().getClientVersion(data.getPlayer()).isNewerThan(ClientVersion.v_1_8)) {
                return;
            }
            final WrappedPacketInUseEntity wrapper = new WrappedPacketInUseEntity(packet.getRawPacket());
            if (wrapper.getAction() == WrappedPacketInUseEntity.EntityUseAction.ATTACK) {
                final boolean sword = this.data.getPlayer().getItemInHand().getType().toString().contains("SWORD");
                final boolean invalid = this.data.getActionProcessor().isSendingDig();
                if (invalid && sword) {
                    if (increaseBuffer() > 5.0) {
                    }
                }
                else {
                    setBuffer(Math.max(getBuffer() - 1.0, 0.0));
                }
            }
        }
    }
}
