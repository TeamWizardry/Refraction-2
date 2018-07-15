package com.teamwizardry.refraction.common.block;

import com.teamwizardry.refraction.client.render.RenderMirror;
import com.teamwizardry.refraction.common.tile.TileSplitter;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;

/**
 * Created by Demoniaque
 */
public class BlockSplitter extends BlockMirrorBase {

	public BlockSplitter() {
		super("splitter", Material.IRON);
		setHardness(1F);
		setSoundType(SoundType.METAL);
	}

	@SideOnly(Side.CLIENT)
	public void initModel() {//"blocks/mirror_splitter"
		ClientRegistry.bindTileEntitySpecialRenderer(TileSplitter.class, new RenderMirror());
	}

	@Nullable
	@Override
	public TileEntity createTileEntity(World world, IBlockState iBlockState) {
		return new TileSplitter();
	}
}
