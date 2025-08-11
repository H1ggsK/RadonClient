package com.h1ggsk.radon.module.modules.donut;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import com.h1ggsk.radon.event.EventListener;
import com.h1ggsk.radon.event.events.EntitySpawnEvent;
import com.h1ggsk.radon.module.Category;
import com.h1ggsk.radon.module.Module;
import com.h1ggsk.radon.utils.EncryptedString;

import java.util.ArrayList;

public final class AntiTrap extends Module {
    public AntiTrap() {
        super(EncryptedString.of("AntiTrap"), EncryptedString.of("Module that helps you escape Polish traps"), -1, Category.DONUT);
        this.addSettings();
    }

    @Override
    public void onEnable() {
        this.removeTrapEntities();
        super.onEnable();
    }

    @Override
    public void onDisable() {
        super.onDisable();
    }

    @EventListener
    public void onEntitySpawn(final EntitySpawnEvent entitySpawnEvent) {
        if (this.isTrapEntity(entitySpawnEvent.packet.getEntityType())) {
            entitySpawnEvent.cancel();
        }
    }

    private void removeTrapEntities() {
        if (mc.world == null) {
            return;
        }
        final ArrayList<Entity> trapEntities = new ArrayList<>();
        mc.world.getEntities().forEach(entity -> {
            if (entity != null && this.isTrapEntity(entity.getType())) {
                trapEntities.add(entity);
            }
        });
        trapEntities.forEach(trapEntity -> {
            if (!trapEntity.isRemoved()) {
                trapEntity.remove(Entity.RemovalReason.DISCARDED);
            }
        });
    }

    private boolean isTrapEntity(final EntityType<?> entityType) {
        return entityType != null && (entityType.equals(EntityType.ARMOR_STAND) || entityType.equals(EntityType.CHEST_MINECART));
    }
}