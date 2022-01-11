package dev.isnow.fox.check.impl.player.crasher;

import dev.isnow.fox.check.Check;
import dev.isnow.fox.check.api.CheckInfo;
import dev.isnow.fox.data.PlayerData;
import dev.isnow.fox.packet.Packet;
import io.github.retrooper.packetevents.packetwrappers.play.in.steervehicle.WrappedPacketInSteerVehicle;

@CheckInfo(name = "Crasher", description = "Checks for disablers.", type = "A")
public final class CrasherA extends Check {

    public CrasherA(final PlayerData data) {
        super(data);
    }

    @Override
    public void handle(final Packet packet) {
        if (packet.isSteerVehicle()) {
            final WrappedPacketInSteerVehicle wrapper = new WrappedPacketInSteerVehicle(packet.getRawPacket());

            final float forwardValue = Math.abs(wrapper.getForwardValue());
            final float sideValue = Math.abs(wrapper.getSideValue());

            final boolean invalid = forwardValue > .98F || sideValue > .98F;

            if (invalid) {
                fail(forwardValue);
            }
        }
    }
}