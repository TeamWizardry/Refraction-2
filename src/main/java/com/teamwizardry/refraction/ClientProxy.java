package com.teamwizardry.refraction;

import net.minecraft.client.resources.IResourceManager;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.Event;

/**
 * Created by Demoniaque.
 */
public class ClientProxy extends CommonProxy {

	public void preInit(FMLPreInitializationEvent event) {
		super.preInit(event);
	}

	public void init(FMLInitializationEvent event) {
		super.init(event);
	}

	public void postInit(FMLPostInitializationEvent event) {
		super.postInit(event);
	}


	public static class ResourceReloadEvent extends Event {
		public final IResourceManager resourceManager;

		public ResourceReloadEvent(IResourceManager manager) {
			resourceManager = manager;
		}
	}
}
