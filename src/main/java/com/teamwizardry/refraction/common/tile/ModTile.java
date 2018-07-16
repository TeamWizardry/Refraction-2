package com.teamwizardry.refraction.common.tile;

import com.teamwizardry.librarianlib.features.base.block.tile.TileMod;
import com.teamwizardry.librarianlib.features.saving.Save;
import com.teamwizardry.refraction.api.utils.Utils;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ModTile extends TileMod {

	@Save
	private List<UUID> uuidSet;

	public UUID getUUID(int index) {
		if (uuidSet == null) uuidSet = new ArrayList<>();

		if (uuidSet.size() - 1 < index) {
			UUID uuid = Utils.createUUID(pos.toLong() + "-" + index);
			uuidSet.add(uuid);
			return uuid;
		}
		return uuidSet.get(index);
	}
}
