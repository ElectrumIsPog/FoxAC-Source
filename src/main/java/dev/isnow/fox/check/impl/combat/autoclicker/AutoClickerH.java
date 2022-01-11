package dev.isnow.fox.check.impl.combat.autoclicker;

import dev.isnow.fox.check.Check;
import dev.isnow.fox.check.api.CheckInfo;
import dev.isnow.fox.data.PlayerData;
import dev.isnow.fox.exempt.type.ExemptType;
import dev.isnow.fox.packet.Packet;
import dev.isnow.fox.util.MathUtil;

import java.util.ArrayDeque;

@CheckInfo(name = "AutoClicker", type = "H", description = "Checks for poor randomization", experimental = true)
public class AutoClickerH extends Check {
    private final ArrayDeque<Integer> samples = new ArrayDeque<>();
    private double lastDeviation;

    private int ticks;


    public AutoClickerH(PlayerData data) {
        super(data);
    }

    @Override
    public void handle(Packet packet) {
        if (packet.isArmAnimation() && !isExempt(ExemptType.AUTOCLICKER) && ticks != 0) {
            samples.add(ticks);

            if (samples.size() == 30) {
                final double deviation = MathUtil.getStandardDeviation(samples);
                final double difference = Math.abs(deviation - lastDeviation);

                final boolean invalid = difference < 0.5;

                debug("std=" + difference);
                if (invalid) {
                    if (increaseBuffer() > 5) {
                        fail("deviation=" + deviation + " difference=" + difference);
                    }
                } else {
                    decreaseBufferBy(2);
                }

                lastDeviation = deviation;

                samples.clear();
            }
            ticks = 0;
        } else if (packet.isFlying()) {
            ticks++;
        }
    }
}
