package dev.isnow.fox.check.impl.combat.aim;

import dev.isnow.fox.check.Check;
import dev.isnow.fox.check.api.CheckInfo;
import dev.isnow.fox.data.PlayerData;
import dev.isnow.fox.packet.Packet;
import dev.isnow.fox.util.MathUtil;

@CheckInfo(name = "Aim", description = "Checks for consistent gcd.", type = "K", experimental = true)
public class AimK extends Check {
    public AimK(PlayerData data) {
        super(data);
    }

    private float lastDeltaPitch = 0.0f;
    private boolean applied = false;

    private int rotations = 0;
    private final long[] grid = new long[10];

    @Override
    public void handle(Packet packet) {
        if(packet.isRotation()) {
            final float deltaYaw = data.getRotationProcessor().getDeltaYaw();
            final float deltaPitch = data.getRotationProcessor().getDeltaPitch();

            final boolean cinematic = data.getRotationProcessor().isCinematic();
            final boolean attacking = data.getCombatProcessor().getLastAttackTick() < 10;

            final long deviation = getDeviation(deltaPitch);

            ++rotations;
            grid[rotations % grid.length] = deviation;

            // If the player wasn't using cinematic, where attacking and weren't spamming their aim
            if (deltaYaw > 0.0 && deltaPitch > 0.0 && deltaYaw < 30.f && deltaPitch < 30.f && !cinematic && attacking) {
                final boolean reached = rotations > grid.length;

                // If the rotations made were greater than the gcd length
                if (reached) {
                    double deviationMax = 0;

                    // Get the max deviation from the gcd log
                    for (final double l : grid) {
                        if (deviation != 0 && l != 0)
                            deviationMax = Math.max(Math.max(l, deviation) % Math.min(l, deviation), deviationMax);
                    }

                    // If both the deviation and the max deviation were greater than 0,9
                    if (deviationMax > 0.0 && deviation > 0.0) {
                        fail("devMax=" + deviationMax + ", dev=" + deviation);

                        applied = false;
                    }
                }
            }

            this.lastDeltaPitch = deltaPitch;
        }
    }

    private long getDeviation(final float deltaPitch) {
        final long expandedPitch = (long) (deltaPitch * MathUtil.EXPANDER);
        final long previousExpandedPitch = (long) (lastDeltaPitch * MathUtil.EXPANDER);

        final long result = applied ? MathUtil.getGcd(expandedPitch, previousExpandedPitch) : 0;

        if (applied) {
            applied = false;

            return result;
        }

        return 0L;
    }
}