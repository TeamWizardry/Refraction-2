package com.teamwizardry.refraction.api;

import com.teamwizardry.librarianlib.features.network.PacketHandler;
import com.teamwizardry.librarianlib.features.saving.Savable;
import com.teamwizardry.librarianlib.features.saving.Save;
import com.teamwizardry.refraction.api.utils.PosUtils;
import com.teamwizardry.refraction.common.network.PacketAddBeam;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.util.HashSet;
import java.util.UUID;

/**
 * Created by LordSaad.
 */
@Savable
public class Beam {

	@Save
	@NotNull
	private final World world;
	@Save
	@NotNull
	private Vec3d origin;
	@Save
	@NotNull
	private Vec3d slope;
	@Save
	@NotNull
	private Color color = Color.WHITE;
	@Save
	@NotNull
	private final HashSet<UUID> entitySkipList = new HashSet<>();
	@Save
	@NotNull
	private UUID uuid = UUID.randomUUID();
	@Save
	private double range = 256.0;
	@Save
	private int bounceLimit;
	@Save
	private int bouncedTimes;
	@Save
	private boolean ignoreEntities = false;

	public Beam(@NotNull World world, @NotNull Vec3d origin, @NotNull Vec3d slope, double range, @NotNull Color color) {
		this.world = world;
		this.origin = origin;
		this.slope = slope;
		this.range = range;
		this.color = color;
	}

	@NotNull
	public Vec3d getOrigin() {
		return origin;
	}

	public Beam setOrigin(@NotNull Vec3d origin) {
		this.origin = origin;
		return this;
	}

	@NotNull
	public World getWorld() {
		return world;
	}

	@NotNull
	public Vec3d getSlope() {
		return slope;
	}

	public Beam setSlope(@NotNull Vec3d slope) {
		this.slope = slope;
		return this;
	}

	@NotNull
	public Color getColor() {
		return color;
	}

	public Beam setColor(@NotNull Color color) {
		this.color = color;
		return this;
	}

	public void addEntityToSkip(@NotNull UUID uuid) {
		this.entitySkipList.add(uuid);
	}

	public double getRange() {
		return range;
	}

	public Beam setRange(double range) {
		this.range = range;
		return this;
	}

	public int getBouncedTimes() {
		return bouncedTimes;
	}

	public int getBounceLimit() {
		return bounceLimit;
	}

	public Beam setBounceLimit(int bounceLimit) {
		this.bounceLimit = bounceLimit;
		return this;
	}

	public boolean isIgnoreEntities() {
		return ignoreEntities;
	}

	public Beam setIgnoreEntities(boolean ignoreEntities) {
		this.ignoreEntities = ignoreEntities;
		return this;
	}

	@NotNull
	public UUID getUuid() {
		return uuid;
	}

	public boolean spawn() {
		if (world.isRemote) return false;
		if (color.getAlpha() <= 1) return false;
		if (--bouncedTimes > bounceLimit) return false;

		RayTraceResult result = PosUtils.raytrace(world, slope, origin, range, entitySkipList, ignoreEntities);
		if (result == null) return false;
		if (result.hitVec == null) return false;
		if (result.hitVec.distanceTo(origin) < 0.2) return false;

		PacketHandler.NETWORK.sendToAll(new PacketAddBeam(origin, result.hitVec, color));

		return true;
	}
}
