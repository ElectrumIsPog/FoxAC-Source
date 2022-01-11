package dev.isnow.fox.check.impl.combat.aura;

import dev.isnow.fox.check.Check;
import dev.isnow.fox.check.api.CheckInfo;
import dev.isnow.fox.data.PlayerData;
import dev.isnow.fox.packet.Packet;
import io.github.retrooper.packetevents.packetwrappers.play.in.useentity.WrappedPacketInUseEntity;
import org.bukkit.entity.EntityType;

@CheckInfo(name = "Aura", description = "Checks for invalid hit-swings.", type = "U")

public class AuraU extends Check {
    private int swings;
    private int hits;

    public AuraU(PlayerData data) {
        super(data);
    }

    public void handle(Packet packet) {
        if (packet.isReceiving()) {
            if (packet.getPacketId() == 43) {
                ++this.swings;
                if (this.swings >= 100) {
                    if (this.hits > 75) {
                        this.fail();
                    }

                    this.swings = 0;
                    this.hits = 0;
                }
            } else if (packet.getPacketId() == 14) {
                WrappedPacketInUseEntity wrappedPacketInUseEntity = new WrappedPacketInUseEntity(packet.getRawPacket());
                if (wrappedPacketInUseEntity.getEntity().getType() == EntityType.PLAYER) {
                    ++this.hits;
                }
            }
        }

    }
}