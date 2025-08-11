package com.h1ggsk.radon.gui.components;

import com.h1ggsk.radon.Radon;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;
import org.lwjgl.glfw.GLFW;
import com.h1ggsk.radon.module.setting.StringSetting;
import com.h1ggsk.radon.utils.Utils;
import com.h1ggsk.radon.utils.MathUtil;
import com.h1ggsk.radon.utils.RenderUtils;
import com.h1ggsk.radon.utils.TextRenderer;

import java.awt.*;

class StringBox extends Screen {
    private final StringSetting setting;
    private String content;
    private int cursorPosition;
    private int selectionStart;
    private final boolean selecting;
    private long lastCursorBlink;
    private boolean cursorVisible;
    private final int CURSOR_BLINK_SPEED = 530;
    final /* synthetic */ TextBox this$0;

    public StringBox(final TextBox this$0, final StringSetting setting) {
        super(Text.empty());
        this.this$0 = this$0;
        this.selectionStart = -1;
        this.selecting = false;
        this.lastCursorBlink = 0L;
        this.cursorVisible = true;
        this.setting = setting;
        this.content = setting.getValue();
        this.cursorPosition = this.content.length();
    }

    public void render(final DrawContext drawContext, final int n, final int n2, final float n3) {
        RenderUtils.unscaledProjection();
        super.render(drawContext, n * (int) MinecraftClient.getInstance().getWindow().getScaleFactor(), n2 * (int) MinecraftClient.getInstance().getWindow().getScaleFactor(), n3);
        final long currentTimeMillis = System.currentTimeMillis();
        if (currentTimeMillis - this.lastCursorBlink > 530L) {
            this.cursorVisible = !this.cursorVisible;
            this.lastCursorBlink = currentTimeMillis;
        }
        final int width = this.this$0.mc.getWindow().getWidth();
        final int height = this.this$0.mc.getWindow().getHeight();
        int a;
        if (com.h1ggsk.radon.module.modules.client.Radon.renderBackground.getValue()) {
            a = 180;
        } else {
            a = 0;
        }
        drawContext.fill(0, 0, width, height, new Color(0, 0, 0, a).getRGB());
        final int width2 = this.this$0.mc.getWindow().getWidth();
        final int height2 = this.this$0.mc.getWindow().getHeight();
        final int a2 = MathUtil.clampInt(TextRenderer.getWidth(this.content) + 80, 600, this.this$0.mc.getWindow().getWidth() - 100);
        final int n4 = (width2 - a2) / 2;
        final int n5 = (height2 - 120) / 2;
        RenderUtils.renderRoundedQuad(drawContext.getMatrices(), new Color(30, 30, 35, 240), n4, n5, n4 + a2, n5 + 120, 8.0, 8.0, 8.0, 8.0, 20.0);
        RenderUtils.renderRoundedQuad(drawContext.getMatrices(), new Color(40, 40, 45, 255), n4, n5, n4 + a2, n5 + 30, 8.0, 8.0, 0.0, 0.0, 20.0);
        drawContext.fill(n4, n5 + 30, n4 + a2, n5 + 31, Utils.getMainColor(255, 1).getRGB());
        TextRenderer.drawCenteredString(this.setting.getName(), drawContext, n4 + a2 / 2, n5 + 8, new Color(245, 245, 245, 255).getRGB());
        final int n6 = n4 + 20;
        final int n7 = n5 + 50;
        final int n8 = a2 - 40;
        RenderUtils.renderRoundedQuad(drawContext.getMatrices(), new Color(20, 20, 25, 255), n6, n7, n6 + n8, n7 + 30, 5.0, 5.0, 5.0, 5.0, 20.0);
        RenderUtils.renderRoundedOutline(drawContext, new Color(60, 60, 65, 255), n6, n7, n6 + n8, n7 + 30, 5.0, 5.0, 5.0, 5.0, 1.0, 20.0);
        final String content = this.content;
        final int n9 = n6 + 10;
        final int n10 = n7 + 10;
        final String substring = this.content.substring(0, this.cursorPosition);
        content.substring(this.cursorPosition);
        final int n11 = n9 + TextRenderer.getWidth(substring);
        if (this.selectionStart != -1 && this.selectionStart != this.cursorPosition) {
            final int min = Math.min(this.selectionStart, this.cursorPosition);
            final int max = Math.max(this.selectionStart, this.cursorPosition);
            final String substring2 = content.substring(0, min);
            final String substring3 = content.substring(min, max);
            final String substring4 = content.substring(max);
            final int a3 = TextRenderer.getWidth(substring2);
            final int a4 = TextRenderer.getWidth(substring3);
            TextRenderer.drawString(substring2, drawContext, n9, n10, new Color(245, 245, 245, 255).getRGB());
            drawContext.fill(n9 + a3, n10 - 2, n9 + a3 + a4, n10 + 14, Utils.getMainColor(100, 1).getRGB());
            TextRenderer.drawString(substring3, drawContext, n9 + a3, n10, new Color(255, 255, 255, 255).getRGB());
            TextRenderer.drawString(substring4, drawContext, n9 + a3 + a4, n10, new Color(245, 245, 245, 255).getRGB());
        } else {
            TextRenderer.drawString(content, drawContext, n9, n10, new Color(245, 245, 245, 255).getRGB());
            if (this.cursorVisible) {
                drawContext.fill(n11, n10 - 2, n11 + 1, n10 + 14, new Color(245, 245, 245, 255).getRGB());
            }
        }
        final int n12 = n5 + 120 - 30;
        final int n13 = n4 + a2 - 80 - 20;
        final int n14 = n13 - 80 - 10;
        RenderUtils.renderRoundedQuad(drawContext.getMatrices(), Utils.getMainColor(255, 1), n13, n12, n13 + 80, n12 + 25, 5.0, 5.0, 5.0, 5.0, 20.0);
        TextRenderer.drawCenteredString("Save", drawContext, n13 + 40, n12 + 6, new Color(245, 245, 245, 255).getRGB());
        RenderUtils.renderRoundedQuad(drawContext.getMatrices(), new Color(60, 60, 65, 255), n14, n12, n14 + 80, n12 + 25, 5.0, 5.0, 5.0, 5.0, 20.0);
        TextRenderer.drawCenteredString("Cancel", drawContext, n14 + 40, n12 + 6, new Color(245, 245, 245, 255).getRGB());
        TextRenderer.drawString("Press Escape to save and close", drawContext, n4 + 20, n5 + 120 - 20, new Color(150, 150, 150, 200).getRGB());
        RenderUtils.scaledProjection();
    }

