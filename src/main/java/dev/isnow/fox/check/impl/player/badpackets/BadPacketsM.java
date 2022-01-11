

package dev.isnow.fox.check.impl.player.badpackets;

import dev.isnow.fox.check.Check;
import dev.isnow.fox.check.api.CheckInfo;
import dev.isnow.fox.data.PlayerData;
import dev.isnow.fox.packet.Packet;
import io.github.retrooper.packetevents.packetwrappers.play.in.clientcommand.WrappedPacketInClientCommand;

@CheckInfo(name = "BadPackets", type = "M", description = "Checks if player is trying to respawn while not dead.")
public final class BadPacketsM extends Check {
    public BadPacketsM(final PlayerData data) {
        super(data);
    }

    @Override
    public void handle(final Packet packet) {
        if (packet.isClientCommand()) {
            final WrappedPacketInClientCommand wrapper = new WrappedPacketInClientCommand(packet.getRawPacket());

            if (wrapper.getClientCommand() == WrappedPacketInClientCommand.ClientCommand.PERFORM_RESPAWN) {
                if (data.getPlayer().getHealth() > 0.0 && increaseBuffer() > 2) {
                    fail();
                }
                else {
                    setBuffer(0);
                }
            }
        }
    }
}
