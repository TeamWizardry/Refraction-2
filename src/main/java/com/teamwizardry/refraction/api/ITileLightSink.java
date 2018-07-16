package com.teamwizardry.refraction.api;

import javax.annotation.Nonnull;

public interface ITileLightSink {

	/**
	 * Handle a beam. The default implementation is provided for backwards compatibility.
	 *
	 * @param beam The beam being handled
	 * @return Whether the beam should be stopped
	 */
	boolean handleBeam(@Nonnull Beam beam);

}
