package dannypx.foe.placeholder.functions;

import net.minecraft.network.chat.MutableComponent;

import java.util.List;

public interface PlaceholderComponentFunction {
    MutableComponent resolve(List<String> args);
}
