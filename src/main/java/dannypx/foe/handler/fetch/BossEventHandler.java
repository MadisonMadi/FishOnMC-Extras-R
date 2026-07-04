package dannypx.foe.handler.fetch;

import com.google.gson.*;
import dannypx.foe.handler.Handler;
import dannypx.foe.handler.logic.PlaceholderHandler;
import dannypx.foe.helper.TextHelper;
import dannypx.foe.type.tuple.Pair;
import dannypx.foe.type.placeholder.PlaceholderValue;
import dannypx.foe.type.placeholder.ComponentValue;
import dannypx.foe.mixin.accessor.BossHealthOverlayAccessor;
import java.util.*;
import java.util.regex.Pattern;
import net.minecraft.client.gui.components.LerpingBossEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.TextColor;

public class BossEventHandler extends Handler {
    private static BossEventHandler INSTANCE = new BossEventHandler();

    public static BossEventHandler instance() {
        if (INSTANCE == null) {
            INSTANCE = new BossEventHandler();
        }
        return INSTANCE;
    }

    private static final Map<String, TextColor> LOCATION_COLORS = new HashMap<>();
    private static final Map<String, List<TextColor>> LOCATION_LETTER_COLORS = new HashMap<>();

    static {
        LOCATION_COLORS.put("Cypress Lake", TextColor.fromRgb(0x5CAE65));
        LOCATION_COLORS.put("Kenai River", TextColor.fromRgb(0x68D499));
        LOCATION_COLORS.put("Lake Biwa", TextColor.fromRgb(0xFBC0FA));
        LOCATION_COLORS.put("Murray River", TextColor.fromRgb(0xCD5916));
        LOCATION_COLORS.put("Everglades", TextColor.fromRgb(0x2EBB8D));
        LOCATION_COLORS.put("Key West", TextColor.fromRgb(0xFBF17C));
        LOCATION_COLORS.put("Toledo Bend Reservoir", TextColor.fromRgb(0x99A7D0));
        LOCATION_COLORS.put("Great Lakes", TextColor.fromRgb(0x3CABF3));
        LOCATION_COLORS.put("Danube River", TextColor.fromRgb(0xFBC598));
        LOCATION_COLORS.put("Oil Rig", TextColor.fromRgb(0xFFEE48));
        LOCATION_COLORS.put("Amazon River", TextColor.fromRgb(0x3EA729));
        LOCATION_COLORS.put("Mediterranean Sea", TextColor.fromRgb(0xF0FB37));
        LOCATION_COLORS.put("Cape Cod", TextColor.fromRgb(0xBBF5FB));
        LOCATION_LETTER_COLORS.put("Hawaii", Arrays.asList(TextColor.fromRgb(0xFB933B), TextColor.fromRgb(0xFCB140), TextColor.fromRgb(0xEACD4D), TextColor.fromRgb(0xB2E66C), TextColor.fromRgb(0x75F0A6), TextColor.fromRgb(0x35F4EF)));
        LOCATION_LETTER_COLORS.put("Lofoten Islands", Arrays.asList(TextColor.fromRgb(0xCDDAD7), TextColor.fromRgb(0xCCDFD2), TextColor.fromRgb(0xCCE3CC), TextColor.fromRgb(0xCBE8C7), TextColor.fromRgb(0xCBECC1), TextColor.fromRgb(0xCAF1BC), TextColor.fromRgb(0xCFF2C3), TextColor.fromRgb(0xD9F3D0), TextColor.fromRgb(0xDEF4D7), TextColor.fromRgb(0xE4F5DD), TextColor.fromRgb(0xE9F6E4), TextColor.fromRgb(0xEEF6EB), TextColor.fromRgb(0xF3F7F1), TextColor.fromRgb(0xF8F8F8)));
        LOCATION_COLORS.put("Cairns", TextColor.fromRgb(0xA1C2FB));
    }

    //region Fields
    private MutableComponent location = Component.empty();
    private MutableComponent weather = Component.empty();
    private MutableComponent time = Component.empty();
    private MutableComponent temperature = Component.empty();
    private MutableComponent subLocation = Component.empty();
    private String prevBossEvent = "";

    public MutableComponent getLocation() {
        return location;
    }

    public MutableComponent getWeather() {
        return weather;
    }

    public MutableComponent getTime() {
        return time;
    }

    public MutableComponent getTemperature() {
        return temperature;
    }

    public MutableComponent getSubLocation() {
        return subLocation;
    }

    public Pair<Boolean, PlaceholderValue> getBossBar(String[] params) {
        if(params.length > 0) {
            Pattern fieldPattern = Pattern.compile("^(location|weather|time|temperature|sub_location)$");

            if(fieldPattern.matcher(params[0]).matches()
                    && params.length == 1
            ) {
                return switch(params[0]) {
                    case "location" -> PlaceholderHandler.getPlaceholderValue(ComponentValue.of(getLocation()));
                    case "weather" -> PlaceholderHandler.getPlaceholderValue(ComponentValue.of(getWeather()));
                    case "time" -> PlaceholderHandler.getPlaceholderValue(ComponentValue.of(getTime()));
                    case "temperature" -> PlaceholderHandler.getPlaceholderValue(ComponentValue.of(getTemperature()));
                    case "sub_location" -> PlaceholderHandler.getPlaceholderValue(ComponentValue.of(getSubLocation()), true);
                    default -> PlaceholderHandler.noResult();
                };
            }
        }
        return PlaceholderHandler.noResult();
    }
    //endregion

