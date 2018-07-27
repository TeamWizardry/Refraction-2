package com.teamwizardry.refraction.common;

import com.teamwizardry.refraction.common.item.ItemLaserPen;
import com.teamwizardry.refraction.common.item.ItemScrewDriver;

public class ModItems {

	public static ItemScrewDriver SCREWDRIVER;
	public static ItemLaserPen LASERPEN;

	public static void init() {
		SCREWDRIVER = new ItemScrewDriver();
		LASERPEN = new ItemLaserPen();
	}
}
