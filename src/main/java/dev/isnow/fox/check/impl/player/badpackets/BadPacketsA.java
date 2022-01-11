

package dev.isnow.fox.check.impl.player.badpackets;

import dev.isnow.fox.check.Check;
import dev.isnow.fox.check.api.CheckInfo;
import dev.isnow.fox.data.PlayerData;
import dev.isnow.fox.exempt.type.ExemptType;
import dev.isnow.fox.packet.Packet;

@CheckInfo(name = "BadPackets", type = "A", description = "Checks if the player pitch is an impossible value.")
public final class BadPacketsA extends Check {
    public BadPacketsA(final PlayerData data) {
        super(data);
    }

    @Override
    public void handle(final Packet packet) {
        if (packet.isFlying()) {
            final float pitch = data.getRotationProcessor().getPitch();

            if (Math.abs(pitch)> 90.0f && !isExempt(ExemptType.CLIMBABLE)) {
                fail();
            }
        }
    }
}
