package dev.isnow.fox.check.impl.combat.aura;

import dev.isnow.fox.check.Check;
import dev.isnow.fox.check.api.CheckInfo;
import dev.isnow.fox.data.PlayerData;
import dev.isnow.fox.packet.Packet;

@CheckInfo(name = "Aura", type = "F", description = "Invalid BLOCK PLACE & BLOCK DIG")
public class AuraF extends Check {
    public AuraF(PlayerData data) {
        super(data);
    }

    private boolean blockPlaced, blockDigged, entityUsed;

    @Override
    public void handle(Packet packet) {
        if (packet.isFlyingType() || packet.isBlockPlace() || packet.isBlockDig() || packet.isUseEntity()) {
            if (packet.isBlockPlace()) {
                blockPlaced = true;
            } else if (packet.isBlockDig()) {
                blockDigged = true;
            } else if (packet.isUseEntity()) {
                entityUsed = true;
            } else if (packet.isFlyingType()) {
                if (blockDigged && blockPlaced && entityUsed)
                    fail();
                blockPlaced = false;
                blockDigged = false;
                entityUsed = false;
            }
        }
    }
}