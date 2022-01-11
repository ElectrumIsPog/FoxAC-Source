package dev.isnow.fox.check.impl.combat.hitbox;

import dev.isnow.fox.check.Check;
import dev.isnow.fox.check.api.CheckInfo;
import dev.isnow.fox.data.PlayerData;
import dev.isnow.fox.packet.Packet;
import dev.isnow.fox.util.reach.reach.PlayerReachEntity;
import dev.isnow.fox.util.reach.reach.ReachUtils;
import dev.isnow.fox.util.reach.reach.SimpleCollisionBox;
import io.github.retrooper.packetevents.PacketEvents;
import io.github.retrooper.packetevents.packetwrappers.play.in.useentity.WrappedPacketInUseEntity;
import io.github.retrooper.packetevents.packetwrappers.play.out.entity.WrappedPacketOutEntity;
import io.github.retrooper.packetevents.packetwrappers.play.out.entityteleport.WrappedPacketOutEntityTeleport;
import io.github.retrooper.packetevents.packetwrappers.play.out.namedentityspawn.WrappedPacketOutNamedEntitySpawn;
import io.github.retrooper.packetevents.utils.player.ClientVersion;
import io.github.retrooper.packetevents.utils.vector.Vector3d;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

@CheckInfo(name = "HitBox", type = "A", description = "Modified HitBox (Detected using transactions)")
public class HitBoxA extends Check {

    public final ConcurrentHashMap<Integer, PlayerReachEntity> entityMap = new ConcurrentHashMap<>();
    private final ConcurrentLinkedQueue<Integer> playerAttackQueue = new ConcurrentLinkedQueue<>();

    private boolean hasSentPreWavePacket = false;

    public HitBoxA(PlayerData data) {
        super(data);
    }

    @Override
    public void handle(final Packet packet) {
        if (packet.isSpawnEntity()) {
            WrappedPacketOutNamedEntitySpawn spawn = new WrappedPacketOutNamedEntitySpawn(packet.getRawPacket());
            Entity entity = spawn.getEntity();
            if (entity != null && entity.getType() == EntityType.PLAYER) {
                handleSpawnPlayer(spawn.getEntityId(), spawn.getPosition());
            }
        } else if (packet.isRelEntityMove()) {
            WrappedPacketOutEntity.WrappedPacketOutRelEntityMove move = new WrappedPacketOutEntity.WrappedPacketOutRelEntityMove(packet.getRawPacket());

            PlayerReachEntity reachEntity = entityMap.get(move.getEntityId());
            if (reachEntity != null) {
                // We can't hang two relative moves on one transaction
                if (reachEntity.lastTransactionHung == data.getConnectionProcessor().getLastTransactionSent().get())
                    data.getConnectionProcessor().sendTransaction();
                reachEntity.lastTransactionHung = data.getConnectionProcessor().getLastTransactionSent().get();
                handleMoveEntity(move.getEntityId(), move.getDeltaX(), move.getDeltaY(), move.getDeltaZ(), true);
            }
        } else if (packet.isEntityTeleport()) {
            WrappedPacketOutEntityTeleport teleport = new WrappedPacketOutEntityTeleport(packet.getRawPacket());

            PlayerReachEntity reachEntity = entityMap.get(teleport.getEntityId());
            if (reachEntity != null) {
                // We can't hang two relative moves on one transaction
                if (reachEntity.lastTransactionHung == data.getConnectionProcessor().lastTransactionSent.get()) data.getConnectionProcessor().sendTransaction();
                reachEntity.lastTransactionHung = data.getConnectionProcessor().lastTransactionSent.get();

                Vector3d pos = teleport.getPosition();
                handleMoveEntity(teleport.getEntityId(), pos.getX(), pos.getY(), pos.getZ(), false);
            }
        } else if (packet.isUseEntity()) {
            WrappedPacketInUseEntity action = new WrappedPacketInUseEntity(packet.getRawPacket());

            if (data.getPlayer().getGameMode() == GameMode.CREATIVE) return;
            //if (data.getPositionProcessor().isInVehicle()) return;

            if (action.getAction() == WrappedPacketInUseEntity.EntityUseAction.ATTACK) {
                checkReach(action.getEntityId());
            }

        } else if (packet.isFlyingType()) {
            tickFlying();
        }
    }

