package dev.isnow.fox.check.impl.movement.flight;

import dev.isnow.fox.check.Check;
import dev.isnow.fox.check.api.CheckInfo;
import dev.isnow.fox.data.PlayerData;
import dev.isnow.fox.exempt.type.ExemptType;
import dev.isnow.fox.packet.Packet;

@CheckInfo(name = "Flight", type = "D", description = "Checks for too fast accel.")
public final class FlightD extends Check
{
    public FlightD(final PlayerData data) {
        super(data);
    }

    @Override
    public void handle(final Packet packet) {
        if (packet.isPosition()) {
            final int serverAirTicks = this.data.getPositionProcessor().getAirTicks();
            final int clientAirTicks = this.data.getPositionProcessor().getClientAirTicks();
            final double deltaY = this.data.getPositionProcessor().getDeltaY();
            final double lastDeltaY = this.data.getPositionProcessor().getLastDeltaY();
            final double acceleration = deltaY - lastDeltaY;
            final boolean exempt = this.isExempt(ExemptType.LAGGINGHARD, ExemptType.BUKKIT_PLACING, ExemptType.VELOCITY_ON_TICK, ExemptType.PISTON, ExemptType.VEHICLE, ExemptType.TELEPORT, ExemptType.LIQUID, ExemptType.BOAT, ExemptType.FLYING, ExemptType.SLIME, ExemptType.CLIMBABLE);
            final boolean invalid = acceleration > 0.0 && (serverAirTicks > 8 || clientAirTicks > 8);

            debug("deltaY: " + deltaY + "accel: " +acceleration+ "clientAir: " + clientAirTicks + "serverAir: " + clientAirTicks + "lastDeltaY" +lastDeltaY);

            if (invalid && !exempt) {
                if (increaseBuffer() > 5) {
                    fail();
                }
            }
            else {
                this.decreaseBufferBy(0.1);
            }
        }
    }
}