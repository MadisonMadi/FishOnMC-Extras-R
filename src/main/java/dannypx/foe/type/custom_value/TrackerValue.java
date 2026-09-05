package dannypx.foe.type.custom_value;

public sealed interface TrackerValue permits BooleanValue, EmptyValue, ErrorValue, ItemStackValue, NumberValue, PlaceholderStringValue {
    default TrackerValue toggleValue() { return null; }
    default <T> TrackerValue addValue(T value) { return null; }
    default <T> TrackerValue subtractValue(T value) { return null; }
}