    private void tickFlying() {
        double maxReach = 3;

        Integer attackQueue = playerAttackQueue.poll();
        while (attackQueue != null) {
            PlayerReachEntity reachEntity = entityMap.get(attackQueue);
            SimpleCollisionBox targetBox = reachEntity.getPossibleCollisionBoxes();

            targetBox.expand(0.1f);

            targetBox.expand(0.0005);


            Location from = new Location(null, data.getPositionProcessor().getLastX(), data.getPositionProcessor().getLastY(), data.getPositionProcessor().getLastZ(), data.getRotationProcessor().getLastYaw(), data.getRotationProcessor().getLastPitch());

            double minDistance = Double.MAX_VALUE;

            // https://bugs.mojang.com/browse/MC-67665
            List<Vector> possibleLookDirs = new ArrayList<>(Arrays.asList(
                    ReachUtils.getLook(data.getRotationProcessor().getLastYaw(), data.getRotationProcessor().getPitch()),
                    ReachUtils.getLook(data.getRotationProcessor().getYaw(), data.getRotationProcessor().getPitch())
            ));

            // 1.7 players do not have any of these issues! They are always on the latest look vector
            if (PacketEvents.get().getPlayerUtils().getClientVersion(data.getPlayer()).isOlderThan(ClientVersion.v_1_8)) {
                possibleLookDirs = Collections.singletonList(ReachUtils.getLook(data.getRotationProcessor().getYaw(), data.getRotationProcessor().getPitch()));
            }

            for (Vector lookVec : possibleLookDirs) {
                for (double eye : Arrays.asList(1.54, 1.62)) {
                    Vector eyePos = new Vector(from.getX(), from.getY() + eye, from.getZ());
                    Vector endReachPos = eyePos.clone().add(new Vector(lookVec.getX() * 6, lookVec.getY() * 6, lookVec.getZ() * 6));

                    Vector intercept = ReachUtils.calculateIntercept(targetBox, eyePos, endReachPos);

                    if (ReachUtils.isVecInside(targetBox, eyePos)) {
                        minDistance = 0;
                        break;
                    }

                    if (intercept != null) {
                        minDistance = Math.min(eyePos.distance(intercept), minDistance);
                    }
                }
            }

            if (minDistance == Double.MAX_VALUE) {
                fail("");
            } else if (minDistance > maxReach) {
                fail("Reach: " + String.format("%.55f", minDistance));
            }

            attackQueue = playerAttackQueue.poll();
        }

        for (PlayerReachEntity entity : entityMap.values()) {
            entity.onMovement();
        }
    }

    public void checkReach(int entityID) {
        if (entityMap.containsKey(entityID))
            playerAttackQueue.add(entityID);
    }

    private void handleSpawnPlayer(int playerID, Vector3d spawnPosition) {
        entityMap.put(playerID, new PlayerReachEntity(spawnPosition.getX(), spawnPosition.getY(), spawnPosition.getZ(), data));
    }

    private void handleMoveEntity(int entityId, double deltaX, double deltaY, double deltaZ, boolean isRelative) {
        PlayerReachEntity reachEntity = entityMap.get(entityId);

        if (reachEntity != null) {
            // Only send one transaction before each wave, without flushing
            if (!hasSentPreWavePacket) data.getConnectionProcessor().sendTransaction();
            hasSentPreWavePacket = true; // Also functions to mark we need a post wave transaction

            // Update the tracked server's entity position
            if (isRelative)
                reachEntity.serverPos = reachEntity.serverPos.add(new Vector3d(deltaX, deltaY, deltaZ));
            else
                reachEntity.serverPos = new Vector3d(deltaX, deltaY, deltaZ);

            int lastTrans = data.getConnectionProcessor().lastTransactionSent.get();
            Vector3d newPos = reachEntity.serverPos;

            data.getConnectionProcessor().addRealTimeTask(lastTrans, () -> reachEntity.onFirstTransaction(newPos.getX(), newPos.getY(), newPos.getZ(), data));
            data.getConnectionProcessor().addRealTimeTask(lastTrans + 1, reachEntity::onSecondTransaction);
        }
    }

}