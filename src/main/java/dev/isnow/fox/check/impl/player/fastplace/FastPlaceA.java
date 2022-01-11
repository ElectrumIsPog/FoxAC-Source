

package dev.isnow.fox.check.impl.player.fastplace;

import dev.isnow.fox.check.Check;
import dev.isnow.fox.check.api.CheckInfo;
import dev.isnow.fox.data.PlayerData;
import dev.isnow.fox.packet.Packet;
import dev.isnow.fox.util.MathUtil;

import java.util.LinkedList;
import java.util.Queue;

@CheckInfo(name = "FastPlace", type = "A", description = "Checks if player placing blocks too fast.")
public final class FastPlaceA extends Check {

    private final Queue<Integer> delays = new LinkedList<>();

    private int movements;


    public FastPlaceA(final PlayerData data) {
        super(data);
    }

    @Override
    public void handle(final Packet packet) {
        if(packet.isBlockPlace() && !isBridging()) {
            if (movements < 10) {
                if (delays.add(movements) && delays.size() == 35) {
                    double avg = MathUtil.getAverage(delays);
                    double stDev = MathUtil.getStandardDeviation(delays);

                    if (avg < 4 && stDev < 0.15) {
                        if  (increaseBuffer() > 2) {
                            fail();
                        }
                    }

                    delays.clear();
                }
            }

            movements = 0;
        }
        if(packet.isFlying() && !isBridging()) {
            ++movements;
        }
    }
}
