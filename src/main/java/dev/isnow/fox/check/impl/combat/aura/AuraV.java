package dev.isnow.fox.check.impl.combat.aura;

import dev.isnow.fox.check.Check;
import dev.isnow.fox.check.api.CheckInfo;
import dev.isnow.fox.data.PlayerData;
import dev.isnow.fox.packet.Packet;
import io.github.retrooper.packetevents.packetwrappers.play.in.useentity.WrappedPacketInUseEntity;

@CheckInfo(name = "Aura", type = "V", description = "Invalid post useEntity.")
public class AuraV extends Check {

    private long lastFlying;
    private boolean sent;

    public AuraV(final PlayerData data) {
        super(data);
    }

    @Override
    public void handle(final Packet packet) {
        if (packet.isFlying()) {
            final long now = System.currentTimeMillis();
            final long delay = now - lastFlying;

            if (sent) {
                if (delay > 40L && delay < 100L) {
                    if (increaseBuffer() > 3) {
                        fail("delay=" + delay);
                    }
                } else {
                    decreaseBufferBy(.125);
                }
                sent = false;
            }

            lastFlying = now;
        } else if (packet.isUseEntity()) {
            final WrappedPacketInUseEntity wrapper = new WrappedPacketInUseEntity(packet.getRawPacket());

            check: {
                if (wrapper.getAction() != WrappedPacketInUseEntity.EntityUseAction.ATTACK) break check;
                final long delay = System.currentTimeMillis()- lastFlying;
                if (delay < 10) {
                    sent = true;
                }
            }
        }
    }
}