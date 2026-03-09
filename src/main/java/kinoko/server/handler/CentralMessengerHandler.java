package kinoko.server.handler;

import kinoko.packet.CentralPacket;
import kinoko.packet.field.MessengerPacket;
import kinoko.packet.world.BroadcastPacket;
import kinoko.server.messenger.Messenger;
import kinoko.server.messenger.MessengerRequest;
import kinoko.server.messenger.MessengerUser;
import kinoko.server.node.CentralServerNode;
import kinoko.server.node.RemoteServerNode;
import kinoko.server.packet.InPacket;
import kinoko.server.packet.OutPacket;
import kinoko.server.user.RemoteUser;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Optional;
import java.util.function.BiConsumer;

public final class CentralMessengerHandler {
    private static final Logger log = LogManager.getLogger(CentralMessengerHandler.class);
    private final CentralServerNode centralServerNode;

    public CentralMessengerHandler(CentralServerNode centralServerNode) {
        this.centralServerNode = centralServerNode;
    }

    public void handleMessengerRequest(RemoteServerNode remoteServerNode, InPacket inPacket) {
        final int characterId = inPacket.decodeInt();
        final MessengerRequest messengerRequest = MessengerRequest.decode(inPacket);
        // Resolve requester user
        final Optional<RemoteUser> remoteUserResult = centralServerNode.getUserByCharacterId(characterId);
        if (remoteUserResult.isEmpty()) {
            log.error("Failed to resolve user with character ID : {} for MessengerRequest", characterId);
            return;
        }
        final RemoteUser remoteUser = remoteUserResult.get();
        // Process request
        switch (messengerRequest.getRequestType()) {
            case MSMP_Enter -> {
                final int messengerId = messengerRequest.getMessengerId();
                final MessengerUser messengerUser = messengerRequest.getMessengerUser();
                // Check if already in messenger
                final Optional<Messenger> userMessengerResult = centralServerNode.getMessengerById(remoteUser.getMessengerId());
                if (userMessengerResult.isPresent()) {
                    log.error("Tried to enter messenger ID {} while already in messenger ID {}", messengerId, remoteUser.getMessengerId());
                    remoteServerNode.write(CentralPacket.userPacketReceive(remoteUser.getCharacterId(), BroadcastPacket.alert("This request has failed due to an unknown error.")));
                    return;
                }
                // Create messenger
                if (messengerId == 0) {
                    createMessenger(remoteServerNode, remoteUser, messengerUser);
                    return;
                }
                // Resolve messenger
                final Optional<Messenger> targetMessengerResult = centralServerNode.getMessengerById(messengerId);
                if (targetMessengerResult.isEmpty()) {
                    // Create messenger
                    createMessenger(remoteServerNode, remoteUser, messengerUser);
                    remoteServerNode.write(CentralPacket.userPacketReceive(remoteUser.getCharacterId(), MessengerPacket.selfEnterResult(-1))); // You have been unable to join the invited chat room.
                    return;
                }
                // Join messenger
                try (var lockedMessenger = targetMessengerResult.get().acquire()) {
                    final Messenger messenger = lockedMessenger.get();
                    if (!messenger.addUser(remoteUser, messengerUser)) {
                        // Create messenger
                        createMessenger(remoteServerNode, remoteUser, messengerUser);
                        remoteServerNode.write(CentralPacket.userPacketReceive(remoteUser.getCharacterId(), MessengerPacket.selfEnterResult(-1))); // You have been unable to join the invited chat room.
                        return;
                    }
                    remoteUser.setMessengerId(messenger.getMessengerId());
                    remoteServerNode.write(CentralPacket.messengerResult(remoteUser.getCharacterId(), messenger.getMessengerId()));
                    // Update users
                    final int userIndex = messenger.getUserIndex(remoteUser);
                    final OutPacket outPacket = MessengerPacket.enter(userIndex, messengerUser, true);
                    forEachMessengerUser(messenger, (user, node) -> {
                        if (user.getCharacterId() == remoteUser.getCharacterId()) {
                            node.write(CentralPacket.userPacketReceive(user.getCharacterId(), MessengerPacket.selfEnterResult(userIndex)));
                            for (var entry : messenger.getMessengerUsers().entrySet()) {
                                if (entry.getKey() != userIndex) {
                                    node.write(CentralPacket.userPacketReceive(user.getCharacterId(), MessengerPacket.enter(entry.getKey(), entry.getValue(), false)));
                                }
                            }
                        } else {
                            node.write(CentralPacket.userPacketReceive(user.getCharacterId(), outPacket));
                        }
                    });
                }
            }
            case MSMP_Leave -> {
                leaveMessenger(remoteUser);
                remoteUser.setMessengerId(0);
                remoteServerNode.write(CentralPacket.messengerResult(remoteUser.getCharacterId(), 0));
            }
            case MSMP_Chat -> {
                final String message = messengerRequest.getMessage();
                // Resolve messenger
                final Optional<Messenger> messengerResult = centralServerNode.getMessengerById(remoteUser.getMessengerId());
                if (messengerResult.isEmpty()) {
                    log.error("Could not resolve messenger for MSMP_Chat");
                    return;
                }
                // Update users
                try (var lockedMessenger = messengerResult.get().acquire()) {
                    final Messenger messenger = lockedMessenger.get();
                    final OutPacket outPacket = MessengerPacket.chat(message);
                    forEachMessengerUser(messenger, (user, node) -> {
                        if (user.getCharacterId() != remoteUser.getCharacterId()) {
                            node.write(CentralPacket.userPacketReceive(user.getCharacterId(), outPacket));
                        }
                    });
                }

            }
            case MSMP_Avatar -> {
                final MessengerUser messengerUser = messengerRequest.getMessengerUser();
                // Resolve messenger
                final Optional<Messenger> messengerResult = centralServerNode.getMessengerById(remoteUser.getMessengerId());
                if (messengerResult.isEmpty()) {
                    log.error("Could not resolve messenger for MSMP_Avatar");
                    return;
                }
                // Update users
                try (var lockedMessenger = messengerResult.get().acquire()) {
                    final Messenger messenger = lockedMessenger.get();
                    final int userIndex = messengerResult.get().getUserIndex(remoteUser);
                    if (userIndex < 0) {
                        log.error("Could not update user avatar in messenger ID {}", messenger.getMessengerId());
                        return;
                    }
                    final OutPacket outPacket = MessengerPacket.avatar(userIndex, messengerUser.getAvatarLook());
                    forEachMessengerUser(messengerResult.get(), (user, node) -> {
                        if (user.getCharacterId() != remoteUser.getCharacterId()) {
                            node.write(CentralPacket.userPacketReceive(user.getCharacterId(), outPacket));
                        }
                    });
                }
            }
            case MSMP_Migrated -> {
                // Resolve messenger
                final Optional<Messenger> messengerResult = centralServerNode.getMessengerById(remoteUser.getMessengerId());
                if (messengerResult.isEmpty()) {
                    remoteServerNode.write(CentralPacket.messengerResult(remoteUser.getCharacterId(), 0));
                    return;
                }
                try (var lockedMessenger = messengerResult.get().acquire()) {
                    final Messenger messenger = lockedMessenger.get();
                    final int userIndex = messengerResult.get().getUserIndex(remoteUser);
                    if (userIndex < 0) {
                        log.error("Could not migrate in messenger ID {}", messenger.getMessengerId());
                        return;
                    }
                    for (var entry : messenger.getMessengerUsers().entrySet()) {
                        if (entry.getKey() != userIndex) {
                            remoteServerNode.write(CentralPacket.userPacketReceive(remoteUser.getCharacterId(), MessengerPacket.enter(entry.getKey(), entry.getValue(), false)));
                        }
                    }
                }
            }
        }
    }


