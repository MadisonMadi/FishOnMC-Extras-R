package dannypx.foe.screens.element.hud;

import dannypx.foe.FishOnMCExtras;
import dannypx.foe.handler.fetch.TabOverlayHandler;
import dannypx.foe.handler.logic.InventoryHandler;
import dannypx.foe.handler.logic.LoadingHandler;
import dannypx.foe.helper.GuiGraphicsHelper;
import dannypx.foe.helper.TextHelper;
import dannypx.foe.config.Configs;
import dannypx.foe.screens.element.Element;
import dannypx.foe.type.StringStyle;
import net.minecraft.ChatFormatting;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;

import java.awt.*;

public class PetElement extends Element {
    //region Fields
    private static final int TEXTURE_WIDTH = 160;
    private static final int TEXTURE_HEIGHT = 37;

    private static final Identifier PET_TEXTURE = Identifier.fromNamespaceAndPath(FishOnMCExtras.MOD_ID, "elements/pet");
    private static final Identifier PET_TEXTURE_FLIP = Identifier.fromNamespaceAndPath(FishOnMCExtras.MOD_ID, "elements/pet_flip");
    //endregion

    public PetElement() {
        super(TEXTURE_WIDTH,
                TEXTURE_HEIGHT,
                Configs.hudConfig.petElementXPosition.get() / 100f,
                Configs.hudConfig.petElementYPosition.get() / 100f,
                Configs.hudConfig.petElementAlignment.get(),
                Configs.hudConfig.petElementGroup.translation("Pet Element"),
                false);
    }

    public PetElement(boolean isCopy) {
        super(TEXTURE_WIDTH,
                TEXTURE_HEIGHT,
                Configs.hudConfig.petElementXPosition.get() / 100f,
                Configs.hudConfig.petElementYPosition.get() / 100f,
                Configs.hudConfig.petElementAlignment.get(),
                Configs.hudConfig.petElementGroup.translation("Pet Element"),
                isCopy);
    }

    //region Methods
    @Override
    public void extractRenderState(GuiGraphicsExtractor guiGraphicsExtractor, DeltaTracker deltaTracker) {
        int scaledWidth = (int) (Minecraft.getInstance().getWindow().getGuiScaledWidth() * (1 / Configs.hudConfig.petElementScale.get()));
        int scaledHeight = (int) (Minecraft.getInstance().getWindow().getGuiScaledHeight() * (1 / Configs.hudConfig.petElementScale.get()));

        guiGraphicsExtractor.pose().pushMatrix();
        guiGraphicsExtractor.pose().scale(Configs.hudConfig.petElementScale.get(), Configs.hudConfig.petElementScale.get());
        if(LoadingHandler.instance().isLoadingDone()
                && Configs.hudConfig.showPetElement.get()
                && TabOverlayHandler.instance().isInInstance()
        ) {
            // Position
            if(!isCopy) {
                xPos = Configs.hudConfig.petElementXPosition.get() / 100f;
                yPos = Configs.hudConfig.petElementYPosition.get() / 100f;
            }

            int x = switch (Configs.hudConfig.petElementAlignment.get()) {
                case TOP_LEFT -> Math.round(scaledWidth * xPos);
                case TOP_RIGHT -> scaledWidth
                        - Math.round(scaledWidth * xPos);
                default -> 0;
            };
            int y = Math.round(scaledHeight * yPos);

            this.extractRenderTexture(guiGraphicsExtractor, x, y);
            this.extractRenderText(guiGraphicsExtractor, Minecraft.getInstance().font, x, y);
            this.extractRenderPetIcon(guiGraphicsExtractor, x, y);
        }
        guiGraphicsExtractor.pose().popMatrix();
    }

    private void extractRenderPetIcon(GuiGraphicsExtractor guiGraphicsExtractor, int x, int y) {
        if(Minecraft.getInstance().player != null && InventoryHandler.instance().hasPet()) {
            ItemStack pet = InventoryHandler.instance().getCurrentPet().getItemStack();

            guiGraphicsExtractor.pose().pushMatrix();
            switch (Configs.hudConfig.petElementAlignment.get()) {
                case TOP_LEFT -> {
                    guiGraphicsExtractor.pose().translate(x + 7, y + 7);
                    guiGraphicsExtractor.pose().scale(1.5f, 1.5f);
                }
                case TOP_RIGHT -> {
                    guiGraphicsExtractor.pose().translate(x - 7 - 24, y + 7);
                    guiGraphicsExtractor.pose().scale(1.5f, 1.5f);
                }
            }
            guiGraphicsExtractor.item(pet, 0, 0);
            guiGraphicsExtractor.pose().popMatrix();
        }
    }

