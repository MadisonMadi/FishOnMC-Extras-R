package dannypx.foe.handler.store;

import dannypx.foe.handler.Handler;
import dannypx.foe.handler.io.DataFileHandler;
import dannypx.foe.handler.io.DataModels;
import dannypx.foe.helper.TextHelper;
import dannypx.foe.type.tuple.Pair;

import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Stream;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.item.ItemStack;

public class ConstantDataHandler extends Handler {
    private static ConstantDataHandler INSTANCE = new ConstantDataHandler();

    public static ConstantDataHandler instance() {
        if (INSTANCE == null) {
            INSTANCE = new ConstantDataHandler();
        }
        return INSTANCE;
    }

    //region Fields
    private ConstantDataModel constantData = new ConstantDataModel();
    private boolean needsUpdate = false;

    public ConstantDataModel getConstantData() {
        return constantData;
    }

    public void setConstantData(ConstantDataModel constantData) {
        this.constantData = constantData;
        this.updateConstantData();
    }

    private void updateConstantData() {
        if(needsUpdate) {
            DataFileHandler.instance().saveToFile(DataModels.DataModelType.CONSTANT_DATA);
        }
        this.needsUpdate = false;
    }
    //endregion

    //region Methods
    public void tick() {
        if(constantData.uuid == null && minecraft.player != null) {
            constantData.uuid = minecraft.player.getUUID();
        } else if(constantData.uuid != null && this.needsUpdate) {
            this.updateConstantData();
        } else if(!ConstantDataModel.CONSTANT_DATA_MODEL_VERSION.equals(constantData.version)) {
            constantData.version = ConstantDataModel.CONSTANT_DATA_MODEL_VERSION;
            needsUpdate = true;
        }
    }

    public void init() {
        if(minecraft.player != null) this.setUUID(minecraft.player.getUUID());
    }

    private void setUUID(UUID uuid) {
        this.constantData.uuid = uuid;
    }

    public void updateFishData(String category, String field, Component value) {
        Map<String, Component> categoryData = this.constantData.fishData.getOrDefault(category, new HashMap<>());
        if(!categoryData.containsKey(field)) {
            categoryData.put(field, value);
            this.constantData.fishData.put(category, categoryData);
            this.needsUpdate = true;
        }
    }

    public void updatePetData(String category, String field, Component value) {
        Map<String, Component> categoryData = this.constantData.petData.getOrDefault(category, new HashMap<>());
        if(!categoryData.containsKey(field)) {
            categoryData.put(field, value);
            this.constantData.petData.put(category, categoryData);
            this.needsUpdate = true;
        }
    }

    public void updateItemData(String category, ItemStack itemStack) {
        List<ItemStack> categoryData = this.constantData.itemData.getOrDefault(category, new ArrayList<>());
        boolean containsItem = categoryData.stream().anyMatch(item -> item.getHoverName().getString().equals(itemStack.getHoverName().getString()));
        if(!containsItem) {
            categoryData.add(itemStack);
            this.constantData.itemData.put(category, categoryData);
            this.needsUpdate = true;
        }
    }

    public Component getConstantFishComponent(String field) {
        AtomicReference<Component> fieldComponent = new AtomicReference<>(Component.empty());
        this.getConstantData().fishData.forEach((key, mapFields) -> {
            Component result = mapFields.getOrDefault(field, Component.empty());

            if(!Objects.equals(result, Component.empty())) {
                fieldComponent.set(result);
            }
        });

        return fieldComponent.get();
    }

    public static Stream<String> keysFromField(Map<String, Component> map, String value) {
        return map
                .entrySet()
                .stream()
                .filter(entry -> value.equals(entry.getValue().getString().trim()))
                .map(Map.Entry::getKey);
    }
    //endregion

    //region Model
    public static class ConstantDataModel extends DataModels.DataModel {
        private static final String CONSTANT_DATA_MODEL_VERSION = "0.1";

        /**
         * Fish
         * - Rarities
         * - Size
         * - Variants
         */
        public Map<String, Map<String, Component>> fishData = new HashMap<>(
                Map.of(
                        "size", new HashMap<>(Map.of(
                                "baby", Component.literal("ʙᴀʙʏ").withColor(0x468CE7),
                                "juvenile", Component.literal("ᴊᴜᴠᴇɴɪʟᴇ").withColor(0x22EA08),
                                "adult", Component.literal("ᴀᴅᴜʟᴛ").withColor(0x1C7DA0),
                                "large", Component.literal("ʟᴀʀɢᴇ").withColor(0xFF9000),
                                "gigantic", Component.literal("ɢɪɢᴀɴᴛɪᴄ").withColor(0xAF3333)
                        )),
                        "variant", new HashMap<>(Map.of(
                                "normal", Component.literal("\uF040").withStyle(ChatFormatting.WHITE),
                                "albino", Component.literal("\uF041").withStyle(ChatFormatting.WHITE),
                                "melanistic", Component.literal("\uF042").withStyle(ChatFormatting.WHITE),
                                "trophy", Component.literal("\uF043").withStyle(ChatFormatting.WHITE),
                                "fabled", Component.literal("\uF044").withStyle(ChatFormatting.WHITE)
                        )),
                        "rarity", new HashMap<>(Map.of(
                                "common", Component.literal("\uF033").withStyle(ChatFormatting.WHITE),
                                "rare", Component.literal("\uF034").withStyle(ChatFormatting.WHITE),
                                "epic", Component.literal("\uF035").withStyle(ChatFormatting.WHITE),
                                "legendary", Component.literal("\uF036").withStyle(ChatFormatting.WHITE),
                                "mythical", Component.literal("\uF037").withStyle(ChatFormatting.WHITE)
                        ))
                )
        );

        /**
         * Pet
         * - Rarities
         * - Rating
         */
        public Map<String, Map<String, Component>> petData = new HashMap<>();

        public Map<String, List<ItemStack>> itemData = new HashMap<>();

        public ConstantDataModel() {
            super(CONSTANT_DATA_MODEL_VERSION, null);
        }
    }
    //endregion

    //region Dev
    /// Field, Pair<Value, Tooltip>
    protected Map<String, Pair<MutableComponent, MutableComponent>> _getFields() {
        return Map.of(
                "constantData", Pair.of(Component.literal("[constantData]"), TextHelper.literal(getConstantData()))
        );
    }
    //endregion
}
