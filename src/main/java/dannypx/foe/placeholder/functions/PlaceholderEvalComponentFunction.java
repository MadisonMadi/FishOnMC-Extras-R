package dannypx.foe.placeholder.functions;

import net.minecraft.network.chat.MutableComponent;

import java.util.List;

public interface PlaceholderEvalComponentFunction {
    MutableComponent resolve(List<PlaceholderValue> args);
}
