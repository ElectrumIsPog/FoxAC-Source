package dev.isnow.fox.check.impl.player.badpackets;

import dev.isnow.fox.check.Check;
import dev.isnow.fox.check.api.CheckInfo;
import dev.isnow.fox.data.PlayerData;
import dev.isnow.fox.exempt.type.ExemptType;
import dev.isnow.fox.packet.Packet;

@CheckInfo(name = "BadPackets", type = "L", description = "Checks for autistic people [0 delta & pitch]")
public final class BadPacketsL extends Check {

    public BadPacketsL(final PlayerData data) {
        super(data);
    }

    @Override
    public void handle(Packet packet) {
        if(packet.isPosLook() && data.getRotationProcessor().getDeltaPitch() == 0 && data.getRotationProcessor().getDeltaYaw() == 0 && !isExempt(ExemptType.TELEPORT_DELAY)) {
            if(increaseBuffer() > 5) {
                fail("Autistic People");
            }
        } else {
            decreaseBufferBy(0.15);
        }
    }
}