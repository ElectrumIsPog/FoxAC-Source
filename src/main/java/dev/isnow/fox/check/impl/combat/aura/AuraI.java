package dev.isnow.fox.check.impl.combat.aura;

import dev.isnow.fox.check.Check;
import dev.isnow.fox.check.api.CheckInfo;
import dev.isnow.fox.data.PlayerData;
import dev.isnow.fox.packet.Packet;
import io.github.retrooper.packetevents.packetwrappers.play.in.useentity.WrappedPacketInUseEntity;
import org.bukkit.entity.Entity;

@CheckInfo(name = "Aura", type = "I", description = "Too many USE ENTITY")
public class AuraI extends Check {
    public AuraI(PlayerData data) {
        super(data);
    }

    private Entity lastEntity;

    @Override
    public void handle(Packet packet) {
        if (packet.isUseEntityAttack()) {
            WrappedPacketInUseEntity useEntity = new WrappedPacketInUseEntity(packet.getRawPacket());
            if (lastEntity != null && lastEntity != useEntity.getEntity())
                fail("Multi Aura");
            lastEntity = useEntity.getEntity();
        } else if (packet.isFlyingType()) {
            lastEntity = null;
        }
    }
}