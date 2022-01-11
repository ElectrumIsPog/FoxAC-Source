package dev.isnow.fox.manager;

import dev.isnow.fox.check.Check;
import dev.isnow.fox.check.impl.combat.aim.*;
import dev.isnow.fox.check.impl.combat.aura.*;
import dev.isnow.fox.check.impl.combat.autoclicker.*;
import dev.isnow.fox.check.impl.combat.entity.EntityA;
import dev.isnow.fox.check.impl.combat.hitbox.HitBoxA;
import dev.isnow.fox.check.impl.combat.hitbox.HitBoxB;
import dev.isnow.fox.check.impl.combat.reach.ReachA;
import dev.isnow.fox.check.impl.combat.reach.ReachB;
import dev.isnow.fox.check.impl.combat.velocity.VelocityA;
import dev.isnow.fox.check.impl.combat.velocity.VelocityB;
import dev.isnow.fox.check.impl.movement.flight.*;
import dev.isnow.fox.check.impl.movement.ghostblock.GhostBlockA;
import dev.isnow.fox.check.impl.movement.motion.*;
import dev.isnow.fox.check.impl.movement.speed.SpeedA;
import dev.isnow.fox.check.impl.movement.speed.SpeedB;
import dev.isnow.fox.check.impl.movement.speed.SpeedC;
import dev.isnow.fox.check.impl.movement.speed.SpeedD;
import dev.isnow.fox.check.impl.player.badpackets.*;
import dev.isnow.fox.check.impl.player.breaker.BreakerA;
import dev.isnow.fox.check.impl.player.crasher.CrasherA;
import dev.isnow.fox.check.impl.player.crasher.CrasherB;
import dev.isnow.fox.check.impl.player.crasher.CrasherC;
import dev.isnow.fox.check.impl.player.crasher.CrasherD;
import dev.isnow.fox.check.impl.player.fastplace.FastPlaceA;
import dev.isnow.fox.check.impl.player.ground.GroundA;
import dev.isnow.fox.check.impl.player.ground.GroundB;
import dev.isnow.fox.check.impl.player.ground.GroundC;
import dev.isnow.fox.check.impl.player.inventory.InventoryA;
import dev.isnow.fox.check.impl.player.inventory.InventoryB;
import dev.isnow.fox.check.impl.player.inventory.InventoryC;
import dev.isnow.fox.check.impl.player.inventory.InventoryD;
import dev.isnow.fox.check.impl.player.payload.PayloadA;
import dev.isnow.fox.check.impl.player.payload.PayloadB;
import dev.isnow.fox.check.impl.player.timer.TimerA;
import dev.isnow.fox.check.impl.player.timer.TimerB;
import dev.isnow.fox.check.impl.player.timer.TimerC;
import dev.isnow.fox.config.Config;
import dev.isnow.fox.data.PlayerData;
import org.bukkit.Bukkit;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class CheckManager {

    public static final Class<?>[] CHECKS = new Class[]{
            AimA.class,
            AimB.class,
            AimC.class,
            AimD.class,
            AimE.class,
            AimF.class,
            AimG.class,
            AimH.class,
            AimI.class,
            AimJ.class,
            AimK.class,
            AimL.class,
            AimM.class,
            AimN.class,
            AimO.class,
            AimP.class,
            AimQ.class,
            AimR.class,
            AimS.class,
            AuraA.class,
            AuraB.class,
            AuraC.class,
            AuraD.class,
            AuraE.class,
            AuraF.class,
            AuraG.class,
            AuraH.class,
            AuraI.class,
            AuraJ.class,
            AuraK.class,
            AuraL.class,
            AuraM.class,
            AuraN.class,
            AuraO.class,
            AuraP.class,
            AuraQ.class,
            AuraR.class,
            AuraS.class,
            AuraT.class,
            AuraU.class,
            AuraV.class,
            AuraW.class,
            EntityA.class,
            AutoClickerA.class,
            AutoClickerB.class,
            AutoClickerC.class,
            AutoClickerD.class,
            AutoClickerE.class,
            AutoClickerF.class,
            AutoClickerG.class,
            AutoClickerH.class,
            AutoClickerI.class,
            AutoClickerJ.class,
            AutoClickerK.class,
            AutoClickerL.class,
            AutoClickerM.class,
            ReachA.class,
            ReachB.class,
            VelocityA.class,
            VelocityB.class,
            HitBoxA.class,
            HitBoxB.class,
            SpeedA.class,
            SpeedB.class,
            SpeedC.class,
            SpeedD.class,
            FlightA.class,
            FlightB.class,
            FlightC.class,
            FlightD.class,
            FlightE.class,
            MotionA.class,
            MotionB.class,
            MotionC.class,
            MotionD.class,
            MotionE.class,
            MotionF.class,
            MotionG.class,
            GhostBlockA.class,
            InventoryA.class,
            InventoryB.class,
            InventoryC.class,
            InventoryD.class,
            FastPlaceA.class,
            BreakerA.class,
            BadPacketsA.class,
            BadPacketsB.class,
            BadPacketsC.class,
            BadPacketsD.class,
            BadPacketsE.class,
            BadPacketsF.class,
            BadPacketsG.class,
            BadPacketsH.class,
            BadPacketsI.class,
            BadPacketsJ.class,
            BadPacketsK.class,
            BadPacketsL.class,
            BadPacketsM.class,
            TimerA.class,
            TimerB.class,
            TimerC.class,
            GroundA.class,
            GroundB.class,
            GroundC.class,
            PayloadA.class,
            PayloadB.class,
            CrasherA.class,
            CrasherB.class,
            CrasherC.class,
            CrasherD.class,
    };

    private static final List<Constructor<?>> CONSTRUCTORSALL = new ArrayList<>();

    public static List<Check> allChecks;

    public static List<Check> loadChecks(final PlayerData data) {
        final List<Check> checkList = new ArrayList<>();
        for (final Constructor<?> constructor : CONSTRUCTORSALL) {
            try {
                Check check = (Check) constructor.newInstance(data);
                check.setPunishCommands((ArrayList<String>) Config.PUNISH_COMMANDS.get(constructor.getClass().getSimpleName()));
                check.setEnabled(Config.ENABLED_CHECKS.stream().anyMatch(s -> s.equals(check.getClass().getSimpleName())));
                try {
                    check.setMaxVl(Config.MAX_VIOLATIONS.get(constructor.getClass().getSimpleName()));
                } catch(NullPointerException e) {
                    check.setMaxVl(50);
                }
                checkList.add(check);
            } catch (final Exception exception) {
                System.err.println("Failed to load checks for " + data.getPlayer().getName());
                exception.printStackTrace();
            }
        }
        allChecks = checkList;
        return checkList;
    }

    public static Map<Check, Integer> loadChecksMap(final PlayerData data, List<Check> checks) {
        final Map<Check, Integer> checkList = new HashMap<>();
        for (final Constructor<?> constructor : CONSTRUCTORSALL) {
            try {
                if(checks.stream().anyMatch(check -> check.getFullName().equals(constructor.getName()))) {
                    Check check = checks.stream().filter(check1 -> check1.getFullName().equals(constructor.getName())).findFirst().get();
                    checkList.put(check, 0);
                }
            } catch (final Exception exception) {
                System.err.println("Failed to load checks for " + data.getPlayer().getName());
                exception.printStackTrace();
            }
        }
        return checkList;
    }
    public static void setup() {
        for (final Class<?> clazz : CHECKS) {
            if (Config.ENABLED_CHECKS.contains(clazz.getSimpleName())) {
                try {
                    CONSTRUCTORSALL.add(clazz.getConstructor(PlayerData.class));
                    Bukkit.getLogger().info(clazz.getSimpleName() + " is enabled!");
                } catch (final NoSuchMethodException exception) {
                    exception.printStackTrace();
                }
            } else {
                try {
                    CONSTRUCTORSALL.add(clazz.getConstructor(PlayerData.class));
                    Bukkit.getLogger().info(clazz.getSimpleName() + " is disabled!");
                } catch (final NoSuchMethodException exception) {
                    exception.printStackTrace();
                }
            }
        }
    }
}

