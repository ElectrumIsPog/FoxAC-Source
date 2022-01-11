package dev.isnow.fox.check.impl.movement.flight;

import dev.isnow.fox.check.Check;
import dev.isnow.fox.check.api.CheckInfo;
import dev.isnow.fox.data.PlayerData;
import dev.isnow.fox.exempt.type.ExemptType;
import dev.isnow.fox.packet.Packet;

@CheckInfo(name = "Flight", type = "B", description = "Checks for invalid gravity.")
public final class FlightB extends Check {
    private double minDelta;

    public FlightB(PlayerData data) {
        super(data);
    }

    @Override
    public void handle(Packet packet) {
        if (packet.isPosition()) {
            if (this.isExempt(ExemptType.CHUNK, ExemptType.FLYING, ExemptType.GETTINGCOMBOED,ExemptType.PEARL, ExemptType.ICE, ExemptType.PLACING, ExemptType.SLIME, ExemptType.VOID, ExemptType.RESPAWN, ExemptType.VEHICLE, ExemptType.TELEPORT, ExemptType.GHOST_BLOCK, ExemptType.CREATIVE, ExemptType.COMBAT, ExemptType.UPWARDS_VEL)) {
                return;
            }

            final double dY = this.data.getPositionProcessor().getDeltaY();
            this.minDelta = Math.min(this.minDelta, dY);
            if (this.data.getPositionProcessor().getAirTicks() > 20) {
                if (dY > this.minDelta) {
                    fail("DeltaY: " + dY + " MinDelta: " + this.minDelta);
                }
            }
            else {
                this.minDelta = 0.0;
            }
        }
    }
}