package dev.isnow.fox.check.impl.player.crasher;

import dev.isnow.fox.check.Check;
import dev.isnow.fox.check.api.CheckInfo;
import dev.isnow.fox.data.PlayerData;
import dev.isnow.fox.packet.Packet;
import io.github.retrooper.packetevents.packetwrappers.play.in.steervehicle.WrappedPacketInSteerVehicle;

@CheckInfo(name = "Crasher", description = "Checks for disablers.", type = "B")
public final class CrasherB extends Check {

    public CrasherB(final PlayerData data) {
        super(data);
    }

    @Override
    public void handle(final Packet packet) {
        if (packet.isSteerVehicle()) {
            final WrappedPacketInSteerVehicle wrapper = new WrappedPacketInSteerVehicle(packet.getRawPacket());

            final boolean unmount = wrapper.isDismount();

            final boolean invalid = data.getPlayer().getVehicle() == null && !unmount;

            if (invalid) {
                if (++buffer > 5) {
                    fail("Invalid Vehicle");
                    buffer /= 2;
                }
            } else {
                buffer = 0;
            }
        }
    }
}