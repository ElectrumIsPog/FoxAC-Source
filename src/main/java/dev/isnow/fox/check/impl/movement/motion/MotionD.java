package dev.isnow.fox.check.impl.movement.motion;

import dev.isnow.fox.check.Check;
import dev.isnow.fox.check.api.CheckInfo;
import dev.isnow.fox.data.PlayerData;
import dev.isnow.fox.exempt.type.ExemptType;
import dev.isnow.fox.packet.Packet;
import dev.isnow.fox.util.BlockUtil;
import io.github.retrooper.packetevents.PacketEvents;
import io.github.retrooper.packetevents.utils.player.ClientVersion;
import org.bukkit.Material;

@CheckInfo(name = "Motion", type = "D", description = "Checks for invalid motion of liquids.")
public final class MotionD extends Check {

    public MotionD(PlayerData data) {
        super(data);
    }

    @Override
    public void handle(Packet packet) {
        if(packet.isFlying()) {
            if (BlockUtil.isLiquid(data.getPlayer().getLocation().subtract(0, 0.1, 0).getBlock())
                    && !BlockUtil.isLiquid(data.getPlayer().getLocation().clone().add(0, 0.2, 0).getBlock())
                    && !data.getVelocityProcessor().isTakingVelocity()
                    && data.getPositionProcessor().getBlocksBelow().stream().noneMatch(block -> block.getType() == Material.WATER_LILY)
                    && data.getPositionProcessor().getWebTicks() == 0) {

                if(PacketEvents.get().getPlayerUtils().getClientVersion(data.getPlayer()).isNewerThanOrEquals(ClientVersion.v_1_13)) {
                    return;
                }

                if (!data.getPositionProcessor().isOnGround() && increaseBuffer() > 9 && !isExempt(ExemptType.FLYING)) {
                    fail("DeltaY:" + data.getPositionProcessor().getDeltaY());
                }
                else {
                    decreaseBufferBy(0.5);
                }
            }
        }
    }
}
