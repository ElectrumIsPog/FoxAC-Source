package dev.isnow.fox.util.reach.reach;

public class GetBoundingBox {
    public static SimpleCollisionBox getBoundingBoxFromPosAndSize(double centerX, double minY, double centerZ, double width, double height) {
        double minX = centerX - (width / 2);
        double maxX = centerX + (width / 2);
        double maxY = minY + height;
        double minZ = centerZ - (width / 2);
        double maxZ = centerZ + (width / 2);

        return new SimpleCollisionBox(minX, minY, minZ, maxX, maxY, maxZ, false);
    }
}