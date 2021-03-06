package com.teamwizardry.refraction.api;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import javax.annotation.Nonnull;

/**
 * Created by TheCodeWarrior
 */
public interface ILightSink extends IBeamImmune {
	/**
	 * Handle a beam. The default implementation is provided for backwards compatibility.
	 *
	 * @param world The world the block is in
	 * @param pos   The pos of the block
	 * @param beam  The beam being handled
	 * @return Whether the beam should be stopped
	 */
	default boolean handleBeam(@Nonnull World world, @Nonnull BlockPos pos, @Nonnull Beam beam) {
		return true;
	}

	@Override
	default boolean isImmune(@Nonnull World world, @Nonnull BlockPos pos) {
		return true;
	}
}
