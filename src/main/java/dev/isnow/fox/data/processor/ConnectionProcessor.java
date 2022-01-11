

package dev.isnow.fox.data.processor;

import dev.isnow.fox.data.PlayerData;
import dev.isnow.fox.util.type.Pair;
import io.github.retrooper.packetevents.PacketEvents;
import io.github.retrooper.packetevents.packetwrappers.play.out.transaction.WrappedPacketOutTransaction;
import io.github.retrooper.packetevents.utils.list.ConcurrentList;
import lombok.Getter;

import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicInteger;

@Getter
public final class ConnectionProcessor {

    private final PlayerData data;

    public ConnectionProcessor(PlayerData data) {
        this.data = data;
    }

    public AtomicInteger packetLastTransactionReceived = new AtomicInteger(0);
    public AtomicInteger lastTransactionSent = new AtomicInteger(0);
    public final ConcurrentList<Short> didWeSendThatTrans = new ConcurrentList<>();
    private final AtomicInteger transactionIDCounter = new AtomicInteger(0);

    private int transactionPing = 0;
    private long playerClockAtLeast = 0;

    public final ConcurrentLinkedQueue<Pair<Short, Long>> transactionsSent = new ConcurrentLinkedQueue<>();
    private final ConcurrentLinkedQueue<Pair<Integer, Runnable>> nettySyncTransactionMap = new ConcurrentLinkedQueue<>();


    public boolean addTransactionResponse(short id) {

        Pair<Short, Long> data = null;
        boolean hasID = false;
        for (Pair<Short, Long> iterator : transactionsSent) {
            if (iterator.getX() == id) {
                hasID = true;
                break;
            }

        }

        if (hasID) {
            do {
                data = transactionsSent.poll();
                if (data == null)
                    break;

                int incrementingID = packetLastTransactionReceived.incrementAndGet();
                transactionPing = (int) (System.nanoTime() - data.getY());
                playerClockAtLeast = data.getY();

                handleNettySyncTransaction(incrementingID);
            } while (data.getX() != id);
        }

        // Were we the ones who sent the packet?
        return data != null && data.getX() == id;
    }

    public short getNextTransactionID(int add) {
        return (short) (-1 * (transactionIDCounter.getAndAdd(add) & 0x7FFF));
    }

    public int sendTransaction() {
        short transactionID = getNextTransactionID(1);
        try {
            addTransactionSend(transactionID);

            PacketEvents.get().getPlayerUtils().sendPacket(data.getPlayer(), new WrappedPacketOutTransaction(0, transactionID, false));
        } catch (Exception exception) {
            exception.printStackTrace();
        }
        return 0;
    }

    public void addTransactionSend(short id) {
        didWeSendThatTrans.add(id);
    }

    private void tickUpdates(ConcurrentLinkedQueue<Pair<Integer, Runnable>> map, int transaction) {
        Pair<Integer, Runnable> next = map.peek();
        while (next != null) {
            if (transaction < next.getX())
                break;

            map.poll();
            next.getY().run();
            next = map.peek();
        }
    }

    public void handleNettySyncTransaction(int transaction) {
        tickUpdates(nettySyncTransactionMap, transaction);
    }

    public void addRealTimeTask(int transaction, Runnable runnable) {
        nettySyncTransactionMap.add(new Pair<>(transaction, runnable));
    }
}
