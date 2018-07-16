package com.teamwizardry.refraction.common.tile;

import com.teamwizardry.librarianlib.features.base.block.tile.TileMod;
import com.teamwizardry.librarianlib.features.saving.Save;
import com.teamwizardry.refraction.api.utils.Utils;

import java.util.UUID;

public class ModTile extends TileMod {

	@Save
	public UUID uuid;

	@Override
	public void onLoad() {
		uuid = Utils.createUUID(pos);
	}
}
