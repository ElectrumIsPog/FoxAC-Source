package dev.isnow.fox.check.impl.combat.autoclicker;

import dev.isnow.fox.check.Check;
import dev.isnow.fox.check.api.CheckInfo;
import dev.isnow.fox.data.PlayerData;
import dev.isnow.fox.exempt.type.ExemptType;
import dev.isnow.fox.packet.Packet;
import dev.isnow.fox.util.MathUtil;
import dev.isnow.fox.util.type.Pair;

import java.util.ArrayDeque;
import java.util.List;

//Copied from https://github.com/ElevatedDev/Frequency/blob/master/src/main/java/xyz/elevated/frequency/check/impl/autoclicker/AutoClickerC.java
@CheckInfo(name = "AutoClicker", type = "J", description = "Checks for low patterns")
public class AutoClickerJ extends Check {

    public AutoClickerJ(PlayerData data) {
        super(data);
    }

    private int ticks;

    private final ArrayDeque<Integer> samples = new ArrayDeque<>();

    @Override
    public void handle(Packet packet) {
        if (packet.isArmAnimation()) {
            final boolean valid = ticks < 4 && !isExempt(ExemptType.AUTOCLICKER) && ticks != 0;
            if(valid) samples.add(ticks);

            if(samples.size() == 15) {
                final Pair<List<Double>, List<Double>> outlierPair = MathUtil.getOutliers(samples);

                final double skewness = MathUtil.getSkewness(samples);
                final double kurtosis = MathUtil.getKurtosis(samples);
                final double outliers = outlierPair.getX().size() + outlierPair.getY().size();

                String debug = String.format(
                        "sk=%.2f, ku=%.2f, ou=%.2f",
                        skewness, kurtosis, outliers
                );

                debug(debug);

                if (skewness < 0.75 && kurtosis < 0.0 && outliers < 2) {
                    if (++buffer > 1) {
                        fail(debug);
                    }
                } else {
                    buffer = 0;
                }

                samples.clear();
            }

            ticks = 0;
        } else if (packet.isFlying()) {
            ticks++;
        }
    }
}
