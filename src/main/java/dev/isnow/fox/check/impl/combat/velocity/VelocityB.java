package dev.isnow.fox.check.impl.combat.velocity;

import dev.isnow.fox.check.Check;
import dev.isnow.fox.check.api.CheckInfo;
import dev.isnow.fox.data.PlayerData;
import dev.isnow.fox.exempt.type.ExemptType;
import dev.isnow.fox.packet.Packet;
import dev.isnow.fox.util.BlockUtil;
import dev.isnow.fox.util.MathUtil;
import org.bukkit.Location;

@CheckInfo(name = "Velocity", type = "B", description = "Checks for horizontal velocity modifications.")
public final class VelocityB extends Check {

    private double kbX, kbZ;
    private double friction = 0.91F;

    public VelocityB(final PlayerData data) {
        super(data);
    }

    @Override
    public void handle(final Packet packet) {
        if (packet.isFlying()) {
            final boolean sprinting = data.getActionProcessor().isSprinting();

            final int ticksSinceVelocity = data.getVelocityProcessor().getTicksSinceVelocity();

            if (ticksSinceVelocity == 1) {
                kbX = data.getVelocityProcessor().getVelocityX();
                kbZ = data.getVelocityProcessor().getVelocityZ();
            }

            if (hitTicks() <= 1 && sprinting) {
                kbX *= 0.6D;
                kbZ *= 0.6D;
            }

            final double deltaXZ = data.getPositionProcessor().getDeltaXZ();
            final double lastDeltaXZ = data.getPositionProcessor().getLastDeltaXZ();

            final double velocityXZ = MathUtil.hypot(kbX, kbZ);

            final double diffH = Math.max((deltaXZ / velocityXZ), (lastDeltaXZ / velocityXZ));
            final double percentage = diffH * 100.0;

            final boolean exempt = isExempt(ExemptType.LIQUID, ExemptType.PISTON, ExemptType.CLIMBABLE,
                    ExemptType.UNDERBLOCK, ExemptType.NEAR_WALL, ExemptType.TELEPORT, ExemptType.FLYING);
            final boolean invalid = percentage < 70.0;

            if (kbX != 0 || kbZ != 0) {
                if (invalid && !exempt) {
                    if (increaseBuffer() > 3) {
                        fail();
                    }

                    resetState();
                } else {
                    decreaseBuffer();
                }
            }

            kbX *= this.friction;
            kbZ *= this.friction;

            if (Math.abs(kbX) < 0.005 || Math.abs(kbZ) < 0.005) {
                resetState();
            }

            if (ticksSinceVelocity >= 2) {
                resetState();
            }


            final double x = data.getPositionProcessor().getX();
            final double y = data.getPositionProcessor().getY();
            final double z = data.getPositionProcessor().getZ();

            final Location blockLocation = new Location(data.getPlayer().getWorld(), x, Math.floor(y) - 1, z);

            this.friction = BlockUtil.getBlockFriction(blockLocation) * 0.91F;
        }
    }

    public void resetState() {
        kbX = 0;
        kbZ = 0;
    }
}