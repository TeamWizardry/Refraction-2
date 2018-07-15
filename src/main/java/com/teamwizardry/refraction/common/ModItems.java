package com.teamwizardry.refraction.common;

import com.teamwizardry.refraction.common.item.ItemScrewDriver;

public class ModItems {

	public static ItemScrewDriver SCREWDRIVER;

	public static void init() {
		SCREWDRIVER = new ItemScrewDriver();
	}
}
