package de.florianmichael.viamcp.fixes;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Movement attributes introduced in 26.2 (minecraft:air_drag_modifier, minecraft:bounciness,
 * minecraft:friction_modifier), stored per entity id. ViaMCP fills them from the attribute packet
 * on the network thread, the physics code reads them on the main thread.
 * <p>
 * The attribute values replace the vanilla constants directly, and the defaults are those constants,
 * so entities without data (and servers that never send the attributes) keep vanilla 1.8 movement.
 */
public class MovementAttributes {
    private static final Map<Integer, MovementAttributes> BY_ENTITY = new ConcurrentHashMap<>();
    /** Vanilla vertical drag, the float 0.98F widened exactly as the vanilla code multiplies it. */
    public static final double DEFAULT_AIR_DRAG = 0.98F;
    /** Vanilla block friction factor, multiplied with the block's slipperiness. */
    public static final double DEFAULT_FRICTION = 0.91F;
    private static final MovementAttributes DEFAULT = new MovementAttributes();

    /** Replaces the 0.98 vertical drag. */
    private volatile double airDrag = DEFAULT_AIR_DRAG;
    /** How much horizontal motion is reflected on a wall collision, 0.0 = stop dead. */
    private volatile double bounciness = 0.0D;
    /** Replaces the 0.91 block friction (in the air and times slipperiness on the ground). */
    private volatile double friction = DEFAULT_FRICTION;

    public static MovementAttributes get(int entityId) {
        MovementAttributes attributes = BY_ENTITY.get(entityId);
        return attributes != null ? attributes : DEFAULT;
    }

    public static MovementAttributes getOrCreate(int entityId) {
        return BY_ENTITY.computeIfAbsent(entityId, id -> new MovementAttributes());
    }

    /** Called on every new connection, entity ids are reused between servers. */
    public static void reset() {
        BY_ENTITY.clear();
    }

    public double getAirDrag() {
        return this.airDrag;
    }

    public void setAirDrag(double airDrag) {
        this.airDrag = clamp(airDrag, 0.0D, 2048.0D);
    }

    public double getBounciness() {
        return this.bounciness;
    }

    public void setBounciness(double bounciness) {
        this.bounciness = clamp(bounciness, 0.0D, 1.0D);
    }

    public double getFriction() {
        return this.friction;
    }

    public void setFriction(double friction) {
        this.friction = clamp(friction, 0.0D, 2048.0D);
    }

    private static double clamp(double value, double min, double max) {
        return Math.max(min, Math.min(max, value));
    }
}
