package dev.isnow.fox.util.type;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.block.Block;
import org.bukkit.util.NumberConversions;

@AllArgsConstructor @Getter
public class CollideEntry {
    private final Block block;
    private final BoundingBox boundingBox;

    public boolean isChunkLoaded() {
        return block.getLocation().getWorld().isChunkLoaded(
                NumberConversions.floor(block.getLocation().getX()) >> 4,
                NumberConversions.floor(block.getLocation().getZ()) >> 4);
    }
}