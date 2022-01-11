package dev.isnow.fox.check.impl.combat.aura;

import dev.isnow.fox.check.Check;
import dev.isnow.fox.check.api.CheckInfo;
import dev.isnow.fox.data.PlayerData;
import dev.isnow.fox.packet.Packet;

@CheckInfo(name = "Aura", description = "Checks for invalid samples.", type = "N")

public class AuraN extends Check {

    private int swings;
    private int hits;

    public AuraN(PlayerData data) {
        super(data);
    }

    @Override
    public void handle(Packet packet) {
        if (packet.getPacketId() == 43) {
            if (++this.swings >= 100) {
                if (this.hits > 85) {
                    this.fail(swings);
                }

                this.swings = 0;
                this.hits = 0;
            }
        } else if (packet.getPacketId() == 14) {
            ++this.hits;
        }

    }
}