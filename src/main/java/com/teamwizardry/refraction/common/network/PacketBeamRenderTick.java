package com.teamwizardry.refraction.common.network;

import com.teamwizardry.librarianlib.features.network.PacketBase;
import com.teamwizardry.refraction.client.render.BeamRenderer;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import org.jetbrains.annotations.NotNull;

/**
 * Created by LordSaad.
 */
public class PacketBeamRenderTick extends PacketBase {

	@Override
	public void handle(@NotNull MessageContext messageContext) {
		BeamRenderer.INSTANCE.update();
	}
}
