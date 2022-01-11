package dev.isnow.fox.check.impl.player.payload;

import dev.isnow.fox.check.Check;
import dev.isnow.fox.check.api.CheckInfo;
import dev.isnow.fox.config.Config;
import dev.isnow.fox.data.PlayerData;
import dev.isnow.fox.manager.AlertManager;
import dev.isnow.fox.packet.Packet;
import dev.isnow.fox.util.ColorUtil;
import io.github.retrooper.packetevents.packetwrappers.play.in.custompayload.WrappedPacketInCustomPayload;

@CheckInfo(name = "Payload", description = "Checks for spamming payloads.", type = "B")
public final class PayloadB extends Check {

    public PayloadB(final PlayerData data) {
        super(data);
    }

    @Override
    public void handle(Packet packet) {
        if (packet.isCustomPayload()) {
            WrappedPacketInCustomPayload wrappedPacketInCustomPayload = new WrappedPacketInCustomPayload(packet.getRawPacket());
            String payload = wrappedPacketInCustomPayload.getChannelName();
            if ((payload.equals("MC|BOpen") || payload.equals("MC|BEdit")) && (this.buffer += 2) > 4) {
                if (buffer > 2) {
                    AlertManager.sendAntiExploitAlert("Checks for clients spamming payloads.", "Payload Spam");
                    data.getPlayer().kickPlayer(ColorUtil.translate(Config.PAYLOADKICK));
                }
            }
        }
    }
}