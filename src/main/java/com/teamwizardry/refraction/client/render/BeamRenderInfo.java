package com.teamwizardry.refraction.client.render;

import net.minecraft.util.math.Vec3d;

import java.awt.*;

/**
 * Created by Demoniaque.
 */
public class BeamRenderInfo {

	private final Vec3d origin;
	private final Vec3d target;
	private final Color color;

	public BeamRenderInfo(Vec3d origin, Vec3d target, Color color) {
		this.origin = origin;
		this.target = target;
		this.color = color;
	}

	public Vec3d getOrigin() {
		return origin;
	}

	public Vec3d getTarget() {
		return target;
	}

	public Color getColor() {
		return color;
	}
}
