package dannypx.foe.handler.logic;

import dannypx.foe.config.Configs;
import dannypx.foe.handler.Handler;
import dannypx.foe.handler.store.StatsDataHandler;
import dannypx.foe.item.PetTagObject;
import dannypx.foe.item.TagObject;
import dannypx.foe.item.ValidateItem;
import dannypx.foe.type.placeholder.ComponentValue;
import dannypx.foe.type.placeholder.PlaceholderValue;
import dannypx.foe.type.placeholder.StringValue;
import dannypx.foe.type.tuple.Pair;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.item.ItemStack;

import java.util.*;
import java.util.regex.Pattern;

public class QuestHandler extends Handler {
    private static QuestHandler INSTANCE = new QuestHandler();

    public static QuestHandler instance() {
        if (INSTANCE == null) {
            INSTANCE = new QuestHandler();
        }
        return INSTANCE;
    }

    //region Fields
    private long startScanTime = 0L;
    private boolean scanDone = true;

    private PetTagObject lastRewardedPet = PetTagObject.empty();

    private List<Pair<TagObject, Integer>> lastRewardedItems = new ArrayList<>();

    public PetTagObject getLastRewardedPet() {
        return lastRewardedPet;
    }

    public List<Pair<TagObject, Integer>> getLastRewardedItems() {
        return Collections.unmodifiableList(lastRewardedItems);
    }
    //endregion

    //region Methods
    public void tick() {
        this.scanRewards();
    }

    private void scanRewards() {
        if(!scanDone
                && System.currentTimeMillis() < startScanTime + (Configs.handlerConfig.rewardStatusCooldown.get() * 1000L)
        ) {
            CodeExecuterHandler.runLater(Configs.handlerConfig.rewardsItemsDelayCheck.get(), () -> {
                this.checkForItems();
                EventHandler.instance().onQuestComplete();
            });
            this.scanDone = true;
        } else if (!scanDone && System.currentTimeMillis() > startScanTime + (Configs.handlerConfig.rewardStatusCooldown.get() * 1000L)){
            this.scanDone = true;
            LoggerHandler._debug("Did not find items");
        }
    }

    private void checkForItems() {
        LoggerHandler._debug("Start finding items");
        LoggerHandler._debug("Start Time: " + System.currentTimeMillis());
        LoggerHandler._debug("Search Window: " + (Configs.handlerConfig.rewardsItemsCheckWindow.get() + (System.currentTimeMillis() - startScanTime)));
        this.lastRewardedItems.clear();

        if(minecraft.player != null) {
            InventoryHandler.instance().getSnapshottedItems().stream()
                    .filter(item -> System.currentTimeMillis() - item.value1()
                            < Configs.handlerConfig.rewardsItemsCheckWindow.get() + (System.currentTimeMillis() - startScanTime))
                    .forEach(item -> {
                        LoggerHandler._debug(item.value2().getCustomName());
                        scanItem(item.value2(), item.value3());
                    });
        }
    }

    private void scanItem(ItemStack itemStack, int count) {
        Pair<Boolean, TagObject> validatedItem = ValidateItem.isType(itemStack);

        if(validatedItem.value1()) {
            Pair<Boolean, PetTagObject> validatePet = ValidateItem.isPet(validatedItem.value2());
            if(validatePet.value1()) {
                StatsDataHandler.instance().setQuestPet(validatePet.value2());
                lastRewardedPet = validatePet.value2();
            } else {
                StatsDataHandler.instance().setQuestItem(validatedItem.value2(), count);
                lastRewardedItems.add(Pair.of(validatedItem.value2(), count));
            }

            LoggerHandler._debug("Found Item: " + itemStack.getHoverName().getString(), itemStack);
        }
    }

    public void initScan() {
        if(scanDone) {
            LoggerHandler._debug("Start finding items");
            this.startScan();
        }
    }

    private void startScan() {
        startScanTime = System.currentTimeMillis();
        scanDone = false;
    }
    //endregion

    //region Dev

    /// Field, Pair<Value, Tooltip>
    @Override
    protected Map<String, Pair<MutableComponent, MutableComponent>> _getFields() {
        return Map.of(
                "key", Pair.of(Component.literal("value"), Component.empty())
        );
    }
    //endregion
}
