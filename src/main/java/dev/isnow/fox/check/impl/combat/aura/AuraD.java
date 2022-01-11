package dev.isnow.fox.check.impl.combat.aura;

import dev.isnow.fox.check.Check;
import dev.isnow.fox.check.api.CheckInfo;
import dev.isnow.fox.data.PlayerData;
import dev.isnow.fox.packet.Packet;
import dev.isnow.fox.util.PlayerUtil;

@CheckInfo(name = "Aura", type = "D", description = "Invalid Sword Block Movement")
public class AuraD extends Check {
    public AuraD(PlayerData data) {
        super(data);
    }

    private boolean blocked, stopSprint;

    //Fix, I think it falses when you block on the ground
    //It could also be caused by desync but I doubt that
    @Override
    public void handle(Packet packet) {
        if (PlayerUtil.isHoldingSword(data.getPlayer()) && data.getActionProcessor().isSprinting() && packet.isBlockPlace()) {
            blocked = true;
        } else if (packet.isStopSprinting()) {
            stopSprint = true;
        } else if (packet.isFlyingType()) {

            //if (blocked && !stopSprint)
            //flag();
            blocked = false;
            stopSprint = false;
        }
    }
}