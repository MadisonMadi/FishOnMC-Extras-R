package dannypx.foe.screens;

import dannypx.foe.handler.store.CustomHudDataHandler;
import dannypx.foe.handler.store.CustomHudIconDataHandler;
import dannypx.foe.type.Alignment;
import dannypx.foe.type.tuple.Pair;
import dannypx.foe.config.Configs;
import dannypx.foe.screens.element.Element;
import dannypx.foe.screens.element.hud.*;
import dannypx.foe.screens.widget.MovableBoxWidget;
import me.fzzyhmstrs.fzzy_config.api.ConfigApiJava;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import java.util.ArrayList;
import java.util.List;

public class MoveElementScreen extends DefaultModScreen {
    //region Fields
    List<Pair<String, Element>> customHudElements = new ArrayList<>();
    //endregion

    //region Methods
    public MoveElementScreen(Screen parent) {
        super(parent, Component.literal("Move Elements Screen"), true);
    }

    @Override
    protected void init() {
        super.init();
        this.assembleCustomHudElements();
        this.extractRenderWidgets();
    }

    private void assembleCustomHudElements() {
        customHudElements.clear();
        CustomHudDataHandler.instance().getCustomHudData().customHudRawDataList
                .forEach((key, hud) -> customHudElements.add(Pair.of(key, new CustomHudElement(hud, Component.literal(key)))));
        CustomHudIconDataHandler.instance().getCustomHudIconData().customHudIconDataList
                .forEach((key, icon) -> customHudElements.add(Pair.of(key, new CustomHudIconElement(icon, Component.literal(key)))));
    }

    @Override
    public void extractRenderState(GuiGraphicsExtractor guiGraphicsExtractor, int mouseX, int mouseY, float delta) {
        super.extractRenderState(guiGraphicsExtractor, mouseX, mouseY, delta);
    }

