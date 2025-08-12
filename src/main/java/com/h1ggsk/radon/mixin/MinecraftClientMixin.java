package com.h1ggsk.radon.mixin;

import com.h1ggsk.radon.Radon;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.util.Window;
import net.minecraft.client.world.ClientWorld;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import com.h1ggsk.radon.event.events.*;
import com.h1ggsk.radon.manager.EventManager;

@Mixin({MinecraftClient.class})
public class MinecraftClientMixin {
    @Shadow
    @Nullable
    public ClientWorld world;
    @Shadow
    @Final
    private Window window;
    @Shadow
    private int itemUseCooldown;

    @Inject(method = {"tick"}, at = {@At("HEAD")})
    private void onStartTick(final CallbackInfo ci) {
        if (this.world != null) {
            EventManager.throwEvent(new StartTickEvent());
        }
    }

    @Inject(method = {"tick"}, at = {@At("RETURN")})
    private void onEndTick(final CallbackInfo ci) {
        if (this.world != null) {
            EventManager.throwEvent(new EndTickEvent());
        }
    }

    @Inject(method = {"onResolutionChanged"}, at = {@At("HEAD")})
    private void onResolutionChanged(final CallbackInfo ci) {
        EventManager.throwEvent(new ResolutionChangedEvent(this.window));
    }

    @Inject(method = {"doItemUse"}, at = {@At("RETURN")}, cancellable = true)
    private void onItemUseReturn(final CallbackInfo ci) {
        final PostItemUseEvent event = new PostItemUseEvent(this.itemUseCooldown);
        EventManager.throwEvent(event);
        if (event.isCancelled()) ci.cancel();
        this.itemUseCooldown = event.cooldown;
    }

    @Inject(method = {"doItemUse"}, at = {@At("HEAD")}, cancellable = true)
    private void onItemUseHead(final CallbackInfo ci) {
        final PreItemUseEvent event = new PreItemUseEvent(this.itemUseCooldown);
        EventManager.throwEvent(event);
        if (event.isCancelled()) ci.cancel();
        this.itemUseCooldown = event.cooldown;
    }

    @Inject(method = {"doAttack"}, at = {@At("HEAD")}, cancellable = true)
    private void onAttack(final CallbackInfoReturnable<Boolean> cir) {
        final AttackEvent event = new AttackEvent();
        EventManager.throwEvent(event);
        if (event.isCancelled()) cir.setReturnValue(false);
    }

    @Inject(method = {"handleBlockBreaking"}, at = {@At("HEAD")}, cancellable = true)
    private void onBlockBreaking(final boolean breaking, final CallbackInfo ci) {
        final BlockBreakingEvent event = new BlockBreakingEvent();
        EventManager.throwEvent(event);
        if (event.isCancelled()) ci.cancel();
    }

    @Inject(method = {"setScreen"}, at = {@At("HEAD")}, cancellable = true)
    private void onSetScreen(final Screen screen, final CallbackInfo ci) {
        final SetScreenEvent event = new SetScreenEvent(screen);
        EventManager.throwEvent(event);
        if (event.isCancelled()) ci.cancel();

    }

    @Inject(method = {"stop"}, at = {@At("HEAD")})
    private void onClose(final CallbackInfo callbackInfo) {
        Radon.configManager.shutdown();
        Radon.INSTANCE.getConfigManager().shutdown();
    }
}