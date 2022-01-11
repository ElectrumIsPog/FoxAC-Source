package dev.isnow.fox.check.impl.combat.velocity;

import dev.isnow.fox.check.Check;
import dev.isnow.fox.check.api.CheckInfo;
import dev.isnow.fox.data.PlayerData;
import dev.isnow.fox.exempt.type.ExemptType;
import dev.isnow.fox.packet.Packet;

@CheckInfo(name = "Velocity", type = "A", description = "Checks for invalid vertical.")
public final class VelocityA extends Check {
    public VelocityA(final PlayerData data) {
        super(data);
    }

    @Override
    public void handle(final Packet packet) {
        if (packet.isFlying()) {
            final int ticksSinceVelocity = data.getVelocityProcessor().getTicksSinceVelocity();
            if (ticksSinceVelocity != 1) return;

            final double deltaY = data.getPositionProcessor().getDeltaY();
            final double expectedDeltaY = data.getVelocityProcessor().getVelocityY();

            final double difference = Math.abs(deltaY - expectedDeltaY);
            final double percentage = (deltaY * 100.0) / expectedDeltaY;

            final boolean exempt = isExempt(ExemptType.LIQUID, ExemptType.PISTON, ExemptType.CLIMBABLE,
                    ExemptType.UNDERBLOCK, ExemptType.TELEPORT, ExemptType.FLYING, ExemptType.JUMP, ExemptType.NEAR_WALL);
            final boolean invalid = difference > 1E-10 && expectedDeltaY > 1E-2;

            if (invalid && !exempt) {
                if (increaseBuffer() > 2) {
                    fail();
                }
            } else {
                decreaseBufferBy(0.25);
            }
        }
    }
}