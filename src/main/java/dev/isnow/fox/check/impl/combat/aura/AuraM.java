package dev.isnow.fox.check.impl.combat.aura;

import dev.isnow.fox.check.Check;
import dev.isnow.fox.check.api.CheckInfo;
import dev.isnow.fox.data.PlayerData;
import dev.isnow.fox.exempt.type.ExemptType;
import dev.isnow.fox.packet.Packet;
import dev.isnow.fox.util.TimeUtils;


@CheckInfo(name = "Aura", type = "M", description = "Checks if pitch doesn't match angle")
public class AuraM extends Check {

    public AuraM(PlayerData data) {
        super(data);
    }

    @Override
    public void handle(Packet packet) {
        if (packet.isPosLook()) {
            if (TimeUtils.elapsed(data.getCombatProcessor().getLastAttack()) <= 850L && !isExempt(ExemptType.TELEPORT)) {
                /*
                Lmao patches sigma, lb, impact HEHEHEHEHEHEHEHEEHEHHEEHEHHEHEHHEHEH
                 */

                if (data.getRotationProcessor().getPitch() == 0) {
                    if (increaseBuffer() > 2) fail("0");
                } else {
                    decreaseBufferBy(0.25);
                }
            }
        }
    }
}