    private void extractRenderWidgets() {
        List<AbstractWidget> widgets = new ArrayList<>();

        widgets.add(new MovableBoxWidget(this.minecraft,
                new ProfileElement(true),
                Alignment.getTopCorners(),
                new MovableBoxWidget.Callback() {
                    @Override
                    public void onRelease(int xPercent, int yPercent, Alignment alignment) {
                        Configs.hudConfig.profileElementXPosition.accept(xPercent);
                        Configs.hudConfig.profileElementYPosition.accept(yPercent);
                        Configs.hudConfig.profileElementAlignment.accept(alignment);
                        Configs.hudConfig.save();
                    }

                    @Override
                    public void onConfig() {
                        ConfigApiJava.INSTANCE.openScreen(Configs.hudConfig.showProfileElement.translationKey());
                    }
                }
        ));

        widgets.add(new MovableBoxWidget(this.minecraft,
                new LocationElement(true),
                Alignment.getTopCorners(),
                new MovableBoxWidget.Callback() {
                    @Override
                    public void onRelease(int xPercent, int yPercent, Alignment alignment) {
                        Configs.hudConfig.locationElementXPosition.accept(xPercent);
                        Configs.hudConfig.locationElementYPosition.accept(yPercent);
                        Configs.hudConfig.locationElementAlignment.accept(alignment);
                        Configs.hudConfig.save();
                    }

                    @Override
                    public void onConfig() {
                        ConfigApiJava.INSTANCE.openScreen(Configs.hudConfig.showLocationElement.translationKey());
                    }
                }
        ));

        widgets.add(new MovableBoxWidget(this.minecraft,
                new HotbarElement(true),
                Alignment.getBottom(),
                new MovableBoxWidget.Callback() {
                    @Override
                    public void onRelease(int xPercent, int yPercent, Alignment alignment) {
                        Configs.hudConfig.hotbarElementXPosition.accept(xPercent);
                        Configs.hudConfig.hotbarElementYPosition.accept(yPercent);
                        Configs.hudConfig.hotbarElementAlignment.accept(alignment);
                        Configs.hudConfig.save();
                    }

                    @Override
                    public void onConfig() {
                        ConfigApiJava.INSTANCE.openScreen(Configs.hudConfig.showHotbarElement.translationKey());
                    }
                }
        ));

        widgets.add(new MovableBoxWidget(this.minecraft,
                new PetElement(true),
                Alignment.getTopCorners(),
                new MovableBoxWidget.Callback() {
                    @Override
                    public void onRelease(int xPercent, int yPercent, Alignment alignment) {
                        Configs.hudConfig.petElementXPosition.accept(xPercent);
                        Configs.hudConfig.petElementYPosition.accept(yPercent);
                        Configs.hudConfig.petElementAlignment.accept(alignment);
                        Configs.hudConfig.save();
                    }

                    @Override
                    public void onConfig() {
                        ConfigApiJava.INSTANCE.openScreen(Configs.hudConfig.showPetElement.translationKey());
                    }
                }
        ));

        widgets.add(new MovableBoxWidget(this.minecraft,
                new NotifierElement(true),
                Alignment.getCorners(),
                new MovableBoxWidget.Callback() {
                    @Override
                    public void onRelease(int xPercent, int yPercent, Alignment alignment) {
                        Configs.hudConfig.notifierElementXPosition.accept(xPercent);
                        Configs.hudConfig.notifierElementYPosition.accept(yPercent);
                        Configs.hudConfig.notifierElementAlignment.accept(alignment);
                        Configs.hudConfig.save();
                    }

                    @Override
                    public void onConfig() {
                        ConfigApiJava.INSTANCE.openScreen(Configs.hudConfig.showNotifierElement.translationKey());
                    }
                }
        ));

        customHudElements.forEach(element -> {
            switch (element.value2()) {
                case CustomHudElement ignored -> {
                    widgets.add(new MovableBoxWidget(this.minecraft,
                            element.value2(),
                            Alignment.getAll(),
                            new MovableBoxWidget.Callback() {
                                @Override
                                public void onRelease(int xPercent, int yPercent, Alignment alignment) {
                                    CustomHudDataHandler.instance().updateHud(element.value1(), xPercent, yPercent, alignment);
                                }

                                @Override
                                public void onConfig() {
                                    Minecraft.getInstance().setScreen(new CustomHudMakerScreen(Minecraft.getInstance().screen));
                                }
                            }
                    ));
                }
                case CustomHudIconElement ignored -> {
                    widgets.add(new MovableBoxWidget(this.minecraft,
                            element.value2(),
                            Alignment.getAll(),
                            new MovableBoxWidget.Callback() {
                                @Override
                                public void onRelease(int xPercent, int yPercent, Alignment alignment) {
                                    CustomHudIconDataHandler.instance().updateHudIcon(element.value1(), xPercent, yPercent, alignment);
                                }

                                @Override
                                public void onConfig() {
                                    Minecraft.getInstance().setScreen(new CustomHudIconMakerScreen(Minecraft.getInstance().screen));
                                }
                            }
                    ));
                }
                default -> throw new IllegalStateException("Unexpected value: " + element.value2());
            }
        });

        if(Configs.debugConfig.debugMode.get()) {
            widgets.add(new MovableBoxWidget(this.minecraft,
                    new _DebugField(true),
                    Alignment.getCorners(),
                    new MovableBoxWidget.Callback() {
                        @Override
                        public void onRelease(int xPercent, int yPercent, Alignment alignment) {
                            Configs.debugConfig.debugFieldXPosition.accept(xPercent);
                            Configs.debugConfig.debugFieldYPosition.accept(yPercent);
                            Configs.debugConfig.debugFieldAlignment.accept(alignment);
                            Configs.debugConfig.save();
                        }

                        @Override
                        public void onConfig() {
                            ConfigApiJava.INSTANCE.openScreen(Configs.debugConfig.debugFieldElement.translationKey());
                        }
                    }
            ));
        }

        widgets.forEach(this::addRenderableWidget);
    }
    //endregion
}
