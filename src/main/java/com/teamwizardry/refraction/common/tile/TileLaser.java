package com.teamwizardry.refraction.common.tile;

import com.teamwizardry.librarianlib.features.autoregister.TileRegister;
import com.teamwizardry.librarianlib.features.base.block.tile.TileModTickable;
import com.teamwizardry.librarianlib.features.base.block.tile.module.ModuleEnergy;
import com.teamwizardry.librarianlib.features.saving.Module;
import com.teamwizardry.refraction.api.Beam;
import com.teamwizardry.refraction.api.ConfigValues;
import com.teamwizardry.refraction.api.utils.Utils;
import net.minecraft.block.BlockDirectional;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.energy.IEnergyStorage;

import java.awt.*;

@TileRegister("laser")
public class TileLaser extends TileModTickable implements IEnergyStorage {

	@Module
	private ModuleEnergy energyStorage = new ModuleEnergy(ConfigValues.laserCapacity, ConfigValues.laserMaxInput);

	@Override
	public void tick() {
		if (canFire()) {
			IBlockState state = world.getBlockState(pos);
			EnumFacing facing = state.getValue(BlockDirectional.FACING);

			new Beam(world, new Vec3d(getPos()).addVector(0.5, 0.5, 0.5), new Vec3d(facing.getDirectionVec()), 25, Color.WHITE, Utils.createUUID(pos)).spawn();

			energyStorage.getHandler().extractEnergy(ConfigValues.laserConsume, false);
		}
	}

	private boolean canFire() {
		World world = getWorld();
		return !world.isRemote && !world.isBlockPowered(pos) && world.isBlockIndirectlyGettingPowered(pos) == 0 &&
				energyStorage.getHandler().extractEnergy(ConfigValues.laserConsume, true) == ConfigValues.laserConsume;
	}


	@Override
	public int receiveEnergy(int maxReceive, boolean simulate) {
		return energyStorage.getHandler().receiveEnergy(maxReceive, simulate);
	}

	@Override
	public int extractEnergy(int maxExtract, boolean simulate) {
		return energyStorage.getHandler().extractEnergy(maxExtract, simulate);
	}

	@Override
	public int getEnergyStored() {
		return energyStorage.getHandler().getEnergyStored();
	}

	@Override
	public int getMaxEnergyStored() {
		return energyStorage.getHandler().getMaxEnergyStored();
	}

	@Override
	public boolean canExtract() {
		return false;
	}

	@Override
	public boolean canReceive() {
		return true;
	}
}
