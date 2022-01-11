package dev.isnow.fox.check.impl.combat.aura;

import dev.isnow.fox.check.Check;
import dev.isnow.fox.check.api.CheckInfo;
import dev.isnow.fox.data.PlayerData;
import dev.isnow.fox.packet.Packet;
import io.github.retrooper.packetevents.packetwrappers.play.in.useentity.WrappedPacketInUseEntity;
import org.bukkit.entity.Entity;

@CheckInfo(name = "Aura", description = "Checks for switch auras.", type = "T")
public final class AuraT extends Check {

    public AuraT(PlayerData data) {
        super(data);
    }

    @Override
    public void handle(final Packet packet) {
        if (packet.isUseEntity()) {
            final WrappedPacketInUseEntity wrapper = new WrappedPacketInUseEntity(packet.getRawPacket());

            if (wrapper.getAction() == WrappedPacketInUseEntity.EntityUseAction.ATTACK) {
                final Entity target = data.getCombatProcessor().getTarget();
                final Entity lastTarget = data.getCombatProcessor().getLastTarget();

                final boolean exempt = target == lastTarget;

                if (!exempt) {
                    if (increaseBuffer() > 1) {
                        fail();
                    }
                }
            }
        } else if (packet.isFlying()) {
            resetBuffer();
        }
    }
}