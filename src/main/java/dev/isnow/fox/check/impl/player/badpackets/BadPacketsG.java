

package dev.isnow.fox.check.impl.player.badpackets;

import dev.isnow.fox.check.Check;
import dev.isnow.fox.check.api.CheckInfo;
import dev.isnow.fox.data.PlayerData;
import dev.isnow.fox.packet.Packet;
import io.github.retrooper.packetevents.packetwrappers.play.in.useentity.WrappedPacketInUseEntity;

@CheckInfo(name = "BadPackets", type = "G", description = "Checks if player attacked themselves.")
public final class BadPacketsG extends Check {
    public BadPacketsG(final PlayerData data) {
        super(data);
    }

    @Override
    public void handle(final Packet packet) {
        if (packet.isUseEntity()) {
            final WrappedPacketInUseEntity wrapper = new WrappedPacketInUseEntity(packet.getRawPacket());

            if (data.getPlayer().getEntityId() == wrapper.getEntityId()) {
                fail();
            }
        }
    }
}
