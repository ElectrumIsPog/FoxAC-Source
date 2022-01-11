

package dev.isnow.fox.check.impl.combat.reach;

import dev.isnow.fox.Fox;
import dev.isnow.fox.check.Check;
import dev.isnow.fox.check.api.CheckInfo;
import dev.isnow.fox.data.PlayerData;
import dev.isnow.fox.exempt.type.ExemptType;
import dev.isnow.fox.packet.Packet;
import dev.isnow.fox.util.MathUtil;
import dev.isnow.fox.util.PlayerUtil;
import io.github.retrooper.packetevents.packetwrappers.play.in.useentity.WrappedPacketInUseEntity;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

@CheckInfo(name = "Reach", type = "A", description = "Checks for impossible attack distance.")
public final class ReachA extends Check {

    private boolean attacked;

    public ReachA(final PlayerData data) {
        super(data);
    }

    @Override
    public void handle(final Packet packet) {
        if (packet.isFlying()) {
            if (!attacked) return;
            attacked = false;

            final Entity target = data.getCombatProcessor().getTarget();
            final Entity lastTarget = data.getCombatProcessor().getLastTarget();

            if (target != lastTarget) return;

            if (!(target instanceof Player)) return;
            if (data.getTargetLocations().size() < 30) return;

            final int now = Fox.INSTANCE.getTickManager().getTicks();
            final int latencyInTicks = MathUtil.msToTicks(PlayerUtil.getPing(data.getPlayer()));

            final double x = data.getPositionProcessor().getX();
            final double z = data.getPositionProcessor().getZ();

            final Vector origin = new Vector(x, 0.0, z);

            final double maxDistance = 3.5;
            final double distance = data.getTargetLocations().stream()
                    .filter(pair -> Math.abs(now - pair.getY() - latencyInTicks) < 3)
                    .mapToDouble(pair -> {
                        final Vector targetLocation = pair.getX().toVector().setY(0.0);

                        return origin.distance(targetLocation) - 0.5658;
                    })
                    .min().orElse(-1);

            final boolean invalid = distance > maxDistance && !isExempt(ExemptType.LAGGING, ExemptType.CREATIVE);

            if (invalid) {
                if (increaseBuffer() > 2) {
                    fail(distance);
                }
            } else {
                decreaseBufferBy(0.05);
            }
        } else if (packet.isUseEntity()) {
            final WrappedPacketInUseEntity wrapper = new WrappedPacketInUseEntity(packet.getRawPacket());

            if (wrapper.getAction() == WrappedPacketInUseEntity.EntityUseAction.ATTACK) {
                attacked = true;
            }
        }
    }
}
