package dannypx.foe.placeholder.handler;

import dannypx.foe.handler.Handler;
import dannypx.foe.handler.logic.LoggerHandler;
import dannypx.foe.placeholder.evaluator.PlaceholderEvaluator;
import dannypx.foe.placeholder.evaluator.PlaceholderResult;
import dannypx.foe.placeholder.registry.PlaceholderRegistry;
import dannypx.foe.type.tuple.Pair;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class PlaceholderHandlerV2 extends Handler {
    private static PlaceholderHandlerV2 INSTANCE = new PlaceholderHandlerV2();

    public static PlaceholderHandlerV2 instance() {
        if (INSTANCE == null) {
            INSTANCE = new PlaceholderHandlerV2();
        }
        return INSTANCE;
    }

    //region Fields
    private final PlaceholderEvaluator evaluator = new PlaceholderEvaluator();
    private final Map<String, PlaceholderCompiler.ThrottledPlaceholder> throttled = new ConcurrentHashMap<>();
    //endregion

    //region Methods
    @Override
    public void tick() {
        PlaceholderCompiler.tick();
    }

    @Override
    public void init() {
        PlaceholderRegistry.init();
    }

    public PlaceholderResult resolve(String placeholderString) {
        PlaceholderCompiler.ThrottledPlaceholder t = throttled.computeIfAbsent(placeholderString, PlaceholderCompiler.ThrottledPlaceholder::new);
        return t.get(evaluator);
    }

    public void setUpdateIntervalMillis(int millis) {
        PlaceholderCompiler.setUpdateIntervalMillis(millis);
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
