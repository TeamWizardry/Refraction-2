package com.teamwizardry.refraction.api;

import com.teamwizardry.librarianlib.features.config.ConfigDoubleRange;
import com.teamwizardry.librarianlib.features.config.ConfigIntRange;
import com.teamwizardry.librarianlib.features.config.ConfigProperty;

public class ConfigValues {

	@ConfigIntRange(min = 0, max = 100000)
	@ConfigProperty(category = "energy", comment = "Maximum FE the solar panel can store")
	public static int solarPanelCapacity = 5000;

	@ConfigIntRange(min = 0, max = 100)
	@ConfigProperty(category = "energy", comment = "Maximum FE output per tick")
	public static int solarPanelMaxOutput = 20;

	@ConfigIntRange(min = 0, max = 100)
	@ConfigProperty(category = "energy", comment = "Amount of FE produced each tick during daytime (further multiplied by sun angle)")
	public static int solarPanelGeneration = 10;

	@ConfigDoubleRange(min = 0, max = 1)
	@ConfigProperty(category = "energy", comment = "Multiplier during rain")
	public static double solarPanelRainMultiplier = 0.6;

	@ConfigDoubleRange(min = 0, max = 1)
	@ConfigProperty(category = "energy", comment = "Multiplier during thunderstorm")
	public static double solarPanelThunderMultiplier = 0.4;


	@ConfigIntRange(min = 0, max = 100000)
	@ConfigProperty(category = "energy", comment = "Maximum FE the laser can store")
	public static int laserCapacity = 5000;

	@ConfigIntRange(min = 0, max = 100)
	@ConfigProperty(category = "energy", comment = "Maximum FE output per tick")
	public static int laserMaxInput = 20;

	@ConfigIntRange(min = 0, max = 100)
	@ConfigProperty(category = "energy", comment = "Amount of FE per tick the laser consumes")
	public static int laserConsume= 10;

	@ConfigIntRange(min = 0, max = 100)
	@ConfigProperty(category = "energy", comment = "Amount of FE per tick the pump consumes")
	public static int pumpConsume= 10;
}
