package dev.isnow.fox.check.impl.combat.autoclicker;

import dev.isnow.fox.check.Check;
import dev.isnow.fox.check.api.CheckInfo;
import dev.isnow.fox.data.PlayerData;
import dev.isnow.fox.exempt.type.ExemptType;
import dev.isnow.fox.packet.Packet;
import io.github.retrooper.packetevents.utils.player.ClientVersion;
import lombok.val;

@CheckInfo(name = "AutoClicker", type = "A", description = "Checks for concurrent clicks")
public class AutoClickerA extends Check {
    private long lastSwing, lastDelay;

    public AutoClickerA(PlayerData data) {
        super(data);
    }

    @Override
    public void handle(Packet packet) {
        if (packet.isArmAnimation() && !isExempt(ExemptType.AUTOCLICKER)) {
            if(data.getVersion().isNewerThanOrEquals(ClientVersion.v_1_8)) { // Doesn't work in 1.7 since no click delay
                val now = System.currentTimeMillis();
                val delay = now - this.lastSwing;
                if (delay > 10L && delay < 200 && data.getClickProcessor().getCps() >= 7) {
                    if (Math.abs(delay - this.lastDelay) > 50L) {
                        buffer = 0;
                    } else if (delay > 35L && buffer++ > 80) {
                        fail("Delay: " + delay);
                    }
                    this.lastDelay = delay;
                }
                this.lastSwing = now;
            }
        }
    }
}
