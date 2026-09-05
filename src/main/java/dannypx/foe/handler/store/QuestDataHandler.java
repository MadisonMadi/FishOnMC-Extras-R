package dannypx.foe.handler.store;

import dannypx.foe.handler.Handler;
import dannypx.foe.handler.fetch.BossEventHandler;
import dannypx.foe.handler.io.DataFileHandler;
import dannypx.foe.handler.io.DataModels;
import dannypx.foe.handler.logic.LoggerHandler;
import dannypx.foe.handler.fetch.QuestScreenHandler;
import dannypx.foe.helper.TextHelper;
import dannypx.foe.item.FishTagObject;
import dannypx.foe.type.tuple.Pair;

import java.util.*;

import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;

public class QuestDataHandler extends Handler {
    private static QuestDataHandler INSTANCE = new QuestDataHandler();

    public static QuestDataHandler instance() {
        if (INSTANCE == null) {
            INSTANCE = new QuestDataHandler();
        }
        return INSTANCE;
    }

    //region Fields
    private QuestDataModel questData = new QuestDataModel();
    private boolean needsUpdate = false;

    public QuestDataModel getQuestData() {
        return questData;
    }

    public void setQuestData(QuestDataModel questData) {
        this.questData = questData;
        this.updateQuestData();
    }

    private void updateQuestData() {
        if(needsUpdate) {
            DataFileHandler.instance().saveToFile(DataModels.DataModelType.QUEST_DATA);
        }
        this.needsUpdate = false;
    }
    //endregion

    //region Methods
    public void tick() {
        if(questData.uuid == null && minecraft.player != null) {
            questData.uuid = minecraft.player.getUUID();
        } else if(questData.uuid != null && this.needsUpdate) {
            this.updateQuestData();
        } else if(!QuestDataModel.QUEST_DATA_MODEL_VERSION.equals(questData.version)) {
            questData.version = QuestDataModel.QUEST_DATA_MODEL_VERSION;
            needsUpdate = true;
        }
    }

    public void init() {
        if(minecraft.player != null) this.setUUID(minecraft.player.getUUID());
    }

    private void setUUID(UUID uuid) {
        this.questData.uuid = uuid;
    }

    public void setQuest(List<Quest> questList) {
        String location = BossEventHandler.instance().getLocation().getString();

        questData.questList.put(location, questList);
        LoggerHandler._debug("Quests updated");
        this.needsUpdate = true;
    }

    public void setFish(FishTagObject fishNbtObject) {
        questData.questList.getOrDefault(BossEventHandler.instance().getLocation().getString(), new ArrayList<>()).forEach(quest -> {
            if(Objects.equals(quest.goal, fishNbtObject.getFishSize()) || Objects.equals(quest.goal, fishNbtObject.getRarity())) {
                quest.addCurrent();
                this.needsUpdate = true;
            }
        });
        QuestScreenHandler.instance().checkForCompletedQuests();
    }
    //endregion

    //region Model
    public static class QuestDataModel extends DataModels.DataModel {
        private static final String QUEST_DATA_MODEL_VERSION = "0.2";

        // Location, Quests
        public Map<String, List<Quest>> questList = new HashMap<>();

        public QuestDataModel() {
            super(QUEST_DATA_MODEL_VERSION, null);
        }

    }
    //endregion

    //region Quest Object
    public static class Quest {
        public final String goal;
        public final int max;
        public int current;

        public Quest(String goal, int max, int current) {
            this.goal = goal;
            this.max = max;
            this.current = current;
        }

        public void addCurrent() {
            if(this.current < this.max) {
                this.current++;
            }
        }

        public boolean isDone() {
            return current == max;
        }
    }
    //

    //region Dev
    /// Field, Pair<Value, Tooltip>
    protected Map<String, Pair<MutableComponent, MutableComponent>> _getFields() {
        return Map.of(
                "questData", Pair.of(Component.literal("[questData]"), TextHelper.literal(getQuestData()))
        );
    }
    //endregion
}
