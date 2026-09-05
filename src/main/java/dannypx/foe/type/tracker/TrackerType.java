package dannypx.foe.type.tracker;

public enum TrackerType {
    BOOLEAN,
    INTEGER,
    ITEMSTACK;

    private static final TrackerType[] vals = values();

    public TrackerType next() {
        return vals[(this.ordinal() + 1) % vals.length];
    }
}
