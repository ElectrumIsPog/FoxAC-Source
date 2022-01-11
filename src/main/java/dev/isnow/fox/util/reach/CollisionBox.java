package dev.isnow.fox.util.reach;



import dev.isnow.fox.util.reach.reach.SimpleCollisionBox;

import java.util.List;

public interface CollisionBox {
    boolean isCollided(SimpleCollisionBox other);

    boolean isIntersected(SimpleCollisionBox other);

    CollisionBox copy();

    CollisionBox offset(double x, double y, double z);

    void downCast(List<SimpleCollisionBox> list);

    boolean isNull();

    boolean isFullBlock();
}