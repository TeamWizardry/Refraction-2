package com.teamwizardry.refraction.api.utils;

import com.teamwizardry.refraction.api.Beam;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

import java.util.UUID;

public class Utils {

	public static UUID createUUID(BlockPos pos) {
		return createUUID(pos.toLong() + "");
	}

	public static UUID createUUID(String hash) {
		return UUID.nameUUIDFromBytes(hash.getBytes());
	}

	public static UUID createUUID(BlockPos pos, Beam beam, int index) {
		return Utils.createUUID(pos.toLong() + "-" + beam.uuid.hashCode() + "-" + index);
	}

	public static UUID createUUID(BlockPos pos, Beam beam) {
		return Utils.createUUID(pos.toLong() + "-" + beam.uuid.hashCode());
	}

	public static UUID createUUID(BlockPos pos, int index) {
		return Utils.createUUID(pos.toLong() + "-" + index);
	}

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
