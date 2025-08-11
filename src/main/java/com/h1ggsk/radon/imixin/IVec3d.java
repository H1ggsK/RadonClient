package com.h1ggsk.radon.imixin;

import net.minecraft.util.math.Vec3i;
import org.joml.Vector3d;

public interface IVec3d {
    void set(final double p0, final double p1, final double p2);

    default void a(final Vec3i vec3i) {
        this.set(vec3i.getX(), vec3i.getY(), vec3i.getZ());
    }

    default void a(final Vector3d vector3d) {
        this.set(vector3d.x, vector3d.y, vector3d.z);
    }

    void setXZ(final double p0, final double p1);

    void setY(final double p0);
}
