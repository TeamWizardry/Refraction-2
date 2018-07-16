package com.teamwizardry.refraction.common.tile;

import com.teamwizardry.librarianlib.features.base.block.tile.TileModTickable;
import com.teamwizardry.refraction.api.Beam;
import com.teamwizardry.refraction.api.ITileLightSink;
import com.teamwizardry.refraction.api.utils.Utils;

import javax.annotation.Nonnull;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Consumer;

public abstract class MultiBeamTile extends TileModTickable implements ITileLightSink {

	private Set<Beam> beamSet = new HashSet<>();

	@Override
	public boolean handleBeam(@Nonnull Beam beam) {
		if (beam.uuid.equals(Utils.createUUID(pos))) return false;
		beamSet.add(beam);
		return true;
	}

	public abstract Consumer<Set<Beam>> getMultipleBeamConsumer();

	@Override
	public void tick() {
		if (beamSet.isEmpty()) return;

		getMultipleBeamConsumer().accept(beamSet);

		if (world.getTotalWorldTime() % 5 == 0)
			beamSet.clear();
	}
}
