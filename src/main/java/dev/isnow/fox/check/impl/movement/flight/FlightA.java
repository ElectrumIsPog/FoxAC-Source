package dev.isnow.fox.check.impl.movement.flight;

import dev.isnow.fox.check.Check;
import dev.isnow.fox.check.api.CheckInfo;
import dev.isnow.fox.data.PlayerData;
import dev.isnow.fox.exempt.type.ExemptType;
import dev.isnow.fox.packet.Packet;

@CheckInfo(name = "Flight", type = "A", description = "Predicts invalid movements.")
public final class FlightA extends Check {

    public FlightA(final PlayerData data) {
        super(data);
    }

    @Override
    public void handle(Packet packet) {
        if (packet.isPosition()) {
            final double deltaY = data.getPositionProcessor().getDeltaY();
            final double lastdeltaY = data.getPositionProcessor().getLastDeltaY();

            final boolean onGround = data.getPositionProcessor().getClientAirTicks() < 3 || data.getPositionProcessor().getAirTicks() < 4;

            final double prediction = Math.abs((lastdeltaY - 0.08) * 0.9800000190734863F) < 0.005 ? -0.08 * 0.9800000190734863F : (lastdeltaY - 0.08) * 0.9800000190734863F;
            final double difference = Math.abs(deltaY - prediction);

            final boolean exempt = isExempt(ExemptType.CHUNK,
                    ExemptType.CLIMBABLE,
                    ExemptType.FLYING,
                    ExemptType.VELOCITY_ON_TICK,
                    ExemptType.LIQUID,
                    ExemptType.SLIME,
                    ExemptType.TELEPORT_DELAY,
                    ExemptType.NEARSTAIRS);
            final boolean invalid = !exempt && difference > 0.001D && !onGround;

            debug("posY=" + data.getPositionProcessor().getY() + " dY=" + deltaY + " at=" + onGround);

            if (invalid) {
                if (increaseBuffer() > 5) {
                    fail(prediction);
                }
            } else {
                decreaseBufferBy(1);
            }

        }
    }
}