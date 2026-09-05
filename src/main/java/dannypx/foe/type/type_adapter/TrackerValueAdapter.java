package dannypx.foe.type.type_adapter;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;
import dannypx.foe.helper.ItemStackHelper;
import dannypx.foe.type.custom_value.*;
import net.minecraft.world.item.ItemStack;

import java.io.IOException;

public class TrackerValueAdapter extends TypeAdapter<TrackerValue> {
    @Override
    public void write(JsonWriter writer, TrackerValue value) throws IOException {
        if(value == null) {
            return;
        }
        switch (value) {
            case BooleanValue booleanValue -> writer.value(booleanValue.value());
            case NumberValue numberValue -> writer.value(numberValue.value());
            case PlaceholderStringValue placeholderStringValue -> writer.value(placeholderStringValue.value());
            case ItemStackValue itemStackValue -> writer.value(itemStackValue.toJson());
            case EmptyValue ignored -> writer.value("");
            default -> throw new IllegalStateException("Unexpected value: " + value);
        }
    }

    @Override
    public TrackerValue read(JsonReader reader) throws IOException {
        if (reader.peek() == JsonToken.NULL) {
            reader.nextNull();
            return null;
        }

        try {
            boolean parsed = reader.nextBoolean();
            return BooleanValue.of(parsed);
        } catch (Exception ignored) {}

        try {
            int parsed = reader.nextInt();
            return NumberValue.of((float) parsed);
        } catch (Exception ignored) {}

        try {
            double parsed = reader.nextDouble();
            return NumberValue.of((float) parsed);
        } catch (Exception ignored) {}

        try {
            String parsed = reader.nextString();

            if(parsed.isEmpty()) return EmptyValue.getDefault();
            if(parsed.startsWith("%") && parsed.endsWith("%")) return PlaceholderStringValue.of(parsed);
            return ItemStackValue.fromJson(parsed);
        } catch (Exception ignored) {}

        return new ErrorValue();
    }
}
