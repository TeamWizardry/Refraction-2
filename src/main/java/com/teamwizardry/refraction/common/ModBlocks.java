package com.teamwizardry.refraction.common;

import com.teamwizardry.refraction.common.block.BlockMirror;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ModBlocks {

	public static BlockMirror MIRROR;

	public static void init() {
		MIRROR = new BlockMirror();
	}

	@SideOnly(Side.CLIENT)
	public static void initModels() {
		MIRROR.initModel();
	}
}
