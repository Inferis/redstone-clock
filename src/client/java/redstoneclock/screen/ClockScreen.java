package redstoneclock.screen;

import com.mojang.blaze3d.pipeline.RenderPipeline;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import redstoneclock.RedstoneClock;
import redstoneclock.networking.OpenEditorS2CPayload;
import redstoneclock.networking.SaveIntervalsC2SPayload;

@Environment(EnvType.CLIENT)
public class ClockScreen extends Screen {
    public static final Identifier BACKGROUND_TEXTURE = Identifier.of(RedstoneClock.MODID, "textures/gui/container/screen.png");
    protected static final int BACKGROUND_WIDTH = 290;
    protected static final int BACKGROUND_HEIGHT = 128;

    private BlockPos blockPos; 
    private int activeInterval;
    private int inactiveInterval;
    private int signalStrength;
    private int originX;
    private int originY;

    public TextFieldWidget activeTextField; 
    public TextFieldWidget inactiveTextField;
    public TextFieldWidget signalStrengthTextField;

    public ButtonWidget activeDecrement10Button = ButtonWidget.builder(
        Text.literal("-10"), button -> { adjustActiveInterval(-10); })
        .dimensions(0, 0, 30, 20)
        .build();
    public ButtonWidget activeDecrement1Button = ButtonWidget.builder(
        Text.literal("-1"), button -> { adjustActiveInterval(-1); })
        .dimensions(0, 0, 20, 20)
        .build();
    public ButtonWidget activeIncrement1Button = ButtonWidget.builder(
        Text.literal("+1"), button -> { adjustActiveInterval(+1); })
        .dimensions(0, 0, 20, 20)
        .build();
    public ButtonWidget activeIncrement10Button = ButtonWidget.builder(
        Text.literal("+10"), button -> { adjustActiveInterval(+10); })
        .dimensions(0, 0, 30, 20)
        .build();
    public ButtonWidget inactiveDecrement10Button = ButtonWidget.builder(
        Text.literal("-10"), button -> { adjustInactiveInterval(-10); })
        .dimensions(0, 0, 30, 20)
        .build();
    public ButtonWidget inactiveDecrement1Button = ButtonWidget.builder(
        Text.literal("-1"), button -> { adjustInactiveInterval(-1); })
        .dimensions(0, 0, 20, 20)
        .build();
    public ButtonWidget inactiveIncrement1Button = ButtonWidget.builder(
        Text.literal("+1"), button -> { adjustInactiveInterval(+1); })
        .dimensions(0, 0, 20, 20)
        .build();
    public ButtonWidget inactiveIncrement10Button = ButtonWidget.builder(
        Text.literal("+10"), button -> { adjustInactiveInterval(+10); })
        .dimensions(0, 0, 30, 20)
        .build();
    public ButtonWidget signalStrengthDecrement1Button = ButtonWidget.builder(
        Text.literal("-1"), button -> { adjustSignalStrength(-1); })
        .dimensions(0, 0, 20, 20)
        .build();
    public ButtonWidget signalStrengthIncrement1Button = ButtonWidget.builder(
        Text.literal("+1"), button -> { adjustSignalStrength(+1); })
        .dimensions(0, 0, 20, 20)
        .build();
    public ButtonWidget signalStrengthSet15Button = ButtonWidget.builder(
        Text.literal("15"), button -> { signalStrength = 15; signalStrengthTextField.setText("15"); })
        .dimensions(0, 0, 30, 20)
        .build();
            
