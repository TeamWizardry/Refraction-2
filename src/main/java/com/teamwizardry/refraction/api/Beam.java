package com.teamwizardry.refraction.api;

import com.google.common.base.Predicate;
import com.teamwizardry.librarianlib.features.network.PacketHandler;
import com.teamwizardry.librarianlib.features.saving.Savable;
import com.teamwizardry.librarianlib.features.saving.Save;
import com.teamwizardry.refraction.api.utils.PosUtils;
import com.teamwizardry.refraction.api.utils.RayTrace;
import com.teamwizardry.refraction.common.network.PacketAddBeam;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.awt.*;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

/**
 * Created by Demoniaque.
 */
public class Beam {

	@Save
	@NotNull
	public final World world;
	@Save
	@NotNull
	public final Vec3d origin;
	@Save
	@NotNull
	public final Vec3d slope;
	@Save
	@NotNull
	public final Color color;
	@Save
	@NotNull
	public final Set<UUID> entitySkipList = new HashSet<>();
	@Save
	@NotNull
	public final UUID uuid = UUID.randomUUID();
	@Save
	public final double range;
	@Save
	public int bounceLimit = 10;
	@Save
	private int bouncedTimes;
	@Save
	public final boolean ignoreEntities = false;

	public Beam(@NotNull World world, @NotNull Vec3d origin, @NotNull Vec3d slope, double range, @NotNull Color color) {
		this.world = world;
		this.origin = origin;
		this.slope = slope;
		this.range = range;
		this.color = color;
	}

	public Beam addEntityToSkip(@NotNull UUID uuid) {
		this.entitySkipList.add(uuid);
		return this;
	}

	public Beam setBounceLimit(int bounceLimit) {
		this.bounceLimit = bounceLimit;
		return this;
	}

	/**
	 * Will create a beam that's exactly like the one passed.
	 *
	 * @return The new beam created. Can be modified as needed.
	 */
	public Beam createSimilarBeam() {
		return createSimilarBeam(origin);
	}

	/**
	 * Will create a beam that's exactly like the one passed except in color.
	 *
	 * @return The new beam created. Can be modified as needed.
	 */
	public Beam createSimilarBeam(Color color) {
		return createSimilarBeam(origin, slope, color);
	}

	/**
	 * Will create a similar beam that starts from the position this beam ended at
	 * and will set it's slope to the one specified. So it's a new beam from the position
	 * you last hit to the new one you specify.
	 *
	 * @param slope The slope or destination or final location the beam will point to.
	 * @return The new beam created. Can be modified as needed.
	 */
	public Beam createSimilarBeam(Vec3d slope) {
		return createSimilarBeam(origin, slope);
	}


	/**
	 * Will create a similar beam that starts and ends in the positions you specify
	 *
	 * @param init The initial location or origin to spawn the beam from.
	 * @param slope  The direction or slope or final destination or location the beam will point to.
	 * @return The new beam created. Can be modified as needed.
	 */
	public Beam createSimilarBeam(Vec3d init, Vec3d slope) {
		return createSimilarBeam(init, slope, color);
	}

	/**
	 * Will create a similar beam that starts and ends in the positions you specify, with a custom color.
	 *
	 * @param init The initial location or origin to spawn the beam from.
	 * @param dir  The direction or slope or final destination or location the beam will point to.
	 * @return The new beam created. Can be modified as needed.
	 */
	public Beam createSimilarBeam(Vec3d init, Vec3d slope, Color color) {
		return new Beam(world, init, slope, range, color)
				.setBounceLimit(bounceLimit-bouncedTimes);
	}

	public boolean spawn() {
		if (world.isRemote) return false;
		//if (color.getAlpha() <= 1) return false;
		if (--bouncedTimes > bounceLimit) return false;

		RayTraceResult result = new RayTrace(world, slope, origin, range)
				.setIgnoreBlocksWithoutBoundingBoxes(false)
				.setReturnLastUncollidableBlock(true)
				.setSkipEntities(ignoreEntities)
				.addBlockToSkip(new BlockPos(origin))
				.setEntityFilter(input -> {
					if (input == null) return false;
					return !entitySkipList.contains(input.getUniqueID());
				}).trace();

		if (result.hitVec == null) return false;
		if (result.hitVec.distanceTo(origin) < 0.2) return false;

		PacketHandler.NETWORK.sendToAll(new PacketAddBeam(origin, result.hitVec, color));

		return true;
	}
}
