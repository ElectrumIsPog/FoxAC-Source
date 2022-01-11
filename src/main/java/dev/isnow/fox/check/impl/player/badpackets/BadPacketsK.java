

package dev.isnow.fox.check.impl.player.badpackets;

import dev.isnow.fox.check.Check;
import dev.isnow.fox.check.api.CheckInfo;
import dev.isnow.fox.data.PlayerData;
import dev.isnow.fox.packet.Packet;
import io.github.retrooper.packetevents.PacketEvents;
import io.github.retrooper.packetevents.packetwrappers.play.in.useentity.WrappedPacketInUseEntity;
import io.github.retrooper.packetevents.utils.player.ClientVersion;

@CheckInfo(name = "BadPackets", type = "K", description = "Checks for retards not swinging.")
public final class BadPacketsK extends Check {

    private boolean swung;

    public BadPacketsK(final PlayerData data) {
        super(data);
    }

    @Override
    public void handle(final Packet packet) {
        if(PacketEvents.get().getPlayerUtils().getClientVersion(data.getPlayer()).isNewerThan(ClientVersion.v_1_8)) {
            return;
        }
        if (packet.isUseEntity()) {
            final WrappedPacketInUseEntity wrapper = new WrappedPacketInUseEntity(packet.getRawPacket());

            if (wrapper.getAction() == WrappedPacketInUseEntity.EntityUseAction.ATTACK) {
                if (!swung) fail();
            }
        }

        else if (packet.isArmAnimation()) {
            swung = true;
        }

        else if (packet.isFlying()) {
            swung = false;
        }
    }
}
