package dev.isnow.fox.check.impl.combat.aim;

import com.google.common.collect.Lists;
import dev.isnow.fox.check.Check;
import dev.isnow.fox.check.api.CheckInfo;
import dev.isnow.fox.data.PlayerData;
import dev.isnow.fox.packet.Packet;
import dev.isnow.fox.util.MathUtil;

import java.util.Deque;

@CheckInfo(name = "Aim", description = "Checks for duplicate rotations.", type = "O", experimental = true)
public class AimO extends Check {

    public AimO(PlayerData data) {
        super(data);
    }

    private final Deque<Float> samples = Lists.newLinkedList();

    @Override
    public void handle(Packet packet) {
        if(packet.isRotation()) {
            // Get the deltas from the rotation update
            final float deltaYaw = data.getRotationProcessor().getDeltaYaw();
            final float deltaPitch = data.getRotationProcessor().getDeltaPitch();

            // Make sure the player isn't using cinematic
            final boolean cinematic = data.getRotationProcessor().isCinematic();
            final boolean attacking = data.getCombatProcessor().getLastAttackTick() < 2;

            // If the conditions are met, add to the list
            if (deltaYaw > 0.0 && deltaPitch > 0.0 && deltaYaw < 30.f && deltaPitch < 30.f && !cinematic && attacking) {
                samples.add(deltaPitch);
            }

            // If the list has reached a sample size of 120
            if (samples.size() == 120) {
                // Get the duplicates through the distinct method in the list
                final int distinct = MathUtil.getDistinct(samples);
                final int duplicates = samples.size() - distinct;

                // Get the average from all the rotations to make sure they were't just spamming around their aim
                final double average = samples.stream().mapToDouble(d -> d).average().orElse(0.0);

                // If the duplicates are extremely low the player didn't have a valid rotation constant
                if (duplicates <= 9 && average < 30.f && distinct > 130) {
                    if (++buffer > 4) {
                        fail("dupl=" + duplicates + ", avg=" + average);
                    }
                } else {
                    buffer = Math.max(buffer - 3, 0);
                }

                // Clear the samples
                samples.clear();
            }
        }
    }
}