package dannypx.foe.screens.element.hud;

import dannypx.foe.FishOnMCExtras;
import dannypx.foe.handler.fetch.BossEventHandler;
import dannypx.foe.handler.fetch.TabOverlayHandler;
import dannypx.foe.handler.logic.CrewHandler;
import dannypx.foe.handler.logic.LoadingHandler;
import dannypx.foe.helper.TextHelper;
import dannypx.foe.config.Configs;
import dannypx.foe.helper.GuiGraphicsHelper;
import dannypx.foe.screens.element.Element;
import net.minecraft.ChatFormatting;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;

public class LocationElement extends Element {
    //region Fields
    private static final int TEXTURE_WIDTH = 160;
    private static final int TEXTURE_HEIGHT = 36;

    private static final Identifier LOCATION_TEXTURE = Identifier.fromNamespaceAndPath(FishOnMCExtras.MOD_ID, "elements/location");
    private static final Identifier LOCATION_TEXTURE_FLIP = Identifier.fromNamespaceAndPath(FishOnMCExtras.MOD_ID, "elements/location_flip");
    //endregion

    public LocationElement() {
        super(TEXTURE_WIDTH,
                TEXTURE_HEIGHT,
                Configs.hudConfig.locationElementXPosition.get() / 100f,
                Configs.hudConfig.locationElementYPosition.get() / 100f,
                Configs.hudConfig.locationElementAlignment.get(),
                Configs.hudConfig.locationElementGroup.translation("Location Element"),
                false);
    }

    public LocationElement(boolean isCopy) {
        super(TEXTURE_WIDTH,
                TEXTURE_HEIGHT,
                Configs.hudConfig.locationElementXPosition.get() / 100f,
                Configs.hudConfig.locationElementYPosition.get() / 100f,
                Configs.hudConfig.locationElementAlignment.get(),
                Configs.hudConfig.locationElementGroup.translation("Location Element"),
                isCopy);
    }

    //region Methods
    @Override
    public void extractRenderState(GuiGraphicsExtractor guiGraphicsExtractor, DeltaTracker deltaTracker) {
        int scaledWidth = (int) (Minecraft.getInstance().getWindow().getGuiScaledWidth() * (1 / Configs.hudConfig.locationElementScale.get()));
        int scaledHeight = (int) (Minecraft.getInstance().getWindow().getGuiScaledHeight() * (1 / Configs.hudConfig.locationElementScale.get()));

        guiGraphicsExtractor.pose().pushMatrix();
        guiGraphicsExtractor.pose().scale(Configs.hudConfig.locationElementScale.get(), Configs.hudConfig.locationElementScale.get());
        if(LoadingHandler.instance().isLoadingDone()
                && Configs.hudConfig.showLocationElement.get()
                && TabOverlayHandler.instance().isInInstance()
        ) {
            // Position
            if(!isCopy) {
                xPos = Configs.hudConfig.locationElementXPosition.get() / 100f;
                yPos = Configs.hudConfig.locationElementYPosition.get() / 100f;
            }

            int x = switch (Configs.hudConfig.locationElementAlignment.get()) {
                case TOP_LEFT -> Math.round(scaledWidth * xPos);
                case TOP_RIGHT -> scaledWidth
                        - Math.round(scaledWidth * xPos);
                default -> 0;
            };
            int y = Math.round(scaledHeight * yPos);

            this.extractRenderTexture(guiGraphicsExtractor, x, y);
            this.extractRenderText(guiGraphicsExtractor, Minecraft.getInstance().font, x, y);
        }
        guiGraphicsExtractor.pose().popMatrix();
    }

