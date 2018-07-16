package com.teamwizardry.refraction.common;

import com.teamwizardry.refraction.common.block.*;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ModBlocks {

	public static BlockMirror MIRROR;
	public static BlockLaser LASER;
	public static BlockSplitter SPLITTER;
	public static BlockSolarPanel SOLARPANEL;
	public static BlockReflectionChamber REFLECTCHAMBER;
	public static BlockPrism PRISM;

	public static void init() {
		MIRROR = new BlockMirror();
		LASER = new BlockLaser();
		SPLITTER = new BlockSplitter();
		SOLARPANEL = new BlockSolarPanel();
		REFLECTCHAMBER = new BlockReflectionChamber();
		PRISM = new BlockPrism();
	}

	@SideOnly(Side.CLIENT)
	public static void initModels() {
		MIRROR.initModel();
		SPLITTER.initModel();
		REFLECTCHAMBER.initModel();
	}
}
