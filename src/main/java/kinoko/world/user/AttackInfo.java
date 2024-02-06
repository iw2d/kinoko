package kinoko.world.user;

public final class AttackInfo {
    public int mobId;
    public byte hitAction;
    public byte actionAndDir;

    public byte[] critical = new byte[15];
    public int[] damage = new int[15];
}
