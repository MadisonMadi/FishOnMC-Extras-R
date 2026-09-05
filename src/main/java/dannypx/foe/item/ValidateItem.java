package dannypx.foe.item;

import dannypx.foe.helper.ItemStackHelper;
import dannypx.foe.type.tuple.Pair;
import java.util.Objects;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

public class ValidateItem {
    private static final String PET = "pet";
    private static final String ARMOR = "armor";

    private static Pair<Boolean, TagObject> isServerItem(ItemStack itemStack) {
        return isValidItem(itemStack);
    }

    public static Pair<Boolean, TagObject> isServerItem(ItemStack itemStack, Item itemType) {
        //isValidItem
        return Pair.of(itemStack.getItem() == itemType, isValidItem(itemStack).value2());
    }

    public static Pair<Boolean, TagObject> isServerItem(ItemStack itemStack, boolean isStrictValidation) {
        return isStrictValidation ? isServerItem(itemStack) : isLoreItem(itemStack);
    }

    public static Pair<Boolean, TagObject> isLoreItem(ItemStack itemStack) {
        if(!itemStack.isEmpty()) {
            CompoundTag compoundTag = ItemStackHelper.getTag(itemStack);
            return Pair.of(hasLore(itemStack),
                    TagObject.of(compoundTag, itemStack));
        }
        return Pair.ofFalse(TagObject.of(new CompoundTag(), itemStack));
    }

    private static Pair<Boolean, TagObject> isValidItem(ItemStack itemStack) {
        if(!itemStack.isEmpty()) {
            CompoundTag compoundTag = ItemStackHelper.getTag(itemStack);
            return Pair.of(hasLore(itemStack)
                            && !isShopItem(compoundTag)
                            && hasCustomData(itemStack)
                            && (isType(compoundTag) || isFish(compoundTag) || isOther(itemStack)),
                    TagObject.of(compoundTag, itemStack));
        }
        return Pair.ofFalse(TagObject.of(new CompoundTag(), itemStack));
    }

    private static boolean isType(CompoundTag compoundTag) {
        return compoundTag != null && compoundTag.contains("type");
    }

    private static boolean isFish(CompoundTag compoundTag) {
        return compoundTag != null && compoundTag.contains("fish");
    }

    private static boolean isOther(ItemStack itemStack) {
        return itemStack.getItem() == Items.FISHING_ROD;
    }

    public static Pair<Boolean, TagObject> isType(ItemStack itemStack) {
        if(!itemStack.isEmpty()
                && hasLore(itemStack)
                && hasCustomData(itemStack)) {
            CompoundTag nbtCompound = ItemStackHelper.getTag(itemStack);
            return Pair.of(!isShopItem(nbtCompound) && isType(nbtCompound), TagObject.of(nbtCompound, itemStack));
        }
        return Pair.ofFalse(TagObject.of(new CompoundTag(), itemStack));
    }

    public static Pair<Boolean, PetTagObject> isPet(ItemStack itemStack) {
        Pair<Boolean, TagObject> validatedItem = isType(itemStack);
        if(validatedItem.value1()) {
            return isPet(validatedItem.value2());
        }
        return Pair.ofFalse(PetTagObject.of(validatedItem.value2().compoundTag, validatedItem.value2().itemStack));
    }

    public static Pair<Boolean, PetTagObject> isPet(TagObject item) {
        return Pair.of(Objects.equals(item.getType(), PET), PetTagObject.of(item.compoundTag, item.itemStack));
    }

    public static Pair<Boolean, ArmorTagObject> isArmor(ItemStack itemStack) {
        Pair<Boolean, TagObject> validatedItem = isType(itemStack);
        if(validatedItem.value1()) {
            return isArmor(validatedItem.value2());
        }
        return Pair.ofFalse(ArmorTagObject.of(validatedItem.value2().compoundTag, validatedItem.value2().itemStack));
    }

    public static Pair<Boolean, ArmorTagObject> isArmor(TagObject item) {
        return Pair.of(Objects.equals(item.getType(), ARMOR), ArmorTagObject.of(item.compoundTag, item.itemStack));
    }

    public static Pair<Boolean, FishTagObject> isFish(ItemStack itemStack) {
        if(!itemStack.isEmpty()
                && hasLore(itemStack)
                && hasCustomData(itemStack)) {
            CompoundTag nbtCompound = ItemStackHelper.getTag(itemStack);
            return Pair.of(!isShopItem(nbtCompound) && isFish(nbtCompound), FishTagObject.of(nbtCompound, itemStack));
        }
        return Pair.ofFalse(FishTagObject.of(new CompoundTag(), itemStack));
    }

    public static Pair<Boolean, FishingRodTagObject> isFishingRod(ItemStack itemStack) {
        Pair<Boolean, TagObject> serverItem = isServerItem(itemStack, Items.FISHING_ROD);
        return Pair.of(serverItem.value1(), FishingRodTagObject.of(serverItem.value2().compoundTag, serverItem.value2().getItemStack()));
    }

    private static boolean hasLore(ItemStack itemStack) {
        return itemStack.get(DataComponents.LORE) != null;
    }

    private static boolean hasCustomData(ItemStack itemStack) {
        return itemStack.get(DataComponents.CUSTOM_DATA) != null;
    }

    public static boolean isAuctionItem(TagObject tagObject) {
        return tagObject.isAuctionItem();
    }

    private static boolean isShopItem(CompoundTag compoundTag) {
        return Objects.requireNonNull(compoundTag).getBoolean("shopitem").orElse(false);
    }
}
