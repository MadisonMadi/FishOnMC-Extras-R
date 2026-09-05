package dannypx.foe.type.custom_value;

public record PlaceholderStringValue(String value) implements TrackerValue {
    public static TrackerValue of(String value) {
        return new PlaceholderStringValue(value);
    }
}
