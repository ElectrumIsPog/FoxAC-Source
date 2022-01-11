package dev.isnow.fox.check.impl.movement.motion;

import dev.isnow.fox.check.Check;
import dev.isnow.fox.check.api.CheckInfo;
import dev.isnow.fox.data.PlayerData;
import dev.isnow.fox.packet.Packet;
import io.github.retrooper.packetevents.PacketEvents;
import io.github.retrooper.packetevents.packetwrappers.play.in.helditemslot.WrappedPacketInHeldItemSlot;
import io.github.retrooper.packetevents.packetwrappers.play.out.helditemslot.WrappedPacketOutHeldItemSlot;

@CheckInfo(name = "Motion", type = "F", description = "Checks if player isn't slowing down while blocking.")
public class MotionF extends Check {
    public MotionF(PlayerData data) {
        super(data);
    }

    private int lastSlot;
    private int savedSlot;
    private boolean nextTick;

    @Override
    public void handle(Packet packet) {
        if (packet.isFlyingType() && nextTick) {
            nextTick = false;
            WrappedPacketOutHeldItemSlot slotChange = new WrappedPacketOutHeldItemSlot(savedSlot);
            PacketEvents.getAPI().getPlayerUtils().sendPacket(data.getPlayer(), slotChange);
        }
        if (packet.isSlot())
            lastSlot = new WrappedPacketInHeldItemSlot(packet.getRawPacket()).getCurrentSelectedSlot();
        if (packet.isPosition()) {
            if (data.getActionProcessor().isSprinting() && data.getActionProcessor().isBlocking()) {
                data.getActionProcessor().setBlocking(false);
                savedSlot = lastSlot;
                WrappedPacketOutHeldItemSlot slotChange = new WrappedPacketOutHeldItemSlot(getNewSlot());
                PacketEvents.getAPI().getPlayerUtils().sendPacket(data.getPlayer(), slotChange);
                nextTick = true;
            }
        }
    }

    private int getNewSlot() {
        if (lastSlot == 8)
            return 0;
        return lastSlot + 1;
    }

}