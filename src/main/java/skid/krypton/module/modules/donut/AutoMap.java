package skid.krypton.module.modules.donut;

import net.minecraft.client.gui.screen.ingame.GenericContainerScreen;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.screen.GenericContainerScreenHandler;
import net.minecraft.screen.slot.SlotActionType;
import skid.krypton.event.EventListener;
import skid.krypton.event.events.StartTickEvent;
import skid.krypton.module.Category;
import skid.krypton.module.Module;
import skid.krypton.module.setting.NumberSetting;
import skid.krypton.module.setting.StringSetting;
import skid.krypton.utils.EncryptedString;

public final class AutoMap extends Module {
    private final NumberSetting delay = new NumberSetting(EncryptedString.of("Delay"), 0.0, 100.0, 20.0, 1.0)
            .getValue(EncryptedString.of("Delay between actions in ticks"));
    private final StringSetting price = new StringSetting("Sell Price", "20k");
    private int delayCounter;
    private int currentHotbarSlot = 0;
    private boolean waitingForSellGui = false;
    private boolean waitingForConfirmation = false;
    private int postSelectWaitTicks = 0;

    public AutoMap() {
        super(EncryptedString.of("Auto Map"), EncryptedString.of("Automatically sells filled maps from your hotbar"), -1, Category.DONUT);
        this.addSettings(this.delay);
    }

    @Override
    public void onEnable() {
        super.onEnable();
        resetState();
    }

    private void resetState() {
        this.delayCounter = 0;
        this.currentHotbarSlot = 0;
        this.waitingForSellGui = false;
        this.waitingForConfirmation = false;
        this.postSelectWaitTicks = 0;
    }

    @EventListener
    public void onTick(final StartTickEvent event) {
        if (mc.player == null || mc.interactionManager == null) return;
        if (delayCounter > 0) { delayCounter--; return; }

        if (postSelectWaitTicks > 0) {
            postSelectWaitTicks--;
            if (postSelectWaitTicks == 0) {
                mc.getNetworkHandler().sendChatCommand("ah sell " + price.getValue());
                waitingForSellGui = true;
                delayCounter = delay.getIntValue();
            }
            return;
        }

        if (mc.currentScreen instanceof GenericContainerScreen) {
            GenericContainerScreenHandler handler = (GenericContainerScreenHandler) mc.player.currentScreenHandler;

            if (handler.getRows() >= 3) {
                for (int i = 0; i < handler.slots.size(); i++) {
                    ItemStack st = handler.getSlot(i).getStack();
                    if (st != null && !st.isEmpty() && st.getItem() == Items.LIME_STAINED_GLASS_PANE) {
                        mc.interactionManager.clickSlot(handler.syncId, i, 0, SlotActionType.PICKUP, mc.player);
                        waitingForConfirmation = false;
                        delayCounter = delay.getIntValue();
                        return;
                    }
                }
            }

            if (handler.getRows() == 5) {
                ItemStack sellSlot = handler.getSlot(13).getStack();
                if (sellSlot != null && !sellSlot.isEmpty()) {
                    mc.interactionManager.clickSlot(handler.syncId, 22, 0, SlotActionType.PICKUP, mc.player);
                    waitingForConfirmation = true;
                    delayCounter = delay.getIntValue();
                    return;
                }
            }
        }

        if (!waitingForSellGui && !waitingForConfirmation && mc.currentScreen == null) {
            if (currentHotbarSlot >= 9) {
                this.toggle();
                return;
            }

            // Check the item in the slot currently selected by the player
            int actualSlot = mc.player.getInventory().selectedSlot;
            if (actualSlot != currentHotbarSlot) {
                currentHotbarSlot = actualSlot;
            }

            // Skip empty or non-map items automatically
            boolean found = false;
            for (int i = currentHotbarSlot; i < 9; i++) {
                ItemStack stack = mc.player.getInventory().getStack(i);
                if (!stack.isEmpty() && stack.getItem() == Items.FILLED_MAP) {
                    mc.player.getInventory().selectedSlot = i;
                    postSelectWaitTicks = 5;
                    currentHotbarSlot = i + 1;
                    found = true;
                    break;
                }
            }

            if (!found) {
                this.toggle();
                return;
            }

            delayCounter = delay.getIntValue();
        } else if (mc.currentScreen == null) {
            waitingForSellGui = false;
            waitingForConfirmation = false;
            delayCounter = delay.getIntValue();
        }
    }
}
