package dev.isnow.fox.check.impl.movement.flight;

import dev.isnow.fox.check.Check;
import dev.isnow.fox.check.api.CheckInfo;
import dev.isnow.fox.data.PlayerData;
import dev.isnow.fox.exempt.type.ExemptType;
import dev.isnow.fox.packet.Packet;

@CheckInfo(name = "Flight", type = "C", description = "Checks if player isn't falling in air.")
public final class FlightC extends Check {

    private double stableY;

    public FlightC(PlayerData data) {
        super(data);
    }

    @Override
    public void handle(Packet packet) {
        if (packet.isPosition()) {
            if (this.isExempt(ExemptType.FLYING, ExemptType.TELEPORT_DELAY, ExemptType.CREATIVE, ExemptType.PLACING)) {
                return;
            }
            this.stableY = this.data.getPositionProcessor().getY() == this.data.getPositionProcessor().getLastY() && this.data.getPositionProcessor().isInAir() ? (this.stableY += 1.0) : 0.0;
            if (this.stableY > 2.0) {
                fail(this.stableY);
            }
        }
    }
}