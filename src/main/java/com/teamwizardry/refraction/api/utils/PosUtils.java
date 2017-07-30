package com.teamwizardry.refraction.api.utils;

import net.minecraft.entity.Entity;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.HashSet;
import java.util.List;
import java.util.UUID;

/**
 * Created by Saad on 8/27/2016.
 */
public final class PosUtils {

	/**
	 * Credits to Masa on discord for providing the base of the code. I heavily editted it.
	 * This raytracer will precisely trace entities and blocks (including misses) without snapping to the grid.
	 *
	 * @param world  The world obj.
	 * @param slope  The angle vector to trace along.
	 * @param origin The origin of the trace.
	 * @param range  The maximum range to trace by.
	 * @param skip   An optional entity you can add to skip the trace with.
	 * @return The RaytraceResult.
	 */
	public static RayTraceResult raytrace(World world, Vec3d slope, Vec3d origin, double range, @Nullable HashSet<UUID> skip, boolean ignoreEntities) {
		Vec3d lookVec = origin.add(slope.scale(range));

		RayTraceResult result = world.rayTraceBlocks(origin, lookVec, false, false, true);

		Entity targetEntity = null;
		RayTraceResult entityTrace = null;
		AxisAlignedBB bb = new AxisAlignedBB(lookVec.x, lookVec.y, lookVec.z, lookVec.x, lookVec.y, lookVec.z);

		if (!ignoreEntities) {
			List<Entity> list = world.getEntitiesWithinAABBExcludingEntity(null, bb.expand(range, range, range));
			double closest = 0.0D;

			for (Entity entity : list) {
				if (skip != null && skip.contains(entity.getUniqueID())) continue;

				bb = entity.getEntityBoundingBox();
				RayTraceResult traceTmp = bb.calculateIntercept(lookVec, origin);

				if (traceTmp != null) {
					double tmp = origin.distanceTo(traceTmp.hitVec);

					if (tmp < closest || closest == 0.0D) {
						targetEntity = entity;
						entityTrace = traceTmp;
						closest = tmp;
					}
				}
			}
			if (targetEntity != null) result = new RayTraceResult(targetEntity, entityTrace.hitVec);
		}

		return result;
	}

	/**
	 * Takes several cartesian vectors, and returns one going in the average
	 * direction, regardless of vector magnitues
	 *
	 * @param vectors The list of vectors to average. Will be normalized.
	 * @return The average direction of all the given vectors.
	 */
	public static Vec3d averageDirection(List<Vec3d> vectors) {
		if (vectors == null) return Vec3d.ZERO;
		if (vectors.isEmpty()) return Vec3d.ZERO;
		double x = 0;
		double y = 0;
		double z = 0;

		for (Vec3d vec : vectors) {
			if (vec == null) continue;
			vec.normalize();
			x += vec.x;
			y += vec.y;
			z += vec.z;
		}

		return new Vec3d(x, y, z);
	}

	public static Vec3d vecFromRotations(float rotationPitch, float rotationYaw) {
		return Vec3d.fromPitchYaw(rotationPitch, rotationYaw);
	}

	public static float[] vecToRotations(Vec3d vec) {
		float yaw = (float) MathHelper.atan2(vec.z, vec.x);
		float pitch = (float) Math.asin(vec.y / vec.lengthVector());
		return new float[]{(float) Math.toDegrees(pitch), (float) Math.toDegrees(yaw) + 90};
	}
}
