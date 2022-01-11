package dev.isnow.fox.check.impl.combat.aura;

import dev.isnow.fox.check.Check;
import dev.isnow.fox.check.api.CheckInfo;
import dev.isnow.fox.data.PlayerData;
import dev.isnow.fox.packet.Packet;
import io.github.retrooper.packetevents.packetwrappers.play.in.blockplace.WrappedPacketInBlockPlace;
import io.github.retrooper.packetevents.utils.player.Direction;

@CheckInfo(name = "Aura", type = "L", description = "Invalid BLOCK PLACE face")
public class AuraL extends Check {
    public AuraL(PlayerData data) {
        super(data);
    }

    private boolean sentBlock, sentUseEntity;

    @Override
    public void handle(Packet packet) {
        if (packet.isBlockPlace()) {
            sentBlock = true;
            if (sentUseEntity) {
                WrappedPacketInBlockPlace wrapper = new WrappedPacketInBlockPlace(packet.getRawPacket());
                if (wrapper.getDirection() != Direction.OTHER)
                    fail();
            }
        } else if (packet.isUseEntity()) {
            sentUseEntity = true;
        } else if (packet.isFlyingType()) {
            sentBlock = false;
            sentUseEntity = false;
        }
    }
}