    // HELPER METHODS --------------------------------------------------------------------------------------------------

    private void createMessenger(RemoteServerNode remoteServerNode, RemoteUser remoteUser, MessengerUser messengerUser) {
        final Messenger newMessenger = centralServerNode.createNewMessenger(remoteUser, messengerUser);
        remoteUser.setMessengerId(newMessenger.getMessengerId());
        remoteServerNode.write(CentralPacket.messengerResult(remoteUser.getCharacterId(), newMessenger.getMessengerId()));
    }

    public void leaveMessenger(RemoteUser remoteUser) {
        final Optional<Messenger> messengerResult = centralServerNode.getMessengerById(remoteUser.getMessengerId());
        if (messengerResult.isEmpty()) {
            return;
        }
        try (var lockedMessenger = messengerResult.get().acquire()) {
            final Messenger messenger = lockedMessenger.get();
            final int userIndex = messenger.removeUser(remoteUser);
            if (userIndex < 0) {
                log.error("Could not remove user from messenger ID : {}", messenger.getMessengerId());
                return;
            }
            // Check if empty
            if (messenger.getMessengerUsers().isEmpty()) {
                if (!centralServerNode.removeMessenger(messenger)) {
                    log.error("Could not remove messenger ID : {}", messenger.getMessengerId());
                }
                return;
            }
            // Update users
            final OutPacket outPacket = MessengerPacket.leave(userIndex);
            forEachMessengerUser(messenger, (user, node) -> {
                node.write(CentralPacket.userPacketReceive(user.getCharacterId(), outPacket));
            });
        }
    }

    public void updateMessengerUser(RemoteUser remoteUser) {
        final Optional<Messenger> messengerResult = centralServerNode.getMessengerById(remoteUser.getMessengerId());
        if (messengerResult.isEmpty()) {
            return;
        }
        try (var lockedMessenger = messengerResult.get().acquire()) {
            lockedMessenger.get().updateUser(remoteUser);
        }
    }

    private void forEachMessengerUser(Messenger messenger, BiConsumer<RemoteUser, RemoteServerNode> biConsumer) {
        messenger.forEachUser((member) -> {
            final Optional<RemoteServerNode> targetNodeResult = centralServerNode.getChannelServerNodeById(member.getChannelId());
            if (targetNodeResult.isEmpty()) {
                return;
            }
            biConsumer.accept(member, targetNodeResult.get());
        });
    }
}
