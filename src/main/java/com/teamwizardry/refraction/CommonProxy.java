package com.teamwizardry.refraction;

import com.teamwizardry.librarianlib.features.network.PacketHandler;
import com.teamwizardry.refraction.client.render.BeamRenderInfo;
import com.teamwizardry.refraction.client.render.BeamRenderer;
import com.teamwizardry.refraction.common.ModBlocks;
import com.teamwizardry.refraction.common.ModItems;
import com.teamwizardry.refraction.common.network.PacketAddBeam;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.relauncher.Side;

/**
 * Created by Demoniaque.
 */
public class CommonProxy {

	public void preInit(FMLPreInitializationEvent event) {

		ModBlocks.init();
		ModItems.init();
	}

	public void init(FMLInitializationEvent event) {

	}

	public void postInit(FMLPostInitializationEvent event) {

	}
}
