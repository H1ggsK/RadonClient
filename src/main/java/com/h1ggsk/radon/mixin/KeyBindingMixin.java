package com.h1ggsk.radon.mixin;

import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import com.h1ggsk.radon.Radon;
import com.h1ggsk.radon.imixin.IKeybinding;

@Mixin({KeyBinding.class})
public abstract class KeyBindingMixin implements IKeybinding {
    @Shadow
    private InputUtil.Key boundKey;

    @Shadow public abstract void setPressed(boolean pressed);

    @Override
    public boolean radon$isActuallyPressed() {
        return InputUtil.isKeyPressed(Radon.mc.getWindow().getHandle(), this.boundKey.getCode());
    }

    @Override
    public void radon$resetPressed() {
        this.setPressed(this.radon$isActuallyPressed());
    }
}