package dev.isnow.fox.config;

import dev.isnow.fox.Fox;
import dev.isnow.fox.check.api.CheckInfo;
import dev.isnow.fox.data.processor.GhostBlockProcessor;
import dev.isnow.fox.manager.CheckManager;
import dev.isnow.fox.util.ColorUtil;
import lombok.experimental.UtilityClass;
import org.bukkit.Bukkit;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@UtilityClass
public final class Config {

    public String LOG_FORMAT, ALERT_FORMAT, PREFIX, CLIENT_KICK_MESSAGE, KEY, VPN_MESSAGE, KBTEST, ALERTSON, PAYLOADKICK, ANTICRASHKICKEDMESSAGE, ANTICRASHALERT, SCARE, ALERTSOFF, CLICKCOMMAND, VANISHON, VANISHOFF, BROADCASTMESSAGE, ALERT_URL, BAN_URL, DISCORDNAME, IMAGEDISCORD;

    public boolean LOGGING_ENABLED, VPN_ENABLED, STRICTAF_GHOSTBLOCK_MODE, SUS, API_ENABLED, GHOST_BLOCK_ENABLED, GLOBALCMD, WEBHOOK, DISCORDBAN, BANTIMER, BROADCASTBAN, CLIENT_ENABLED, CLIENT_CASE_SENSITIVE;

    public GhostBlockProcessor.Mode GHOST_BLOCK_MODE;

    public int GHOST_BLOCK_MAX_PING, BANTIMERTIME;

    public List<String> ENABLED_CHECKS = new ArrayList<>();
    public List<String> SETBACK_CHECKS = new ArrayList<>();
    public Map<String, Integer> MAX_VIOLATIONS = new HashMap<>();
    public Map<String, List<String>> PUNISH_COMMANDS = new HashMap<>();

    public List<String> GLOBAL_COMMANDS, TIMER_COMMANDS, BLOCKED_CLIENTS;

