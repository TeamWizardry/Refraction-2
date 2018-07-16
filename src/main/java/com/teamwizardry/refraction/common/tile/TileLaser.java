package com.teamwizardry.refraction.common.tile;

import com.teamwizardry.librarianlib.features.autoregister.TileRegister;
import com.teamwizardry.librarianlib.features.base.block.tile.TileModTickable;
import com.teamwizardry.librarianlib.features.saving.Save;
import com.teamwizardry.refraction.api.Beam;
import net.minecraft.block.BlockDirectional;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.Vec3d;

import java.awt.*;
import java.util.UUID;

@TileRegister("laser")
public class TileLaser extends TileModTickable {

	@Save
	private UUID uuid;

	@Override
	public void onLoad() {
		uuid = UUID.nameUUIDFromBytes((pos.toLong() + "").getBytes());
	}

	@Override
	public void tick() {

		IBlockState state = world.getBlockState(pos);
		EnumFacing facing = state.getValue(BlockDirectional.FACING);

		new Beam(world, new Vec3d(getPos()).addVector(0.5, 0.5, 0.5), new Vec3d(facing.getDirectionVec()), 25, Color.RED)
				.setUUID(uuid)
				.spawn();
	}
}
