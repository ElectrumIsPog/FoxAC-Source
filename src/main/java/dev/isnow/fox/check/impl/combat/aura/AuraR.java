package dev.isnow.fox.check.impl.combat.aura;

import dev.isnow.fox.check.Check;
import dev.isnow.fox.check.api.CheckInfo;
import dev.isnow.fox.data.PlayerData;
import dev.isnow.fox.packet.Packet;

@CheckInfo(name = "Aura", description = "Checks for values that match auras.", type = "R", experimental = true)
public class AuraR extends Check {

    private int ticks, invalidTicks, lastTicks, totalTicks;

    public AuraR(PlayerData data) {
        super(data);
    }

    @Override
    public void handle(final Packet packet) {
        if (packet.isFlying()) {
            ++ticks;
        } else if (packet.isUseEntity()) {
            if (ticks <= 8) {
                if (lastTicks == ticks) {
                    ++invalidTicks;
                }

                if (++totalTicks >= 25) {
                    if (invalidTicks > 22) {
                        fail();
                    }

                    totalTicks = 0;
                    invalidTicks = 0;
                }

                lastTicks = ticks;
            }

            ticks = 0;
        }
    }
}