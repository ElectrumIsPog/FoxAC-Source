package dev.isnow.fox.check.impl.combat.aura;

import dev.isnow.fox.check.Check;
import dev.isnow.fox.check.api.CheckInfo;
import dev.isnow.fox.data.PlayerData;
import dev.isnow.fox.packet.Packet;
import io.github.retrooper.packetevents.packetwrappers.play.in.useentity.WrappedPacketInUseEntity;

@CheckInfo(name = "Aura", type = "Q", description = "Attacked two entities at once.")
public class AuraQ extends Check {

    private int ticks, lastEntityId;

    public AuraQ(final PlayerData data) {
        super(data);
    }

    @Override
    public void handle(final Packet packet) {
        if (packet.isUseEntity()) {

            final WrappedPacketInUseEntity wrapper = new WrappedPacketInUseEntity(packet.getRawPacket());

            check: {

                if (wrapper.getAction() != WrappedPacketInUseEntity.EntityUseAction.ATTACK) break check;

                if (wrapper.getEntityId() != lastEntityId) {
                    if (++ticks > 1) {
                        fail();
                    }
                }
                lastEntityId = wrapper.getEntityId();
            }
        } else if (packet.isFlying()) {
            ticks = 0;
        }
    }
}