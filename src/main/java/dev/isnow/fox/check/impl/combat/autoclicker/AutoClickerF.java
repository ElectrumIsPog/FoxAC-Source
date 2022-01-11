package dev.isnow.fox.check.impl.combat.autoclicker;


import dev.isnow.fox.check.Check;
import dev.isnow.fox.check.api.CheckInfo;
import dev.isnow.fox.data.PlayerData;
import dev.isnow.fox.exempt.type.ExemptType;
import dev.isnow.fox.packet.Packet;
import dev.isnow.fox.util.MathUtil;

import java.util.ArrayDeque;
import java.util.Deque;

@CheckInfo(name = "AutoClicker", type = "F", description = "Checks for invalid kurtosis")
public class AutoClickerF extends Check {
    private final Deque<Integer> samples = new ArrayDeque<>();
    private int ticks;

    public AutoClickerF(PlayerData data) {
        super(data);
    }

    @Override
    public void handle(Packet packet) {
        if (packet.isArmAnimation() && !isExempt(ExemptType.AUTOCLICKER)) {
            if (ticks > 50 || ticks == 0) samples.clear();
            else samples.add(ticks * 50);

            if (samples.size() == 30) {
                final double kurtosis = MathUtil.getKurtosis(samples);

                final boolean invalid = Double.isNaN(kurtosis);

                debug(invalid);
                if (invalid) {
                    if (increaseBuffer() > 1) {
                        fail("kurtosis=" + kurtosis);
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
