package dev.isnow.fox.check.impl.combat.autoclicker;

import com.google.common.collect.Lists;
import dev.isnow.fox.check.Check;
import dev.isnow.fox.check.api.CheckInfo;
import dev.isnow.fox.data.PlayerData;
import dev.isnow.fox.exempt.type.ExemptType;
import dev.isnow.fox.packet.Packet;
import dev.isnow.fox.util.MathUtil;
import dev.isnow.fox.util.type.Pair;

import java.util.Deque;
import java.util.List;

@CheckInfo(name = "AutoClicker", type = "K", description = "Checks for rounded cps")
public class AutoClickerK extends Check {
    public AutoClickerK(PlayerData data) {
        super(data);
    }

    private final Deque<Integer> samples = Lists.newLinkedList();


    private int ticks;

    @Override
    public void handle(Packet packet) {
        if(packet.isArmAnimation()) {
            final boolean valid = ticks < 4 && !isExempt(ExemptType.AUTOCLICKER) && ticks != 0;

            if(valid) samples.add(ticks);

            if(samples.size() == 15) {
                final Pair<List<Double>, List<Double>> outlierPair = MathUtil.getOutliers(samples);

                // Get the deviation outliers the the cps from the math util
                final double deviation = MathUtil.getStandardDeviation(samples);
                final double outliers = outlierPair.getX().size() + outlierPair.getY().size();
                final double cps = data.getClickProcessor().getCps();

                final String debug = String.format(
                        "deviation=%.2f, outliers=%.2f, cps%.2f",
                        deviation, outliers, cps
                );

                debug(debug);

                if (deviation < 0.3 && outliers < 2 && cps % 1.0 == 0.0) {
                    buffer += 0.25;

                    if (buffer > 0.75) {
                        fail(debug);
                    }
                } else {
                    buffer = Math.max(buffer - 0.2, 0);
                }

                samples.clear();
            }

            ticks = 0;
        } else if(packet.isFlying()) {
            ticks++;
        }
    }
}
