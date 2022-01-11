package dev.isnow.fox.check.impl.player.crasher;

import dev.isnow.fox.check.Check;
import dev.isnow.fox.check.api.CheckInfo;
import dev.isnow.fox.data.PlayerData;
import dev.isnow.fox.packet.Packet;
import io.github.retrooper.packetevents.packetwrappers.play.in.flying.WrappedPacketInFlying;

@CheckInfo(name = "Crasher", description = "Checks for disablers.", type = "D")
public final class CrasherD extends Check {

    public CrasherD(final PlayerData data) {
        super(data);
    }

    @Override
    public void handle(final Packet packet) {
        if(packet.isFlying()) {
            WrappedPacketInFlying wrapped = new WrappedPacketInFlying(packet.getRawPacket());
            if (wrapped.getYaw() > 1200.0f && (wrapped.getYaw() % 360.0f > 1200.0f)) {
                fail("deltaYaw: " + wrapped.getYaw() );
            }
        }
    }
}