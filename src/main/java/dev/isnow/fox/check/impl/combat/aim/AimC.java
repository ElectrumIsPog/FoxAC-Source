package dev.isnow.fox.check.impl.combat.aim;

import dev.isnow.fox.check.Check;
import dev.isnow.fox.check.api.CheckInfo;
import dev.isnow.fox.data.PlayerData;
import dev.isnow.fox.data.processor.RotationProcessor;
import dev.isnow.fox.packet.Packet;
import dev.isnow.fox.util.MathUtil;
import dev.isnow.fox.util.TimeUtils;

@CheckInfo(name = "Aim", description = "Checks for jitter.", type = "C")
public class AimC extends Check {
    public AimC(PlayerData data) {
        super(data);

    }

    @Override
    public void handle(Packet packet) {
        if (packet.isRotation() && TimeUtils.elapsed(data.getCombatProcessor().getLastAttack()) <= 1000L) {
            final RotationProcessor processor = data.getRotationProcessor();

            final float deltaYaw = processor.getDeltaYaw();
            final float lastDeltaYaw = processor.getLastDeltaYaw();
            final float deltaPitch = processor.getDeltaPitch();

            final double divisorYaw = MathUtil.getGcd((long) (deltaYaw * MathUtil.EXPANDER), (long) (lastDeltaYaw * MathUtil.EXPANDER));

            final double epik = data.getRotationProcessor().getGcd() / divisorYaw;
            debug(epik);
            if (deltaYaw > 0.0 && deltaPitch > 0.0 && deltaYaw < 1 && deltaPitch < 1) {
                if (epik > 1.0E-7) {
                    if (buffer++ > 10) {
                        buffer = 5;
                        fail(epik);
                    }
                } else {
                    decreaseBufferBy(2);
                }
            }
        }
    }
}
