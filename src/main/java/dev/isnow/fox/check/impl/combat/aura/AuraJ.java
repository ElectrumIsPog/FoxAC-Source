package dev.isnow.fox.check.impl.combat.aura;

import dev.isnow.fox.check.Check;
import dev.isnow.fox.check.api.CheckInfo;
import dev.isnow.fox.data.PlayerData;
import dev.isnow.fox.packet.Packet;
import dev.isnow.fox.util.PlayerUtil;

@CheckInfo(name = "Aura", type = "J", description = "Missing USE ENTITY Interact")
public class AuraJ extends Check {
    public AuraJ(PlayerData data) {
        super(data);
    }

    private boolean sentInteract;
    private boolean sentInteractAt;

    @Override
    public void handle(Packet packet) {

        if (PlayerUtil.is1_7(data.getPlayer())) return;

        if (packet.isUseEntityInteractAt()) {
            sentInteract = true;
        } else if (packet.isUseEntityInteract()) {
            sentInteractAt = true;
        } else if (packet.isFlyingType()) {
            if (sentInteract != sentInteractAt)
                fail("Interact: " + sentInteract + "\n" + "Interact At:" + sentInteractAt);
            sentInteract = false;
            sentInteractAt = false;
        }
    }
}