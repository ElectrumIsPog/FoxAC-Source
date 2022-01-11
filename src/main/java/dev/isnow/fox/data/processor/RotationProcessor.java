

package dev.isnow.fox.data.processor;

import dev.isnow.fox.Fox;
import dev.isnow.fox.data.PlayerData;
import dev.isnow.fox.util.MathUtil;
import lombok.Getter;

import java.util.ArrayDeque;

@Getter
public final class RotationProcessor {

    private final PlayerData data;
    private float yaw, pitch, lastYaw, lastPitch,
    deltaYaw, deltaPitch, lastDeltaYaw, lastDeltaPitch,
    yawAccel, pitchAccel, lastYawAccel, lastPitchAccel, gcd, actualGcd, joltYaw, joltPitch, lastJoltYaw, lastJoltPitch;

    private int mouseDeltaX, mouseDeltaY, lastMouseDeltaX, lastMouseDeltaY;

    private double finalSensitivity, cinematicTicks;

    private float cinematicTime;

    private final ArrayDeque<Integer> sensitivitySamples = new ArrayDeque<>();

    private int sensitivity, lastCinematic;

    private boolean cinematic;

    public RotationProcessor(final PlayerData data) {
        this.data = data;
    }

    public void handle(final float yaw, final float pitch) {
        lastYaw = this.yaw;
        lastPitch = this.pitch;

        this.yaw = yaw;
        this.pitch = pitch;

        lastDeltaYaw = deltaYaw;
        lastDeltaPitch = deltaPitch;

        deltaYaw = Math.abs(yaw - lastYaw) % 360F;
        deltaPitch = Math.abs(pitch - lastPitch);

        lastJoltPitch = joltPitch;
        lastJoltYaw = joltYaw;

        joltYaw = Math.abs(deltaYaw - lastDeltaYaw);
        joltPitch = Math.abs(deltaPitch - lastDeltaPitch);

        processCinematic();

        if (deltaPitch > 0 && deltaPitch < 30) {
            processSensitivity();
        }
    }

    private void processCinematic() {
        final float yawAccelAccel = Math.abs(joltYaw - lastJoltYaw);
        final float pitchAccelAccel = Math.abs(joltPitch - lastJoltPitch);

        final boolean invalidYaw = yawAccelAccel < .05 && yawAccelAccel > 0;
        final boolean invalidPitch = pitchAccelAccel < .05 && pitchAccelAccel > 0;

        final boolean exponentialYaw = String.valueOf(yawAccelAccel).contains("E");
        final boolean exponentialPitch = String.valueOf(pitchAccelAccel).contains("E");

        if (sensitivity < 100 && (exponentialYaw || exponentialPitch)) {
            cinematicTicks += 3;
        } else if (invalidYaw || invalidPitch) {
            cinematicTicks += 1;
        } else {
            if (cinematicTicks > 0) cinematicTicks--;
        }
        if (cinematicTicks > 20) {
            cinematicTicks--;
        }

        cinematic = cinematicTicks > 8 || (Fox.INSTANCE.getTickManager().getTicks() - lastCinematic < 120);

        if (cinematic && cinematicTicks > 8) {
            lastCinematic = Fox.INSTANCE.getTickManager().getTicks();
        }
    }

    private void processSensitivity() {
        final float gcd = (float) MathUtil.getGcd(deltaPitch, lastDeltaPitch);

        final double sensitivityModifier = Math.cbrt(0.83333333333 * gcd);
        final double sensitivityStepTwo = (1.666666666666 * sensitivityModifier) - 0.3333333333333;
        final double finalSensitivity = sensitivityStepTwo * 200;

        this.finalSensitivity = finalSensitivity;

        sensitivitySamples.add((int)finalSensitivity);

        if (sensitivitySamples.size() >= 40) {
            this.sensitivity = MathUtil.getMode(sensitivitySamples);

            final float gcdOne = (sensitivity / 200F) * 0.6F + 0.2F;

            this.gcd = gcdOne * gcdOne * gcdOne * 1.2F;
            this.actualGcd = gcdOne * gcdOne * gcdOne * 8.0F;

            sensitivitySamples.clear();
        }
    }
}
