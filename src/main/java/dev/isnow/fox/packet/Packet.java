

package dev.isnow.fox.packet;

import io.github.retrooper.packetevents.packettype.PacketType;
import io.github.retrooper.packetevents.packetwrappers.NMSPacket;
import io.github.retrooper.packetevents.packetwrappers.play.in.entityaction.WrappedPacketInEntityAction;
import io.github.retrooper.packetevents.packetwrappers.play.in.pong.WrappedPacketInPong;
import io.github.retrooper.packetevents.packetwrappers.play.in.transaction.WrappedPacketInTransaction;
import io.github.retrooper.packetevents.packetwrappers.play.in.useentity.WrappedPacketInUseEntity;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public final class Packet {

    private final Direction direction;
    private final NMSPacket rawPacket;
    private final byte packetId;
    private final long timeStamp;

    public boolean isReceiving() {
        return direction == Direction.RECEIVE;
    }

    public boolean isSending() {
        return direction == Direction.SEND;
    }

    public boolean isFlying() {
        return isReceiving() && PacketType.Play.Client.Util.isInstanceOfFlying(packetId);
    }

    public boolean isUseEntity() {
        return isReceiving() && packetId == PacketType.Play.Client.USE_ENTITY;
    }

    public boolean isRotation() {
        return isReceiving() && (packetId == PacketType.Play.Client.LOOK || packetId == PacketType.Play.Client.POSITION_LOOK);
    }

    public boolean isHitEntity() {
        return isReceiving() && packetId == PacketType.Play.Client.USE_ENTITY && new WrappedPacketInUseEntity(rawPacket).getAction() == WrappedPacketInUseEntity.EntityUseAction.ATTACK;
    }

    public boolean isTick() {
        return isPingPong() && getTransactionID() < 0;
    }

    public int getTransactionID() {
        if(packetId == PacketType.Play.Client.TRANSACTION) {
            return new WrappedPacketInTransaction(getRawPacket()).getActionNumber();
        } else if(packetId == PacketType.Play.Client.PONG) {
            return new WrappedPacketInPong(getRawPacket()).getId();
        }
        return 1;
    }

    public boolean isSlot() { return(packetId == PacketType.Play.Client.HELD_ITEM_SLOT); }

    public boolean isPingPong() {
        return isReceiving() && packetId == PacketType.Play.Client.PONG ||
                packetId == PacketType.Play.Client.TRANSACTION;
    }

    public boolean isPosition() {
        return isReceiving() && (packetId == PacketType.Play.Client.POSITION || packetId == PacketType.Play.Client.POSITION_LOOK);
    }

    public boolean isStopSprinting() {
        return (isAction() && new WrappedPacketInEntityAction(rawPacket).getAction() == WrappedPacketInEntityAction.PlayerAction.STOP_SPRINTING);
    }

    public boolean isAction() {
        return (packetId == PacketType.Play.Client.ENTITY_ACTION);
    }

    public boolean isUseEntityInteract() {
        return (isUseEntity() && new WrappedPacketInUseEntity(rawPacket).getAction() == WrappedPacketInUseEntity.EntityUseAction.INTERACT);
    }

    public boolean isUseEntityInteractAt() {
        return (isUseEntity() && new WrappedPacketInUseEntity(rawPacket).getAction() == WrappedPacketInUseEntity.EntityUseAction.INTERACT_AT);
    }

    public boolean isTransaction() {
        return (packetId == PacketType.Play.Client.TRANSACTION && new WrappedPacketInTransaction(rawPacket).getActionNumber() < 0);
    }

    public boolean isTransactionSussi() {
        return (packetId == PacketType.Play.Client.TRANSACTION && new WrappedPacketInTransaction(rawPacket).getActionNumber() < 0);
    }

    public boolean isFlyingType() {
        return PacketType.Play.Client.Util.isInstanceOfFlying(packetId);
    }

    public boolean isUseEntityAttack() {
        return (isUseEntity() && new WrappedPacketInUseEntity(rawPacket).getAction() == WrappedPacketInUseEntity.EntityUseAction.ATTACK);
    }

    public boolean isLook() {
        return (packetId == PacketType.Play.Client.LOOK || packetId == PacketType.Play.Client.POSITION_LOOK);
    }

    public boolean isPositionLook() {
        return (packetId == PacketType.Play.Client.POSITION_LOOK);
    }


    public boolean isArmAnimation() {
        return isReceiving() && packetId == PacketType.Play.Client.ARM_ANIMATION;
    }

    public boolean isIncomingAbilities() {
        return isReceiving() && packetId == PacketType.Play.Client.ABILITIES;
    }

    public boolean isOutgoingAbilities() {
        return isReceiving() && packetId == PacketType.Play.Client.ABILITIES;
    }

    public boolean isBlockPlace() {
        return isReceiving() && packetId == PacketType.Play.Client.BLOCK_PLACE;
    }

    public boolean isBukkitBlockPlace() {
        return isReceiving() && packetId == Byte.MAX_VALUE;
    }

    public boolean isBlockDig() {
        return isReceiving() && packetId == PacketType.Play.Client.BLOCK_DIG;
    }

    public boolean isWindowClick() { return isReceiving() && packetId == PacketType.Play.Client.WINDOW_CLICK; }

    public boolean isEntityAction() {
        return isReceiving() && packetId == PacketType.Play.Client.ENTITY_ACTION;
    }

    public boolean isPosLook() {
        return isReceiving() && packetId == PacketType.Play.Client.POSITION_LOOK;
    }

    public boolean isCloseWindow() { return isReceiving() && packetId == PacketType.Play.Client.CLOSE_WINDOW; }

    public boolean isIncomingKeepAlive() { return isReceiving() && packetId == PacketType.Play.Client.KEEP_ALIVE; }

    public boolean isOutgoingKeepAlive() { return isSending() && packetId == PacketType.Play.Server.KEEP_ALIVE; }

    public boolean isSteerVehicle() {
        return isReceiving() && packetId == PacketType.Play.Client.STEER_VEHICLE;
    }

    public boolean isIncomingHeldItemSlot() {
        return isReceiving() && packetId == PacketType.Play.Client.HELD_ITEM_SLOT;
    }

    public boolean isOutgoingHeldItemSlot() {
        return isSending() && packetId == PacketType.Play.Server.HELD_ITEM_SLOT;
    }

    public boolean isClientCommand() {
        return isReceiving() && packetId == PacketType.Play.Client.CLIENT_COMMAND;
    }

    public boolean isCustomPayload() { return isReceiving() && packetId == PacketType.Play.Client.CUSTOM_PAYLOAD; }

    public boolean isIncomingTransaction () {
        return isReceiving() && packetId == PacketType.Play.Client.TRANSACTION;
    }

    public boolean isOutgoingTransaction() {
        return isSending() && packetId == PacketType.Play.Server.TRANSACTION;
    }

    public boolean isTeleport() { return isSending() && packetId == PacketType.Play.Server.POSITION; }

    public boolean isVelocity() {
        return isSending() && packetId == PacketType.Play.Server.ENTITY_VELOCITY;
    }

    public boolean isSpawnEntity() {
        return isSending() && packetId == PacketType.Play.Server.NAMED_ENTITY_SPAWN;
    }

    public boolean isEntityRem() {
        return isSending() && packetId == PacketType.Play.Server.ENTITY_DESTROY;
    }

    public boolean isEntityTeleport() {
        return isSending() && packetId == PacketType.Play.Server.ENTITY_TELEPORT;
    }
    public boolean isRelEntityMove() {
        return isSending() && packetId == PacketType.Play.Server.REL_ENTITY_MOVE;
    }

    public boolean isChat() {
        return isReceiving() && packetId == PacketType.Play.Client.CHAT;
    }

    public enum Direction { SEND, RECEIVE }
}
