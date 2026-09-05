package dannypx.foe.handler.logic;

import dannypx.foe.handler.Handler;
import dannypx.foe.helper.TextHelper;
import dannypx.foe.type.tuple.Pair;
import dannypx.foe.type.placeholder.PlaceholderValue;
import dannypx.foe.type.placeholder.StringValue;
import java.util.Map;
import java.util.regex.Pattern;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;

public class ConnectionHandler extends Handler {
    private static ConnectionHandler INSTANCE = new ConnectionHandler();

    public static ConnectionHandler instance() {
        if (INSTANCE == null) {
            INSTANCE = new ConnectionHandler();
        }
        return INSTANCE;
    }

    //region Fields
    private boolean isOnServer = false;
    private boolean wasOnServer = false;

    public boolean isOnServer() {
        return isOnServer;
    }

    public boolean wasOnServer() {
        return wasOnServer;
    }
    //endregion

    //region Methods
    public void init() {
        this.checkIfOnServer();
    }

    public void onLeave() {
        isOnServer = false;
    }

    private void checkIfOnServer() {
        isOnServer = this.checkFOMCAddress();
        if(isOnServer) {
            LoggerHandler.info("On server (play.fishonmc.net)");
            wasOnServer = true;
        } else {
            LoggerHandler.info("Not on server");
        }
    }

    private boolean checkFOMCAddress() {
        ServerData serverData = minecraft.getCurrentServer();
        if(serverData != null) {
            return serverData.ip.equalsIgnoreCase("play.fishonmc.net")
                    || serverData.ip.equalsIgnoreCase("asia.fishonmc.net")
                    || serverData.ip.equalsIgnoreCase("fishonmc.net");
        } return false;
    }
    //endregion

    //region Dev
    protected Map<String, Pair<MutableComponent, MutableComponent>> _getFields() {
        return Map.of(
                "isOnServer", Pair.of(TextHelper.literal(isOnServer()), Component.empty()),
                "wasOnServer", Pair.of(TextHelper.literal(wasOnServer()), Component.empty())
        );
    }
    //endregion
}
