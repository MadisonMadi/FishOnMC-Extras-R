package dannypx.foe.type.placeholder;

import net.minecraft.network.chat.Component;

import java.util.UUID;

public record StringValue(String value) implements PlaceholderValue {
    @Override
    public String getString() {
        return value;
    }

    public static StringValue valueOf(int value) { return new StringValue(String.valueOf(value)); }
    public static StringValue valueOf(short value) { return new StringValue(String.valueOf(value)); }
    public static StringValue valueOf(char value) { return new StringValue(String.valueOf(value)); }
    public static StringValue valueOf(float value) { return new StringValue(String.valueOf(value)); }
    public static StringValue valueOf(long value) { return new StringValue(String.valueOf(value)); }
    public static StringValue valueOf(double value) { return new StringValue(String.valueOf(value)); }
    public static StringValue valueOf(boolean value) { return new StringValue(String.valueOf(value)); }
    public static StringValue valueOf(UUID value) { return new StringValue(String.valueOf(value)); }
    // public static StringValue valueOf(Object value) { return new StringValue(String.valueOf(value)); }

    public static StringValue toString(Component value) { return new StringValue(value.getString()); }

    public static StringValue of(String value) { return new StringValue(value); }

    public static StringValue empty() { return new StringValue(""); }
}
