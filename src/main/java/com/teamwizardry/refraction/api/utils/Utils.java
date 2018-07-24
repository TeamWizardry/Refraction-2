package com.teamwizardry.refraction.api.utils;

import com.teamwizardry.librarianlib.features.base.block.tile.module.ModuleInventory;
import com.teamwizardry.refraction.api.Beam;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.items.IItemHandler;

import java.util.List;
import java.util.UUID;

public class Utils {

	public static UUID createUUID(BlockPos pos) {
		return createUUID(pos.toLong() + "");
	}

	public static UUID createUUID(String hash) {
		return UUID.nameUUIDFromBytes(hash.getBytes());
	}

	public static UUID createUUID(BlockPos pos, Beam beam, int index) {
		return Utils.createUUID(pos.toLong() + "-" + beam.uuid.hashCode() + "-" + index);
	}

	public static UUID createUUID(BlockPos pos, Beam beam) {
		return Utils.createUUID(pos.toLong() + "-" + beam.uuid.hashCode());
	}

	public static UUID createUUID(BlockPos pos, int index) {
		return Utils.createUUID(pos.toLong() + "-" + index);
	}

	/**
	 * Takes several cartesian vectors, and returns one going in the average
	 * direction, regardless of vector magnitues
	 *
	 * @param vectors The list of vectors to average. Will be normalized.
	 * @return The average direction of all the given vectors.
	 */
	public static Vec3d averageDirection(List<Vec3d> vectors) {
		if (vectors == null) return Vec3d.ZERO;
		if (vectors.isEmpty()) return Vec3d.ZERO;
		double x = 0;
		double y = 0;
		double z = 0;

		for (Vec3d vec : vectors) {
			if (vec == null) continue;
			vec.normalize();
			x += vec.x;
			y += vec.y;
			z += vec.z;
		}

		return new Vec3d(x, y, z);
	}

	public static float signAngle(Vec3d a, Vec3d b, Vec3d n) {
		Vec3d cross = a.crossProduct(b);
		double s = cross.lengthVector();
		double c = a.dotProduct(b);
		double angle = MathHelper.atan2(s, c);

		if (n != null) {
			if (n.dotProduct(cross) < 0) {
				angle = -angle;
			}
		}

		return (float) Math.toDegrees(angle);
	}

	public static boolean simpleAreStacksEqual(ItemStack stack, ItemStack stack2) {
		return stack.getItem() == stack2.getItem() && stack.getItemDamage() == stack2.getItemDamage();
	}

	public static int getOccupiedSlotCount(ModuleInventory inventory) {
		return getOccupiedSlotCount(inventory.getHandler());
	}
	public static int getOccupiedSlotCount(IItemHandler inventory) {
		int x = 0;
		for (int i = 0; i < inventory.getSlots(); i++) if (inventory.getStackInSlot(i) != ItemStack.EMPTY) x++;
		return x;
	}

	public static int getLastOccupiedSlot(ModuleInventory inventory) {
		return getLastOccupiedSlot(inventory.getHandler());
	}
	public static int getLastOccupiedSlot(IItemHandler inventory) {
		for (int i = inventory.getSlots() - 1; i > 0; i--) if (inventory.getStackInSlot(i) != ItemStack.EMPTY) return i;
		return 0;
	}
}
