package com.teamwizardry.refraction.client.render;

import com.teamwizardry.refraction.api.Beam;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.Objects;

/**
 * Created by Demoniaque.
 */
public class BeamRenderInfo {

	public Beam beam;

	@Nullable
	public BlockPos source;
	public long lastTime;

	public BeamRenderInfo(World world, Beam beam) {
		this.beam = beam;
		lastTime = world.getTotalWorldTime();
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		BeamRenderInfo that = (BeamRenderInfo) o;
		return Objects.equals(beam, that.beam);
	}

	@Override
	public int hashCode() {

		return Objects.hash(beam);
	}
}
