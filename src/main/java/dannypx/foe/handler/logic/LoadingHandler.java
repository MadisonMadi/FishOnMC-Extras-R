package dannypx.foe.handler.logic;

import dannypx.foe.handler.Handler;
import dannypx.foe.item.FishingRodTagObject;
import dannypx.foe.item.ValidateItem;
import dannypx.foe.type.tuple.Pair;
import dannypx.foe.type.placeholder.PlaceholderValue;
import dannypx.foe.type.placeholder.StringValue;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.regex.Pattern;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.item.ItemStack;

public class LoadingHandler extends Handler {
    private static LoadingHandler INSTANCE = new LoadingHandler();

    public static LoadingHandler instance() {
        if (INSTANCE == null) {
            INSTANCE = new LoadingHandler();
        }
        return INSTANCE;
    }

    //region Fields
    private boolean isLoadingDone = false;
    private boolean isError = false;

    public boolean isLoadingDone() {
        return isLoadingDone;
    }

    public boolean isError() {
        return isError;
    }

    public void setError(boolean error) {
        isError = error;
    }
    //endregion

    //region Methods
    public void tick() {
        if(minecraft.player != null) {
            ItemStack firstSlot = minecraft.player.getInventory().getNonEquipmentItems().getFirst();

            if(this.checkFishingRodLoaded(firstSlot)) {
                isLoadingDone = this.scanFish();
            }

            if(isLoadingDone) {
                EventHandler.instance().onJoin();
                LoggerHandler.info("Loading Done");
            }
        }
    }

    public void init() {
        isLoadingDone = false;
        LoggerHandler.info("Loading Started");
    }

    public void onLeave() {
        isLoadingDone = false;
    }

    private boolean checkFishingRodLoaded(ItemStack itemStack) {
        Pair<Boolean, @Nullable FishingRodTagObject> validatedFishingRod = ValidateItem.isFishingRod(itemStack);

        if(validatedFishingRod.value1()) {
            InventoryHandler.instance().setCurrentFishingRod(validatedFishingRod.value2());
            return true;
        }
        return false;
    }

    private boolean scanFish() {
        return InventoryHandler.instance().trackAllFish();
    }
    //endregion

    //region Dev
    protected Map<String, Pair<MutableComponent, MutableComponent>> _getFields() {
        return Map.of(
                "isLoadingDone", Pair.of(Component.literal(Boolean.toString(isLoadingDone())), Component.empty())
        );
    }
    //endregion
}
