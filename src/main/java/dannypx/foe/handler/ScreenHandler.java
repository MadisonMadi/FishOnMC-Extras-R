package dannypx.foe.handler;

import dannypx.foe.config.Configs;
import dannypx.foe.helper.KeyBindHelper;
import dannypx.foe.helper.TextHelper;
import dannypx.foe.screens.interfaces.ScreenConstants;
import dannypx.foe.type.tuple.Pair;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.util.CommonColors;

public abstract class ScreenHandler implements ScreenConstants {
    protected final Minecraft minecraft = Minecraft.getInstance();

    public void init(Screen screen) {}
    public void extractRenderState(Screen screen, GuiGraphicsExtractor guiGraphicsExtractor, int mouseX, int mouseY, float tickDelta) {}
    protected abstract Map<String, Pair<MutableComponent, MutableComponent>> _getFields();

    public void extractRenderButtonHelp(GuiGraphicsExtractor guiGraphicsExtractor, boolean showInspect, boolean showScroll) {
        Font font = minecraft.font;
        List<Component> listHelp = new ArrayList<>();

        if(showInspect) listHelp.add(TextHelper.concat(
                Component.literal("Show more info "),
                Component.literal(KeyBindHelper.getKeyUnicode(Configs.keyBindConfig.inspectKeybind))
        ));
        if(showScroll) listHelp.add(Component.literal("Scroll through pages \uDB80\uDC67"));

        for (int i = 0; i < listHelp.size(); i++) {
            Component component = listHelp.get(i);

            guiGraphicsExtractor.text(
                    font, component,
                    minecraft.getWindow().getGuiScaledWidth() - PADDING - font.width(component),
                    minecraft.getWindow().getGuiScaledHeight() - PADDING - font.lineHeight
                            - (font.lineHeight + 12) * i,
                    CommonColors.WHITE,
                    true
            );
        }
    }
}