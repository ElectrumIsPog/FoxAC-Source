

package dev.isnow.fox.check.impl.player.badpackets;

import dev.isnow.fox.check.Check;
import dev.isnow.fox.check.api.CheckInfo;
import dev.isnow.fox.data.PlayerData;
import dev.isnow.fox.exempt.type.ExemptType;
import dev.isnow.fox.packet.Packet;

@CheckInfo(name = "BadPackets", type = "H", description = "Detects retarded clients.")
public final class BadPacketsH extends Check {
    public BadPacketsH(final PlayerData data) {
        super(data);
    }

    @Override
    public void handle(final Packet packet) {
        if (packet.isFlying()) {
            final double deltaY = data.getPositionProcessor().getDeltaY();

            final int groundTicks = data.getPositionProcessor().getGroundTicks();
            final int airTicks = data.getPositionProcessor().getAirTicks();

            final boolean exempt = isExempt(ExemptType.SLIME);
            final boolean invalid = deltaY == 0.0 && groundTicks == 1 && airTicks == 0;

            if (invalid && !exempt) {
                if (increaseBuffer() > 8) {
                    fail();
                }
            } else {
                resetBuffer();
            }
        }
    }
}
