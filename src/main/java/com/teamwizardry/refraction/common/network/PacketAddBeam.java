package com.teamwizardry.refraction.common.network;

import com.teamwizardry.librarianlib.core.LibrarianLib;
import com.teamwizardry.librarianlib.features.autoregister.PacketRegister;
import com.teamwizardry.librarianlib.features.network.PacketBase;
import com.teamwizardry.librarianlib.features.saving.Save;
import com.teamwizardry.refraction.api.Beam;
import com.teamwizardry.refraction.client.render.BeamRenderer;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import org.jetbrains.annotations.NotNull;

/**
 * Created by Demoniaque.
 */
@PacketRegister(Side.CLIENT)
public class PacketAddBeam extends PacketBase {

	@Save
	private Beam beam;

	public PacketAddBeam() {
	}

	public PacketAddBeam(Beam beam) {
		this.beam = beam;
	}

	@Override
	public void handle(@NotNull MessageContext messageContext) {
		if (messageContext.side.isServer()) return;

		World world = LibrarianLib.PROXY.getClientPlayer().world;
		if (world == null) return;

		if (beam == null) return;
		BeamRenderer.addBeam(world, beam);
	}
}
