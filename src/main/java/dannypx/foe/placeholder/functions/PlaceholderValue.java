package dannypx.foe.placeholder.functions;

import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import org.jspecify.annotations.Nullable;

public class PlaceholderValue {
    @Nullable
    private final String stringValue;
    @Nullable
    private final MutableComponent componentValue;
    @Nullable
    private final Number numberValue;
    @Nullable
    private final Boolean booleanValue;
    private final boolean forcedFailure;

    private PlaceholderValue(String stringValue, MutableComponent componentValue, Number numberValue, Boolean booleanValue, boolean forcedFailure) {
        this.stringValue = stringValue;
        this.componentValue = componentValue;
        this.numberValue = numberValue;
        this.booleanValue = booleanValue;
        this.forcedFailure = forcedFailure;
    }

    public static PlaceholderValue text(String s) {
        return new PlaceholderValue(s, null, null, null, false);
    }

    public static PlaceholderValue component(MutableComponent c) {
        return new PlaceholderValue(null, c, null, null, false);
    }

    public static PlaceholderValue number(Number n) {
        return new PlaceholderValue(null, null, n, null, false);
    }

    public static PlaceholderValue bool(Boolean b) {
        return new PlaceholderValue(null, null, null, b, false);
    }

    public static PlaceholderValue emptyText() {
        return new PlaceholderValue("", null, null, null, false);
    }

    public PlaceholderValue markFailure() {
        return new PlaceholderValue(stringValue, componentValue, numberValue, booleanValue, true);
    }

    public boolean isForcedFailure() {
        return forcedFailure;
    }

    public boolean isNull() {
        return stringValue == null && componentValue == null && numberValue == null && booleanValue == null;
    }

    public boolean isString() {
        return stringValue != null;
    }

    public boolean isComponent() {
        return componentValue != null;
    }

    public boolean isNumber() {
        return numberValue != null;
    }

    public boolean isBoolean() {
        return booleanValue != null;
    }

    public boolean isEmpty() {
        return !isNull() && this.toString().isEmpty();
    }

    public boolean isValidNumber() {
        if(numberValue != null) return true;
        try {
            Double.parseDouble(this.toString());
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    public MutableComponent toComponent() {
        if(this.isComponent()) return componentValue;
        return Component.literal(this.toString());
    }

    @Override
    public String toString() {
        if(this.isString()) return stringValue;
        if(this.isComponent()) return componentValue.getString();
        if(this.isNumber()) return PlaceholderValue.formatNumber(numberValue);
        if(this.isBoolean()) return booleanValue.toString();
        return "";
    }

    public boolean toBoolean() {
        if(this.isBoolean()) return booleanValue.booleanValue();
        return Boolean.parseBoolean(this.toString());
    }

    public double toDouble() {
        if(this.isNumber()) return numberValue.doubleValue();
        try {
            return Double.parseDouble(this.toString());
        } catch (NumberFormatException ignored) {
            return 0.0d;
        }
    }

    public int toInteger() {
        if(this.isNumber()) return numberValue.intValue();
        try {
            return Integer.parseInt(this.toString());
        } catch (NumberFormatException ignored) {
            return 0;
        }
    }

    private static String formatNumber(Number n) {
        double d = n.doubleValue();
        if(d == Math.floor(d) && !Double.isInfinite(d)) {
            return String.valueOf((long) d);
        }
        return String.valueOf(d);
    }
}
