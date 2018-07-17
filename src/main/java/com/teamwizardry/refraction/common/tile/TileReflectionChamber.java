package com.teamwizardry.refraction.common.tile;

import com.teamwizardry.librarianlib.features.autoregister.TileRegister;
import com.teamwizardry.refraction.api.Beam;
import com.teamwizardry.refraction.api.utils.Utils;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nonnull;
import java.awt.*;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Collectors;

/**
 * Created by Demoniaque
 */
@TileRegister("reflection_chamber")
public class TileReflectionChamber extends MultiBeamTile {

	@Nonnull
	@SideOnly(Side.CLIENT)
	@Override
	public AxisAlignedBB getRenderBoundingBox() {
		return INFINITE_EXTENT_AABB;
	}

	@Override
	public Consumer<Set<Beam>> getMultipleBeamConsumer() {
		return beams -> {

			List<Vec3d> angles = beams.stream().map(beam -> beam.slope).collect(Collectors.toList());
			Vec3d outputDir = Utils.averageDirection(angles);

			int red = 0, green = 0, blue = 0;

			for (Beam beam : beams) {
				Color color = new Color(beam.red, beam.green, beam.blue);

				double colorCount = 0;
				if (color.getRed() > 0) colorCount++;
				if (color.getGreen() > 0) colorCount++;
				if (color.getBlue() > 0) colorCount++;
				if (colorCount <= 0) continue;

				red += color.getRed() * color.getAlpha() / 255F / colorCount;
				green += color.getGreen() * color.getAlpha() / 255F / colorCount;
				blue += color.getBlue() * color.getAlpha() / 255F / colorCount;
			}

			red = Math.min(red / beams.size(), 255);
			green = Math.min(green / beams.size(), 255);
			blue = Math.min(blue / beams.size(), 255);

			Beam beam = new Beam(new Vec3d(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5), outputDir, 255, red, green, blue, Utils.createUUID(pos))
					.addTag("potential_recursion");
			beam.spawn(world);
		};
	}
}
