package dev.isnow.fox.check.impl.combat.aura;

import dev.isnow.fox.check.Check;
import dev.isnow.fox.check.api.CheckInfo;
import dev.isnow.fox.data.PlayerData;
import dev.isnow.fox.packet.Packet;

@CheckInfo(name = "Aura", type = "H", description = "Missing ARM ANIMATION packet")
public class AuraH extends Check {
    public AuraH(PlayerData data) {
        super(data);
    }

    private boolean armAnimation, useEntity;

    @Override
    public void handle(Packet packet) {
        if (packet.isArmAnimation()) {
            armAnimation = true;
        } else if (packet.isUseEntityAttack()) {
            useEntity = true;
        } else if (packet.isFlyingType()) {
            if (useEntity && !armAnimation)
                fail("No Swing");
            armAnimation = false;
            useEntity = false;
        }
    }
}