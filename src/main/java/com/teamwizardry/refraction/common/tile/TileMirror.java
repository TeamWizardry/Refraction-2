package com.teamwizardry.refraction.common.tile;

import com.teamwizardry.librarianlib.features.autoregister.TileRegister;
import com.teamwizardry.refraction.Refraction;
import com.teamwizardry.refraction.api.Beam;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nonnull;

@TileRegister("mirror")
public class TileMirror extends TileMirrorBase {

	@Override
	protected void handleBeam(Beam beam, Vec3d incomingDir, Vec3d normal) {
		if (incomingDir.dotProduct(normal) > 0) return; // hit the back of the mirror, shouldn't reflect

		Vec3d outgoingDir = incomingDir.subtract(normal.scale(incomingDir.dotProduct(normal) * 2));

		//TODO setPotency((int) (beam.getAlpha() / 1.05))).
		beam.createSimilarBeam(beam.endLoc, outgoingDir, getUUID(beam, 0)).spawn();
	}

	@SideOnly(Side.CLIENT)
	@Nonnull
	@Override
	public ResourceLocation getMirrorHeadLocation() {
		return new ResourceLocation(Refraction.MOD_ID, "blocks/mirror_normal");
	}
}
