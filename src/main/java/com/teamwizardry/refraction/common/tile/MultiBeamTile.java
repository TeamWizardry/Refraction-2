package com.teamwizardry.refraction.common.tile;

import com.teamwizardry.librarianlib.features.base.block.tile.TileModTickable;
import com.teamwizardry.refraction.api.Beam;
import com.teamwizardry.refraction.api.ITileLightSink;

import javax.annotation.Nonnull;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Consumer;

public abstract class MultiBeamTile extends TileModTickable implements ITileLightSink {

	private Set<BeamTimeObject> beamSet = new HashSet<>();

	@Override
	public boolean handleBeam(@Nonnull Beam beam) {
		if (beam.hasTag("potential_recursion")) return false;
		for (BeamTimeObject object : beamSet) {
			if (beam.equals(object.beam)) {
				object.lastTime = object.beam.world.getTotalWorldTime();
				return true;
			}
		}
		beamSet.add(new BeamTimeObject(beam));
		return true;
	}

	public abstract Consumer<Set<Beam>> getMultipleBeamConsumer();

	@Override
	public void tick() {
		if (world.isRemote) return;
		if (beamSet.isEmpty()) return;

		HashSet<Beam> beams = new HashSet<>();

		beamSet.removeIf(beamTimeObject -> {
			if (world.getTotalWorldTime() - beamTimeObject.lastTime > 2)
				return true;

			beams.add(beamTimeObject.beam);
			return false;
		});

		getMultipleBeamConsumer().accept(beams);
	}

	public static class BeamTimeObject {

		public Beam beam;
		public long lastTime;

		public BeamTimeObject(Beam beam) {
			this.beam = beam;
			this.lastTime = beam.world.getTotalWorldTime();
		}
	}
}
