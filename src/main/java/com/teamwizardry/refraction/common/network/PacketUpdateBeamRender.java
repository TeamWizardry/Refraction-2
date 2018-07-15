package com.teamwizardry.refraction.common.network;

import com.teamwizardry.librarianlib.core.LibrarianLib;
import com.teamwizardry.librarianlib.features.autoregister.PacketRegister;
import com.teamwizardry.librarianlib.features.network.PacketBase;
import com.teamwizardry.librarianlib.features.saving.Save;
import com.teamwizardry.refraction.client.render.BeamRenderInfo;
import com.teamwizardry.refraction.client.render.BeamRenderer;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.util.UUID;

/**
 * Created by Demoniaque.
 */
@PacketRegister(Side.CLIENT)
public class PacketUpdateBeamRender extends PacketBase {

	@Override
	public void handle(@NotNull MessageContext messageContext) {
		if (messageContext.side.isServer()) return;

		BeamRenderer.update();
	}
}
