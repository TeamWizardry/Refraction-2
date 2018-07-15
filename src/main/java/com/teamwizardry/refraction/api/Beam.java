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