    private void extractRenderText(GuiGraphicsExtractor guiGraphicsExtractor, Font font, int x, int y) {
        int component1x = 24;
        int component1y = 7;

        Component temperature = BossEventHandler.instance().getTemperature();
        Component weather = TextHelper.concat(
                BossEventHandler.instance().getWeather(),
                Component.literal(" "),
                temperature
        );
        int weatherWidth = font.width(TextHelper.smallCaps(weather.getString()));

        int component2x = 52;
        int component2y = 7;

        Component location = BossEventHandler.instance().getLocation();
        Component subLocation = BossEventHandler.instance().getSubLocation();

        Component locationTotal = switch (Configs.hudConfig.locationElementAlignment.get()) {
            case TOP_LEFT -> subLocation.getString().isBlank() ? TextHelper.concat(location) : TextHelper.concat(
                    location,
                    Component.literal(" | ").withStyle(ChatFormatting.DARK_GRAY),
                    subLocation
            );
            case TOP_RIGHT -> subLocation.getString().isBlank() ? TextHelper.concat(location) : TextHelper.concat(
                    subLocation,
                    Component.literal(" | ").withStyle(ChatFormatting.DARK_GRAY),
                    location
            );
            default -> Component.empty();
        };
        int locationWidth = font.width(TextHelper.smallCaps(locationTotal.getString()));

        int component3x = 16;
        int component3y = 26;

        Component time = BossEventHandler.instance().getTime();
        Component nearbyCrew = TextHelper.literal(CrewHandler.instance().isCrewNearby(), true);

        Component crewtimeTotal = switch (Configs.hudConfig.locationElementAlignment.get()) {
            case TOP_LEFT -> TextHelper.concat(
                    time,
                    Component.literal(" | ").withStyle(ChatFormatting.DARK_GRAY),
                    Component.literal("Crew Nearby: ").withStyle(ChatFormatting.WHITE),
                    nearbyCrew
            );
            case TOP_RIGHT -> TextHelper.concat(
                    Component.literal("Crew Nearby: ").withStyle(ChatFormatting.WHITE),
                    nearbyCrew,
                    Component.literal(" | ").withStyle(ChatFormatting.DARK_GRAY),
                    time
            );
            default -> Component.empty();
        };
        int crewtimeWidth = font.width(TextHelper.smallCaps(crewtimeTotal.getString()));

        switch (Configs.hudConfig.locationElementAlignment.get()) {
            case TOP_LEFT -> {
                GuiGraphicsHelper.text(guiGraphicsExtractor, font,
                        weather,
                        x + component1x - (weatherWidth / 2), y + component1y,
                        true,
                        true,
                        false,
                        true);

                GuiGraphicsHelper.text(guiGraphicsExtractor, font,
                        locationTotal,
                        x + component2x, y + component2y,
                        true,
                        true,
                        false,
                        true);

                GuiGraphicsHelper.text(guiGraphicsExtractor, font,
                        crewtimeTotal,
                        x + component3x, y + component3y,
                        true,
                        true,
                        false,
                        true);
            }
            case TOP_RIGHT -> {
                GuiGraphicsHelper.text(guiGraphicsExtractor, font,
                        weather,
                        x - component1x - (weatherWidth / 2), y + component1y,
                        true,
                        true,
                        false,
                        true);

                GuiGraphicsHelper.text(guiGraphicsExtractor, font,
                        locationTotal,
                        x - component2x - locationWidth, y + component2y,
                        true,
                        true,
                        false,
                        true);

                GuiGraphicsHelper.text(guiGraphicsExtractor, font,
                        crewtimeTotal,
                        x - component3x - crewtimeWidth, y + component3y,
                        true,
                        true,
                        false,
                        true);
            }
        }
    }

    private void extractRenderTexture(GuiGraphicsExtractor guiGraphicsExtractor, int x, int y) {
        switch (Configs.hudConfig.locationElementAlignment.get()) {
            case TOP_LEFT -> guiGraphicsExtractor.blitSprite(RenderPipelines.GUI_TEXTURED,
                    LOCATION_TEXTURE,
                    x, y,
                    width, height
            );
            case TOP_RIGHT -> guiGraphicsExtractor.blitSprite(RenderPipelines.GUI_TEXTURED,
                    LOCATION_TEXTURE_FLIP,
                    x - width, y,
                    width, height
            );
        }
    }
    //endregion
}
