package com.teamwizardry.refraction.common.tile;

import com.teamwizardry.librarianlib.features.autoregister.TileRegister;
import com.teamwizardry.librarianlib.features.base.block.tile.TileModTickable;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nonnull;

/**
 * Created by Demoniaque
 */
@TileRegister("reflection_chamber")
public class TileReflectionChamber extends TileModTickable {

	@Nonnull
	@SideOnly(Side.CLIENT)
	@Override
	public AxisAlignedBB getRenderBoundingBox() {
		return INFINITE_EXTENT_AABB;
	}

	@Override
	public void tick() {
		//Beam beam = outputBeam;
		//if (beam == null) return;
		//EnumFacing facing = EnumFacing.getFacingFromVector((float) beam.slope.x, (float) beam.slope.y, (float) beam.slope.z);
		//IBlockState state = world.getBlockState(pos.offset(facing));
		//if (state.getBlock() == ModBlocks.OPTIC_FIBER && state.getValue(BlockOpticFiber.FACING).contains(facing))
		//	beam.setSlope(PosUtils.getVecFromFacing(facing)).spawn();
		//else beam.spawn();
	}
}
