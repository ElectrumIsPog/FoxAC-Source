package dev.isnow.fox.check.impl.combat.aim;

import dev.isnow.fox.check.Check;
import dev.isnow.fox.check.api.CheckInfo;
import dev.isnow.fox.data.PlayerData;
import dev.isnow.fox.data.processor.RotationProcessor;
import dev.isnow.fox.exempt.type.ExemptType;
import dev.isnow.fox.packet.Packet;
import dev.isnow.fox.util.MathUtil;

@CheckInfo(name = "Aim", description = "Checks for GCD bypasses.", type = "N", experimental = true)
public class AimN extends Check {
    public AimN(PlayerData data) {
        super(data);
    }

    @Override
    public void handle(Packet packet) {
        if (packet.isRotation() && System.currentTimeMillis() - data.getCombatProcessor().getLastAttack() < 850) {
            final RotationProcessor processor = data.getRotationProcessor();

            final float deltaPitch = processor.getDeltaPitch();
            final float lastDeltaPitch = processor.getLastDeltaPitch();

            if (deltaPitch > 1 && !isExempt(ExemptType.TELEPORT)) {
                final long expanded = (long) (deltaPitch * MathUtil.EXPANDER);
                final long lastExpanded = (long) (lastDeltaPitch * MathUtil.EXPANDER);

                final long gcd = MathUtil.getGcd(expanded, lastExpanded);

                final double divisor = gcd / MathUtil.EXPANDER;

                final double moduloPitch = Math.abs(processor.getPitch() % divisor);

                debug("modulo=" + moduloPitch);

                if (moduloPitch < 1.2E-5) {
                    if (buffer++ > 3) {
                        fail("modulo=" + moduloPitch);
                    }
                }
            } else {
                decreaseBufferBy(0.5);
            }
        }
    }
}