    public void updateConfig() {
        try {
            LOGGING_ENABLED = getBoolean("alerts.log");
            LOG_FORMAT = getString("alerts.log-format");

            ALERT_FORMAT = getString("alerts.message");

            ANTICRASHKICKEDMESSAGE = getString("messages.anticrash-kick");
            ANTICRASHALERT = getString("messages.antiexploit-alert");
            PAYLOADKICK = getString("messages.payload-kick");

            Config.WEBHOOK = getBoolean("discord.enabled");
            ALERT_URL = getString("discord.url");
            BAN_URL = getString("discord.banurl");
            DISCORDNAME = getString("discord.name");
            IMAGEDISCORD = getString("discord.image");
            DISCORDBAN = getBoolean("discord.ban-message");

            KBTEST = getString("messages.kb-test");
            SCARE = getString("messages.scare-cmd");

            VANISHON = getString("messages.vanish-on");
            VANISHOFF = getString("messages.vanish-off");

            KEY = getString("api.key");

            GHOST_BLOCK_ENABLED = getBoolean("ghost-block.enabled");
            GHOST_BLOCK_MAX_PING = getInteger("ghost-block.max-ms");
            STRICTAF_GHOSTBLOCK_MODE = getBoolean("ghost-block.strict");
            GHOST_BLOCK_MODE = GhostBlockProcessor.Mode.valueOf(getString("ghost-block.mode"));

            SUS = getBoolean("bans.sus");

            GLOBALCMD = getBoolean("bans.enabled");
            GLOBAL_COMMANDS = getList("bans.first-commands");
            BANTIMER = getBoolean("bans.timer");
            BANTIMERTIME = getInteger("bans.time");
            TIMER_COMMANDS = getList("bans.timer-commands");

            CLIENT_ENABLED = getBoolean("clients.enabled");
            CLIENT_CASE_SENSITIVE = getBoolean("clients.sensitive");
            BLOCKED_CLIENTS = getList("clients.blocked");
            CLIENT_KICK_MESSAGE = getString("messages.client-kick");

            PREFIX = getString("messages.prefix");

            BROADCASTBAN = getBoolean("broadcast.enabled");
            BROADCASTMESSAGE = getString("broadcast.message");

            CLICKCOMMAND = getString("alerts.click-command");

            ALERTSON = getString("messages.alerts-on");
            ALERTSOFF = getString("messages.alerts-off");

            VPN_ENABLED = getBoolean("settings.vpn");
            VPN_MESSAGE = ColorUtil.translate(getString("messages.vpn-kick").replaceAll("%nl%", "\n"));

            for (final Class<?> check : CheckManager.CHECKS) {
                final CheckInfo checkInfo = check.getAnnotation(CheckInfo.class);

                String checkType = "";

                if (check.getName().contains("combat")) {
                    checkType = "combat";
                } else if (check.getName().contains("movement")) {
                    checkType = "movement";
                } else if (check.getName().contains("player")) {
                    checkType = "player";
                }

                for (final Field field : check.getDeclaredFields()) {
                    if (field.getType().equals(ConfigValue.class)) {
                        final boolean accessible = field.isAccessible();
                        field.setAccessible(true);

                        final String name = ((ConfigValue) field.get(null)).getName();
                        final ConfigValue value = ((ConfigValue) field.get(null));
                        final ConfigValue.ValueType type = value.getType();

                        switch (type) {
                            case BOOLEAN:
                                value.setValue(getBooleanChecks("checks." + checkType + "." + checkInfo.name().toLowerCase() + "." + checkInfo.type() + "." + name));
                                break;
                            case INTEGER:
                                value.setValue(getIntegerChecks("checks." + checkType + "." + checkInfo.name().toLowerCase() + "." + checkInfo.type() + "." + name));
                                break;
                            case DOUBLE:
                                value.setValue(getDoubleChecks("checks." + checkType + "." + checkInfo.name().toLowerCase() + "." + checkInfo.type() + "." + name));
                                break;
                            case STRING:
                                value.setValue(getStringChecks("checks." + checkType + "." + checkInfo.name().toLowerCase() + "." + checkInfo.type() + "." + name));
                                break;
                            case LONG:
                                value.setValue(getLongChecks("checks." + checkType + "." + checkInfo.name().toLowerCase() + "." + checkInfo.type() + "." + name));
                                break;
                        }

                        field.setAccessible(accessible);
                    }
                }

                final boolean enabled = getBooleanChecks("checks." + checkType + "." + checkInfo.name().toLowerCase() + "." + checkInfo.type().toLowerCase() + ".enabled");

                final int maxViolations = getIntegerChecks("checks." + checkType.toLowerCase() + "." + checkInfo.name().toLowerCase() + "." + checkInfo.type().toLowerCase() + ".max-violations");
                final List<String> punishCommand = getListChecks("checks." + checkType + "." + checkInfo.name().toLowerCase() + "." + checkInfo.type().toLowerCase() + ".punish-commands");
                if (checkType.equals("movement")) {
                    final boolean setBack = getBooleanChecks("checks.movement." + checkInfo.name().toLowerCase() + "." + checkInfo.type() + ".setback");

                    if (setBack) {
                        SETBACK_CHECKS.add(check.getSimpleName());
                    }
                }

                if (enabled) {
                    ENABLED_CHECKS.add(check.getSimpleName());
                }
                PUNISH_COMMANDS.put(check.getSimpleName(), punishCommand);
                if (maxViolations == 0) {
                    return;
                }
                MAX_VIOLATIONS.put(check.getSimpleName(), maxViolations);
            }
        } catch (final Exception exception) {
            Bukkit.getLogger().severe("Could not properly load config.");
            exception.printStackTrace();
        }

    }

    private boolean getBoolean(final String string) {
        return Fox.INSTANCE.getPlugin().getConfig().getBoolean(string);
    }

    private boolean getBooleanChecks(final String string) {
        return Fox.INSTANCE.getYaml().getBoolean(string);
    }

    public String getString(final String string) {
        return Fox.INSTANCE.getPlugin().getConfig().getString(string);
    }

    public String getStringChecks(final String string) {
        return Fox.INSTANCE.getYaml().getString(string);
    }

    private int getInteger(final String string) {
        return Fox.INSTANCE.getPlugin().getConfig().getInt(string);
    }

    private int getIntegerChecks(final String string) {
        return Fox.INSTANCE.getYaml().getInt(string);
    }

    private double getDouble(final String string) {
        return Fox.INSTANCE.getPlugin().getConfig().getDouble(string);
    }

    private double getDoubleChecks(final String string) {
        return Fox.INSTANCE.getYaml().getDouble(string);
    }

    private long getLong(final String string) {
        return Fox.INSTANCE.getPlugin().getConfig().getLong(string);
    }

    private long getLongChecks(final String string) {
        return Fox.INSTANCE.getYaml().getLong(string);
    }

    private List<String> getList(final String string) {
        return Fox.INSTANCE.getPlugin().getConfig().getStringList(string);
    }

    private List<String> getListChecks(final String string) {
        return Fox.INSTANCE.getYaml().getStringList(string);
    }
}
