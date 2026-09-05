package dannypx.foe.handler.logic;

import dannypx.foe.handler.Handler;
import dannypx.foe.handler.fetch.ChatHandler;
import dannypx.foe.handler.store.CustomTrackerDataHandler;
import dannypx.foe.type.placeholder.PlaceholderValue;
import dannypx.foe.type.placeholder.StringValue;
import dannypx.foe.type.tuple.Pair;
import dannypx.foe.handler.store.CustomTimerDataHandler;
import dannypx.foe.type.tuple.Triplet;
import java.util.*;
import java.util.regex.Pattern;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;

public class TimerHandler extends Handler {
    private static TimerHandler INSTANCE = new TimerHandler();

    public static TimerHandler instance() {
        if (INSTANCE == null) {
            INSTANCE = new TimerHandler();
        }
        return INSTANCE;
    }

    //region Fields
    private List<CustomTimerDataHandler.CustomTimer> timers = new ArrayList<>();

    private Map<CustomTimerDataHandler.CustomTimer, Runnable> callbacks = new HashMap<>();
    private Map<CustomTimerDataHandler.CustomTimer, Runnable> endOfOnCallbacks = new HashMap<>();
    private Map<CustomTimerDataHandler.CustomTimer, Runnable> endOfOffCallbacks = new HashMap<>();
    private Map<CustomTimerDataHandler.CustomTimer, Long> lastTrigger = new HashMap<>();
    private Map<CustomTimerDataHandler.CustomTimer, Long> lastPos = new HashMap<>();
    private Map<String, Long> lastTriggerCycle = new HashMap<>();

    public List<CustomTimerDataHandler.CustomTimer> getTimers() {
        return Collections.unmodifiableList(timers);
    }
    //endregion

    //region Methods
    public void tick() {
        long timeMillis = System.currentTimeMillis();

        timers.forEach(timer -> {
            long adjustedWithOffset = timeMillis + timer.getOffset() * 1000L;

            if(timer instanceof CustomTimerDataHandler.CustomTimerPeriod timerPeriod) {
                long cycle = timerPeriod.getTimer() * 1000L + timerPeriod.getOffTimer() * 1000L;
                if(cycle > 0) {
                    long pos = adjustedWithOffset % cycle;
                    long cycleIndex = adjustedWithOffset / cycle;
                    long prevPos = lastPos.getOrDefault(timerPeriod, pos);

                    long lastOffCycle  = lastTriggerCycle.getOrDefault("off_"  + timerPeriod.hashCode(), -1L);
                    long lastOnCycle   = lastTriggerCycle.getOrDefault("on_"   + timerPeriod.hashCode(), -1L);

                    if (crossed(prevPos, pos, 0) && cycleIndex != lastOffCycle) {
                        lastTriggerCycle.put("off_" + timerPeriod.hashCode(), cycleIndex);
                        this.triggerTimer(timerPeriod, timeMillis, endOfOffCallbacks.get(timerPeriod));
                    }

                    if (crossed(prevPos, pos, timerPeriod.getTimer() * 1000L) && cycleIndex != lastOnCycle) {
                        lastTriggerCycle.put("on_" + timerPeriod.hashCode(), cycleIndex);
                        this.triggerTimer(timerPeriod, timeMillis, endOfOnCallbacks.get(timerPeriod));

                        // Clean Chat Trigger
                        String[] chatTriggers = timer.getCleanUpChatTrigger().split(",");
                        ChatHandler.instance().cleanChatTriggerStore(chatTriggers);
                    }

                    lastPos.put(timerPeriod, pos);
                }
            } else {
                if(timer.getTimer() > 0) {
                    long interval = timer.getTimer() * 1000L;
                    long pos = adjustedWithOffset % interval;
                    long cycleIndex = adjustedWithOffset / interval;
                    long prevPos = lastPos.getOrDefault(timer, pos);

                    long lastCycle = lastTriggerCycle.getOrDefault("singular_" + timer.hashCode(), -1L);

                    if (crossed(prevPos, pos, 0) && cycleIndex != lastCycle) {
                        lastTriggerCycle.put("singular_" + timer.hashCode(), cycleIndex);
                        this.triggerTimer(timer, timeMillis, callbacks.get(timer));

                        // Clean Chat Trigger
                        String[] chatTriggers = timer.getCleanUpChatTrigger().split(",");
                        ChatHandler.instance().cleanChatTriggerStore(chatTriggers);
                    }

                    lastPos.put(timer, pos);
                }
            }
        });
    }

