package dev.isnow.fox.check.impl.combat.autoclicker;

import dev.isnow.fox.check.Check;
import dev.isnow.fox.check.api.CheckInfo;
import dev.isnow.fox.data.PlayerData;
import dev.isnow.fox.exempt.type.ExemptType;
import dev.isnow.fox.packet.Packet;
import io.github.retrooper.packetevents.packetwrappers.play.in.useentity.WrappedPacketInUseEntity;

@CheckInfo(name = "AutoClicker", description = "Checks for high click speed.", type = "D")
public final class AutoClickerD extends Check {

    private int ticks, cps;

    public AutoClickerD(final PlayerData data) {
        super(data);
    }

    @Override
    public void handle(final Packet packet) {
        if (packet.isFlying()) {
            if (++ticks >= 20) {
                debug("cps=" + cps);
                if (cps > 26 && !isExempt(ExemptType.AUTOCLICKER)) {
                    fail("cps=" + cps);
                }
                ticks = cps = 0;
            }
        } else if (packet.isUseEntity()) {
            final WrappedPacketInUseEntity wrapper = new WrappedPacketInUseEntity(packet.getRawPacket());
            if (wrapper.getAction() == WrappedPacketInUseEntity.EntityUseAction.ATTACK) {
                ++cps;
            }
        }
    }
}