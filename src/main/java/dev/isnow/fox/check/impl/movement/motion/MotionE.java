package dev.isnow.fox.check.impl.movement.motion;

import dev.isnow.fox.check.Check;
import dev.isnow.fox.check.api.CheckInfo;
import dev.isnow.fox.data.PlayerData;
import dev.isnow.fox.exempt.type.ExemptType;
import dev.isnow.fox.packet.Packet;
import dev.isnow.fox.util.PlayerUtil;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

@CheckInfo(name = "Motion", type = "E", description = "Checks for sprinting backwards.")
public final class MotionE extends Check {
    public MotionE(final PlayerData data) {
        super(data);
    }

    @Override
    public void handle(final Packet packet) {
        if (packet.isFlying()) {
            final boolean onGround = data.getPositionProcessor().isOnGround();
            final boolean sprinting = data.getActionProcessor().isSprinting();

            final double yaw = data.getRotationProcessor().getYaw();
            final Vector direction = new Vector(-Math.sin(yaw * Math.PI / 180.0F) * (float) 1 * 0.5F, 0, Math.cos(yaw * Math.PI / 180.0F) * (float) 1 * 0.5F);

            final double deltaX = data.getPositionProcessor().getDeltaX();
            final double deltaZ = data.getPositionProcessor().getDeltaZ();

            final double deltaXZ = data.getPositionProcessor().getDeltaXZ();

            final Vector move = new Vector(deltaX, 0.0, deltaZ);
            final double delta = move.distanceSquared(direction);

            final boolean exempt = isExempt(ExemptType.LIQUID, ExemptType.WEB, ExemptType.VELOCITY, ExemptType.CHUNK, ExemptType.UNDERBLOCK, ExemptType.ICE);
            final boolean invalid = delta > getLimit() && deltaXZ > 0.1 && sprinting && onGround;

            if (invalid && !exempt) {
                if (increaseBuffer() > 4) {
                    fail();
                }
            } else {
                resetBuffer();
            }
        }
    }

    private double getLimit() {
        return data.getPlayer().getWalkSpeed() > 0.2f ? .23 * 1 + ((data.getPlayer().getWalkSpeed() / 0.2f) * 0.36) : 0.23 + (PlayerUtil.getPotionLevel(data.getPlayer(), PotionEffectType.SPEED) * 0.062f);
    }
}