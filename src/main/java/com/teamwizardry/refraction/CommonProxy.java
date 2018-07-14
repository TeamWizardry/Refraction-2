package com.teamwizardry.refraction;

import com.teamwizardry.librarianlib.features.network.PacketHandler;
import com.teamwizardry.refraction.client.render.BeamRenderInfo;
import com.teamwizardry.refraction.client.render.BeamRenderer;
import com.teamwizardry.refraction.common.network.PacketAddBeam;
import com.teamwizardry.refraction.common.network.PacketBeamRenderTick;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.relauncher.Side;

/**
 * Created by Demoniaque.
 */
public class CommonProxy {

	public void preInit(FMLPreInitializationEvent event) {

		BeamRenderer.INSTANCE.getClass();

		PacketHandler.register(PacketBeamRenderTick.class, Side.CLIENT);
		PacketHandler.register(PacketAddBeam.class, Side.CLIENT);
	}

	public void init(FMLInitializationEvent event) {

	}

	public void postInit(FMLPostInitializationEvent event) {

	}
}
