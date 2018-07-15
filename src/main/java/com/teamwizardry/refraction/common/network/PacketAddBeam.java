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
public class PacketAddBeam extends PacketBase {

	@Save
	private Vec3d origin;
	@Save
	private Vec3d target;
	@Save
	private Color color;
	@Save
	private UUID uuid;

	public PacketAddBeam() {
	}

	public PacketAddBeam(Vec3d origin, Vec3d target, Color color, UUID uuid) {
		this.origin = origin;
		this.target = target;
		this.color = color;
		this.uuid = uuid;
	}

	@Override
	public void handle(@NotNull MessageContext messageContext) {
		if (messageContext.side.isServer()) return;

		World world = LibrarianLib.PROXY.getClientPlayer().world;
		if (world == null) return;

		BeamRenderer.addBeam(world, origin, target, color, uuid);
	}
}