    public void mouseMoved(final double n, final double n2) {
        super.mouseMoved(n, n2);
        this.cursorVisible = true;
        this.lastCursorBlink = System.currentTimeMillis();
    }

    public boolean mouseClicked(final double n, final double n2, final int n3) {
        final double n4 = n * MinecraftClient.getInstance().getWindow().getScaleFactor();
        final double n5 = n2 * MinecraftClient.getInstance().getWindow().getScaleFactor();
        final int width = this.this$0.mc.getWindow().getWidth();
        final int height = this.this$0.mc.getWindow().getHeight();
        final int max = Math.max(600, MathUtil.clampInt(TextRenderer.getWidth(this.content) + 80, 400, this.this$0.mc.getWindow().getWidth() - 100));
        final int n6 = (height - 120) / 2 + 120 - 30;
        final int n7 = (width - max) / 2 + max - 80 - 20;
        if (this.isHovered(n4, n5, n7, n6, 80, 25)) {
            this.setting.setValue(this.content.trim());
            this.this$0.mc.setScreen(Radon.INSTANCE.GUI);
            return true;
        }
        if (this.isHovered(n4, n5, n7 - 80 - 10, n6, 80, 25)) {
            this.this$0.mc.setScreen(Radon.INSTANCE.GUI);
            return true;
        }
        return super.mouseClicked(n4, n5, n3);
    }

    private boolean isHovered(final double n, final double n2, final int n3, final int n4, final int n5, final int n6) {
        return n >= n3 && n <= n3 + n5 && n2 >= n4 && n2 <= n4 + n6;
    }

