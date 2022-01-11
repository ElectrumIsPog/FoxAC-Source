

package dev.isnow.fox.check.impl.player.inventory;

import dev.isnow.fox.check.Check;
import dev.isnow.fox.check.api.CheckInfo;
import dev.isnow.fox.data.PlayerData;
import dev.isnow.fox.packet.Packet;
import io.github.retrooper.packetevents.packetwrappers.play.in.clientcommand.WrappedPacketInClientCommand;

@CheckInfo(name = "Inventory", type = "D", description = "Checks if player is swinging or attacking while opening inventory.")
public final class InventoryD extends Check {

    private boolean attacking, swinging;

    public InventoryD(final PlayerData data) {
        super(data);
    }

    @Override
    public void handle(final Packet packet) {
        if (packet.isClientCommand()) {
            final WrappedPacketInClientCommand wrapper = new WrappedPacketInClientCommand(packet.getRawPacket());

            if (wrapper.getClientCommand() != WrappedPacketInClientCommand.ClientCommand.OPEN_INVENTORY_ACHIEVEMENT) return;

            if (attacking || swinging) {
                if (increaseBuffer() > 4) {
                    fail();
                }
            } else {
                resetBuffer();
            }

        } else if (packet.isFlying()) {
            attacking = false;
            swinging = false;
        } else if (packet.isArmAnimation()) {
            swinging = true;
        } else if (packet.isUseEntity()) {
            attacking = true;
        }
    }
}
