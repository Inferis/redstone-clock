package redstoneclock.screen;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.core.BlockPos;
import org.jspecify.annotations.NonNull;
import redstoneclock.RedstoneClock;
import redstoneclock.networking.OpenEditorS2CPayload;
import redstoneclock.networking.SaveIntervalsC2SPayload;

@Environment(EnvType.CLIENT)
public class ClockScreen extends Screen {
    public static final Identifier BACKGROUND_TEXTURE = Identifier.fromNamespaceAndPath(RedstoneClock.MODID, "textures/gui/container/screen.png");
    protected static final int BACKGROUND_WIDTH = 290;
    protected static final int BACKGROUND_HEIGHT = 128;

    private BlockPos blockPos; 
    private int activeInterval;
    private int inactiveInterval;
    private int signalStrength;
    private int originX;
    private int originY;

    public EditBox activeTextField;
    public EditBox inactiveTextField;
    public EditBox signalStrengthTextField;

    public Button activeDecrement10Button = Button.builder(
        Component.literal("-10"), button -> { adjustActiveInterval(-10); })
        .bounds(0, 0, 30, 20)
        .build();
    public Button activeDecrement1Button = Button.builder(
        Component.literal("-1"), button -> { adjustActiveInterval(-1); })
        .bounds(0, 0, 20, 20)
        .build();
    public Button activeIncrement1Button = Button.builder(
        Component.literal("+1"), button -> { adjustActiveInterval(+1); })
        .bounds(0, 0, 20, 20)
        .build();
    public Button activeIncrement10Button = Button.builder(
        Component.literal("+10"), button -> { adjustActiveInterval(+10); })
        .bounds(0, 0, 30, 20)
        .build();
    public Button inactiveDecrement10Button = Button.builder(
        Component.literal("-10"), button -> { adjustInactiveInterval(-10); })
        .bounds(0, 0, 30, 20)
        .build();
    public Button inactiveDecrement1Button = Button.builder(
        Component.literal("-1"), button -> { adjustInactiveInterval(-1); })
        .bounds(0, 0, 20, 20)
        .build();
    public Button inactiveIncrement1Button = Button.builder(
        Component.literal("+1"), button -> { adjustInactiveInterval(+1); })
        .bounds(0, 0, 20, 20)
        .build();
    public Button inactiveIncrement10Button = Button.builder(
        Component.literal("+10"), button -> { adjustInactiveInterval(+10); })
        .bounds(0, 0, 30, 20)
        .build();
    public Button signalStrengthDecrement1Button = Button.builder(
        Component.literal("-1"), button -> { adjustSignalStrength(-1); })
        .bounds(0, 0, 20, 20)
        .build();
    public Button signalStrengthIncrement1Button = Button.builder(
        Component.literal("+1"), button -> { adjustSignalStrength(+1); })
        .bounds(0, 0, 20, 20)
        .build();
    public Button signalStrengthSet15Button = Button.builder(
        Component.literal("15"), button -> { signalStrength = 15; signalStrengthTextField.setValue("15"); })
        .bounds(0, 0, 30, 20)
        .build();
            
    public ClockScreen(OpenEditorS2CPayload payload) {
        super(Component.translatable("redstoneclock.clockscreen.title"));

        this.blockPos = payload.blockPos();
        this.activeInterval = payload.activeInterval();
        this.inactiveInterval = payload.inactiveInterval();
        this.signalStrength = payload.signalStrength();
    }