    public boolean keyPressed(final int n, final int n2, final int n3) {
        this.cursorVisible = true;
        this.lastCursorBlink = System.currentTimeMillis();
        if (n == 256) {
            this.setting.setValue(this.content.trim());
            this.this$0.mc.setScreen(Radon.INSTANCE.GUI);
            return true;
        }
        if (n == 257) {
            this.setting.setValue(this.content.trim());
            this.this$0.mc.setScreen(Radon.INSTANCE.GUI);
            return true;
        }
        if (isPaste(n)) {
            final String clipboard = this.this$0.mc.keyboard.getClipboard();
            if (this.selectionStart != -1 && this.selectionStart != this.cursorPosition) {
                final int min = Math.min(this.selectionStart, this.cursorPosition);
                this.content = this.content.substring(0, min) + clipboard + this.content.substring(Math.max(this.selectionStart, this.cursorPosition));
                this.cursorPosition = min + clipboard.length();
            } else {
                this.content = this.content.substring(0, this.cursorPosition) + clipboard + this.content.substring(this.cursorPosition);
                this.cursorPosition += clipboard.length();
            }
            this.selectionStart = -1;
            return true;
        }
        if (isCopy(n)) {
            if (this.selectionStart != -1 && this.selectionStart != this.cursorPosition) {
                GLFW.glfwSetClipboardString(this.this$0.mc.getWindow().getHandle(), this.content.substring(Math.min(this.selectionStart, this.cursorPosition), Math.max(this.selectionStart, this.cursorPosition)));
            }
            return true;
        }
        if (isCut(n)) {
            if (this.selectionStart != -1 && this.selectionStart != this.cursorPosition) {
                final int min2 = Math.min(this.selectionStart, this.cursorPosition);
                final int max = Math.max(this.selectionStart, this.cursorPosition);
                GLFW.glfwSetClipboardString(this.this$0.mc.getWindow().getHandle(), this.content.substring(min2, max));
                this.content = this.content.substring(0, min2) + this.content.substring(max);
                this.cursorPosition = min2;
                this.selectionStart = -1;
            }
            return true;
        }
        if (n == 259) {
            if (this.selectionStart != -1 && this.selectionStart != this.cursorPosition) {
                final int min3 = Math.min(this.selectionStart, this.cursorPosition);
                this.content = this.content.substring(0, min3) + this.content.substring(Math.max(this.selectionStart, this.cursorPosition));
                this.cursorPosition = min3;
                this.selectionStart = -1;
            } else if (this.cursorPosition > 0) {
                this.content = this.content.substring(0, this.cursorPosition - 1) + this.content.substring(this.cursorPosition);
                --this.cursorPosition;
            }
            return true;
        }
        if (n == 261) {
            if (this.selectionStart != -1 && this.selectionStart != this.cursorPosition) {
                final int min4 = Math.min(this.selectionStart, this.cursorPosition);
                this.content = this.content.substring(0, min4) + this.content.substring(Math.max(this.selectionStart, this.cursorPosition));
                this.cursorPosition = min4;
                this.selectionStart = -1;
            } else if (this.cursorPosition < this.content.length()) {
                this.content = this.content.substring(0, this.cursorPosition) + this.content.substring(this.cursorPosition + 1);
            }
            return true;
        }
        if (n == 263) {
            if ((n3 & 0x1) != 0x0) {
                if (this.selectionStart == -1) {
                    this.selectionStart = this.cursorPosition;
                }
            } else {
                this.selectionStart = -1;
            }
            if (this.cursorPosition > 0) {
                --this.cursorPosition;
            }
            return true;
        }
        if (n == 262) {
            if ((n3 & 0x1) != 0x0) {
                if (this.selectionStart == -1) {
                    this.selectionStart = this.cursorPosition;
                }
            } else {
                this.selectionStart = -1;
            }
            if (this.cursorPosition < this.content.length()) {
                ++this.cursorPosition;
            }
            return true;
        }
        if (n == 268) {
            if ((n3 & 0x1) != 0x0) {
                if (this.selectionStart == -1) {
                    this.selectionStart = this.cursorPosition;
                }
            } else {
                this.selectionStart = -1;
            }
            this.cursorPosition = 0;
            return true;
        }
        if (n == 269) {
            if ((n3 & 0x1) != 0x0) {
                if (this.selectionStart == -1) {
                    this.selectionStart = this.cursorPosition;
                }
            } else {
                this.selectionStart = -1;
            }
            this.cursorPosition = this.content.length();
            return true;
        }
        return super.keyPressed(n, n2, n3);
    }

    public boolean charTyped(final char c, final int n) {
        if (this.selectionStart != -1 && this.selectionStart != this.cursorPosition) {
            final int min = Math.min(this.selectionStart, this.cursorPosition);
            this.content = this.content.substring(0, min) + c + this.content.substring(Math.max(this.selectionStart, this.cursorPosition));
            this.cursorPosition = min + 1;
            this.selectionStart = -1;
        } else {
            this.content = this.content.substring(0, this.cursorPosition) + c + this.content.substring(this.cursorPosition);
            ++this.cursorPosition;
        }
        this.cursorVisible = true;
        this.lastCursorBlink = System.currentTimeMillis();
        return true;
    }

    public static boolean isPaste(final int n) {
        return n == 86 && hasControlDown();
    }

    public static boolean isCopy(final int n) {
        return n == 67 && hasControlDown();
    }

    public static boolean isCut(final int n) {
        return n == 88 && hasControlDown();
    }

    public static boolean hasControlDown() {
        int n;
        if (MinecraftClient.IS_SYSTEM_MAC) {
            n = GLFW.glfwGetKey(MinecraftClient.getInstance().getWindow().getHandle(), 343);
        } else {
            n = GLFW.glfwGetKey(MinecraftClient.getInstance().getWindow().getHandle(), 341);
        }
        return n == 1;
    }

    public void renderBackground(final DrawContext drawContext, final int n, final int n2, final float n3) {
    }

    public boolean shouldCloseOnEsc() {
        return false;
    }
}
