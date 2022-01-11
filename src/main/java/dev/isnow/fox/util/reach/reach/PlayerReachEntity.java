package dev.isnow.fox.util.reach.reach;

import dev.isnow.fox.data.PlayerData;
import io.github.retrooper.packetevents.PacketEvents;
import io.github.retrooper.packetevents.utils.player.ClientVersion;
import io.github.retrooper.packetevents.utils.vector.Vector3d;

public class PlayerReachEntity {
    public Vector3d serverPos;
    public ReachInterpolationData oldPacketLocation;
    public ReachInterpolationData newPacketLocation;
    public int lastTransactionHung;
    public int removeTrans = Integer.MAX_VALUE;


    public PlayerReachEntity(double x, double y, double z, PlayerData player) {
        serverPos = new Vector3d(x, y, z);
        this.newPacketLocation = new ReachInterpolationData(GetBoundingBox.getBoundingBoxFromPosAndSize(x, y, z, 0.6, 1.8),
                serverPos.getX(), serverPos.getY(), serverPos.getZ(), PacketEvents.get().getPlayerUtils().getClientVersion(player.getPlayer()).isNewerThanOrEquals(ClientVersion.v_1_9));
    }

    // Set the old packet location to the new one
    // Set the new packet location to the updated packet location
    public void onFirstTransaction(double x, double y, double z, PlayerData player) {
        this.oldPacketLocation = newPacketLocation;
        this.newPacketLocation = new ReachInterpolationData(oldPacketLocation.getPossibleLocationCombined(), x, y, z, PacketEvents.get().getPlayerUtils().getClientVersion(player.getPlayer()).isNewerThanOrEquals(ClientVersion.v_1_9));
    }

    // Remove the possibility of the old packet location
    public void onSecondTransaction() {
        //GrimAC.staticGetLogger().info("Received second transaction for the previous movement");
        this.oldPacketLocation = null;
    }

    // If the old and new packet location are split, we need to combine bounding boxes
    public void onMovement() {
        newPacketLocation.tickMovement(oldPacketLocation == null);

        // Handle uncertainty of second transaction spanning over multiple ticks
        if (oldPacketLocation != null) {
            oldPacketLocation.tickMovement(true);
            newPacketLocation.updatePossibleStartingLocation(oldPacketLocation.getPossibleLocationCombined());
        }
    }

    public SimpleCollisionBox getPossibleCollisionBoxes() {
        if (oldPacketLocation == null) {
            return newPacketLocation.getPossibleLocationCombined();
        }

        //GrimAC.staticGetLogger().info(ChatColor.GOLD + "Uncertain!  Combining collision boxes " + oldPacketLocation.getPossibleLocationCombined() + " and " +  newPacketLocation.getPossibleLocationCombined() + " into " + ReachInterpolationData.combineCollisionBox(oldPacketLocation.getPossibleLocationCombined(), newPacketLocation.getPossibleLocationCombined()));
        return ReachInterpolationData.combineCollisionBox(oldPacketLocation.getPossibleLocationCombined(), newPacketLocation.getPossibleLocationCombined());
    }

    public void setDestroyed(int trans) {
        if (removeTrans != Integer.MAX_VALUE) return;
        removeTrans = trans;
    }

    @Override
    public String toString() {
        return "PlayerReachEntity{" +
                "serverPos=" + serverPos +
                ", oldPacketLocation=" + oldPacketLocation +
                ", newPacketLocation=" + newPacketLocation +
                '}';
    }
}