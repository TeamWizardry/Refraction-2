package com.teamwizardry.refraction.api.utils;

import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

public class Utils {

	public static float signAngle(Vec3d a, Vec3d b, Vec3d n) {
		Vec3d cross = a.crossProduct(b);
		double s = cross.lengthVector();
		double c = a.dotProduct(b);
		double angle = MathHelper.atan2(s, c);

		if (n != null) {
			if (n.dotProduct(cross) < 0) {
				angle = -angle;
			}
		}

		return (float) Math.toDegrees(angle);
	}
}
