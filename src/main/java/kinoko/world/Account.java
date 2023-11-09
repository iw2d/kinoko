package kinoko.world;

import kinoko.world.user.AvatarData;

import java.util.List;

public final class Account {
    private final int id;
    private final String username;
    private int slotCount;
    private int nxCredit;
    private int nxPrepaid;
    private int maplePoint;

    // TRANSIENT
    private boolean hasSecondaryPassword;
    private Channel selectedChannel;
    private List<AvatarData> characterList;

    public Account(int id, String username) {
        this.id = id;
        this.username = username;
    }

    public int getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public int getSlotCount() {
        return slotCount;
    }

    public void setSlotCount(int slotCount) {
        this.slotCount = slotCount;
    }

    public int getNxCredit() {
        return nxCredit;
    }

    public void setNxCredit(int nxCredit) {
        this.nxCredit = nxCredit;
    }

    public int getNxPrepaid() {
        return nxPrepaid;
    }

    public void setNxPrepaid(int nxPrepaid) {
        this.nxPrepaid = nxPrepaid;
    }

    public int getMaplePoint() {
        return maplePoint;
    }

    public void setMaplePoint(int maplePoint) {
        this.maplePoint = maplePoint;
    }

    // TRANSIENT -------------------------------------------------------------------------------------------------------

    public boolean hasSecondaryPassword() {
        return hasSecondaryPassword;
    }

    public void setHasSecondaryPassword(boolean hasSecondaryPassword) {
        this.hasSecondaryPassword = hasSecondaryPassword;
    }

    public Channel getSelectedChannel() {
        return selectedChannel;
    }

    public void setSelectedChannel(Channel selectedChannel) {
        this.selectedChannel = selectedChannel;
    }

    public List<AvatarData> getCharacterList() {
        return characterList;
    }

    public void setCharacterList(List<AvatarData> characterList) {
        this.characterList = characterList;
    }

    public boolean canSelectCharacter(int characterId) {
        return getCharacterList() != null &&
                getCharacterList().stream().anyMatch(avatarData -> avatarData.getCharacterId() == characterId);
    }
}