    @Override
    protected void init() {
        super.init();
        
        originX = (width - BACKGROUND_WIDTH) / 2;
        originY = (height - BACKGROUND_HEIGHT) / 2;
    
        final int activeRowY = originY + 30;
        final int inactiveRowY = originY + 52;
        final int signalStrengthRowY = originY + 80;

        // Active Interval Row
        activeDecrement10Button.setPosition(originX + 120, activeRowY);
        addRenderableWidget(activeDecrement10Button);
        activeDecrement1Button.setPosition(originX + 152, activeRowY);
        addRenderableWidget(activeDecrement1Button);
        activeTextField = new EditBox(this.font, originX + 174, activeRowY, 50, 20, Component.literal("Active Interval"));
        activeTextField.setValue("" + activeInterval);
        activeTextField.setResponder(text -> {
            var interval = text.isBlank() ? 0 : Integer.parseInt(text);
            if (interval > 0) { 
                activeInterval = interval;
            }
            else {
                activeTextField.setValue("1");
                activeInterval = 1;
            }
        });
        addRenderableWidget(activeTextField);
        activeIncrement1Button.setPosition(originX + 226, activeRowY);
        addRenderableWidget(activeIncrement1Button);
        activeIncrement10Button.setPosition(originX + 248, activeRowY);
        addRenderableWidget(activeIncrement10Button);

        // Inactive Interval Row
        inactiveDecrement10Button.setPosition(originX + 120, inactiveRowY);
        addRenderableWidget(inactiveDecrement10Button);
        inactiveDecrement1Button.setPosition(originX + 152, inactiveRowY);
        addRenderableWidget(inactiveDecrement1Button);
        inactiveTextField = new EditBox(this.font, originX + 174, inactiveRowY, 50, 20, Component.literal("Inactive Interval"));
        inactiveTextField.setValue("" + inactiveInterval);
        inactiveTextField.setResponder(text -> {
            var interval = (text.isBlank()) ? 0 : Integer.parseInt(text);
            if (interval > 0) { 
                inactiveInterval = interval;
            }
            else {
                inactiveTextField.setValue("1");
                inactiveInterval = 1;
            }
        });
        addRenderableWidget(inactiveTextField);
        inactiveIncrement1Button.setPosition(originX + 226, inactiveRowY);
        addRenderableWidget(inactiveIncrement1Button);
        inactiveIncrement10Button.setPosition(originX + 248, inactiveRowY);
        addRenderableWidget(inactiveIncrement10Button);

        // Signal Strength Row
        signalStrengthDecrement1Button.setPosition(originX + 152, signalStrengthRowY);
        addRenderableWidget(signalStrengthDecrement1Button);
        signalStrengthTextField = new EditBox(this.font, originX + 174, signalStrengthRowY, 50, 20, Component.literal("Signal Strength"));
        signalStrengthTextField.setValue("" + signalStrength);
        signalStrengthTextField.setResponder(text -> {
            var strength = text.isBlank() ? 0 : Integer.parseInt(text);
            if (strength < 1) { 
                signalStrengthTextField.setValue("1");
                signalStrength = 1;
            }
            else if (strength > 15) {
                signalStrengthTextField.setValue("15");
                signalStrength = 15;
            }
            else {
                signalStrength = strength;
            }
        });
        addRenderableWidget(signalStrengthTextField);
        signalStrengthIncrement1Button.setPosition(originX + 226, signalStrengthRowY);
        addRenderableWidget(signalStrengthIncrement1Button);
        signalStrengthSet15Button.setPosition(originX + 248, signalStrengthRowY);
        addRenderableWidget(signalStrengthSet15Button);
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    @Override
    public void extractRenderState(final @NonNull GuiGraphicsExtractor graphics, final int mouseX, final int mouseY, final float a) {
        super.extractRenderState(graphics, mouseX, mouseY, a);
        graphics.text(this.font,
            Component.translatable("screen.redstoneclock.clock.title"),
            originX + 10, originY + 10, 0xFF333333, false);
        graphics.text(this.font,
            Component.translatable("screen.redstoneclock.clock.active_interval"),
            originX + 10, originY + 36, 0xFF333333, false);
        graphics.text(this.font,
            Component.translatable("screen.redstoneclock.clock.inactive_interval"),
            originX + 10, originY + 60, 0xFF333333, false);
        graphics.text(this.font,
            Component.translatable("screen.redstoneclock.clock.signal_strength"),
            originX + 10, originY + 88, 0xFF333333, false);
    }

    @Override
    public void extractBackground(@NonNull GuiGraphicsExtractor graphics, int mouseX, int mouseY, float delta) {
        super.extractBackground(graphics, mouseX, mouseY, delta);
        graphics.blit(RenderPipelines.GUI_TEXTURED, BACKGROUND_TEXTURE, originX, originY, 0, 0, BACKGROUND_WIDTH, BACKGROUND_HEIGHT, 512, 512);
    }

    private void adjustActiveInterval(int amount) {
        activeInterval += amount;
        if (activeInterval < 1) {
            activeInterval = 1;
        }
        activeTextField.setValue("" + activeInterval);
        ClientPlayNetworking.send(new SaveIntervalsC2SPayload(blockPos, activeInterval, inactiveInterval, signalStrength));
    }

    private void adjustInactiveInterval(int amount) {
        inactiveInterval += amount;
        if (inactiveInterval < 1) {
            inactiveInterval = 1;
        }
        inactiveTextField.setValue("" + inactiveInterval);
        ClientPlayNetworking.send(new SaveIntervalsC2SPayload(blockPos, activeInterval, inactiveInterval, signalStrength));
    }

    private void adjustSignalStrength(int amount) {
        signalStrength += amount;
        if (signalStrength < 1) {
            signalStrength = 1;
        }
        else if (signalStrength > 15) {
            signalStrength = 15;
        }
        signalStrengthTextField.setValue("" + signalStrength);
        ClientPlayNetworking.send(new SaveIntervalsC2SPayload(blockPos, activeInterval, inactiveInterval, signalStrength));
    }
}
