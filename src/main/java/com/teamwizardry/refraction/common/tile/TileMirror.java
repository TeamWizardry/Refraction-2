package com.teamwizardry.refraction.common.tile;

import com.teamwizardry.librarianlib.features.autoregister.TileRegister;
import com.teamwizardry.refraction.api.Beam;
import net.minecraft.util.math.Vec3d;

/**
 * Created by Demoniaque
 */
@TileRegister("mirror")
public class TileMirror extends TileMirrorBase {

	@Override
	protected void handleBeam(Beam beam, Vec3d incomingDir, Vec3d normal) {
		if (incomingDir.dotProduct(normal) > 0)
			return; // hit the back of the mirror, shouldn't reflect

		Vec3d outgoingDir = incomingDir.subtract(normal.scale(incomingDir.dotProduct(normal) * 2));

		//TODO setPotency((int) (beam.getAlpha() / 1.05))).
		beam.createSimilarBeam(outgoingDir).spawn();;
	}
}
