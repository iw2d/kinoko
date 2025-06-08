package kinoko.provider.map;

import kinoko.provider.ProviderError;
import kinoko.provider.WzProvider;
import kinoko.provider.wz.serialize.WzProperty;

public final class PhysicsConstants {
    private final double walkForce;
    private final double walkSpeed;
    private final double walkDrag;
    private final double slipForce;
    private final double slipSpeed;
    private final double floatDrag1;
    private final double floatDrag2;
    private final double floatCoefficient;
    private final double swimForce;
    private final double swimSpeed;
    private final double flyForce;
    private final double flySpeed;
    private final double gravityAcc;
    private final double fallSpeed;
    private final double jumpSpeed;
    private final double maxFriction;
    private final double minFriction;
    private final double swimSpeedDec;
    private final double flyJumpDec;

    public PhysicsConstants(double walkForce, double walkSpeed, double walkDrag, double slipForce, double slipSpeed, double floatDrag1, double floatDrag2, double floatCoefficient, double swimForce, double swimSpeed, double flyForce, double flySpeed, double gravityAcc, double fallSpeed, double jumpSpeed, double maxFriction, double minFriction, double swimSpeedDec, double flyJumpDec) {
        this.walkForce = walkForce;
        this.walkSpeed = walkSpeed;
        this.walkDrag = walkDrag;
        this.slipForce = slipForce;
        this.slipSpeed = slipSpeed;
        this.floatDrag1 = floatDrag1;
        this.floatDrag2 = floatDrag2;
        this.floatCoefficient = floatCoefficient;
        this.swimForce = swimForce;
        this.swimSpeed = swimSpeed;
        this.flyForce = flyForce;
        this.flySpeed = flySpeed;
        this.gravityAcc = gravityAcc;
        this.fallSpeed = fallSpeed;
        this.jumpSpeed = jumpSpeed;
        this.maxFriction = maxFriction;
        this.minFriction = minFriction;
        this.swimSpeedDec = swimSpeedDec;
        this.flyJumpDec = flyJumpDec;
    }

    public double getWalkForce() {
        return walkForce;
    }

    public double getWalkSpeed() {
        return walkSpeed;
    }

    public double getWalkDrag() {
        return walkDrag;
    }

    public double getSlipForce() {
        return slipForce;
    }

    public double getSlipSpeed() {
        return slipSpeed;
    }

    public double getFloatDrag1() {
        return floatDrag1;
    }

    public double getFloatDrag2() {
        return floatDrag2;
    }

    public double getFloatCoefficient() {
        return floatCoefficient;
    }

    public double getSwimForce() {
        return swimForce;
    }

    public double getSwimSpeed() {
        return swimSpeed;
    }

    public double getFlyForce() {
        return flyForce;
    }

    public double getFlySpeed() {
        return flySpeed;
    }

    public double getGravityAcc() {
        return gravityAcc;
    }

    public double getFallSpeed() {
        return fallSpeed;
    }

    public double getJumpSpeed() {
        return jumpSpeed;
    }

    public double getMaxFriction() {
        return maxFriction;
    }

    public double getMinFriction() {
        return minFriction;
    }

    public double getSwimSpeedDec() {
        return swimSpeedDec;
    }

    public double getFlyJumpDec() {
        return flyJumpDec;
    }

    @Override
    public String toString() {
        return "Physics{" +
                "walkForce=" + walkForce +
                ", walkSpeed=" + walkSpeed +
                ", walkDrag=" + walkDrag +
                ", slipForce=" + slipForce +
                ", slipSpeed=" + slipSpeed +
                ", floatDrag1=" + floatDrag1 +
                ", floatDrag2=" + floatDrag2 +
                ", floatCoefficient=" + floatCoefficient +
                ", swimForce=" + swimForce +
                ", swimSpeed=" + swimSpeed +
                ", flyForce=" + flyForce +
                ", flySpeed=" + flySpeed +
                ", gravityAcc=" + gravityAcc +
                ", fallSpeed=" + fallSpeed +
                ", jumpSpeed=" + jumpSpeed +
                ", maxFriction=" + maxFriction +
                ", minFriction=" + minFriction +
                ", swimSpeedDec=" + swimSpeedDec +
                ", flyJumpDec=" + flyJumpDec +
                '}';
    }

    public static PhysicsConstants from(WzProperty physicsProp) throws ProviderError {
        return new PhysicsConstants(
                WzProvider.getDouble(physicsProp.get("walkForce")),
                WzProvider.getDouble(physicsProp.get("walkSpeed")),
                WzProvider.getDouble(physicsProp.get("walkDrag")),
                WzProvider.getDouble(physicsProp.get("slipForce")),
                WzProvider.getDouble(physicsProp.get("slipSpeed")),
                WzProvider.getDouble(physicsProp.get("floatDrag1")),
                WzProvider.getDouble(physicsProp.get("floatDrag2")),
                WzProvider.getDouble(physicsProp.get("floatCoefficient")),
                WzProvider.getDouble(physicsProp.get("swimForce")),
                WzProvider.getDouble(physicsProp.get("swimSpeed")),
                WzProvider.getDouble(physicsProp.get("flyForce")),
                WzProvider.getDouble(physicsProp.get("flySpeed")),
                WzProvider.getDouble(physicsProp.get("gravityAcc")),
                WzProvider.getDouble(physicsProp.get("fallSpeed")),
                WzProvider.getDouble(physicsProp.get("jumpSpeed")),
                WzProvider.getDouble(physicsProp.get("maxFriction")),
                WzProvider.getDouble(physicsProp.get("minFriction")),
                WzProvider.getDouble(physicsProp.get("swimSpeedDec")),
                WzProvider.getDouble(physicsProp.get("flyJumpDec"))
        );
    }
}
