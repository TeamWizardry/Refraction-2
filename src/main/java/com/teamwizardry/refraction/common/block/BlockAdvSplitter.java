package com.teamwizardry.refraction.common.block;

import com.teamwizardry.refraction.api.utils.Utils;
import com.teamwizardry.refraction.common.ModItems;
import com.teamwizardry.refraction.common.tile.TileAdvSplitter;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.items.ItemHandlerHelper;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.annotation.Nonnull;

public class BlockAdvSplitter extends BlockMirrorBase {

    public BlockAdvSplitter() {
        super("advanced_splitter", Material.IRON);
        setHardness(1F);
        setSoundType(SoundType.METAL);
    }

    @Nullable
    @Override
    public TileEntity createTileEntity(@NotNull World world, @NotNull IBlockState state) {
        return new TileAdvSplitter();
    }


    @Override
    public boolean onBlockActivated(@Nonnull World worldIn, @Nonnull BlockPos pos, @Nonnull IBlockState state, @Nonnull EntityPlayer playerIn, @Nonnull EnumHand hand, @Nonnull EnumFacing facing, float hitX, float hitY, float hitZ) {
        if (!worldIn.isRemote) {
            ItemStack heldItem = playerIn.getHeldItem(hand);
            TileAdvSplitter splitter = (TileAdvSplitter)getTE(worldIn, pos);
            if (splitter.isValidPane(heldItem) && !splitter.hasPane()) {
                ItemStack stack = heldItem.copy();
                stack.setCount(1);
                ItemStack insert = ItemHandlerHelper.insertItem(splitter.inventory.getHandler(), stack, false);
                if (insert.isEmpty())
                    heldItem.setCount(heldItem.getCount() - 1);
                playerIn.openContainer.detectAndSendChanges();
            } else if (playerIn.isSneaking() && Utils.getOccupiedSlotCount(splitter.inventory) > 0) {
                ItemHandlerHelper.giveItemToPlayer(playerIn, splitter.inventory.getHandler().extractItem(Utils.getLastOccupiedSlot(splitter.inventory), 1, false));
                playerIn.openContainer.detectAndSendChanges();
            } else if (heldItem.getItem() == ModItems.SCREWDRIVER) {
                adjust(worldIn, pos, heldItem, false, facing);
            }
            splitter.markDirty();
        }
        return true;
    }

}
