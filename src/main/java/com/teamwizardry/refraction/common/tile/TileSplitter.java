package com.teamwizardry.refraction.common.tile;

import com.teamwizardry.librarianlib.features.autoregister.TileRegister;
import com.teamwizardry.refraction.api.Beam;
import net.minecraft.util.math.Vec3d;

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
}