    //region Methods
    public void tick() {
        this.fetchFromBossBar();
    }

    private void fetchFromBossBar() {
        Map<UUID, LerpingBossEvent> bossEventMap = ((BossHealthOverlayAccessor) (minecraft.gui.getBossOverlay())).getEvents();
        if(!bossEventMap.isEmpty()) {
            bossEventMap.forEach(((uuid, lerpingBossEvent) -> {
                if(lerpingBossEvent.getName().getString().contains("\uF039") && !Objects.equals(prevBossEvent, lerpingBossEvent.getName().getString())) {
                    prevBossEvent = lerpingBossEvent.getName().getString();
                    String json = TextHelper.componentToJson(lerpingBossEvent.getName());
                    JsonObject jsonObject = JsonParser.parseString(json).getAsJsonObject();

                    if(jsonObject.get("extra") != null) {
                        JsonObject locationObject = jsonObject.get("extra").getAsJsonArray().get(0).getAsJsonObject()
                                .get("extra").getAsJsonArray().get(0).getAsJsonObject();
                        String locationString = locationObject.get("text").getAsString().substring(4).trim();
                        if(locationString.contains("(")) {
                            locationString = locationString.substring(0, locationString.indexOf("(") - 1);
                        }

                        List<TextColor> letterColors = LOCATION_LETTER_COLORS.get(locationString);
                        if (letterColors != null) {
                            MutableComponent styled = Component.empty();
                            for (int i = 0; i < locationString.length(); i++) {
                                int colorValue = (i < letterColors.size()) ? letterColors.get(i).getValue() : 0xFFFFFF;
                                styled.append(Component.literal(String.valueOf(locationString.charAt(i))).withColor(colorValue));
                            }
                            location = styled;
                        } else {
                            TextColor color = LOCATION_COLORS.get(locationString);
                            location = Component.literal(locationString).withColor(color != null ? color.getValue() : 0xFFFFFF);
                        }

                        JsonObject weatherObject = jsonObject.get("extra").getAsJsonArray().get(2).getAsJsonObject()
                                .get("extra").getAsJsonArray().get(0).getAsJsonObject();
                        weather = Component.literal(weatherObject.get("text").getAsString())
                                .withColor(TextColor.parseColor(weatherObject.get("color").getAsString()).getOrThrow().getValue());

                        if(weather.getString().trim().length() != 1) {
                            weather = Component.literal(weatherObject.get("text").getAsString().substring(0, 1))
                                    .withColor(TextColor.parseColor(weatherObject.get("color").getAsString()).getOrThrow().getValue());

                            time = Component.literal(weatherObject.get("text").getAsString().substring(1).trim())
                                    .withColor(TextColor.parseColor(weatherObject.get("color").getAsString()).getOrThrow().getValue());

                            if(weatherObject.get("extra") != null) {
                                JsonObject temperatureObject = weatherObject.get("extra").getAsJsonArray().get(0).getAsJsonObject();
                                temperature = Component.literal(temperatureObject.get("text").getAsString().trim())
                                        .withColor(TextColor.parseColor(temperatureObject.get("color").getAsString()).getOrThrow().getValue());
                            }
                            return;
                        }

                        if(weatherObject.get("extra") != null) {
                            JsonObject timeObject = weatherObject.get("extra").getAsJsonArray().get(0).getAsJsonObject();
                            time = Component.literal(timeObject.get("text").getAsString().trim())
                                    .withColor(TextColor.parseColor(timeObject.get("color").getAsString()).getOrThrow().getValue());

                            if(timeObject.get("extra") != null) {
                                JsonObject temperatureObject = timeObject.get("extra").getAsJsonArray().get(0).getAsJsonObject();
                                temperature = Component.literal(temperatureObject.get("text").getAsString().trim())
                                        .withColor(TextColor.parseColor(temperatureObject.get("color").getAsString()).getOrThrow().getValue());
                            }
                        }
                    }
                } else if(lerpingBossEvent.getName().getString().contains("\uA201\uEEE1\uA208")) {
                    JsonObject jsonObject = JsonParser.parseString(TextHelper.componentToJson(lerpingBossEvent.getName())).getAsJsonObject();

                    if(jsonObject.get("extra") != null) {
                        JsonObject locationObject = jsonObject.get("extra").getAsJsonArray().get(0).getAsJsonObject();
                        subLocation = Component.literal(locationObject.get("text").getAsString())
                                .withColor(TextColor.parseColor(locationObject.get("color").getAsString()).getOrThrow().getValue());
                    }
                } else {
                    subLocation = Component.empty();
                }
            }));
        }
    }
    //endregion

    //region Dev
    /// Field, Pair<Value, Tooltip>
    protected Map<String, Pair<MutableComponent, MutableComponent>> _getFields() {
        return Map.of(
                "location", Pair.of(getLocation(), Component.empty()),
                "weather", Pair.of(getWeather(), Component.empty()),
                "time", Pair.of(getTime(), Component.empty()),
                "temperature", Pair.of(getTemperature(), Component.empty()),
                "subLocation", Pair.of(getSubLocation(), Component.empty())
        );
    }
    //endregion
}
