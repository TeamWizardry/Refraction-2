package com.teamwizardry.refraction.common.tile;

import com.teamwizardry.librarianlib.features.base.block.tile.TileModTickable;
import com.teamwizardry.librarianlib.features.math.Matrix4;
import com.teamwizardry.librarianlib.features.saving.Save;
import com.teamwizardry.refraction.api.Beam;
import com.teamwizardry.refraction.api.ITileLightSink;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;

public abstract class TileMirrorBase extends TileModTickable implements ITileLightSink {

	@Save
	public float rotXUnpowered, rotYUnpowered, rotXPowered = Float.NaN, rotYPowered = Float.NaN;
	@Save
	public float rotDestX, rotPrevX, rotDestY, rotPrevY;
	@Save
	public boolean transitionX = false, transitionY = false, powered = false;
	@Save
	public long worldTime = 0;

	@Nonnull
	@SideOnly(Side.CLIENT)
	@Override
	public AxisAlignedBB getRenderBoundingBox() {
		return INFINITE_EXTENT_AABB;
	}

	public float getRotX() {
		return powered ? rotXPowered : rotXUnpowered;
	}

	public void setRotX(float rotX) {
		if (transitionX) return;
		if (rotX == rotDestX) return;
		rotPrevX = rotDestX;
		rotDestX = rotX;
		transitionX = true;
		worldTime = world.getTotalWorldTime();
		markDirty();
	}

	public float getRotY() {
		return powered ? rotYPowered : rotYUnpowered;
	}

	public void setRotY(float rotY) {
		if (transitionY) return;
		if (rotY == rotDestY) return;
		rotPrevY = rotDestY;
		rotDestY = rotY;
		transitionY = true;
		worldTime = world.getTotalWorldTime();
		markDirty();
	}

	@Override
	public boolean handleBeam(@NotNull Beam beam) {
		float rotX, rotY;
		if (powered) {
			rotX = rotXPowered;
			rotY = rotYPowered;
		} else {
			rotX = rotXUnpowered;
			rotY = rotYUnpowered;
		}

		if (beam.endLoc == null) return false;

		Matrix4 matrix = new Matrix4();
		matrix.rotate(Math.toRadians(rotY), new Vec3d(0, 1, 0));
		matrix.rotate(Math.toRadians(rotX), new Vec3d(1, 0, 0));

		Vec3d normal = matrix.apply(new Vec3d(0, 1, 0));
		Vec3d incomingDir = beam.slope;

		return handleMirrorBeam(beam, incomingDir, normal);
	}

	protected abstract boolean handleMirrorBeam(Beam beam, Vec3d incomingDir, Vec3d normal);

	@Nonnull
	@SideOnly(Side.CLIENT)
	public abstract ResourceLocation getMirrorHeadLocation();

	@Override
	public void tick() {
		double transitionTimeMaxX = Math.max(3, Math.min(Math.abs((rotPrevX - rotDestX) / 2.0), 10)),
				transitionTimeMaxY = Math.max(3, Math.min(Math.abs((rotPrevY - rotDestY) / 2.0), 10));
		double worldTimeTransition = (world.getTotalWorldTime() - worldTime);

		float rotX, rotY;
		if (transitionX) {
			if (worldTimeTransition < transitionTimeMaxX) {
				if (Math.round(rotDestX) > Math.round(rotPrevX))
					rotX = -((rotDestX - rotPrevX) / 2) * MathHelper.cos((float) (worldTimeTransition * Math.PI / transitionTimeMaxX)) + (rotDestX + rotPrevX) / 2;
				else
					rotX = ((rotPrevX - rotDestX) / 2) * MathHelper.cos((float) (worldTimeTransition * Math.PI / transitionTimeMaxX)) + (rotDestX + rotPrevX) / 2;
				if (powered) rotXPowered = rotX;
				else rotXUnpowered = rotX;
			} else {
				rotX = rotDestX;
				if (powered) rotXPowered = rotX;
				else rotXUnpowered = rotX;
				transitionX = false;
			}
			markDirty();
		}
		if (transitionY) {
			if (worldTimeTransition < transitionTimeMaxY) {
				if (Math.round(rotDestY) > Math.round(rotPrevY))
					rotY = -((rotDestY - rotPrevY) / 2) * MathHelper.cos((float) (worldTimeTransition * Math.PI / transitionTimeMaxY)) + (rotDestY + rotPrevY) / 2;
				else
					rotY = ((rotPrevY - rotDestY) / 2) * MathHelper.cos((float) (worldTimeTransition * Math.PI / transitionTimeMaxY)) + (rotDestY + rotPrevY) / 2;
				if (powered) rotYPowered = rotY;
				else rotYUnpowered = rotY;
			} else {
				rotY = rotDestY;
				if (powered) rotYPowered = rotY;
				else rotYUnpowered = rotY;
				transitionY = false;
			}
			markDirty();
		}
	}

	public boolean isPowered() {
		return powered;
	}

	public void setPowered(boolean powered) {
		if (!transitionX && !transitionY) {
			this.powered = powered;
			if (powered) {
				if (!Float.isNaN(rotXPowered) && rotDestX != rotXPowered) setRotX(rotXPowered);
				if (!Float.isNaN(rotYPowered) && rotDestY != rotYPowered) setRotY(rotYPowered);
			} else {
				if (!Float.isNaN(rotXUnpowered) && rotDestX != rotXUnpowered) setRotX(rotXUnpowered);
				if (!Float.isNaN(rotYUnpowered) && rotDestY != rotYUnpowered) setRotY(rotYUnpowered);
			}
		}
	}
}