    private void extractRenderText(GuiGraphicsExtractor guiGraphicsExtractor, Font font, int x, int y) {
        int component1x = 40;
        int component1y = 9;

        if(InventoryHandler.instance().hasPet()) {
            Component pet = TextHelper.concat(
                    InventoryHandler.instance().getCurrentPet().getRarityComponent(),
                    Component.literal(" "),
                    InventoryHandler.instance().getCurrentPet().getName());
            int petWidth = font.width(TextHelper.smallCaps(pet.getString()));

            int component2x = 40;
            int component2y = 21;

            int levelValue = InventoryHandler.instance().getCurrentPet().getLevel();
            MutableComponent level;
            if (levelValue >= 100) {
                level = Component.literal(String.valueOf(levelValue)).withColor(TextHelper.getRainbowColor());
            } else {
                float hue = (float)(levelValue - 1) / 99.0F;
                level = Component.literal(String.valueOf(levelValue)).withColor(Color.HSBtoRGB(hue, 1.0F, 1.0F));
            }

            int bars = 20;
            float rawProgress = InventoryHandler.instance().getCurrentPet().getProgress();
            rawProgress = Math.max(0, Math.min(1, rawProgress));
            int progress = Math.round(rawProgress * bars);
            progress = Math.clamp(progress, 0, bars);
            int progressLeft = bars - progress;

            MutableComponent progressComponent = Component.empty();
            for (int i = 0; i < progress; i++) {
                MutableComponent segment = Component.literal(" ").withStyle(ChatFormatting.STRIKETHROUGH);
                if (level.getStyle().getColor() != null) {
                    segment = segment.withStyle(style -> style.withColor(level.getStyle().getColor()));
                }
                progressComponent.append(segment);
            }

            Component progressLeftComponent = Component.literal(" ".repeat(Math.min(bars, progressLeft)))
                    .withStyle(ChatFormatting.STRIKETHROUGH, ChatFormatting.DARK_GRAY);

            MutableComponent levelComponent = TextHelper.concat(
                    Component.literal("LV. ").withStyle(ChatFormatting.GRAY),
                    level,
                    Component.literal(" [").withStyle(ChatFormatting.DARK_GRAY),
                    progressComponent,
                    progressLeftComponent,
                    Component.literal("]").withStyle(ChatFormatting.DARK_GRAY)
            );
            int levelWidth = font.width(TextHelper.smallCaps(levelComponent.getString()));

            switch (Configs.hudConfig.petElementAlignment.get()) {
                case TOP_LEFT -> {

                    GuiGraphicsHelper.text(guiGraphicsExtractor, font,
                            pet,
                            x + component1x, y + component1y,
                            StringStyle.SHADOW, StringStyle.MIDDLE, StringStyle.HAS_CUSTOM_FONT, StringStyle.SMALL_CAPS);

                    GuiGraphicsHelper.text(guiGraphicsExtractor, font,
                            levelComponent,
                            x + component2x, y + component2y,
                            StringStyle.SHADOW, StringStyle.MIDDLE, StringStyle.SMALL_CAPS);
                }
                case TOP_RIGHT -> {
                    GuiGraphicsHelper.text(guiGraphicsExtractor, font,
                            pet,
                            x - component1x - petWidth, y + component1y,
                            StringStyle.SHADOW, StringStyle.MIDDLE, StringStyle.HAS_CUSTOM_FONT, StringStyle.SMALL_CAPS);

                    GuiGraphicsHelper.text(guiGraphicsExtractor, font,
                            levelComponent,
                            x - component2x - levelWidth, y + component2y,
                            StringStyle.SHADOW, StringStyle.MIDDLE, StringStyle.SMALL_CAPS);
                }
            }
        } else {
            Component pet = Component.literal("No pet equipped").withStyle(ChatFormatting.GRAY);
            int petWidth = font.width(TextHelper.smallCaps(pet.getString()));

            switch (Configs.hudConfig.petElementAlignment.get()) {
                case TOP_LEFT -> GuiGraphicsHelper.text(guiGraphicsExtractor, font,
                        pet,
                        x + component1x, y + component1y,
                        StringStyle.SHADOW, StringStyle.MIDDLE, StringStyle.SMALL_CAPS);
                case TOP_RIGHT -> GuiGraphicsHelper.text(guiGraphicsExtractor, font,
                        pet,
                        x - component1x - petWidth, y + component1y,
                        StringStyle.SHADOW, StringStyle.MIDDLE, StringStyle.SMALL_CAPS);
            }
        }
    }

    private void extractRenderTexture(GuiGraphicsExtractor guiGraphicsExtractor, int x, int y) {
        switch (Configs.hudConfig.petElementAlignment.get()) {
            case TOP_LEFT -> guiGraphicsExtractor.blitSprite(RenderPipelines.GUI_TEXTURED,
                    PET_TEXTURE,
                    x, y,
                    width, height
            );
            case TOP_RIGHT -> guiGraphicsExtractor.blitSprite(RenderPipelines.GUI_TEXTURED,
                    PET_TEXTURE_FLIP,
                    x - width, y,
                    width, height
            );
        }
    }
    //endregion
}