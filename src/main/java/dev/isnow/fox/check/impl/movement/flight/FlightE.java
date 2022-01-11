package dev.isnow.fox.check.impl.movement.flight;

import dev.isnow.fox.check.Check;
import dev.isnow.fox.check.api.CheckInfo;
import dev.isnow.fox.data.PlayerData;
import dev.isnow.fox.exempt.type.ExemptType;
import dev.isnow.fox.packet.Packet;

@CheckInfo(name = "Flight", type = "E", description = "Predicts flying using elon musk api + ai")
public final class FlightE extends Check {

    public FlightE(final PlayerData data) {
        super(data);
    }

    @Override
    public void handle(Packet packet) {
        if (packet.isFlying()) {

            double yDelta = data.getPositionProcessor().getDeltaY();
            boolean isInAir = data.getPositionProcessor().isInAir();
            double ticks = data.getPositionProcessor().getAirTicks();
            boolean exempt = isExempt(ExemptType.NEARSTAIRS, ExemptType.FLYING, ExemptType.CHUNK, ExemptType.CREATIVE, ExemptType.TELEPORT_DELAY)
                    && (data.getPositionProcessor().getSinceTeleportTicks() > 40);

            debug("inAir:" + isInAir + "   ticks:" + data.getPositionProcessor().getAirTicks());

            if (isInAir && ticks > 15 && !exempt && yDelta >= 0) {
                if (increaseBuffer() > 2) {
                    fail(ticks);
                }
            }
            else {
                decreaseBufferBy(0.35);
            }
        }
    }
}
