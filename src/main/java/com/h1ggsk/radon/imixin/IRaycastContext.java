package com.h1ggsk.radon.imixin;

import net.minecraft.entity.Entity;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RaycastContext;

public interface IRaycastContext {
    void a(final Vec3d p0, final Vec3d p1, final RaycastContext.ShapeType p2, final RaycastContext.FluidHandling p3, final Entity p4);
}
