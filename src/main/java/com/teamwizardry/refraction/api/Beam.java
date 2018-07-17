package com.teamwizardry.refraction.api;

import com.teamwizardry.librarianlib.features.network.PacketHandler;
import com.teamwizardry.librarianlib.features.saving.AbstractSaveHandler;
import com.teamwizardry.librarianlib.features.saving.Savable;
import com.teamwizardry.librarianlib.features.saving.Save;
import com.teamwizardry.refraction.api.utils.RayTrace;
import com.teamwizardry.refraction.common.network.PacketAddBeam;
import net.minecraft.block.state.IBlockState;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.common.util.INBTSerializable;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.awt.*;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

/**
 * Created by Demoniaque.
 */
@Savable
public class Beam implements INBTSerializable<NBTTagCompound> {

	@Save
	public Vec3d origin;

	@Save
	public Vec3d slope;

	@Save
	@Nullable
	public Vec3d originalSlope;

	@Save
	public int red, green, blue;

	@Save
	public Set<UUID> entitySkipList = new HashSet<>();

	@Save
	public UUID uuid;

	@Save
	public double range;

	@Save
	public int bounceLimit = 20;
	@Save
	public boolean ignoreEntities = false;
	@Save
	@Nullable
	public Vec3d endLoc;
	@Save
	public Set<String> tags = new HashSet<>();
	@Save
	private int bouncedTimes;

	public Beam(Vec3d origin, Vec3d slope, @Nullable Vec3d originalSlope, int red, int green, int blue, Set<UUID> entitySkipList, UUID uuid, double range, int bounceLimit, int bouncedTimes, boolean ignoreEntities, @Nullable Vec3d endLoc, Set<String> tags) {
		this.origin = origin;
		this.slope = slope;
		this.originalSlope = originalSlope;
		this.red = red;
		this.green = green;
		this.blue = blue;
		this.entitySkipList = entitySkipList;
		this.uuid = uuid;
		this.range = range;
		this.bounceLimit = bounceLimit;
		this.bouncedTimes = bouncedTimes;
		this.ignoreEntities = ignoreEntities;
		this.endLoc = endLoc;
		this.tags = tags;
	}

	public Beam() {
	}

	public Beam(@NotNull Vec3d origin, @NotNull Vec3d slope, double range, @NotNull Color color, @NotNull UUID uuid) {
		this(origin, slope, range, color.getRed(), color.getGreen(), color.getBlue(), uuid);
	}

	public Beam(@NotNull Vec3d origin, @NotNull Vec3d slope, double range, int red, int green, int blue, @NotNull UUID uuid) {
		this.origin = origin;
		this.slope = slope;
		this.range = range;
		this.red = red;
		this.green = green;
		this.blue = blue;
		this.uuid = uuid;
	}

	public static void copyBeamData(Beam beamFrom, Beam beamTo) {
		beamTo.origin = beamFrom.origin;
		beamTo.slope = beamFrom.slope;
		beamTo.endLoc = beamFrom.endLoc;
		beamTo.uuid = beamFrom.uuid;
		beamTo.red = beamFrom.red;
		beamTo.green = beamFrom.green;
		beamTo.blue = beamFrom.blue;
		beamTo.originalSlope = beamFrom.originalSlope;
		beamTo.ignoreEntities = beamFrom.ignoreEntities;
		beamTo.bouncedTimes = beamFrom.bouncedTimes;
		beamTo.bounceLimit = beamFrom.bounceLimit;
		beamTo.entitySkipList = beamFrom.entitySkipList;
	}

	public static RayTraceResult runRawTrace(World world, Vec3d slope, Vec3d origin, double range, boolean ignoreEntities, Set<UUID> entitySkipList) {
		return new RayTrace(world, slope, origin, range)
				.setIgnoreBlocksWithoutBoundingBoxes(false)
				.setReturnLastUncollidableBlock(true)
				.setSkipEntities(ignoreEntities)
				.addBlockToSkip(new BlockPos(origin))
				.setEntityFilter(input -> {
					if (input == null) return false;
					return !entitySkipList.contains(input.getUniqueID());
				}).trace();
	}

	public Beam setTags(Set<String> tags) {
		this.tags = tags;
		return this;
	}

	public Beam addTag(String tag) {
		tags.add(tag);
		return this;
	}

	public Beam removeTag(String tag) {
		tags.remove(tag);
		return this;
	}

	public boolean hasTag(String tag) {
		return tags.contains(tag);
	}

	public Beam setUUID(UUID uuid) {
		this.uuid = uuid;
		return this;
	}

	public Beam addEntityToSkip(@NotNull UUID uuid) {
		this.entitySkipList.add(uuid);
		return this;
	}

	public Beam setEntityskipList(@NotNull Set<UUID> entitySkipList) {
		this.entitySkipList = entitySkipList;
		return this;
	}

