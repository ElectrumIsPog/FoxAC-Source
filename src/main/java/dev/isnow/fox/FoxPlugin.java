
package dev.isnow.fox;

import org.bukkit.plugin.java.JavaPlugin;

public final class FoxPlugin extends JavaPlugin {

    @Override
    public void onLoad() {
        Fox.INSTANCE.load(this);
    }

    @Override
    public void onEnable() {
        Fox.INSTANCE.start(this);
    }

    @Override
    public void onDisable() {
        Fox.INSTANCE.stop(this);
    }

}
