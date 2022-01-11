package dev.isnow.fox.data.processor;

import dev.isnow.fox.data.PlayerData;
import dev.isnow.fox.util.MathUtil;
import dev.isnow.fox.util.type.Velocity;
import io.github.retrooper.packetevents.PacketEvents;
import io.github.retrooper.packetevents.packetwrappers.play.in.transaction.WrappedPacketInTransaction;
import io.github.retrooper.packetevents.packetwrappers.play.out.transaction.WrappedPacketOutTransaction;
import org.bukkit.util.Vector;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

public final class VelocityProcessor {
    private final PlayerData data;
    private double velocityX;
    private double velocityY;
    private double velocityZ;
    private double velocityXZ;
    private double velocityH;
    private double lastVelocityX;
    private double lastVelocityY;
    private double lastVelocityZ;
    private double lastVelocityXZ;
    private int maxVelocityTicks;
    private int velocityTicks;
    private int ticksSinceVelocity;
    private int takingVelocityTicks;
    private short velocityID;
    private final Map<Short, Vector> pendingVelocities = new HashMap<Short, Vector>();
    private final Velocity transactionVelocity = new Velocity(0, 0.0, 0.0, 0.0);
    private int flyingTicks;

    public VelocityProcessor(PlayerData data) {
        this.data = data;
    }

    public void handle(double velocityX, double velocityY, double velocityZ) {
        this.lastVelocityX = this.velocityX;
        this.lastVelocityY = this.velocityY;
        this.lastVelocityZ = this.velocityZ;
        this.lastVelocityXZ = this.velocityXZ;
        this.velocityX = velocityX;
        this.velocityY = velocityY;
        this.velocityH = (int)(((velocityX + velocityZ) / 2.0 + 2.0) * 15.0);
        this.velocityZ = velocityZ;
        this.velocityXZ = MathUtil.hypot(velocityX, velocityZ);
        this.velocityID = (short)ThreadLocalRandom.current().nextInt(Short.MAX_VALUE);
        PacketEvents.get().getPlayerUtils().sendPacket(this.data.getPlayer(), new WrappedPacketOutTransaction(0, this.velocityID, false));
        this.pendingVelocities.put(this.velocityID, new Vector(velocityX, velocityY, velocityZ));
    }

    public void handleTransaction(WrappedPacketInTransaction wrapper) {
        this.pendingVelocities.computeIfPresent(wrapper.getActionNumber(), (id, vector) -> {
            this.ticksSinceVelocity = 0;
            this.transactionVelocity.setVelocityX(vector.getX());
            this.transactionVelocity.setVelocityY(vector.getY());
            this.transactionVelocity.setVelocityZ(vector.getZ());
            this.transactionVelocity.setIndex(this.transactionVelocity.getIndex() + 1);
            this.velocityTicks = this.flyingTicks;
            this.maxVelocityTicks = (int)(((vector.getX() + vector.getZ()) / 2.0 + 2.0) * 15.0);
            this.pendingVelocities.remove(wrapper.getActionNumber());
            return vector;
        });
    }

    public void handleFlying() {
        ++this.ticksSinceVelocity;
        ++this.flyingTicks;
        this.takingVelocityTicks = this.isTakingVelocity() ? ++this.takingVelocityTicks : 0;
    }

    public boolean isTakingVelocity() {
        return Math.abs(this.flyingTicks - this.velocityTicks) < this.maxVelocityTicks;
    }

    public PlayerData getData() {
        return this.data;
    }

    public double getVelocityX() {
        return this.velocityX;
    }

    public double getVelocityY() {
        return this.velocityY;
    }

    public double getVelocityZ() {
        return this.velocityZ;
    }

    public double getVelocityXZ() {
        return this.velocityXZ;
    }

    public double getVelocityH() {
        return this.velocityH;
    }

    public double getLastVelocityX() {
        return this.lastVelocityX;
    }

    public double getLastVelocityY() {
        return this.lastVelocityY;
    }

    public double getLastVelocityZ() {
        return this.lastVelocityZ;
    }

    public double getLastVelocityXZ() {
        return this.lastVelocityXZ;
    }

    public int getMaxVelocityTicks() {
        return this.maxVelocityTicks;
    }

    public int getVelocityTicks() {
        return this.velocityTicks;
    }

    public int getTicksSinceVelocity() {
        return this.ticksSinceVelocity;
    }

    public int getTakingVelocityTicks() {
        return this.takingVelocityTicks;
    }

    public short getVelocityID() {
        return this.velocityID;
    }

    public Map<Short, Vector> getPendingVelocities() {
        return this.pendingVelocities;
    }

    public Velocity getTransactionVelocity() {
        return this.transactionVelocity;
    }

    public int getFlyingTicks() {
        return this.flyingTicks;
    }
}