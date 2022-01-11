

package dev.isnow.fox.data.processor;

import dev.isnow.fox.Fox;
import dev.isnow.fox.data.PlayerData;
import io.github.retrooper.packetevents.packetwrappers.play.in.blockdig.WrappedPacketInBlockDig;
import io.github.retrooper.packetevents.packetwrappers.play.in.clientcommand.WrappedPacketInClientCommand;
import io.github.retrooper.packetevents.packetwrappers.play.in.entityaction.WrappedPacketInEntityAction;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.event.player.PlayerInteractEvent;

@Getter
public final class ActionProcessor {

    private final PlayerData data;

    @Setter
    private boolean sprinting, sneaking, sendingAction, placing, digging, blocking,
            inventory, respawning, sendingDig, eating, bukkitPlacing, dropping;

    private int lastDiggingTick, lastPlaceTick, lastBreakTick, lastBukkitPlaceTick, lastDropTick;

    private int sinceSprintTicks;

    private int sprintingTicks, sneakingTicks, blockingTicks;

    public ActionProcessor(final PlayerData data) {
        this.data = data;
    }

    public void handleDrop() {
        dropping = true;
    }
    public void handleEntityAction(final WrappedPacketInEntityAction wrapper) {
        sendingAction = true;
        switch (wrapper.getAction()) {
            case START_SPRINTING:
                sprinting = true;
                break;
            case STOP_SPRINTING:
                sprinting = false;
                break;
            case START_SNEAKING:
                sneaking = true;
                break;
            case STOP_SNEAKING:
                sneaking = false;
                break;
        }
    }

    public void handleBukkitPlace() {
        bukkitPlacing = true;
    }
    public void handleBlockDig(final WrappedPacketInBlockDig wrapper) {
        sendingDig = true;
        switch (wrapper.getDigType()) {
            case START_DESTROY_BLOCK:
                digging = true;
                lastDiggingTick = Fox.INSTANCE.getTickManager().getTicks();
                break;
            case STOP_DESTROY_BLOCK:
            case ABORT_DESTROY_BLOCK:
                digging = false;
                break;
            case RELEASE_USE_ITEM:
                blocking = false;
                eating = false;
                break;
        }
    }

    public void handleClientCommand(final WrappedPacketInClientCommand wrapper) {
        switch (wrapper.getClientCommand()) {
            case OPEN_INVENTORY_ACHIEVEMENT:
                inventory = true;
                break;
            case PERFORM_RESPAWN:
                respawning = true;
                break;
        }
    }

    public void handleBlockPlace() {
        placing = true;

        if(blocking) {
            blocking = false;
            return;
        }
        if (data.getPlayer().getItemInHand().toString().contains("SWORD")) blocking = true;
        if (data.getPlayer().getItemInHand().getType().isEdible()) eating = true;
    }

    public void handleCloseWindow() {
        inventory = false;
    }

    public void handleInteract(final PlayerInteractEvent event) {
        switch (event.getAction()) {
            case LEFT_CLICK_BLOCK:
                digging = true;
                break;

            case RIGHT_CLICK_AIR:
            case RIGHT_CLICK_BLOCK:
                if (data.getPlayer().getItemInHand().getType().isEdible()) eating = true;
                break;
        }
    }

    public void handleArmAnimation() {
        if (digging) lastDiggingTick = Fox.INSTANCE.getTickManager().getTicks();
    }

    public void handleFlying() {
        if (!data.getPlayer().getItemInHand().toString().contains("SWORD")) blocking = false;
        if (!data.getPlayer().getItemInHand().getType().isEdible()) eating = false;

        if (digging) lastDiggingTick = Fox.INSTANCE.getTickManager().getTicks();
        if (placing) lastPlaceTick = Fox.INSTANCE.getTickManager().getTicks();
        if (digging) lastBreakTick = Fox.INSTANCE.getTickManager().getTicks();
        if (bukkitPlacing) lastBukkitPlaceTick = Fox.INSTANCE.getTickManager().getTicks();
        if (dropping) lastDropTick = Fox.INSTANCE.getTickManager().getTicks();
        if (sprinting) sinceSprintTicks = 0;
        else ++sinceSprintTicks;

        sendingAction = false;
        placing = false;
        respawning = false;
        bukkitPlacing = false;
        dropping = false;
        eating = false;

        if (sprinting) ++sprintingTicks;
        else sprintingTicks = 0;

        if (sneaking) ++sneakingTicks;
        else sneakingTicks = 0;

        if (blocking) ++blockingTicks;
        else blockingTicks = 0;
    }
}
