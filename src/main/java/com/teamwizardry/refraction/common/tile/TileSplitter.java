package com.teamwizardry.refraction.common.tile;

import com.teamwizardry.librarianlib.features.autoregister.TileRegister;
import com.teamwizardry.refraction.Refraction;
import com.teamwizardry.refraction.api.Beam;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

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
		beam.createSimilarBeam(beam.endLoc, outgoingDir).setUUID(uuid).spawn();
		beam.createSimilarBeam(beam.endLoc, incomingDir).setUUID(uuid).spawn();
	}

	@SideOnly(Side.CLIENT)
	@Nonnull
	@Override
	public ResourceLocation getMirrorHeadLocation() {
		return new ResourceLocation(Refraction.MOD_ID, "blocks/mirror_splitter");
	}
}
