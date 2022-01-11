package dev.isnow.fox.check.impl.movement.speed;

import dev.isnow.fox.check.Check;
import dev.isnow.fox.check.api.CheckInfo;
import dev.isnow.fox.data.PlayerData;
import dev.isnow.fox.exempt.type.ExemptType;
import dev.isnow.fox.packet.Packet;
import dev.isnow.fox.util.PlayerUtil;
import org.bukkit.potion.PotionEffectType;

@CheckInfo(name = "Speed", type = "A", description = "Checks for any modified speed advantage")
public final class SpeedA extends Check {
    public SpeedA(PlayerData data) {
        super(data);
    }

    @Override
    public void handle(Packet packet) {
        if (packet.isFlying()) {
            boolean exempt;
            if ((double)this.data.getPlayer().getWalkSpeed() < 0.2) {
                return;
            }
            boolean sprinting = this.data.getActionProcessor().isSprinting();
            double lastDeltaX = this.data.getPositionProcessor().getLastDeltaX();
            double lastDeltaZ = this.data.getPositionProcessor().getLastDeltaZ();
            double deltaXZ = this.data.getPositionProcessor().getDeltaXZ();
            double deltaY = this.data.getPositionProcessor().getDeltaY();
            int groundTicks = this.data.getPositionProcessor().getGroundTicks();
            int airTicks = this.data.getPositionProcessor().getClientAirTicks();
            float modifierJump = (float)PlayerUtil.getPotionLevel(this.data.getPlayer(), PotionEffectType.JUMP) * 0.1f;
            float jumpMotion = 0.42f + modifierJump;
            double groundLimit = PlayerUtil.getBaseGroundSpeed(this.data.getPlayer());
            double airLimit = PlayerUtil.getBaseSpeed(this.data.getPlayer());

            debug("deltaY: " + deltaY + "deltaXY: " + deltaXZ + "deltaXZ: " +deltaXZ+ "airTicks: " + airTicks + "jumpMotion" +jumpMotion);

            if (Math.abs(deltaY - (double)jumpMotion) < 1.0E-4 && airTicks == 1 && sprinting) {
                float f = this.data.getRotationProcessor().getYaw() * ((float)Math.PI / 180);
                double x = lastDeltaX - Math.sin(f) * (double)0.28f;
                double z = lastDeltaZ + Math.cos(f) * (double)0.28f;
                airLimit += Math.hypot(x, z);
            }
            if (this.isExempt(ExemptType.ICE, ExemptType.SLIME)) {
                airLimit += 0.34f;
                groundLimit += 0.34f;
            }
            if (this.isExempt(ExemptType.UNDERBLOCK)) {
                airLimit += 0.91f;
                groundLimit += 0.91f;
            }
            if ((double)this.data.getPlayer().getWalkSpeed() > 0.98) {
                return;
            }
            if (this.data.getVelocityProcessor().getVelocityH() != 0.0) {
                groundLimit += this.data.getVelocityProcessor().getVelocityH() + 0.05;
                airLimit += this.data.getVelocityProcessor().getVelocityH() + 0.05;
            }
            if (groundTicks < 7) {
                groundLimit += 0.25f / (float)groundTicks;
            }
            if (!(exempt = this.isExempt(ExemptType.NEARSTAIRS, ExemptType.VEHICLE, ExemptType.PISTON, ExemptType.FLYING, ExemptType.TELEPORT, ExemptType.CHUNK, ExemptType.VELOCITY_ON_TICK))) {
                if (airTicks > 0) {
                    if (deltaXZ > airLimit) {
                        if (increaseBuffer() > 8) {
                            fail("DeltaXZ: " + deltaXZ + " AirLimit: " + airLimit);
                        }
                    } else {
                        this.decreaseBufferBy(0.15);
                    }
                } else if (deltaXZ > groundLimit) {
                    if (increaseBuffer() > 8) {
                        fail("DeltaXZ: " + deltaXZ + " GroundLimit: " + groundLimit);
                    }
                } else {
                    this.decreaseBufferBy(0.15);
                }
            }
        }
    }
}