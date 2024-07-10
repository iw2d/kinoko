package kinoko.world.item;

public final class RingData {
    private int pairCharacterId;
    private String pairCharacterName;
    private long pairItemSn;

    public RingData() {
    }

    public RingData(RingData ringData) {
        this.pairCharacterId = ringData.pairCharacterId;
        this.pairCharacterName = ringData.pairCharacterName;
        this.pairItemSn = ringData.pairItemSn;
    }

    public int getPairCharacterId() {
        return pairCharacterId;
    }

    public void setPairCharacterId(int pairCharacterId) {
        this.pairCharacterId = pairCharacterId;
    }

    public String getPairCharacterName() {
        return pairCharacterName;
    }

    public void setPairCharacterName(String pairCharacterName) {
        this.pairCharacterName = pairCharacterName;
    }

    public long getPairItemSn() {
        return pairItemSn;
    }

    public void setPairItemSn(long pairItemSn) {
        this.pairItemSn = pairItemSn;
    }

    public static RingData from(int pairCharacterId, String pairCharacterName, long pairItemSn) {
        final RingData ringData = new RingData();
        ringData.setPairCharacterId(pairCharacterId);
        ringData.setPairCharacterName(pairCharacterName);
        ringData.setPairItemSn(pairItemSn);
        return ringData;
    }
}
