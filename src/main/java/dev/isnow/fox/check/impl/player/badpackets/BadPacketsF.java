

package dev.isnow.fox.check.impl.player.badpackets;

import dev.isnow.fox.check.Check;
import dev.isnow.fox.check.api.CheckInfo;
import dev.isnow.fox.data.PlayerData;
import dev.isnow.fox.packet.Packet;

@CheckInfo(name = "BadPackets", type = "F", description = "Checks if player is sending flyings but not responding transactions.")
public final class BadPacketsF extends Check {

    public BadPacketsF(final PlayerData data) {
        super(data);
    }

    @Override
    public void handle(final Packet packet) {
    }
}
