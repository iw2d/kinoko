package kinoko.server.dialog.miniroom;

import kinoko.server.packet.InPacket;
import kinoko.util.Locked;
import kinoko.world.user.User;

import java.util.Map;

public final class EntrustedShop extends MiniRoom {
    private final int ownerId;
    private final String ownerName;
    private final String title;
    private final int templateId;
    private final int foothold;
    private boolean open = false;

    public EntrustedShop(int ownerId, String ownerName, String title, int templateId, int foothold) {
        this.ownerId = ownerId;
        this.ownerName = ownerName;
        this.title = title;
        this.templateId = templateId;
        this.foothold = foothold;
    }

    public int getOwnerId() {
        return ownerId;
    }

    public String getOwnerName() {
        return ownerName;
    }

    public String getTitle() {
        return title;
    }

    public int getTemplateId() {
        return templateId;
    }

    public int getFoothold() {
        return foothold;
    }

    public boolean isOpen() {
        return open;
    }

    public void setOpen(boolean open) {
        this.open = open;
    }

    @Override
    public void handlePacket(Locked<User> locked, MiniRoomProtocol mrp, InPacket inPacket) {

    }

    @Override
    public MiniRoomType getType() {
        return MiniRoomType.EntrustedShop;
    }

    @Override
    public boolean checkPassword(String password) {
        return false;
    }

    @Override
    public int getMaxUsers() {
        return 3;
    }

    @Override
    public boolean addUser(User user) {
        return false;
    }

    @Override
    public Map<Integer, User> getUsers() {
        return null;
    }
}
