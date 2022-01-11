package dev.isnow.fox.check.impl.combat.aim;

import dev.isnow.fox.check.Check;
import dev.isnow.fox.check.api.CheckInfo;
import dev.isnow.fox.data.PlayerData;
import dev.isnow.fox.packet.Packet;
import dev.isnow.fox.util.MathUtil;

@CheckInfo(name = "Aim", description = "Checks for invalid divisor.", type = "A")
public final class AimA
        extends Check {

    private float lastDeltaYaw, lastDeltaPitch;

    public AimA(PlayerData data) {
        super(data);
    }

    @Override
    public void handle(Packet packet) {
        if (packet.isRotation() && !data.getRotationProcessor().isCinematic()) {
            final float deltaYaw = data.getRotationProcessor().getDeltaYaw();
            final float deltaPitch = data.getRotationProcessor().getDeltaPitch();

            final double divisorYaw = MathUtil.getGcd((long) (deltaYaw * MathUtil.EXPANDER), (long) (lastDeltaYaw * MathUtil.EXPANDER));
            final double divisorPitch = MathUtil.getGcd((long) (deltaPitch * MathUtil.EXPANDER), (long) (lastDeltaPitch * MathUtil.EXPANDER));

            final double constantYaw = divisorYaw / MathUtil.EXPANDER;
            final double constantPitch = divisorPitch / MathUtil.EXPANDER;

            final double currentX = deltaYaw / constantYaw;
            final double currentY = deltaPitch / constantPitch;

            final double previousX = lastDeltaYaw / constantYaw;
            final double previousY = lastDeltaPitch / constantPitch;

            final boolean action = data.getCombatProcessor().getLastAttackTick() < 3;

            if (deltaYaw > 0.0 && deltaPitch > 0.0 && deltaYaw < 20.f && deltaPitch < 20.f && action) {
                final double moduloX = currentX % previousX;
                final double moduloY = currentY % previousY;

                final double floorModuloX = Math.abs(Math.floor(moduloX) - moduloX);
                final double floorModuloY = Math.abs(Math.floor(moduloY) - moduloY);

                final boolean invalidX = moduloX > 90.d && floorModuloX > 0.1;
                final boolean invalidY = moduloY > 90.d && floorModuloY > 0.1;

                debug("deltaYaw: " + deltaYaw + " deltaPitch: " +deltaPitch+ " divisorPitch: " + divisorPitch + " divisorYaw: " +divisorYaw+ " constantYaw: " +constantYaw+ " constantPitch: "+ constantYaw);

                if (invalidX && invalidY) {
                    buffer++;
                    if (buffer > 8) {
                        fail(deltaYaw + " deltaPitch: " +deltaPitch+ " divisorPitch: " + divisorPitch + " divisorYaw: " +divisorYaw+ " constantYaw: " +constantYaw+ " constantPitch: "+ constantYaw);
                    }
                } else {
                    buffer = 0;
                }
            }
            this.lastDeltaYaw = deltaYaw;
            this.lastDeltaPitch = deltaPitch;
        }
    }
}