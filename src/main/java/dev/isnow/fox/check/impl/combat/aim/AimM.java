package dev.isnow.fox.check.impl.combat.aim;

import dev.isnow.fox.check.Check;
import dev.isnow.fox.check.api.CheckInfo;
import dev.isnow.fox.data.PlayerData;
import dev.isnow.fox.packet.Packet;

@CheckInfo(name = "Aim", description = "Checks for invalid deltas.", type = "M", experimental = true)
public final class AimM extends Check {

    public AimM(final PlayerData data) {
        super(data);
    }

    @Override
    public void handle(final Packet packet) {
        if (packet.isRotation()) {
            final float deltaPitch = data.getRotationProcessor().getDeltaPitch();
            final float deltaYaw = data.getRotationProcessor().getDeltaYaw();

            final boolean invalid = deltaYaw == 0.0F && deltaPitch >= 20.0F;

            debug("deltaPitch: " + deltaPitch + "deltaYaw: " +deltaYaw);

            if (invalid) {
                if (increaseBuffer() > 3) {
                    fail();
                }
            } else {
                decreaseBufferBy(0.05);
            }
        }
    }
}