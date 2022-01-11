package dev.isnow.fox.check.impl.combat.aim;

import dev.isnow.fox.check.Check;
import dev.isnow.fox.check.api.CheckInfo;
import dev.isnow.fox.data.PlayerData;
import dev.isnow.fox.exempt.type.ExemptType;
import dev.isnow.fox.packet.Packet;
import dev.isnow.fox.util.MathUtil;

@CheckInfo(name = "Aim", description = "Checks for invalid gcd rotations.", type = "H")
public final class AimH
        extends Check {
    public AimH(PlayerData data) {
        super(data);
    }

    @Override
    public void handle(Packet packet) {
        if (packet.isRotation()) {
            float deltaPitch = this.data.getRotationProcessor().getDeltaPitch();
            float lastDeltaPitch = this.data.getRotationProcessor().getLastDeltaPitch();
            long expandedDeltaPitch = (long)((double)deltaPitch * MathUtil.EXPANDER);
            long expandedLastDeltaPitch = (long)((double)lastDeltaPitch * MathUtil.EXPANDER);
            long gcd = MathUtil.getGcd(expandedDeltaPitch, expandedLastDeltaPitch);
            boolean exempt = deltaPitch == 0.0f || lastDeltaPitch == 0.0f || this.isExempt(ExemptType.CINEMATIC);
            debug("gcd: " + gcd + "deltaPitch: " +deltaPitch+ "lastDeltaPitch: " + lastDeltaPitch + "expandDelta" +expandedDeltaPitch);
            if (!exempt && gcd < 131072L && gcd > 66000) {
                if (increaseBuffer() > 5) {
                    fail(gcd);
                    
                }
            } else {
                this.decreaseBufferBy(1.0);
            }
        }
    }
}