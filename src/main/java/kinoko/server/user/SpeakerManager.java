package kinoko.server.user;

import kinoko.packet.world.WvsContext;
import kinoko.server.ServerConfig;
import kinoko.server.node.ClientStorage;
import kinoko.server.node.ServerExecutor;
import kinoko.server.packet.OutPacket;
import kinoko.world.user.User;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public final class SpeakerManager {
    private final Lock lock = new ReentrantLock();
    private final Map<Integer, Instant> speakerCooltimes = new HashMap<>(); // character id -> expire time
    private final List<OutPacket> broadcastQueue = new ArrayList<>();
    private ClientStorage clientStorage;
    private ScheduledFuture<?> broadcastSchedule;
    private Instant nextBroadcastTime = Instant.MAX;

    public void initialize(ClientStorage clientStorage) {
        this.clientStorage = clientStorage;
        this.broadcastSchedule = ServerExecutor.scheduleServiceWithFixedDelay(this::update, 1, 1, TimeUnit.SECONDS);
    }

    public void shutdown() {
        broadcastSchedule.cancel(true);
    }

    public boolean canSubmitAvatarSpeaker() {
        lock.lock();
        try {
            return broadcastQueue.size() <= 3; // queue longer than 15 seconds
        } finally {
            lock.unlock();
        }
    }

    public boolean canSubmitWorldSpeaker(int characterId) {
        lock.lock();
        try {
            return speakerCooltimes.getOrDefault(characterId, Instant.MIN).isBefore(Instant.now());
        } finally {
            lock.unlock();
        }
    }

    public void registerWorldSpeaker(int characterId, boolean avatar, OutPacket outPacket) {
        lock.lock();
        try {
            speakerCooltimes.put(characterId, Instant.now().plus(ServerConfig.WORLD_SPEAKER_COOLTIME, ChronoUnit.SECONDS));
            if (avatar) {
                if (nextBroadcastTime == Instant.MAX) {
                    // No queued broadcasts
                    broadcastPacket(outPacket);
                    nextBroadcastTime = Instant.now().plus(5, ChronoUnit.SECONDS); // clear or update in 5 seconds
                } else {
                    // Queue broadcast
                    broadcastQueue.add(outPacket);
                }
            } else {
                broadcastPacket(outPacket);
            }
        } finally {
            lock.unlock();
        }
    }

    private void update() {
        lock.lock();
        try {
            final Instant now = Instant.now();
            if (now.isBefore(nextBroadcastTime)) {
                return;
            }
            if (broadcastQueue.isEmpty()) {
                broadcastPacket(WvsContext.avatarMegaphoneClearMessage());
                nextBroadcastTime = Instant.MAX;
            } else {
                broadcastPacket(broadcastQueue.removeFirst());
                nextBroadcastTime = now.plus(5, ChronoUnit.SECONDS); // clear or update in 5 seconds
            }
        } finally {
            lock.unlock();
        }
    }

    private void broadcastPacket(OutPacket outPacket) {
        for (User user : clientStorage.getConnectedUsers()) {
            user.write(outPacket);
        }
    }
}
