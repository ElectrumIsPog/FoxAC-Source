package dev.isnow.fox.check.impl.movement.motion;

import dev.isnow.fox.check.Check;
import dev.isnow.fox.check.api.CheckInfo;
import dev.isnow.fox.data.PlayerData;
import dev.isnow.fox.exempt.type.ExemptType;
import dev.isnow.fox.packet.Packet;

@CheckInfo(name = "Motion", type = "B", description = "Checks if client fall speed doesn't match server side.")
public final class MotionB extends Check {

    public MotionB(final PlayerData data) {
        super(data);
    }

    @Override
    public void handle(final Packet packet) {
        if (packet.isPosition()) {
            final boolean onGround = data.getPositionProcessor().isOnSolidGround();
            if (onGround) {
                final double deltaY2 = data.getPositionProcessor().getDeltaY();
                final boolean exempt = isExempt(ExemptType.CHUNK,
                        ExemptType.FLYING,
                        ExemptType.VEHICLE,
                        ExemptType.SLIME,
                        ExemptType.TELEPORT_DELAY,
                        ExemptType.NEARSLABS,
                        ExemptType.NEARSTAIRS,
                        ExemptType.VOID,
                        ExemptType.JOINED,
                        ExemptType.LAGGING,
                        ExemptType.VOID);
                if (deltaY2 <= -4.5 && !exempt) {
                    fail(deltaY2);

                }
            }
        }
    }

}