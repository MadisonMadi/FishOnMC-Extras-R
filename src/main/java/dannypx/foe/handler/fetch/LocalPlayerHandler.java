package dannypx.foe.handler.fetch;

import dannypx.foe.handler.Handler;
import dannypx.foe.helper.TextHelper;
import dannypx.foe.type.tuple.Pair;

import java.util.Map;

import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;

public class LocalPlayerHandler extends Handler {
    private static LocalPlayerHandler INSTANCE = new LocalPlayerHandler();

    public static LocalPlayerHandler instance() {
        if (INSTANCE == null) {
            INSTANCE = new LocalPlayerHandler();
        }
        return INSTANCE;
    }

    //region Fields
    private Component name = Component.empty();
    private int experienceLevel = 0;
    private float experienceProgress = 0f;

    public Component getName() {
        return name;
    }

    public int getExperienceLevel() {
        return experienceLevel;
    }

    public float getExperienceProgress() {
        return experienceProgress;
    }
    //endregion

    //region Methods
    public void tick() {
        LocalPlayer localPlayer = minecraft.player;
        if(localPlayer != null) {
            fetchFromLocalPlayer(localPlayer);
        }
    }

    private void fetchFromLocalPlayer(LocalPlayer localPlayer) {
        this.name = localPlayer.getName();
        this.experienceLevel = localPlayer.experienceLevel;
        this.experienceProgress = localPlayer.experienceProgress;
    }
    //endregion

    //region Dev
    /// Field, Pair<Value, Tooltip>
    protected Map<String, Pair<MutableComponent, MutableComponent>> _getFields() {
        return Map.of(
                "name", Pair.of(getName().copy(), Component.empty()),
                "experienceLevel", Pair.of(TextHelper.literal(getExperienceLevel()), Component.empty()),
                "experienceProgress", Pair.of(TextHelper.literal(getExperienceProgress()), Component.empty())
        );
    }
    //endregion
}
