package com.h1ggsk.radon.mixin;

import net.minecraft.block.spawner.MobSpawnerEntry;
import net.minecraft.block.spawner.MobSpawnerLogic;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin({MobSpawnerLogic.class})
public interface MobSpawnerLogicAccessor {
    @Accessor("spawnEntry")
    MobSpawnerEntry getSpawnEntry();
}