package com.teamwizardry.refraction.api.utils;

import com.google.common.base.Predicate;
import net.minecraft.entity.Entity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.HashSet;
import java.util.List;

/**
 * Copied from Wizardry
 */
public class RayTrace {

	private final World world;
	private final Vec3d slope;
	private final Vec3d origin;
	private final double range;

	private boolean skipBlocks = false;
	private boolean returnLastUncollidableBlock = true;
	private boolean skipEntities = false;
	private boolean ignoreBlocksWithoutBoundingBoxes = false;
	private Predicate<Entity> predicate;
	private HashSet<BlockPos> skipBlockList = new HashSet<>();

	public RayTrace(@Nonnull World world, @Nonnull Vec3d slope, @Nonnull Vec3d origin, double range) {
		this.world = world;
		this.slope = slope;
		this.origin = origin;
		this.range = range;
	}

	public RayTrace addBlockToSkip(@Nonnull BlockPos pos) {
		skipBlockList.add(pos);
		return this;
	}

	public RayTrace setEntityFilter(Predicate<Entity> predicate) {
		this.predicate = predicate;
		return this;
	}

	public RayTrace setReturnLastUncollidableBlock(boolean returnLastUncollidableBlock) {
		this.returnLastUncollidableBlock = returnLastUncollidableBlock;
		return this;
	}

	public RayTrace setIgnoreBlocksWithoutBoundingBoxes(boolean ignoreBlocksWithoutBoundingBoxes) {
		this.ignoreBlocksWithoutBoundingBoxes = ignoreBlocksWithoutBoundingBoxes;
		return this;
	}

	public RayTrace setSkipEntities(boolean skipEntities) {
		this.skipEntities = skipEntities;
		return this;
	}

	/**
	 * Credits to Masa on discord for providing the base of the code. I heavily modified it.
	 * This raytracer will precisely trace entities and blocks (including misses) without snapping to any grid.
	 *
	 * @return The RaytraceResult.
	 */
	@Nonnull
	public RayTraceResult trace() {
		Vec3d lookVec = origin.add(slope.scale(range));

		RayTraceResult result = world.rayTraceBlocks(origin, lookVec, false, ignoreBlocksWithoutBoundingBoxes, returnLastUncollidableBlock);

		if (skipEntities) {
			if (result == null) {
				result = new RayTraceResult(
						RayTraceResult.Type.BLOCK,
						lookVec,
						EnumFacing.getFacingFromVector((float) lookVec.x, (float) lookVec.y, (float) lookVec.z),
						new BlockPos(lookVec));
			}
			return result;
		}

		Entity targetEntity = null;
		RayTraceResult entityTrace = null;
		AxisAlignedBB bb = new AxisAlignedBB(lookVec.x, lookVec.y, lookVec.z, lookVec.x, lookVec.y, lookVec.z);
		List<Entity> list = world.getEntitiesWithinAABB(Entity.class, bb.grow(range, range, range), input -> {
			if (predicate == null) return true;
			else return predicate.test(input);
		});
		double closest = 0.0D;

		for (Entity entity : list) {
			if (entity == null) continue;

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

		if (result == null) {
			result = new RayTraceResult(
					RayTraceResult.Type.BLOCK,
					lookVec,
					EnumFacing.getFacingFromVector((float) lookVec.x, (float) lookVec.y, (float) lookVec.z),
					new BlockPos(lookVec));
		}
		return result;
	}
}
