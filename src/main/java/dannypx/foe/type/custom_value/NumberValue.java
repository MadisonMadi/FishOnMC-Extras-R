package dannypx.foe.type.custom_value;

public record NumberValue(Float value) implements TrackerValue {
    @Override
    public <F> TrackerValue addValue(F value) {
        return new NumberValue(this.value + (Float) value);
    }

    @Override
    public <F> TrackerValue subtractValue(F value) {
        return new NumberValue(this.value - (Float) value);
    }

    public static TrackerValue getDefault() {
        return new NumberValue(0f);
    }

    public static TrackerValue of(Float value) {
        return new NumberValue(value);
    }
}
