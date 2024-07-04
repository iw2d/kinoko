package kinoko.server.messenger;

import kinoko.server.packet.InPacket;
import kinoko.server.packet.OutPacket;
import kinoko.util.Encodable;
import kinoko.world.user.User;

public final class MessengerRequest implements Encodable {
    private final MessengerProtocol requestType;
    private MessengerUser messengerUser;
    private int messengerId;
    private String message;

    MessengerRequest(MessengerProtocol requestType) {
        this.requestType = requestType;
    }

    public MessengerProtocol getRequestType() {
        return requestType;
    }

    public MessengerUser getMessengerUser() {
        return messengerUser;
    }

    public int getMessengerId() {
        return messengerId;
    }

    public String getMessage() {
        return message;
    }

    @Override
    public void encode(OutPacket outPacket) {
        outPacket.encodeByte(requestType.getValue());
        switch (requestType) {
            case MSMP_Enter -> {
                outPacket.encodeInt(messengerId);
                messengerUser.encode(outPacket);
            }
            case MSMP_Leave, MSMP_Migrated -> {
                // no encodes
            }
            case MSMP_Chat -> {
                outPacket.encodeString(message);
            }
            case MSMP_Avatar -> {
                messengerUser.encode(outPacket);
            }
        }
    }

    public static MessengerRequest decode(InPacket inPacket) {
        final int type = inPacket.decodeByte();
        final MessengerRequest messengerRequest = new MessengerRequest(MessengerProtocol.getByValue(type));
        switch (messengerRequest.requestType) {
            case MSMP_Enter -> {
                messengerRequest.messengerId = inPacket.decodeInt();
                messengerRequest.messengerUser = MessengerUser.decode(inPacket);
            }
            case MSMP_Leave, MSMP_Migrated -> {
                // no decodes
            }
            case MSMP_Chat -> {
                messengerRequest.message = inPacket.decodeString();
            }
            case MSMP_Avatar -> {
                messengerRequest.messengerUser = MessengerUser.decode(inPacket);
            }
            case null -> {
                throw new IllegalStateException(String.format("Unknown messenger request type %d", type));
            }
            default -> {
                throw new IllegalStateException(String.format("Unhandled messenger request type %d", type));
            }
        }
        return messengerRequest;
    }

    public static MessengerRequest enter(int messengerId, User user) {
        final MessengerRequest messengerRequest = new MessengerRequest(MessengerProtocol.MSMP_Enter);
        messengerRequest.messengerId = messengerId;
        messengerRequest.messengerUser = MessengerUser.from(user);
        return messengerRequest;
    }

    public static MessengerRequest leave() {
        return new MessengerRequest(MessengerProtocol.MSMP_Leave);
    }

    public static MessengerRequest chat(String message) {
        final MessengerRequest messengerRequest = new MessengerRequest(MessengerProtocol.MSMP_Chat);
        messengerRequest.message = message;
        return messengerRequest;
    }

    public static MessengerRequest avatar(User user) {
        final MessengerRequest messengerRequest = new MessengerRequest(MessengerProtocol.MSMP_Avatar);
        messengerRequest.messengerUser = MessengerUser.from(user);
        return messengerRequest;
    }

    public static MessengerRequest migrated() {
        return new MessengerRequest(MessengerProtocol.MSMP_Migrated);
    }
}
