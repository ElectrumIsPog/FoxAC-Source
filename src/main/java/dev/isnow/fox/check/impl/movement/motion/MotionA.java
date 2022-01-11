package dev.isnow.fox.check.impl.movement.motion;

import dev.isnow.fox.check.Check;
import dev.isnow.fox.check.api.CheckInfo;
import dev.isnow.fox.data.PlayerData;
import dev.isnow.fox.exempt.type.ExemptType;
import dev.isnow.fox.packet.Packet;
import dev.isnow.fox.util.BlockUtil;
import dev.isnow.fox.util.MathUtil;
import io.github.retrooper.packetevents.packetwrappers.play.in.flying.WrappedPacketInFlying;
import org.bukkit.block.Block;
import org.bukkit.util.Vector;

@CheckInfo(name = "Motion", type = "A", description = "Invalid Strafe Angle")
public class MotionA extends Check {
    private final double THRESHOLD;

    public MotionA(PlayerData data) {
        super(data);
        THRESHOLD = Math.toRadians(0.5);
    }

    private boolean hitSlow;
    private float lastFriction;
    private int lastIdleTick, buffer;

    @Override
    public void handle(Packet packet) {
        if (packet.isPosition()) {
            Block footBlock = BlockUtil.getBlockAsync(data.getPlayer().getLocation().clone().add(0, -0.2, 0));
            if (footBlock == null)
                return;

            double dX = data.getPositionProcessor().getX() - data.getPositionProcessor().getLastX();
            double dZ = data.getPositionProcessor().getZ() - data.getPositionProcessor().getLastZ();

            final Vector vector = new Vector(data.getPositionProcessor().getLastlastX(), 0, data.getPositionProcessor().getLastDeltaZ());

            if (Math.abs(vector.getX() * lastFriction) < 0.005) {
                vector.setX(0);
            }
            if (Math.abs(vector.getZ() * lastFriction) < 0.005) {
                vector.setZ(0);
            }

            if (hitSlow) vector.multiply(0.6);
            hitSlow = false;

            dX /= lastFriction;
            dZ /= lastFriction;
            dX -= vector.getX();
            dZ -= vector.getZ();
            float lastFriction = data.getPositionProcessor().getFriction();
            if (this.lastFriction != lastFriction) {
                lastIdleTick = data.getTicks();
            }
            this.lastFriction = lastFriction;

            Vector accelDir = new Vector(dX, 0, dZ);
            if (accelDir.lengthSquared() < 0.03) return;//Screw .03
            if (data.getTicks() - lastIdleTick <= 2 || data.getVelocityProcessor().getTicksSinceVelocity() < 2)
                return;

            Vector yaw = MathUtil.getDirection(data.getRotationProcessor().getYaw(), 0);
            boolean vectorDir = accelDir.clone().crossProduct(yaw).dot(new Vector(0, 1, 0)) >= 0;
            double angle = (vectorDir ? 1 : -1) * MathUtil.angle(accelDir, yaw);
            if (Double.isNaN(angle)) return;

            data.getPositionProcessor().setLastMoveAngle(angle);

            if (!isValidStrafe(angle) && !isExempt(ExemptType.TELEPORT, ExemptType.NEAR_WALL)) {
                buffer += 5;
                if (buffer > 20) {
                    fail("angle=" + angle);
                }
            } else {
                buffer = Math.max(0, buffer - 5);
            }


        } else if (packet.isFlyingType()) {
            WrappedPacketInFlying flying = new WrappedPacketInFlying(packet.getRawPacket());
            if (!flying.isPosition()) lastIdleTick = data.getTicks();
        } else if(packet.isUseEntityAttack() && data.getActionProcessor().isSprinting()) {
            hitSlow = true;
        }
    }

    private boolean isValidStrafe(double angle) {
        double modulo = (angle % (Math.PI / 4)) * (4 / Math.PI); //scaled so that legit values should be close to either 0 or +/-1
        double error = Math.abs(modulo - Math.round(modulo)) * (Math.PI / 4); //compute error (and then scale back to radians)
        return error <= THRESHOLD; //in radians
    }
}