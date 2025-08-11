package com.h1ggsk.radon.gui.components;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.registry.Registries;
import net.minecraft.text.Text;
import com.h1ggsk.radon.module.modules.client.Radon;
import com.h1ggsk.radon.module.setting.ItemSetting;
import com.h1ggsk.radon.utils.Utils;
import com.h1ggsk.radon.utils.RenderUtils;
import com.h1ggsk.radon.utils.TextRenderer;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

class ItemFilter extends Screen {
    private final ItemSetting setting;
    private String searchQuery;
    private final List<Item> allItems;
    private List<Item> filteredItems;
    private int scrollOffset;
    private final int ITEMS_PER_ROW = 11;
    private final int MAX_ROWS_VISIBLE = 6;
    private int selectedIndex;
    private final int ITEM_SIZE = 40;
    private final int ITEM_SPACING = 8;
    final /* synthetic */ ItemBox this$0;

    public ItemFilter(final ItemBox this$0, final ItemSetting setting) {
        super(Text.empty());
        this.this$0 = this$0;
        this.searchQuery = "";
        this.scrollOffset = 0;
        this.selectedIndex = -1;
        this.setting = setting;
        this.allItems = new ArrayList<Item>();
        Registries.ITEM.forEach(item -> {
            if (item != Items.AIR) {
                this.allItems.add(item);
            }
        });
        this.filteredItems = new ArrayList<Item>(this.allItems);
        if (setting.getItem() != null && setting.getItem() != Items.AIR) {
            for (int i = 0; i < this.filteredItems.size(); ++i) {
                if (this.filteredItems.get(i) == setting.getItem()) {
                    this.selectedIndex = i;
                    break;
                }
            }
        }
    }

