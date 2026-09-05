package dannypx.foe.handler.fetch;

import com.mojang.authlib.GameProfile;
import dannypx.foe.handler.Handler;
import dannypx.foe.handler.logic.LoggerHandler;
import dannypx.foe.helper.TextHelper;
import dannypx.foe.type.tuple.Pair;
import dannypx.foe.mixin.accessor.PlayerTabOverlayAccessor;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.PlayerTabOverlay;
import net.minecraft.client.multiplayer.PlayerInfo;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;

public class TabOverlayHandler extends Handler {
    private static TabOverlayHandler INSTANCE = new TabOverlayHandler();

    public static TabOverlayHandler instance() {
        if (INSTANCE == null) {
            INSTANCE = new TabOverlayHandler();
        }
        return INSTANCE;
    }

    //region Fields
    private Component playerName = Component.empty();
    private String instance = "";
    private boolean isInInstance = false;

    public Component getPlayerName() {
        return playerName;
    }

    public String getInstance() {
        return instance;
    }

    public boolean isInInstance() {
        return isInInstance;
    }
    //endregion

    //region Methods
    public void tick() {
        this.fetchFromPlayerTabOverlay();
    }

    public static GameProfile getPlayer(UUID uuid) {
        if (Minecraft.getInstance().getConnection() != null) {
            PlayerInfo playerInfo = Minecraft.getInstance().getConnection().getPlayerInfo(uuid);
            return playerInfo != null ? playerInfo.getProfile() : null;
        }
        return null;
    }

    private void fetchFromPlayerTabOverlay() {
        try {
            PlayerTabOverlay playerTabOverlay = minecraft.gui.getTabList();
            this.fetchFromListing(playerTabOverlay);
            this.fetchFromHeaderAndFooter(playerTabOverlay);
        } catch (Exception e) {
            LoggerHandler.error(e);
        }
    }

    private void fetchFromListing(PlayerTabOverlay playerTabOverlay) {
        if (minecraft.player != null) {
            this.playerName = playerTabOverlay.getNameForDisplay(
                    Objects.requireNonNull(minecraft.getConnection())
                            .getPlayerInfo(minecraft.player.getUUID())
            );
        }
    }

    private void fetchFromHeaderAndFooter(PlayerTabOverlay playerListHud) {
        if(((PlayerTabOverlayAccessor) playerListHud).getFooter() != null) {
            if(((PlayerTabOverlayAccessor) playerListHud).getFooter().getString().contains("ɪɴꜱᴛᴀɴᴄᴇ")) {
                this.isInInstance = true;
                String footer = ((PlayerTabOverlayAccessor) playerListHud).getFooter().getString();
                this.instance = footer.substring(
                        footer.indexOf("ɪɴꜱᴛᴀɴᴄᴇ") + 8,
                        footer.lastIndexOf("(")
                ).trim();
            } else {
                this.isInInstance = false;
            }
        }
    }
    //endregion

    //region Dev
    /// Field, Pair<Value, Tooltip>
    protected Map<String, Pair<MutableComponent, MutableComponent>> _getFields() {
        return Map.of(
                "playerName", Pair.of(getPlayerName().copy(), Component.empty()),
                "instance", Pair.of(Component.literal(getInstance()), Component.empty()),
                "isInInstance", Pair.of(TextHelper.literal(isInInstance()), Component.empty())
        );
    }
    //endregion
}
