package com.teamwizardry.refraction.common.tile;

import com.teamwizardry.librarianlib.features.autoregister.TileRegister;
import com.teamwizardry.librarianlib.features.base.block.tile.TileModTickable;
import com.teamwizardry.librarianlib.features.base.block.tile.module.ModuleEnergy;
import com.teamwizardry.librarianlib.features.base.block.tile.module.ModuleFluid;
import com.teamwizardry.librarianlib.features.saving.Module;
import com.teamwizardry.refraction.api.ConfigValues;
import net.minecraft.init.Blocks;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;

@TileRegister("pump")
public class TilePump extends TileModTickable {

    private final EnumFacing[] pushFaces = EnumFacing.HORIZONTALS;
    private final EnumFacing drainFace = EnumFacing.DOWN;
    private final int rfConsume = ConfigValues.pumpConsume;

    @Module
    public ModuleFluid tank = new ModuleFluid(4000);

    @Module
    public ModuleEnergy energy = new ModuleEnergy(8000);

    @Override
    public void tick() {
        if (world.isRemote) return;

        if (tank.getHandler().getFluidAmount() >= 0) {
            int spill = outputFluid(tank.getHandler().getFluid(), false, pushFaces);
            tank.getHandler().drain(spill, true);
        }

        if (world.isBlockIndirectlyGettingPowered(pos) == 0 && !world.isBlockPowered(pos))
            return;

        BlockPos outSide = pos.offset(drainFace);
        TileEntity tile = world.getTileEntity(outSide);
        if (tile != null && tile.hasCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, drainFace.getOpposite())) {
            IFluidHandler handler = tile.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, drainFace.getOpposite());

            FluidStack fluid = handler.drain(500, false);
            if (fluid == null || fluid.amount <= 0) return;

            int out = outputFluid(fluid, false, new EnumFacing[]{drainFace.getOpposite()});
            handler.drain(out, true);
        } else if (world.getTotalWorldTime() % 20 == 0 //no spam, just pump
                && world.getBlockState(getPos().offset(drainFace)).getBlock() == Blocks.WATER
                && tank.getHandler().fill(new FluidStack(FluidRegistry.WATER, 1000), false) == 1000
                && energy.getHandler().extractEnergy(rfConsume, true) >= rfConsume) {
            this.energy.getHandler().extractEnergy(rfConsume, false);
            this.tank.getHandler().fill(new FluidStack(FluidRegistry.WATER, 1000), true);
        }
    }

    public int outputFluid(FluidStack fs, boolean simulate, EnumFacing[] sides) {
        if (fs == null) return 0;

        int canAccept = fs.amount;
        if (canAccept <= 0) return 0;

        int fluidAmount = 0;
        for (EnumFacing face : sides) {
            TileEntity tile = world.getTileEntity(pos.offset(face));
            if (tile != null && tile.hasCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, face.getOpposite())) {
                IFluidHandler handler = tile.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, face.getOpposite());

                FluidStack insertResource = new FluidStack(fs, fs.amount);
                if (handler.fill(insertResource, false) > 0) {
                    fluidAmount += handler.fill(insertResource, !simulate);
                }
            }
        }
        return fluidAmount;
    }
}
