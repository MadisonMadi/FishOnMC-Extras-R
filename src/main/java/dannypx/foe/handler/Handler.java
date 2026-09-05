package dannypx.foe.handler;

import dannypx.foe.type.tuple.Pair;
import java.util.Map;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.MutableComponent;

public abstract class Handler {
    protected static Minecraft minecraft = Minecraft.getInstance();

    public void init() {}
    public void tick() {}
    protected abstract Map<String, Pair<MutableComponent, MutableComponent>> _getFields();
}