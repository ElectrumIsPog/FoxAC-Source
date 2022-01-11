package dev.isnow.fox.check.impl.combat.aim;

import dev.isnow.fox.check.Check;
import dev.isnow.fox.check.api.CheckInfo;
import dev.isnow.fox.data.PlayerData;
import dev.isnow.fox.exempt.type.ExemptType;
import dev.isnow.fox.packet.Packet;

@CheckInfo(name = "Aim", description = "Checks for snappy deltas.", type = "E")
public final class AimE
        extends Check {
    private float lastDeltaYaw;
    private float lastLastDeltaYaw;

    public AimE(PlayerData data) {
        super(data);
    }

    @Override
    public void handle(Packet packet) {
        if (packet.isRotation()) {
            boolean invalid;
            float deltaYaw = this.data.getRotationProcessor().getDeltaYaw();
            boolean exempt = this.isExempt(ExemptType.TELEPORT);
            boolean bl = invalid = deltaYaw < 1.5f && this.lastDeltaYaw > 30.0f && this.lastLastDeltaYaw < 1.5f;

            debug("deltaYaw: " + deltaYaw + "lastDeltaYaw: " +lastDeltaYaw+ "lastlast: " + lastLastDeltaYaw);

            if (exempt) {
                this.lastDeltaYaw = deltaYaw;
                this.lastLastDeltaYaw = deltaYaw;
            }
            if (invalid && !exempt && increaseBuffer() > 3) {
                fail();
                
                this.lastLastDeltaYaw = this.lastDeltaYaw;
                this.lastDeltaYaw = deltaYaw;
            }
        } else {
            this.decreaseBufferBy(0.25);
        }
    }
}