    public ClockScreen(OpenEditorS2CPayload payload) {
        super(Text.translatable("redstoneclock.clockscreen.title"));

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
        addDrawableChild(activeDecrement10Button);
        activeDecrement1Button.setPosition(originX + 152, activeRowY);
        addDrawableChild(activeDecrement1Button);
        activeTextField = new TextFieldWidget(textRenderer, 50, 20, Text.literal(""));
        activeTextField.setText("" + activeInterval);
        activeTextField.setPosition(originX + 174, activeRowY);
        activeTextField.setTextPredicate(text -> {
            if (text == null || text.length() == 0) {
                return true;
            }
            try {
                return Integer.parseInt(text) > 0;
            }
            catch (NumberFormatException e) {
                return false;
            }
        });
        activeTextField.setChangedListener(text -> { 
            var interval = (text == null || text.length() == 0) ? 0 : Integer.parseInt(text);
            if (interval > 0) { 
                activeInterval = interval;
            }
            else {
                activeTextField.setText("1");
                activeInterval = 1;
            }
        });
        addDrawableChild(activeTextField);
        activeIncrement1Button.setPosition(originX + 226, activeRowY);
        addDrawableChild(activeIncrement1Button);
        activeIncrement10Button.setPosition(originX + 248, activeRowY);
        addDrawableChild(activeIncrement10Button);

        // Inactive Interval Row
        inactiveDecrement10Button.setPosition(originX + 120, inactiveRowY);
        addDrawableChild(inactiveDecrement10Button);
        inactiveDecrement1Button.setPosition(originX + 152, inactiveRowY);
        addDrawableChild(inactiveDecrement1Button);
        inactiveTextField = new TextFieldWidget(textRenderer, 50, 20, Text.literal(""));
        inactiveTextField.setText("" + inactiveInterval);
        inactiveTextField.setPosition(originX + 174, inactiveRowY);
        inactiveTextField.setTextPredicate(text -> {
            if (text == null || text.length() == 0) {
                return true;
            }
            try {
                return Integer.parseInt(text) > 0;
            }
            catch (NumberFormatException e) {
                return false;
            }
        });
        inactiveTextField.setChangedListener(text -> { 
            var interval = (text == null || text.length() == 0) ? 0 : Integer.parseInt(text);
            if (interval > 0) { 
                inactiveInterval = interval;
            }
            else {
                inactiveTextField.setText("1");
                inactiveInterval = 1;
            }
        });
        addDrawableChild(inactiveTextField);
        inactiveIncrement1Button.setPosition(originX + 226, inactiveRowY);
        addDrawableChild(inactiveIncrement1Button);
        inactiveIncrement10Button.setPosition(originX + 248, inactiveRowY);
        addDrawableChild(inactiveIncrement10Button);

        // Signal Strength Row
        signalStrengthDecrement1Button.setPosition(originX + 152, signalStrengthRowY);
        addDrawableChild(signalStrengthDecrement1Button);
        signalStrengthTextField = new TextFieldWidget(textRenderer, 50, 20, Text.literal(""));
        signalStrengthTextField.setText("" + signalStrength);
        signalStrengthTextField.setPosition(originX + 174, signalStrengthRowY);
        signalStrengthTextField.setTextPredicate(text -> {
            if (text == null || text.length() == 0) {
                return true;
            }
            try {
                return Integer.parseInt(text) > 0;
            }
            catch (NumberFormatException e) {
                return false;
            }
        });
        signalStrengthTextField.setChangedListener(text -> { 
            var strength = (text == null || text.length() == 0) ? 0 : Integer.parseInt(text);
            if (strength < 1) { 
                signalStrengthTextField.setText("1");
                signalStrength = 1;
            }
            else if (strength > 15) {
                signalStrengthTextField.setText("15");
                signalStrength = 15;
            }
            else {
                signalStrength = strength;
            }
        });
        addDrawableChild(signalStrengthTextField);
        signalStrengthIncrement1Button.setPosition(originX + 226, signalStrengthRowY);
        addDrawableChild(signalStrengthIncrement1Button);
        signalStrengthSet15Button.setPosition(originX + 248, signalStrengthRowY);
        addDrawableChild(signalStrengthSet15Button);
    }

    @Override
    public boolean shouldPause() {
        return false;
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        super.render(context, mouseX, mouseY, delta);
        context.drawText(textRenderer, 
            Text.translatable("screen.redstoneclock.clock.title"), 
            originX + 10, originY + 10, 0xFF333333, false);
        context.drawText(textRenderer, 
            Text.translatable("screen.redstoneclock.clock.active_interval"), 
            originX + 10, originY + 36, 0xFF333333, false);
            context.drawText(textRenderer, 
            Text.translatable("screen.redstoneclock.clock.inactive_interval"), 
            originX + 10, originY + 60, 0xFF333333, false);
        context.drawText(textRenderer, 
            Text.translatable("screen.redstoneclock.clock.signal_strength"), 
            originX + 10, originY + 88, 0xFF333333, false);
    }

    @Override
    public void renderBackground(DrawContext context, int mouseX, int mouseY, float delta) {
        super.renderBackground(context, mouseX, mouseY, delta);
        context.drawTexture(RenderPipelines.GUI_TEXTURED, BACKGROUND_TEXTURE, originX, originY, 0, 0, BACKGROUND_WIDTH, BACKGROUND_HEIGHT, 512, 512);
    }

    private void adjustActiveInterval(int amount) {
        activeInterval += amount;
        if (activeInterval < 1) {
            activeInterval = 1;
        }
        activeTextField.setText("" + activeInterval);
        ClientPlayNetworking.send(new SaveIntervalsC2SPayload(blockPos, activeInterval, inactiveInterval, signalStrength));
    }

    private void adjustInactiveInterval(int amount) {
        inactiveInterval += amount;
        if (inactiveInterval < 1) {
            inactiveInterval = 1;
        }
        inactiveTextField.setText("" + inactiveInterval);
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
        signalStrengthTextField.setText("" + signalStrength);
        ClientPlayNetworking.send(new SaveIntervalsC2SPayload(blockPos, activeInterval, inactiveInterval, signalStrength));
    }
}
