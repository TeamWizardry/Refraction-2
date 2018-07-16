package com.teamwizardry.refraction.common.block;

import com.teamwizardry.librarianlib.features.base.block.tile.BlockModContainer;
import com.teamwizardry.librarianlib.features.math.Matrix4;
import com.teamwizardry.librarianlib.features.utilities.client.TooltipHelper;
import com.teamwizardry.refraction.Refraction;
import com.teamwizardry.refraction.api.Beam;
import com.teamwizardry.refraction.api.IBlockCollisionRayTrace;
import com.teamwizardry.refraction.api.ILightSink;
import com.teamwizardry.refraction.api.IPrecision;
import com.teamwizardry.refraction.common.item.ItemScrewDriver;
import com.teamwizardry.refraction.common.tile.TileMirrorBase;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.Objects;
import java.util.Random;

public abstract class BlockMirrorBase extends BlockModContainer implements IBlockCollisionRayTrace, IPrecision, ILightSink {

	public BlockMirrorBase(String name, Material material) {
		super(name, material);
	}

	@Nullable
	private TileMirrorBase getTE(World world, BlockPos pos) {
		TileEntity tile = world.getTileEntity(pos);
		if (!(tile instanceof TileMirrorBase)) return null;
		return (TileMirrorBase) tile;
	}

	@Override
	public boolean handleBeam(@Nonnull World world, @Nonnull BlockPos pos, @Nonnull Beam beam) {
		TileMirrorBase te = getTE(world, pos);
		if (te == null) return false;

		te.handleBeam(beam);
		return true;
	}

	@Override
	public void onNeighborChange(IBlockAccess worldIn, BlockPos pos, BlockPos neighbor) {
		TileMirrorBase tile = getTE((World) worldIn, pos);
		if (tile == null) return;

		if (tile.isPowered()) {
			if (!((World) worldIn).isBlockPowered(pos) || ((World) worldIn).isBlockIndirectlyGettingPowered(pos) == 0) {
				tile.setPowered(false);
			}
		} else {
			if (((World) worldIn).isBlockPowered(pos) || ((World) worldIn).isBlockIndirectlyGettingPowered(pos) > 0)
				tile.setPowered(true);
		}
	}

	@Override
	public void onBlockPlacedBy(World worldIn, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack) {
		EnumFacing facing = EnumFacing.getFacingFromVector((float) placer.getLook(0).x, (float) placer.getLook(0).y, (float) placer.getLook(0).z);
		TileMirrorBase tile = getTE(worldIn, pos);
		if (tile == null) return;

		float x = 0, y = 0;

		switch (facing) {
			case WEST: {
				x = -90;
				y = 270;
				break;
			}
			case EAST: {
				x = 270;
				y = 90;
				break;
			}
			case NORTH: {
				x = 90;
				y = 0;
				break;
			}
			case SOUTH: {
				x = 90;
				y = 180;
				break;
			}
		}

		tile.rotDestX = x;
		tile.rotDestY = y;
		tile.rotPrevX = x;
		tile.rotPrevY = y;
		tile.rotXPowered = x;
		tile.rotYPowered = y;
		tile.rotXUnpowered = x;
		tile.rotYUnpowered = y;
	}

	@Override
	public void updateTick(World worldIn, BlockPos pos, IBlockState state, Random rand) {
		TileMirrorBase tile = getTE(worldIn, pos);
		if (tile == null) return;
		if (tile.isPowered() && !worldIn.isBlockPowered(pos)) {
			tile.setPowered(false);
		}
	}

	@Override
	public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
		TooltipHelper.addToTooltip(tooltip, "simple_name." + Refraction.MOD_ID + ":" + getRegistryName().getResourcePath());
	}

	@Override
	public float getRotX(World worldIn, BlockPos pos) {
		TileMirrorBase te = getTE(worldIn, pos);
		return te == null ? 0 : te.getRotX();
	}

	@Override
	public void setRotX(World worldIn, BlockPos pos, float x) {
		TileMirrorBase te = getTE(worldIn, pos);
		if (te == null) return;
		te.setRotX(x);
	}

	@Override
	public float getRotY(World worldIn, BlockPos pos) {
		TileMirrorBase te = getTE(worldIn, pos);
		return te == null ? 0 : te.getRotY();
	}

	@Override
	public void setRotY(World worldIn, BlockPos pos, float y) {
		TileMirrorBase te = getTE(worldIn, pos);
		if (te == null) return;
		te.setRotY(y);
	}

	@Override
	@SuppressWarnings("deprecation")
	public boolean isFullCube(IBlockState state) {
		return false;
	}

	@Override
	@SuppressWarnings("deprecation")
	public boolean isOpaqueCube(IBlockState blockState) {
		return false;
	}

	@Override
	public RayTraceResult rayCollision(@Nonnull IBlockState blockState, @Nonnull World worldIn, @Nonnull BlockPos pos, @Nonnull Vec3d startRaw, @Nonnull Vec3d endRaw) {
		double pixels = 1.0 / 16.0;

		AxisAlignedBB aabb = new AxisAlignedBB(pixels, 0, pixels, 1 - pixels, pixels, 1 - pixels).offset(-0.5, -pixels / 2, -0.5);

		RayTraceResult superResult = blockState.collisionRayTrace(worldIn, pos, startRaw, endRaw);

		TileMirrorBase tile = getTE(worldIn, pos);
		if (tile == null) return null;
		Vec3d start = startRaw.subtract((double) pos.getX(), (double) pos.getY(), (double) pos.getZ());
		Vec3d end = endRaw.subtract((double) pos.getX(), (double) pos.getY(), (double) pos.getZ());

		start = start.subtract(0.5, 0.5, 0.5);
		end = end.subtract(0.5, 0.5, 0.5);

		Matrix4 matrix = new Matrix4();
		matrix.rotate(-Math.toRadians(tile.getRotX()), new Vec3d(1, 0, 0));
		matrix.rotate(-Math.toRadians(tile.getRotY()), new Vec3d(0, 1, 0));

		Matrix4 inverse = new Matrix4();
		inverse.rotate(Math.toRadians(tile.getRotY()), new Vec3d(0, 1, 0));
		inverse.rotate(Math.toRadians(tile.getRotX()), new Vec3d(1, 0, 0));

		start = matrix.apply(start);
		end = matrix.apply(end);
		RayTraceResult result = aabb.calculateIntercept(start, end);
		if (result == null) return null;
		Vec3d a = result.hitVec;

		a = inverse.apply(a);
		a = a.addVector(0.5, 0.5, 0.5);

		return new RayTraceResult(a.add(new Vec3d(pos)), superResult.sideHit, pos);
	}

	@NotNull
	@SideOnly(Side.CLIENT)
	public BlockRenderLayer getBlockLayer() {
		return BlockRenderLayer.CUTOUT;
	}

	@Override
	public boolean isToolEffective(String type, @NotNull IBlockState state) {
		return super.isToolEffective(type, state) || Objects.equals(type, ItemScrewDriver.SCREWDRIVER_TOOL_CLASS);
	}
}
