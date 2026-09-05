package dannypx.foe.type.custom_value;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import dannypx.foe.handler.logic.LoggerHandler;
import dannypx.foe.helper.ItemStackHelper;
import dannypx.foe.type.tuple.Pair;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.inventory.ContainerScreen;
import net.minecraft.world.item.ItemStack;

public record ItemStackValue(Pair<ItemStack, String> value) implements TrackerValue {
    public String toJson() {
        Pair<String, String> pairToEncode = Pair.of(ItemStackHelper.itemStackToJson(value.value1()), value.value2());

        Gson gson = new GsonBuilder().create();
        return gson.toJson(pairToEncode);
    }

    public static TrackerValue fromJson(String json) {
        Gson gson = new GsonBuilder().create();

        try {
            Pair<String, String> pairToDecode = gson.fromJson(json, TypeToken.getParameterized(Pair.class, String.class, String.class).getType());

            return ItemStackValue.of(Pair.of(ItemStackHelper.jsonToItemStack(pairToDecode.value1()), pairToDecode.value2()));

        } catch (JsonSyntaxException e) {
            LoggerHandler.error(e);
            return ItemStackValue.of(ItemStack.EMPTY);
        }

    }

    public static TrackerValue of(ItemStack value) {
        return new ItemStackValue(Pair.of(value, ""));
    }

    public static TrackerValue of(ItemStack value, String value2) {
        return new ItemStackValue(Pair.of(value, value2));
    }

    public static TrackerValue of(String value) {
        ItemStack itemStack = ItemStackHelper.valueOf(value);

        if(itemStack.isEmpty()) {
            try {
                float index = Float.parseFloat(value);

                if(index >= 0) {
                    Minecraft minecraft = Minecraft.getInstance();
                    if(minecraft.screen instanceof ContainerScreen genericContainerScreen) {
                        itemStack = genericContainerScreen.getMenu().slots.get((int) index).getItem();
                    } else {
                        itemStack = minecraft.player.getInventory().getItem((int) index);
                    }
                } else {
                    itemStack = ItemStack.EMPTY;
                }
            } catch (NumberFormatException e) {
                LoggerHandler.error(e);
            }
        }

        return new ItemStackValue(Pair.of(itemStack, value));
    }

    public static TrackerValue of(Pair<ItemStack, String> value) {
        return new ItemStackValue(value);
    }

}
