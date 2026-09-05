package dannypx.foe.handler.fetch;

import dannypx.foe.handler.Handler;
import dannypx.foe.type.tuple.Pair;
import java.util.Map;

import net.minecraft.client.multiplayer.PlayerInfo;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;

public class NetworkHandler extends Handler {
    private static NetworkHandler INSTANCE = new NetworkHandler();

    public static NetworkHandler instance() {
        if (INSTANCE == null) {
            INSTANCE = new NetworkHandler();
        }
        return INSTANCE;
    }

    //region Fields
    private int ping = 0;

    public int getPing() {
        return ping;
    }
    //endregion

    //region Methods

    @Override
    public void tick() {
        this.fetchFromPlayerListEntry();
    }

    private void fetchFromPlayerListEntry() {
        if(minecraft.player != null && minecraft.getConnection() != null) {
            PlayerInfo entry = minecraft.getConnection().getPlayerInfo(minecraft.player.getUUID());

            if(entry != null) {
                ping = entry.getLatency();
            }
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
