package dev.isnow.fox;

import com.google.common.base.Charsets;
import com.google.common.io.ByteStreams;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import dev.isnow.fox.command.CommandManager;
import dev.isnow.fox.command.VanishCommand;
import dev.isnow.fox.command.impl.Alerts;
import dev.isnow.fox.config.Config;
import dev.isnow.fox.gui.GuiManager;
import dev.isnow.fox.listener.bukkit.BukkitEventManager;
import dev.isnow.fox.listener.bukkit.RegistrationListener;
import dev.isnow.fox.listener.packet.NetworkManager;
import dev.isnow.fox.manager.*;
import dev.isnow.fox.packet.processor.ReceivingPacketProcessor;
import dev.isnow.fox.packet.processor.SendingPacketProcessor;
import dev.isnow.fox.update.UpdateChecker;
import io.github.retrooper.packetevents.PacketEvents;
import io.github.retrooper.packetevents.utils.server.ServerVersion;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.io.IOUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.PluginAwareness;
import org.bukkit.plugin.messaging.Messenger;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.Charset;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Getter
public enum Fox {

    INSTANCE;

    private FoxPlugin plugin;

    @Setter
    private YamlConfiguration yaml;

    private long startTime;
    private final TickManager tickManager = new TickManager();
    private final ReceivingPacketProcessor receivingPacketProcessor = new ReceivingPacketProcessor();
    private final SendingPacketProcessor sendingPacketProcessor = new SendingPacketProcessor();

    private final ExecutorService executorService = Executors.newSingleThreadExecutor();
    private final CommandManager commandManager = new CommandManager(this.getPlugin());
    private final VanishCommand vanishCommand = new VanishCommand();

    private final String version = "B11";
    private final UpdateChecker updateChecker = new UpdateChecker();
    private GuiManager guiManager;

    boolean fullyLoaded = false;

    public void load(final FoxPlugin plugin) {
        this.plugin = plugin;
        assert plugin != null : "Error while starting Fox.";



        getPlugin().saveDefaultConfig();
        File checks = new File(getPlugin().getDataFolder(), "checks.yml");
        if(!checks.exists()) {
            getPlugin().saveResource("checks.yml", false);
            checks = new File(getPlugin().getDataFolder(), "checks.yml");
        }
        yaml = YamlConfiguration.loadConfiguration(checks);
        Config.updateConfig();

        try {
            Bukkit.getConsoleSender().sendMessage(ChatColor.GREEN + "License check passed, Welcome " + jsonObject.get("username") + "!");
            fullyLoaded = true;
        } catch (MalformedURLException e) {
            e.printStackTrace();
            Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "FoxAC Couldn't connect to license server, DNS Error?");
            Bukkit.getPluginManager().disablePlugin(getPlugin());
        } catch (IOException e) {
            e.printStackTrace();
            Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "FoxAC Couldn't validate your key, Invalid Key?");
            Bukkit.shutdown();
            Bukkit.getPluginManager().disablePlugin(getPlugin());
        }

        setupPacketEvents();
    }

    public void start(final FoxPlugin plugin) {
        runPacketEvents();

        if(fullyLoaded) {
            CheckManager.setup();
            Bukkit.getOnlinePlayers().forEach(player -> PlayerDataManager.getInstance().add(player));

            guiManager = new GuiManager();
            getPlugin().getCommand("fox").setExecutor(commandManager);
            getPlugin().getCommand("alerts").setExecutor(new Alerts());
            getPlugin().getCommand("vanish").setExecutor(vanishCommand);

            tickManager.start();

            new AFKManager();

            final Messenger messenger = Bukkit.getMessenger();
            messenger.registerIncomingPluginChannel(plugin, "MC|Brand", new ClientBrandListener());

            startTime = System.currentTimeMillis();

            registerEvents();
        }

    }

    public void stop(final FoxPlugin plugin) {
        this.plugin = plugin;
        assert plugin != null : "Error while shutting down fox.";

        tickManager.stop();

        stopPacketEvents();
        Bukkit.getScheduler().cancelAllTasks();
    }

    private void setupPacketEvents() {
        PacketEvents.create(plugin).getSettings()
                .fallbackServerVersion(ServerVersion.v_1_8_8);

        PacketEvents.get().load();
    }

    private void runPacketEvents() {
        PacketEvents.get().init();
    }

    private void stopPacketEvents() {
        PacketEvents.get().terminate();
    }

    private void registerEvents() {
        Bukkit.getServer().getPluginManager().registerEvents(new RegistrationListener(), plugin);
        Bukkit.getServer().getPluginManager().registerEvents(new BukkitEventManager(), plugin);
        Bukkit.getServer().getPluginManager().registerEvents(new ClientBrandListener(), plugin);
        Bukkit.getServer().getPluginManager().registerEvents(new GuiManager(), plugin);
        PacketEvents.get().getEventManager().registerListener(new NetworkManager());
    }

    public InputStream getResource(String filename) {
        if (filename == null) {
            throw new IllegalArgumentException("Filename cannot be null");
        }

        try {
            URL url = Fox.INSTANCE.getPlugin().getClass().getClassLoader().getResource(filename);

            if (url == null) {
                return null;
            }

            URLConnection connection = url.openConnection();
            connection.setUseCaches(false);
            return connection.getInputStream();
        } catch (IOException ex) {
            return null;
        }
    }

    public void saveConfig() {
        try {
            yaml.save(new File(getPlugin().getDataFolder(), "checks.yml"));
        } catch (IOException e) {

        }
    }

    public void reloadConfig() {
        yaml = YamlConfiguration.loadConfiguration(new File(Fox.INSTANCE.getPlugin().getDataFolder(), "checks.yml"));

        final InputStream defConfigStream = getResource("config.yml");
        if (defConfigStream == null) {
            return;
        }

        final YamlConfiguration defConfig;
        if (getPlugin().getDescription().getAwareness().contains(PluginAwareness.Flags.UTF8) || FileConfiguration.UTF8_OVERRIDE) {
            defConfig = YamlConfiguration.loadConfiguration(new InputStreamReader(defConfigStream, Charsets.UTF_8));
        } else {
            final byte[] contents;
            defConfig = new YamlConfiguration();
            try {
                contents = ByteStreams.toByteArray(defConfigStream);
            } catch (final IOException e) {
                return;
            }

            final String text = new String(contents, Charset.defaultCharset());
            if (!text.equals(new String(contents, Charsets.UTF_8))) {
            }

            try {
                defConfig.loadFromString(text);
            } catch (final InvalidConfigurationException e) {
            }
        }

        yaml.setDefaults(defConfig);
    }

}
