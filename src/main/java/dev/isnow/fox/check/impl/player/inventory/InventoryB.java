

package dev.isnow.fox.check.impl.player.inventory;

import dev.isnow.fox.check.Check;
import dev.isnow.fox.check.api.CheckInfo;
import dev.isnow.fox.data.PlayerData;
import dev.isnow.fox.exempt.type.ExemptType;
import dev.isnow.fox.packet.Packet;
import dev.isnow.fox.util.PlayerUtil;

@CheckInfo(name = "Inventory", type = "B", description = "Checks if player is moving while interacting with inventory.")
public final class InventoryB extends Check {
    public InventoryB(final PlayerData data) {
        super(data);
    }

    @Override
    public void handle(final Packet packet) {
        if (packet.isWindowClick()) {
            final boolean onGround = data.getPositionProcessor().isOnGround();

            final double deltaXZ = data.getPositionProcessor().getDeltaXZ();
            final double lastDeltaXZ = data.getPositionProcessor().getLastDeltaXZ();

            final double acceleration = deltaXZ - lastDeltaXZ;

            final boolean exempt = isExempt(ExemptType.WEB, ExemptType.FLYING, ExemptType.PISTON, ExemptType.LIQUID, ExemptType.CLIMBABLE, ExemptType.VELOCITY, ExemptType.CREATIVE);

            final boolean invalidDelta = deltaXZ > PlayerUtil.getBaseSpeed(data.getPlayer(), 0.2F) && onGround;
            final boolean invalidAcceleration = acceleration >= 0.0 && deltaXZ > PlayerUtil.getBaseSpeed(data.getPlayer(), 0.1F);

            final boolean invalid = invalidDelta || invalidAcceleration;

            if (invalid && !exempt) {
                if (increaseBuffer() > 2) {
                    fail();
                }
            } else {
                decreaseBufferBy(0.5);
            }
        }
    }
}
