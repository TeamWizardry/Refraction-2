package com.teamwizardry.refraction.common.tile;

import com.teamwizardry.librarianlib.features.base.block.tile.TileModTickable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.energy.EnergyStorage;
import net.minecraftforge.energy.IEnergyStorage;

public class TileSolarPanel extends TileModTickable implements IEnergyStorage {

	private EnergyStorage energyStorage = new EnergyStorage(1000, 100);

	@Override
	public void tick() {
		if(!world.isRemote) {
			energyStorage.receiveEnergy(getCurrentEnergyGeneration(pos.up()), false);
		}
	}

	public int getCurrentEnergyGeneration(BlockPos pos) {
		return Math.round(10 * getSunIntensity(pos));//TODO CONFIG
	}

	private float getSunIntensity(BlockPos at) {
		float sunIntensity = 0;
		if(world.canBlockSeeSky(at)) {
			// Celestial angle == 0 at zenith.
			float celestialAngleRadians = world.getCelestialAngleRadians(1.0f);
			if(celestialAngleRadians > Math.PI)
				celestialAngleRadians = (float) (2 * Math.PI - celestialAngleRadians);

			sunIntensity =  Math.min(1, Math.max(0, MathHelper.cos(celestialAngleRadians)));

			if(world.isRaining()) {
				sunIntensity *= 0.6; //TODO CONFIG?
			} else if(world.isThundering()) {
				sunIntensity *= 0.4; //TODO CONFIG?
			}
		}
		return sunIntensity
	}

	@Override
	public int receiveEnergy(int maxReceive, boolean simulate) {
		return energyStorage.receiveEnergy(maxReceive, simulate);
	}

	@Override
	public int extractEnergy(int maxExtract, boolean simulate) {
		return energyStorage.extractEnergy(maxExtract, simulate);
	}

	@Override
	public int getEnergyStored() {
		return energyStorage.getEnergyStored();
	}

	@Override
	public int getMaxEnergyStored() {
		return energyStorage.getMaxEnergyStored();
	}

	@Override
	public boolean canExtract() {
		return true;
	}

	@Override
	public boolean canReceive() {
		return false;
	}
}
