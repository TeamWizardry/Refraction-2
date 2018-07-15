package com.teamwizardry.refraction.common.tile;

import com.teamwizardry.librarianlib.features.autoregister.TileRegister;
import com.teamwizardry.librarianlib.features.base.block.tile.TileModTickable;
import com.teamwizardry.librarianlib.features.saving.Save;
import com.teamwizardry.refraction.Refraction;
import com.teamwizardry.refraction.api.Beam;
import net.minecraft.util.math.Vec3d;

import java.awt.*;
import java.util.UUID;

@TileRegister(Refraction.MOD_ID)
public class TileMirror extends TileModTickable {

	@Save
	private UUID uuid;

	@Override
	public void onLoad() {
		uuid = UUID.nameUUIDFromBytes((pos.toLong() + "").getBytes());
	}

	@Override
	public void tick() {

		new Beam(world, new Vec3d(getPos()).addVector(0.5, 0.5, 0.5), new Vec3d(1, 0, 0), 25, Color.RED)
		.setUUID(uuid)
		.spawn();
	}
}