    public void render(final DrawContext drawContext, final int n, final int n2, final float n3) {
        RenderUtils.unscaledProjection();
        final int n4 = n * (int) MinecraftClient.getInstance().getWindow().getScaleFactor();
        final int n5 = n2 * (int) MinecraftClient.getInstance().getWindow().getScaleFactor();
        super.render(drawContext, n4, n5, n3);
        final int width = this.this$0.mc.getWindow().getWidth();
        final int height = this.this$0.mc.getWindow().getHeight();
        int a;
        if (Radon.renderBackground.getValue()) {
            a = 180;
        } else {
            a = 0;
        }
        drawContext.fill(0, 0, width, height, new Color(0, 0, 0, a).getRGB());
        final int n6 = (this.this$0.mc.getWindow().getWidth() - 580) / 2;
        final int n7 = (this.this$0.mc.getWindow().getHeight() - 450) / 2;
        RenderUtils.renderRoundedQuad(drawContext.getMatrices(), new Color(30, 30, 35, 240), n6, n7, n6 + 580, n7 + 450, 8.0, 8.0, 8.0, 8.0, 20.0);
        RenderUtils.renderRoundedQuad(drawContext.getMatrices(), new Color(40, 40, 45, 255), n6, n7, n6 + 580, n7 + 30, 8.0, 8.0, 0.0, 0.0, 20.0);
        drawContext.fill(n6, n7 + 30, n6 + 580, n7 + 31, Utils.getMainColor(255, 1).getRGB());
        TextRenderer.drawCenteredString("Select Item: " + this.setting.getName(), drawContext, n6 + 290, n7 + 8, new Color(245, 245, 245, 255).getRGB());
        final int n8 = n6 + 20;
        final int n9 = n7 + 50;
        RenderUtils.renderRoundedQuad(drawContext.getMatrices(), new Color(20, 20, 25, 255), n8, n9, n8 + 540, n9 + 30, 5.0, 5.0, 5.0, 5.0, 20.0);
        RenderUtils.renderRoundedOutline(drawContext, new Color(60, 60, 65, 255), n8, n9, n8 + 540, n9 + 30, 5.0, 5.0, 5.0, 5.0, 1.0, 20.0);
        final String searchQuery = this.searchQuery;
        String s;
        if (System.currentTimeMillis() % 1000L > 500L) {
            s = "|";
        } else {
            s = "";
        }
        TextRenderer.drawString("Search: " + searchQuery + s, drawContext, n8 + 10, n9 + 9, new Color(200, 200, 200, 255).getRGB());
        final int n10 = n6 + 20;
        final int n11 = n9 + 30 + 15;
        final int n12 = 450 - (n11 - n7) - 60;
        RenderUtils.renderRoundedQuad(drawContext.getMatrices(), new Color(25, 25, 30, 255), n10, n11, n10 + 540, n11 + n12, 5.0, 5.0, 5.0, 5.0, 20.0);
        final double ceil = Math.ceil(this.filteredItems.size() / 11.0);
        final int max = Math.max(0, (int) ceil - 6);
        this.scrollOffset = Math.min(this.scrollOffset, max);
        if ((int) ceil > 6) {
            final int n13 = n10 + 540 - 6 - 5;
            final int n14 = n11 + 5;
            final int n15 = n12 - 10;
            RenderUtils.renderRoundedQuad(drawContext.getMatrices(), new Color(20, 20, 25, 150), n13, n14, n13 + 6, n14 + n15, 3.0, 3.0, 3.0, 3.0, 20.0);
            final float n16 = this.scrollOffset / (float) max;
            final float max2 = Math.max(40.0f, n15 * (6.0f / (int) ceil));
            final int n17 = n14 + (int) ((n15 - max2) * n16);
            RenderUtils.renderRoundedQuad(drawContext.getMatrices(), Utils.getMainColor(255, 1), n13, n17, n13 + 6, n17 + max2, 3.0, 3.0, 3.0, 3.0, 20.0);
        }
        int i;
        for (int n18 = i = this.scrollOffset * 11; i < Math.min(n18 + Math.min(this.filteredItems.size(), 66), this.filteredItems.size()); ++i) {
            final int n19 = n10 + 5 + (i - n18) % 11 * 48;
            final int n20 = n11 + 5 + (i - n18) / 11 * 48;
            Color mainColor;
            if (i == this.selectedIndex) {
                mainColor = Utils.getMainColor(100, 1);
            } else {
                mainColor = new Color(35, 35, 40, 255);
            }
            RenderUtils.renderRoundedQuad(drawContext.getMatrices(), mainColor, n19, n20, n19 + 40, n20 + 40, 4.0, 4.0, 4.0, 4.0, 20.0);
            RenderUtils.drawItem(drawContext, new ItemStack(this.filteredItems.get(i)), n19, n20, 40.0f, 0);
            if (n4 >= n19 && n4 <= n19 + 40 && n5 >= n20 && n5 <= n20 + 40) {
                RenderUtils.renderRoundedOutline(drawContext, Utils.getMainColor(200, 1), n19, n20, n19 + 40, n20 + 40, 4.0, 4.0, 4.0, 4.0, 1.0, 20.0);
            }
        }
        if (this.filteredItems.isEmpty()) {
            TextRenderer.drawCenteredString("No items found", drawContext, n10 + 270, n11 + n12 / 2 - 10, new Color(150, 150, 150, 200).getRGB());
        }
        final int n21 = n7 + 450 - 45;
        final int n22 = n6 + 580 - 80 - 20;
        final int n23 = n22 - 80 - 10;
        final int n24 = n23 - 80 - 10;
        RenderUtils.renderRoundedQuad(drawContext.getMatrices(), Utils.getMainColor(255, 1), n22, n21, n22 + 80, n21 + 30, 5.0, 5.0, 5.0, 5.0, 20.0);
        TextRenderer.drawCenteredString("Save", drawContext, n22 + 40, n21 + 8, new Color(245, 245, 245, 255).getRGB());
        RenderUtils.renderRoundedQuad(drawContext.getMatrices(), new Color(60, 60, 65, 255), n23, n21, n23 + 80, n21 + 30, 5.0, 5.0, 5.0, 5.0, 20.0);
        TextRenderer.drawCenteredString("Cancel", drawContext, n23 + 40, n21 + 8, new Color(245, 245, 245, 255).getRGB());
        RenderUtils.renderRoundedQuad(drawContext.getMatrices(), new Color(70, 40, 40, 255), n24, n21, n24 + 80, n21 + 30, 5.0, 5.0, 5.0, 5.0, 20.0);
        TextRenderer.drawCenteredString("Reset", drawContext, n24 + 40, n21 + 8, new Color(245, 245, 245, 255).getRGB());
        RenderUtils.scaledProjection();
    }

    public boolean mouseClicked(final double n, final double n2, final int n3) {
        final double n4 = n * MinecraftClient.getInstance().getWindow().getScaleFactor();
        final double n5 = n2 * MinecraftClient.getInstance().getWindow().getScaleFactor();
        final int n6 = (this.this$0.mc.getWindow().getWidth() - 600) / 2;
        final int n7 = (this.this$0.mc.getWindow().getHeight() - 450) / 2;
        final int n8 = n7 + 450 - 45;
        final int n9 = n6 + 600 - 80 - 20;
        final int n10 = n9 - 80 - 10;
        if (this.isInBounds(n4, n5, n9, n8, 80, 30)) {
            if (this.selectedIndex >= 0 && this.selectedIndex < this.filteredItems.size()) {
                this.setting.setItem(this.filteredItems.get(this.selectedIndex));
            }
            this.this$0.mc.setScreen(com.h1ggsk.radon.Radon.INSTANCE.GUI);
            return true;
        }
        if (this.isInBounds(n4, n5, n10, n8, 80, 30)) {
            this.this$0.mc.setScreen(com.h1ggsk.radon.Radon.INSTANCE.GUI);
            return true;
        }
        if (this.isInBounds(n4, n5, n10 - 80 - 10, n8, 80, 30)) {
            this.setting.setItem(this.setting.getDefaultValue());
            this.selectedIndex = -1;
            for (int i = 0; i < this.filteredItems.size(); ++i) {
                if (this.filteredItems.get(i) == this.setting.getDefaultValue()) {
                    this.selectedIndex = i;
                    break;
                }
            }
            return true;
        }
        final int n11 = n6 + 20;
        final int n12 = n7 + 50 + 30 + 15;
        if (this.isInBounds(n4, n5, n11, n12, 560, 450 - (n12 - n7) - 60)) {
            final int n13 = this.scrollOffset * 11;
            final int n14 = (int) (n4 - n11 - 5.0) / 48;
            if (n14 >= 0 && n14 < 11) {
                final int selectedIndex = n13 + (int) (n5 - n12 - 5.0) / 48 * 11 + n14;
                if (selectedIndex >= 0 && selectedIndex < this.filteredItems.size()) {
                    this.selectedIndex = selectedIndex;
                    return true;
                }
            }
        }
        return super.mouseClicked(n4, n5, n3);
    }

