package com.teamwizardry.refraction.client.render;

import com.teamwizardry.refraction.api.Beam;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.opencl.CL;

import java.awt.*;
import java.util.Objects;
import java.util.UUID;

/**
 * Created by Demoniaque.
 */
@SideOnly(Side.CLIENT)
public class BeamRenderInfo {

	public Vec3d origin;
	public Vec3d target;
	public Color color;
	public long lastTime;
	public UUID uuid;

	public BeamRenderInfo(World world, Vec3d origin, Vec3d target, Color color, UUID uuid) {
		this.origin = origin;
		this.target = target;
		this.color = color;
		this.lastTime = world.getTotalWorldTime();
		this.uuid = uuid;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		BeamRenderInfo that = (BeamRenderInfo) o;
		return Objects.equals(uuid, that.uuid);
	}

	@Override
	public int hashCode() {
		return Objects.hash(uuid);
	}
}
