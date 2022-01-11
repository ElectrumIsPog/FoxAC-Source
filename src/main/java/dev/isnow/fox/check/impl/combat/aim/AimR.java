package dev.isnow.fox.check.impl.combat.aim;

import dev.isnow.fox.check.Check;
import dev.isnow.fox.check.api.CheckInfo;
import dev.isnow.fox.data.PlayerData;
import dev.isnow.fox.packet.Packet;
import dev.isnow.fox.util.MathUtil;

import java.util.ArrayList;
import java.util.List;

@CheckInfo(name = "Aim", description = "Checks for invalid yaw movements.", type = "R")
public class AimR extends Check {

    private double lastSTD;
    private double lastDeltaYaw;
    private final List<Double> deltaYawList = new ArrayList<>();

    public AimR(PlayerData data) {
        super(data);
    }

    @Override
    public void handle(Packet packet) {
        if(packet.isPosition() && packet.isUseEntity()) {
            double yaw = MathUtil.wrapAngleTo180_float(data.getRotationProcessor().getYaw());

            if (yaw > 150.0) {
                deltaYawList.add(yaw);

                if (deltaYawList.size() >= 25) {
                    double std = MathUtil.getStandardDeviation(deltaYawList);


                    if (std < 0.03 || Math.abs(std - lastSTD) < 0.001 && yaw > 155.0) {
                        fail("STD: " + std + " size:" + deltaYawList.size() + " yaw:" + yaw);
                    }


                    lastSTD = std;
                    deltaYawList.clear();
                }
            }
            lastDeltaYaw = yaw;
        }
    }
}