package dev.isnow.fox.check.impl.combat.autoclicker;


import dev.isnow.fox.check.Check;
import dev.isnow.fox.check.api.CheckInfo;
import dev.isnow.fox.data.PlayerData;
import dev.isnow.fox.exempt.type.ExemptType;
import dev.isnow.fox.packet.Packet;
import dev.isnow.fox.util.MathUtil;
import dev.isnow.fox.util.type.EvictingList;

@CheckInfo(name = "AutoClicker", type = "B", description = "Checks the outliers on your clicks")
public class AutoClickerB extends Check {

    private final EvictingList<Long> tickList = new EvictingList<>(30);
    private double lastDeviation;
    private int tick;

    public AutoClickerB(final PlayerData data) {
        super(data);
    }

    @Override
    public void handle(final Packet packet) {
        if (packet.isArmAnimation()) {
            final boolean exempt = isExempt(ExemptType.DROP, ExemptType.AUTOCLICKER);
            if (!exempt) tickList.add((long) (tick * 50.0));

            if (tickList.isFull()) {
                final double deviation = MathUtil.getStandardDeviation(tickList);
                final double difference = Math.abs(deviation - lastDeviation);

                final boolean invalid = difference < 6;

                debug("diff: " +deviation+ "devi: " +deviation);

                if (invalid && !exempt) {
                    if (increaseBuffer() > 15) {
                        fail(deviation);
                    }
                } else {
                    decreaseBuffer();
                }

                lastDeviation = deviation;
            }
        } else if (packet.isFlying()) {
            tick++;
        }
    }
}