	public Beam setBounceLimit(int bounceLimit) {
		this.bounceLimit = bounceLimit;
		return this;
	}

	public Beam setBouncedTimes(int bouncedTimes) {
		this.bouncedTimes = bouncedTimes;
		return this;
	}

	/**
	 * Will create a beam that's exactly like the one passed.
	 *
	 * @return The new beam created. Can be modified as needed.
	 */
	public Beam createSimilarBeam(UUID uuid) {
		return createSimilarBeam(origin, uuid);
	}

	/**
	 * Will create a beam that's exactly like the one passed except in color.
	 *
	 * @return The new beam created. Can be modified as needed.
	 */
	public Beam createSimilarBeam(Color color, UUID uuid) {
		return createSimilarBeam(origin, slope, color, uuid);
	}

	/**
	 * Will create a beam that's exactly like the one passed except in color.
	 *
	 * @return The new beam created. Can be modified as needed.
	 */
	public Beam createSimilarBeam(int red, int green, int blue, UUID uuid) {
		return createSimilarBeam(origin, slope, red, green, blue, uuid);
	}

	/**
	 * Will create a similar beam that starts from the position this beam ended at
	 * and will set it's slope to the one specified. So it's a new beam from the position
	 * you last hit to the new one you specify.
	 *
	 * @param slope The slope or destination or final location the beam will point to.
	 * @return The new beam created. Can be modified as needed.
	 */
	public Beam createSimilarBeam(Vec3d slope, UUID uuid) {
		return createSimilarBeam(origin, slope, uuid);
	}


	/**
	 * Will create a similar beam that starts and ends in the positions you specify
	 *
	 * @param init  The initial location or origin to spawn the beam from.
	 * @param slope The direction or slope or final destination or location the beam will point to.
	 * @return The new beam created. Can be modified as needed.
	 */
	public Beam createSimilarBeam(Vec3d init, Vec3d slope, UUID uuid) {
		return createSimilarBeam(init, slope, red, green, blue, uuid);
	}

	@Nullable
	public Vec3d getOriginalSlope() {
		return originalSlope;
	}

	public Beam setOriginalSlope(@Nullable Vec3d originalSlope) {
		this.originalSlope = originalSlope;
		return this;
	}

	/**
	 * Will create a similar beam that starts and ends in the positions you specify, with a custom color.
	 *
	 * @param init  The initial location or origin to spawn the beam from.
	 * @param slope The direction or slope or final destination or location the beam will point to.
	 * @return The new beam created. Can be modified as needed.
	 */
	public Beam createSimilarBeam(Vec3d init, Vec3d slope, Color color, UUID uuid) {
		return new Beam(init, slope, range, color, uuid)
				.setTags(tags)
				.setEntityskipList(entitySkipList)
				.setBounceLimit(bounceLimit)
				.setBouncedTimes(bouncedTimes);
	}

	/**
	 * Will create a similar beam that starts and ends in the positions you specify, with a custom color.
	 *
	 * @param init  The initial location or origin to spawn the beam from.
	 * @param slope The direction or slope or final destination or location the beam will point to.
	 * @return The new beam created. Can be modified as needed.
	 */
	public Beam createSimilarBeam(Vec3d init, Vec3d slope, int red, int green, int blue, UUID uuid) {
		return new Beam(init, slope, range, red, green, blue, uuid)
				.setTags(tags)
				.setEntityskipList(entitySkipList)
				.setBounceLimit(bounceLimit)
				.setBouncedTimes(bouncedTimes)
				.setOriginalSlope(this.slope);
	}

	public boolean spawn(World world) {
		if (world.isRemote) return false;
		if (++bouncedTimes > bounceLimit) return false;

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

		endLoc = result.hitVec;

		if (result.typeOfHit == RayTraceResult.Type.BLOCK) {
			BlockPos pos = result.getBlockPos();
			IBlockState state = world.getBlockState(pos);
			if (state.getBlock() instanceof ILightSink) {
				((ILightSink) state.getBlock()).handleBeam(world, pos, this);
			}
		}

		PacketHandler.NETWORK.sendToAll(new PacketAddBeam(this));

		return true;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		Beam beam = (Beam) o;
		return Objects.equals(uuid, beam.uuid);
	}

	@Override
	public int hashCode() {
		return Objects.hash(uuid);
	}

	@Override
	public NBTTagCompound serializeNBT() {
		NBTTagCompound compound = new NBTTagCompound();

		compound.setTag("save", AbstractSaveHandler.writeAutoNBT(this, true));
		return compound;
	}

	@Override
	public void deserializeNBT(NBTTagCompound nbt) {
		if (nbt.hasKey("save")) {
			AbstractSaveHandler.readAutoNBT(this, nbt.getTag("save"), true);
		}
	}
}
