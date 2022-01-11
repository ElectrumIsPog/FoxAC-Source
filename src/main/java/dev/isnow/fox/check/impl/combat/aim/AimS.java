package dev.isnow.fox.check.impl.combat.aim;

import dev.isnow.fox.check.Check;
import dev.isnow.fox.check.api.CheckInfo;
import dev.isnow.fox.data.PlayerData;
import dev.isnow.fox.packet.Packet;

@CheckInfo(name = "Aim", type = "S", description = "Invalid rotation ratio")
public class AimS extends Check {

    public AimS(final PlayerData data) {
        super(data);
    }

    @Override
    public void handle(final Packet packet) {

        if (packet.isRotation() && hitTicks() < 5) {

            final float deltaYaw = data.getRotationProcessor().getDeltaYaw();
            final float deltaPitch = data.getRotationProcessor().getDeltaPitch();
            final boolean invalid = deltaYaw > .5F && deltaPitch < .0001 && deltaPitch > 0;

            if (invalid) {
                if (increaseBuffer() > 4) {
                    fail("deltaYaw=" + deltaYaw + " deltaPitch=" + deltaPitch);
                }
            } else {
                decreaseBufferBy(.25);
            }
        }
    }
}