

package dev.isnow.fox.packet.processor;

import dev.isnow.fox.data.PlayerData;
import dev.isnow.fox.packet.Packet;
import io.github.retrooper.packetevents.packetwrappers.play.out.entityvelocity.WrappedPacketOutEntityVelocity;
import io.github.retrooper.packetevents.packetwrappers.play.out.position.WrappedPacketOutPosition;

public final class SendingPacketProcessor  {

    public void handle(final PlayerData data, final Packet packet) {
        if (packet.isVelocity()) {
            final WrappedPacketOutEntityVelocity wrapper = new WrappedPacketOutEntityVelocity(packet.getRawPacket());

            if (wrapper.getEntityId() == data.getPlayer().getEntityId()) {
                data.getVelocityProcessor().handle(wrapper.getVelocity().getX(), wrapper.getVelocity().getY(), wrapper.getVelocity().getZ());
            }
        }
        if (packet.isTeleport()) {
            final WrappedPacketOutPosition wrapper = new WrappedPacketOutPosition(packet.getRawPacket());

            data.getPositionProcessor().handleTeleport(wrapper);
        }
        data.getChecks().forEach(check -> check.handle(packet));
    }
}
