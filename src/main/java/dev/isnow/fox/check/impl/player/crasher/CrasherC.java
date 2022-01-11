package dev.isnow.fox.check.impl.player.crasher;

import dev.isnow.fox.check.Check;
import dev.isnow.fox.check.api.CheckInfo;
import dev.isnow.fox.config.Config;
import dev.isnow.fox.data.PlayerData;
import dev.isnow.fox.manager.AlertManager;
import dev.isnow.fox.packet.Packet;
import dev.isnow.fox.util.ColorUtil;
import io.github.retrooper.packetevents.packetwrappers.play.in.custompayload.WrappedPacketInCustomPayload;

@CheckInfo(name = "Crasher", description = "Checks for spamming data.", type = "C")
public final class CrasherC extends Check {

    public CrasherC(final PlayerData data) {
        super(data);
    }

    @Override
    public void handle(Packet packet) {
        if (packet.isCustomPayload()) {
            WrappedPacketInCustomPayload wrappedPacketInCustomPayload = new WrappedPacketInCustomPayload(packet.getRawPacket());
            if (wrappedPacketInCustomPayload.getData().length > 15000) {
                AlertManager.sendAntiExploitAlert("Checks for spamming data.", "Data Spam");
                data.getPlayer().kickPlayer(ColorUtil.translate(Config.ANTICRASHKICKEDMESSAGE));
            }

        }
    }
}