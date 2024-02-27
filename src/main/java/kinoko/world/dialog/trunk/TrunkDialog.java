package kinoko.world.dialog.trunk;

import kinoko.world.dialog.Dialog;
import kinoko.world.life.npc.Npc;
import kinoko.world.user.User;

public final class TrunkDialog implements Dialog {
    private final User user;
    private final Npc npc;

    public TrunkDialog(User user, Npc npc) {
        this.user = user;
        this.npc = npc;
    }

    public Trunk getTrunk() {
        return user.getAccount().getTrunk();
    }

    public Npc getNpc() {
        return npc;
    }

    public int getTemplateId() {
        return npc.getTemplateId();
    }

    @Override
    public User getUser() {
        return user;
    }

    public static TrunkDialog from(User user, Npc npc) {
        return new TrunkDialog(user, npc);
    }
}
