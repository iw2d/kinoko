package kinoko.world.user;

import kinoko.world.item.Trunk;

import java.util.List;

public final class Account {
    private final int id;
    private final String username;
    private int slotCount;
    private int nxCredit;
    private int nxPrepaid;
    private int maplePoint;
    private Trunk trunk;
    private Locker locker;
    private List<Integer> wishlist;
    private BanInfo banInfo;

    // TRANSIENT
    private int channelId = -1;
    private boolean hasSecondaryPassword = false;
    private List<AvatarData> characterList = null;

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

    public Trunk getTrunk() {
        return trunk;
    }

    public void setTrunk(Trunk trunk) {
        this.trunk = trunk;
    }

    public Locker getLocker() {
        return locker;
    }

    public void setLocker(Locker locker) {
        this.locker = locker;
    }

    public List<Integer> getWishlist() {
        return wishlist;
    }

    public void setWishlist(List<Integer> wishlist) {
        this.wishlist = wishlist;
    }

    public void setBanInfo(BanInfo banInfo){
        this.banInfo = banInfo;
    }

    public BanInfo getBanInfo(){
        return banInfo;
    }

    // TRANSIENT -------------------------------------------------------------------------------------------------------

    public int getChannelId() {
        return channelId;
    }

    public void setChannelId(int channelId) {
        this.channelId = channelId;
    }

    public boolean hasSecondaryPassword() {
        return hasSecondaryPassword;
    }

    public void setHasSecondaryPassword(boolean hasSecondaryPassword) {
        this.hasSecondaryPassword = hasSecondaryPassword;
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
