package com.teamwizardry.refraction.api.utils;

import net.minecraft.entity.Entity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;

public class RayTraceResultData<T> extends RayTraceResult {

	public T data;

	public RayTraceResultData(Vec3d hitVecIn, EnumFacing sideHitIn, BlockPos blockPosIn) {
		this(RayTraceResult.Type.BLOCK, hitVecIn, sideHitIn, blockPosIn);
	}

	public RayTraceResultData(Vec3d hitVecIn, EnumFacing sideHitIn) {
		this(RayTraceResult.Type.BLOCK, hitVecIn, sideHitIn, BlockPos.ORIGIN);
	}

	public RayTraceResultData(Entity entityIn) {
		this(entityIn, new Vec3d(entityIn.posX, entityIn.posY, entityIn.posZ));
	}

	public RayTraceResultData(RayTraceResult.Type typeIn, Vec3d hitVecIn, EnumFacing sideHitIn, BlockPos blockPosIn) {
		super(typeIn, hitVecIn, sideHitIn, blockPosIn);
	}

	public RayTraceResultData(Entity entityHitIn, Vec3d hitVecIn) {
		super(entityHitIn, hitVecIn);
	}

	public RayTraceResultData<T> data(T data) {
		this.data = data;
		return this;
	}

}