    public boolean mouseScrolled(final double n, final double n2, final double n3, final double n4) {
        final double n5 = n * MinecraftClient.getInstance().getWindow().getScaleFactor();
        final double n6 = n2 * MinecraftClient.getInstance().getWindow().getScaleFactor();
        final int width = this.this$0.mc.getWindow().getWidth();
        final int n7 = (this.this$0.mc.getWindow().getHeight() - 450) / 2;
        final int n8 = n7 + 50 + 30 + 15;
        if (this.isInBounds(n5, n6, (width - 600) / 2 + 20, n8, 560, 450 - (n8 - n7) - 60)) {
            final int max = Math.max(0, (int) Math.ceil(this.filteredItems.size() / 11.0) - 6);
            if (n4 > 0.0) {
                this.scrollOffset = Math.max(0, this.scrollOffset - 1);
            } else if (n4 < 0.0) {
                this.scrollOffset = Math.min(max, this.scrollOffset + 1);
            }
            return true;
        }
        return super.mouseScrolled(n5, n6, n3, n4);
    }

    public boolean keyPressed(final int n, final int n2, final int n3) {
        if (n == 256) {
            if (this.selectedIndex >= 0 && this.selectedIndex < this.filteredItems.size()) {
                this.setting.setItem(this.filteredItems.get(this.selectedIndex));
            }
            this.this$0.mc.setScreen(com.h1ggsk.radon.Radon.INSTANCE.GUI);
            return true;
        }
        if (n == 259) {
            if (!this.searchQuery.isEmpty()) {
                this.searchQuery = this.searchQuery.substring(0, this.searchQuery.length() - 1);
                this.updateFilteredItems();
            }
            return true;
        }
        if (n == 265) {
            if (this.selectedIndex >= 11) {
                this.selectedIndex -= 11;
                this.ensureSelectedItemVisible();
            }
            return true;
        }
        if (n == 264) {
            if (this.selectedIndex + 11 < this.filteredItems.size()) {
                this.selectedIndex += 11;
                this.ensureSelectedItemVisible();
            }
            return true;
        }
        if (n == 263) {
            if (this.selectedIndex > 0) {
                --this.selectedIndex;
                this.ensureSelectedItemVisible();
            }
            return true;
        }
        if (n == 262) {
            if (this.selectedIndex < this.filteredItems.size() - 1) {
                ++this.selectedIndex;
                this.ensureSelectedItemVisible();
            }
            return true;
        }
        if (n == 257) {
            if (this.selectedIndex >= 0 && this.selectedIndex < this.filteredItems.size()) {
                this.setting.setItem(this.filteredItems.get(this.selectedIndex));
                this.this$0.mc.setScreen(com.h1ggsk.radon.Radon.INSTANCE.GUI);
            }
            return true;
        }
        return super.keyPressed(n, n2, n3);
    }

    public boolean charTyped(final char c, final int n) {
        this.searchQuery += c;
        this.updateFilteredItems();
        return true;
    }

    private void updateFilteredItems() {
        if (this.searchQuery.isEmpty()) {
            this.filteredItems = new ArrayList<>(this.allItems);
        } else {
            this.filteredItems = this.allItems.stream().filter(item -> item.getName().getString().toLowerCase().contains(this.searchQuery.toLowerCase())).collect(Collectors.toList());
        }
        this.scrollOffset = 0;
        this.selectedIndex = -1;
        final Item a = this.setting.getItem();
        if (a != null) {
            for (int i = 0; i < this.filteredItems.size(); ++i) {
                if (this.filteredItems.get(i) == a) {
                    this.selectedIndex = i;
                    break;
                }
            }
        }
    }

    private void ensureSelectedItemVisible() {
        if (this.selectedIndex < 0) {
            return;
        }
        final int scrollOffset = this.selectedIndex / 11;
        if (scrollOffset < this.scrollOffset) {
            this.scrollOffset = scrollOffset;
        } else if (scrollOffset >= this.scrollOffset + 6) {
            this.scrollOffset = scrollOffset - 6 + 1;
        }
    }

    private boolean isInBounds(final double n, final double n2, final int n3, final int n4, final int n5, final int n6) {
        return n >= n3 && n <= n3 + n5 && n2 >= n4 && n2 <= n4 + n6;
    }

    public void renderBackground(final DrawContext drawContext, final int n, final int n2, final float n3) {
    }

    public boolean shouldCloseOnEsc() {
        return false;
    }
}
