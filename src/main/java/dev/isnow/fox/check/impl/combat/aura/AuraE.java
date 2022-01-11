package dev.isnow.fox.check.impl.combat.aura;

import dev.isnow.fox.check.Check;
import dev.isnow.fox.check.api.CheckInfo;
import dev.isnow.fox.data.PlayerData;
import dev.isnow.fox.packet.Packet;
import dev.isnow.fox.util.type.EvictingList;

@CheckInfo(name = "Aura", type = "E", description = "Invalid BLOCK PLACE order")
public class AuraE extends Check {
    public AuraE(PlayerData data) {
        super(data);
    }

    private final EvictingList<Packet> packetOrder = new EvictingList<>(3);

    @Override
    public void handle(Packet packet) {
        if (packet.isFlyingType() || packet.isBlockPlace() || packet.isTransaction()) {
            packetOrder.add(packet);

            if (packetOrder.size() == 3) {

                boolean flag = packetOrder.get(2).isTransaction() &&
                        packetOrder.get(1).isBlockPlace() &&
                        packetOrder.get(0).isFlyingType();

                if (flag)
                    fail();
            }

        }
    }
}