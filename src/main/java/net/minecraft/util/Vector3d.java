package net.minecraft.util;

import lombok.Getter;
import lombok.Setter;

public class Vector3d
{
    /** The X coordinate */
    @Getter
    @Setter
    public double x;

    /** The Y coordinate */
    @Getter
    @Setter
    public double y;

    /** The Z coordinate */
    @Getter
    @Setter
    public double z;

    public Vector3d()
    {
        this.x = this.y = this.z = 0.0D;
    }
}
