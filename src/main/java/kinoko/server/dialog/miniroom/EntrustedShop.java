package kinoko.server.dialog.miniroom;

import kinoko.server.packet.InPacket;
import kinoko.util.Locked;
import kinoko.world.user.User;

public final class EntrustedShop extends MiniRoom {
    private final String employerName;
    private final int employerId;
    private final int templateId;
    private int foothold;

    public EntrustedShop(String title, String password, String employerName, int employerId, int templateId) {
        super(title, password, 0);
        this.employerName = employerName;
        this.employerId = employerId;
        this.templateId = templateId;
    }

    public String getEmployerName() {
        return employerName;
    }

    public int getEmployerId() {
        return employerId;
    }

    public int getTemplateId() {
        return templateId;
    }

    public int getFoothold() {
        return foothold;
    }

    @Override
    public MiniRoomType getType() {
        return MiniRoomType.EntrustedShop;
    }

    @Override
    public int getMaxUsers() {
        return 3;
    }

    @Override
    public void handlePacket(Locked<User> locked, MiniRoomProtocol mrp, InPacket inPacket) {

    }

    @Override
    public void leaveUnsafe(User user, LeaveType leaveType) {

    }

    @Override
    public void updateBalloon() {

    }
}