    public void init() {
        this.initTimers();
    }

    public void initTimers() {
        timers.clear();
        callbacks.clear();
        endOfOnCallbacks.clear();
        endOfOffCallbacks.clear();
        lastTrigger.clear();
        lastPos.clear();
        lastTriggerCycle.clear();

        List<CustomTimerDataHandler.CustomTimer> tempTimers = new ArrayList<>();
        CustomTimerDataHandler.instance().getCustomTimerData().timerList.forEach((name, timer) -> {
            if(timer.isUseTimer()) {
                if(timer instanceof CustomTimerDataHandler.CustomTimerPeriod timerPeriod) {
                    tempTimers.add(timerPeriod);

                    this.register(timerPeriod, () -> {
                        CodeExecuterHandler.runLater(1, () -> {
                            String[] notificationIds = timerPeriod.getNotificationToTrigger().split(",");
                            NotifierHandler.instance().notifyOnTrigger(notificationIds);

                            String[] chatNotificationIds = timerPeriod.getChatNotificationToTrigger().split(",");
                            ChatNotifierHandler.instance().notifyChatOnTrigger(chatNotificationIds);

                            String[] trackerIds = timerPeriod.getTrackerToTrigger().split(",");
                            CustomTrackerDataHandler.instance().updateTracker(trackerIds);
                        });
                    }, () -> {
                        CodeExecuterHandler.runLater(1, () -> {
                            String[] notificationIds = timerPeriod.getNotificationToTriggerEnd().split(",");
                            NotifierHandler.instance().notifyOnTrigger(notificationIds);

                            String[] chatNotificationIds = timerPeriod.getChatNotificationToTriggerEnd().split(",");
                            ChatNotifierHandler.instance().notifyChatOnTrigger(chatNotificationIds);

                            String[] trackerIds = timerPeriod.getTrackerToTriggerEnd().split(",");
                            CustomTrackerDataHandler.instance().updateTracker(trackerIds);
                        });
                    });
                } else {
                    tempTimers.add(timer);

                    this.register(timer, () -> {
                        CodeExecuterHandler.runLater(1, () -> {
                            String[] notificationIds = timer.getNotificationToTrigger().split(",");
                            NotifierHandler.instance().notifyOnTrigger(notificationIds);

                            String[] chatNotificationIds = timer.getChatNotificationToTrigger().split(",");
                            ChatNotifierHandler.instance().notifyChatOnTrigger(chatNotificationIds);

                            String[] trackerIds = timer.getTrackerToTrigger().split(",");
                            CustomTrackerDataHandler.instance().updateTracker(trackerIds);
                        });
                    });
                }
            }
        });

        timers = new ArrayList<>(tempTimers);
    }

    public void register(CustomTimerDataHandler.CustomTimer timer, Runnable callback) {
        callbacks.put(timer, callback);
    }

    public void register(CustomTimerDataHandler.CustomTimerPeriod timer, Runnable onCallback, Runnable offCallback) {
        endOfOnCallbacks.put(timer, offCallback);
        endOfOffCallbacks.put(timer, onCallback);
    }

    private void triggerTimer(CustomTimerDataHandler.CustomTimer timer, long time, Runnable callback) {
        if(callback != null) {
            long last = lastTrigger.getOrDefault(timer, -1L);

            if(last != time) {
                lastTrigger.put(timer, time);
                callback.run();
            }
        }
    }

    private boolean crossed(long prev, long curr, long target) {
        if (curr == prev) return false;
        if(prev <= curr) {
            return prev < target && curr >= target;
        } else {
            return prev < target || curr >= target;
        }
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
