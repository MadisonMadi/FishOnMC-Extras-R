package dannypx.foe.screens.widget;

import dannypx.foe.helper.GuiGraphicsHelper;
import dannypx.foe.helper.ItemStackHelper;
import dannypx.foe.helper.TextHelper;
import dannypx.foe.type.StringStyle;
import dannypx.foe.type.tuple.Pair;
import dannypx.foe.screens.element.BoxElement;
import dannypx.foe.screens.element.Element;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;

public class SmallButtonWidget extends AbstractWidget {
    Minecraft minecraft = Minecraft.getInstance();


    private final ClickCallback clickCallback;

    Pattern PATTERN = Pattern.compile("^(?:([a-z_]+:[a-z_]+)(?:\\[(.*)\\])?|(.))$");

    private final String icon;


    Pair<String, Element> box;
    Pair<String, Element> box_hover;

    List<Pair<String, Element>> elements = new ArrayList<>();

    public SmallButtonWidget(int x, int y, int width, int height, String icon, @Nullable Tooltip tooltip, Component message, ClickCallback clickCallback) {
        super(x, y, width, height, message);
        this.icon = icon;
        this.clickCallback = clickCallback;
        this.setTooltip(tooltip);
        this.init();

        box = Pair.of("button_box", new BoxElement(getX(),
                getY(),
                1,
                width, height, true, false));

        box_hover = Pair.of("button_hover_box", new BoxElement(getX(),
                getY(),
                1,
                width, height, true, true));
    }

    private void init() {
        this.initElements();
    }

    private void initElements() {
        elements.clear();
    }

    @Override
    protected void renderWidget(@NotNull GuiGraphics guiGraphics, int mouseX, int mouseY, float delta) {
        this.renderBox(guiGraphics);
        this.renderIcon(guiGraphics);
    }

    private void renderBox(GuiGraphics guiGraphics) {
        (isHovered ? box_hover : box).value2().render(guiGraphics, minecraft.getDeltaTracker());
    }

    private void renderIcon(GuiGraphics guiGraphics) {
        Matcher m = PATTERN.matcher(icon);

        if (m.matches()) {
            if(m.group(1) != null) {
                if(minecraft.player != null) {
                    ItemStack itemStack = ItemStackHelper.valueOf(icon);

                    guiGraphics.pose().pushMatrix();
                    guiGraphics.pose().translate(getX() + ((float) width / 2) - 6, getY() + ((float) height / 2) - 6);
                    guiGraphics.pose().scale(12f / 16f, 12f / 16f);

                    guiGraphics.renderItem(itemStack, 0, 0);

                    guiGraphics.pose().popMatrix();
                }
            } else {
                int stringWidth = minecraft.font.width(TextHelper.smallCaps(icon));
                guiGraphics.pose().pushMatrix();
                guiGraphics.pose().translate(0.0f, 0.0f);

                GuiGraphicsHelper.drawString(guiGraphics,
                        minecraft.font,
                        Component.literal(icon),
                        getX() + (width / 2) - stringWidth / 2, getY() + (height / 2) - minecraft.font.lineHeight / 2,
                        StringStyle.SHADOW, StringStyle.MIDDLE, StringStyle.SMALL_CAPS
                );

                guiGraphics.pose().popMatrix();
            }
        }
    }

    @Override
    protected void updateWidgetNarration(@NotNull NarrationElementOutput narrationElementOutput) {}

    @Override
    public void onClick(@NotNull MouseButtonEvent mouseButtonEvent, boolean doubled) {
        super.onClick(mouseButtonEvent, doubled);
        if(clickCallback != null) {
            this.clickCallback.onClick(this);
        }
    }

    public interface ClickCallback {
        void onClick(SmallButtonWidget smallButtonWidget);
    }
}
