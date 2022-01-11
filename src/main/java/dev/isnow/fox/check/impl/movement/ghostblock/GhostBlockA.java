package dev.isnow.fox.check.impl.movement.ghostblock;

import dev.isnow.fox.check.Check;
import dev.isnow.fox.check.api.CheckInfo;
import dev.isnow.fox.config.Config;
import dev.isnow.fox.data.PlayerData;
import dev.isnow.fox.data.processor.PositionProcessor;
import dev.isnow.fox.exempt.type.ExemptType;
import dev.isnow.fox.packet.Packet;
import dev.isnow.fox.util.PlayerUtil;
import org.bukkit.potion.PotionEffectType;

@CheckInfo(name="GhostBlock", type="A", description="Checks for too many ascention ticks.", experimental = true)
public final class GhostBlockA
        extends Check {
    public GhostBlockA(PlayerData data) {
        super(data);
    }

    @Override
    public void handle(Packet packet) {
        if (packet.isPosition()) {
            final PositionProcessor processor = data.getPositionProcessor();

            final double deltaY = processor.getDeltaY();

            final int airTicksModifier = PlayerUtil.getPotionLevel(data.getPlayer(), PotionEffectType.JUMP);
            final int airTicksLimit = 8 + airTicksModifier;
            final int clientAirTicks = data.getPositionProcessor().getAirTicks();

            final boolean exempt = isExempt(ExemptType.VELOCITY, ExemptType.PISTON, ExemptType.VEHICLE,
                    ExemptType.TELEPORT, ExemptType.LIQUID, ExemptType.FLYING, ExemptType.CLIMBABLE, ExemptType.SLIME);

            final boolean invalid = deltaY > 0 && clientAirTicks > airTicksLimit;

            if (invalid && !exempt) {
                if (increaseBuffer() > 2) {
                    if (Config.STRICTAF_GHOSTBLOCK_MODE && Config.GHOST_BLOCK_ENABLED) {
                        data.dragDown();
                        data.getPlayer().sendMessage("Lagged Back for ghost blocks. [REALTRIPPY]");
                    }
                } else {
                    decreaseBufferBy(0.01);
                }
            }
        }
    }
}