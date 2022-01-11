package dev.isnow.fox.check.impl.combat.hitbox;

import dev.isnow.fox.check.Check;
import dev.isnow.fox.check.api.CheckInfo;
import dev.isnow.fox.data.PlayerData;
import dev.isnow.fox.packet.Packet;
import io.github.retrooper.packetevents.packetwrappers.play.in.useentity.WrappedPacketInUseEntity;
import org.bukkit.entity.Player;

@CheckInfo(name = "HitBox", experimental = true, description = "Checks for hitting outside the entity hitbox.", type = "B")
public final class HitBoxB extends Check {

    public HitBoxB(final PlayerData data) {
        super(data);
    }

    @Override
    public void handle(Packet packet) {
        if (packet.isUseEntityInteractAt()) {
            WrappedPacketInUseEntity interactAt = new WrappedPacketInUseEntity(packet.getRawPacket());
            if (interactAt.getEntity() instanceof Player) {
                if (Math.abs(interactAt.getTarget().get().x) > 0.400001 || Math.abs(interactAt.getTarget().get().z) > 0.400001) {
                    fail("x:" + Math.abs(interactAt.getTarget().get().x) + " z:" + Math.abs(interactAt.getTarget().get().z));
                }
            }
        }
    }
}