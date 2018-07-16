package com.teamwizardry.refraction.common.tile;

import com.teamwizardry.librarianlib.features.autoregister.TileRegister;
import com.teamwizardry.librarianlib.features.base.block.tile.TileModTickable;
import com.teamwizardry.librarianlib.features.base.block.tile.module.ModuleEnergy;
import com.teamwizardry.librarianlib.features.saving.Module;
import com.teamwizardry.refraction.api.ConfigValues;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.IEnergyStorage;

@TileRegister("solar_panel")
public class TileSolarPanel extends TileModTickable implements IEnergyStorage {

	@Module
	private ModuleEnergy energyStorage = new ModuleEnergy(ConfigValues.solarPanelCapacity, ConfigValues.solarPanelMaxOutput);

	@Override
	public void tick() {
		if(!world.isRemote) {
			energyStorage.getHandler().receiveEnergy(getCurrentEnergyGeneration(pos.up()), false);

			destributeEnergy();
		}
	}

	private int getCurrentEnergyGeneration(BlockPos pos) {
		return Math.round(ConfigValues.solarPanelGeneration * getSunIntensity(pos));
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
				sunIntensity *= ConfigValues.solarPanelRainMultiplier;
			} else if(world.isThundering()) {
				sunIntensity *= ConfigValues.solarPanelThunderMultiplier;
			}
		}
		return sunIntensity;
	}

	protected void destributeEnergy() {
		EnumFacing outSide = EnumFacing.DOWN;
		TileEntity neighbor = getWorld().getTileEntity(pos.offset(outSide));
		if(neighbor != null && neighbor.hasCapability(CapabilityEnergy.ENERGY, outSide.getOpposite())) {
			IEnergyStorage target = neighbor.getCapability(CapabilityEnergy.ENERGY, outSide.getOpposite());
			int toTransfer = Math.min(ConfigValues.solarPanelMaxOutput, energyStorage.getHandler().getEnergyStored());
			energyStorage.getHandler().extractEnergy(target.receiveEnergy(toTransfer, false), false);
		}
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
		return true;
	}

	@Override
	public boolean canReceive() {
		return false;
	}
}
