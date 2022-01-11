package dev.isnow.fox.check.impl.combat.aura;

import dev.isnow.fox.check.Check;
import dev.isnow.fox.check.api.CheckInfo;
import dev.isnow.fox.data.PlayerData;
import dev.isnow.fox.packet.Packet;

@CheckInfo(name = "Aura", description = "Checks for impossible accuracy.", type = "O", experimental = true)

public final class AuraO extends Check {

    public AuraO(PlayerData data) {
        super(data);
    }

    @Override
    public void handle(final Packet packet) {
        if(packet.isUseEntity()) {
            final boolean invalid = data.getCombatProcessor().getHitMissRatio() > 99 &&
                    data.getRotationProcessor().getDeltaYaw() > 1.5F &&
                    data.getRotationProcessor().getDeltaPitch() > 0 &&
                    data.getPositionProcessor().getDeltaXZ() > 0.1;

            if (invalid) {
                if (increaseBuffer() > 25) {
                    fail(data.getCombatProcessor().getHitMissRatio());
                }
            } else {
                decreaseBufferBy(2);
            }
        }
    }
}