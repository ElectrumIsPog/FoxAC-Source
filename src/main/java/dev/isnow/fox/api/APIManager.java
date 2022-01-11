

package dev.isnow.fox.api;

import dev.isnow.fox.Fox;
import dev.isnow.fox.api.impl.FoxFlagEvent;
import dev.isnow.fox.api.impl.FoxPunishEvent;
import dev.isnow.fox.check.Check;
import dev.isnow.fox.config.Config;
import org.bukkit.Bukkit;

public final class APIManager {
    public static void callFlagEvent(final Check check) {
        if (!Config.API_ENABLED) return;

        final FoxFlagEvent flagEvent = new FoxFlagEvent(
                check.getData().getPlayer(),
                check.getCheckInfo().name(),
                check.getCheckInfo().type(),
                check.getVl(),
                check.getBuffer()
        );

        Bukkit.getScheduler().runTask(Fox.INSTANCE.getPlugin(), () -> Bukkit.getPluginManager().callEvent(flagEvent));
    }

    public static void callPunishEvent(final Check check) {
        if (!Config.API_ENABLED) return;
        final FoxPunishEvent punishEvent = new FoxPunishEvent(
                check.getData().getPlayer(),
                check.getCheckInfo().name(),
                check.getCheckInfo().type(),
                check.getPunishCommands(),
                check.getVl(),
                check.getBuffer()
        );

        Bukkit.getScheduler().runTask(Fox.INSTANCE.getPlugin(), () -> Bukkit.getPluginManager().callEvent(punishEvent));
    }
}
