package com.teamwizardry.refraction.common;

import com.teamwizardry.refraction.common.block.BlockMirror;
import com.teamwizardry.refraction.common.block.BlockSplitter;
import com.teamwizardry.refraction.common.block.BlockTestLaser;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ModBlocks {

	public static BlockMirror MIRROR;
	public static BlockSplitter SPLITTER;


	public static BlockTestLaser TESTLASER;

	public static void init() {
		MIRROR = new BlockMirror();
		SPLITTER = new BlockSplitter();

		TESTLASER = new BlockTestLaser();
	}

	@SideOnly(Side.CLIENT)
	public static void initModels() {
		MIRROR.initModel();
		SPLITTER.initModel();
	}
}
