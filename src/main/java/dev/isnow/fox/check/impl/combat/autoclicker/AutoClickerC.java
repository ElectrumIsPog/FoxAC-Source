package dev.isnow.fox.check.impl.combat.autoclicker;

import dev.isnow.fox.check.Check;
import dev.isnow.fox.check.api.CheckInfo;
import dev.isnow.fox.data.PlayerData;
import dev.isnow.fox.exempt.type.ExemptType;
import dev.isnow.fox.packet.Packet;

@CheckInfo(name = "AutoClicker", type = "C", description = "Checks for consistent click pattern")
public class AutoClickerC extends Check {

    private double avgSpeed, avgDeviation;
    private int ticks;


    public AutoClickerC(PlayerData data) {
        super(data);
    }

    @Override
    public void handle(Packet packet) {
        if (packet.isArmAnimation() && !isExempt(ExemptType.AUTOCLICKER)) {
            if (ticks > 10 || ticks == 0) {
                return;
            }
            double speed = ticks * 50;
            avgSpeed = ((avgSpeed * 14) + speed) / 15;

            double deviation = Math.abs(speed - avgSpeed);
            avgDeviation = ((avgDeviation * 9) + deviation) / 10;

            if (avgDeviation < 10) {
                if (++buffer > 8) {
                    fail("dev=" + deviation);
                    decreaseBufferBy(3);
                }
            } else {
                buffer *= 0.75;
            }
            ticks = 0;//brb
        } else if (packet.isFlying()) {
            ticks++;
        }
    }
}
