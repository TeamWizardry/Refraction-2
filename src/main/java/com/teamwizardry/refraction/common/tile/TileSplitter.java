package com.teamwizardry.refraction.common.tile;

import com.teamwizardry.librarianlib.features.autoregister.TileRegister;
import com.teamwizardry.librarianlib.features.tesr.TileRenderer;
import com.teamwizardry.refraction.Refraction;
import com.teamwizardry.refraction.api.Beam;
import com.teamwizardry.refraction.client.render.RenderMirror;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.Vec3d;

import javax.annotation.Nonnull;

/**
 * Created by Demoniaque
 */
@TileRegister("splitter")
public class TileSplitter extends TileMirrorBase {

	@Override
	protected void handleBeam(Beam beam, Vec3d incomingDir, Vec3d normal) {
		Vec3d outgoingDir = incomingDir.subtract(normal.scale(incomingDir.dotProduct(normal) * 2));

		//TODO .setPotency(beam.getColor().getAlpha() / 2)
		beam.createSimilarBeam(outgoingDir).spawn();
		beam.createSimilarBeam(incomingDir).spawn();
	}

	@Nonnull
	@Override
	public ResourceLocation getMirrorHeadLocation() {
		return new ResourceLocation(Refraction.MOD_ID, "blocks/mirror_splitter");
	}
}
