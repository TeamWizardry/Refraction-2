package com.teamwizardry.refraction.common.network;

import com.teamwizardry.librarianlib.features.network.PacketBase;
import com.teamwizardry.librarianlib.features.saving.Save;
import com.teamwizardry.refraction.client.render.BeamRenderInfo;
import com.teamwizardry.refraction.client.render.BeamRenderer;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import org.jetbrains.annotations.NotNull;

import java.awt.*;

/**
 * Created by LordSaad.
 */
public class PacketAddBeam extends PacketBase {

	@Save
	private Vec3d origin;
	@Save
	private Vec3d target;
	@Save
	private Color color;

	public PacketAddBeam() {
	}

	public PacketAddBeam(Vec3d origin, Vec3d target, Color color) {
		this.origin = origin;
		this.target = target;
		this.color = color;
	}

	@Override
	public void handle(@NotNull MessageContext messageContext) {
		BeamRenderer.INSTANCE.addBeam(new BeamRenderInfo(origin, target, color));
	}
}
