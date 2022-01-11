package dev.isnow.fox.check.impl.combat.autoclicker;

import dev.isnow.fox.check.Check;
import dev.isnow.fox.check.api.CheckInfo;
import dev.isnow.fox.data.PlayerData;
import dev.isnow.fox.exempt.type.ExemptType;
import dev.isnow.fox.packet.Packet;
import dev.isnow.fox.util.MathUtil;

import java.util.ArrayDeque;

@CheckInfo(name = "AutoClicker", type = "I", description = "Checks for low debounce time", experimental = true)
public class AutoClickerI extends Check {
    private final ArrayDeque<Integer> samples = new ArrayDeque<>();
    private int ticks;
    public AutoClickerI(PlayerData data) {
        super(data);
    }

    @Override
    public void handle(Packet packet) {
        if (packet.isArmAnimation() && !isExempt(ExemptType.AUTOCLICKER)) {
            if (ticks < 4 && data.getClickProcessor().getCps() > 8 && ticks != 0) {
                samples.add(ticks);
            }

            if (samples.size() == 30) {
                final double std = MathUtil.getStandardDeviation(samples);
                final double skewness = MathUtil.getSkewness(samples);

                String debug = String.format(
                        "skewness=%.2f, std=%.2f, buffer=%.2f, cps=%.2f",
                        skewness, std, buffer, data.getClickProcessor().getCps()
                );

                debug(debug);

                if (skewness < 0 && std < 3) {
                    if (buffer++ > 4) {
                        fail(debug);
                    }
                } else {
                    resetBuffer();
                }
                samples.clear();
            }

            ticks = 0;
        } else if (packet.isFlying()) {
            ticks++;
        }
    }
}
