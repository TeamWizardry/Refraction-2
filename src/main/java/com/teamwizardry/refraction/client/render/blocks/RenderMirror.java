package com.teamwizardry.refraction.client.render.blocks;

import com.teamwizardry.librarianlib.features.math.Matrix4;
import com.teamwizardry.refraction.ClientProxy;
import com.teamwizardry.refraction.Refraction;
import com.teamwizardry.refraction.api.Beam;
import com.teamwizardry.refraction.client.render.BeamRenderer;
import com.teamwizardry.refraction.common.tile.TileMirrorBase;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.client.model.IModel;
import net.minecraftforge.client.model.ModelLoaderRegistry;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.lwjgl.opengl.GL11;

/**
 * Created by Demoniaque
 */
public class RenderMirror extends TileEntitySpecialRenderer<TileMirrorBase> {

	private IBakedModel modelArms, modelMirror;

	public RenderMirror() {
		MinecraftForge.EVENT_BUS.register(this);
	}

	@SubscribeEvent
	public void reload(ClientProxy.ResourceReloadEvent event) {
		modelArms = null;
	}

	private void getBakedModels(TileMirrorBase te) {
		IModel model = null;
		if (modelArms == null) {
			try {
				model = ModelLoaderRegistry.getModel(new ResourceLocation(Refraction.MOD_ID, "block/mirror_arms"));
			} catch (Exception e) {
				e.printStackTrace();
			}
			modelArms = model.bake(model.getDefaultState(), DefaultVertexFormats.ITEM,
					location -> Minecraft.getMinecraft().getTextureMapBlocks().getAtlasSprite(location.toString()));
		}
		if (modelMirror == null || te.reloadTexture()) {
			try {
				model = ModelLoaderRegistry.getModel(new ResourceLocation(Refraction.MOD_ID, "block/mirror_head"));
			} catch (Exception e) {
				e.printStackTrace();
			}
			modelMirror = model.bake(model.getDefaultState(), DefaultVertexFormats.ITEM,
					location -> Minecraft.getMinecraft().getTextureMapBlocks().getAtlasSprite(te.getMirrorHeadLocation().toString()));
		}
	}

	@Override
	public void render(TileMirrorBase te, double x, double y, double z, float partialTicks, int destroyStage, float alpha) {
		double subtractedMillis = (te.getWorld().getTotalWorldTime() - te.worldTime);
		double transitionTimeMaxX = Math.max(3, Math.min(Math.abs((te.rotPrevX - te.rotDestX) / 2.0), 10)),
				transitionTimeMaxY = Math.max(3, Math.min(Math.abs((te.rotPrevY - te.rotDestY) / 2.0), 10));
		float rotX = te.getRotX(), rotY = te.getRotY();

		GlStateManager.pushMatrix();
		GlStateManager.enableBlend();
		GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		getBakedModels(te);

		bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
		if (Minecraft.isAmbientOcclusionEnabled())
			GlStateManager.shadeModel(GL11.GL_SMOOTH);
		else
			GlStateManager.shadeModel(GL11.GL_FLAT);


		GlStateManager.translate(x, y, z); // Translate pad to coords here
		GlStateManager.disableRescaleNormal();

		GlStateManager.translate(0.5, 0, 0.5);

		if (te.transitionY) {
			if (subtractedMillis < transitionTimeMaxY) {
				if (Math.round(te.rotDestY) > Math.round(te.rotPrevY))
					rotY = -((te.rotDestY - te.rotPrevY) / 2) * MathHelper.cos((float) (subtractedMillis * Math.PI / transitionTimeMaxY)) + (te.rotDestY + te.rotPrevY) / 2;
				else
					rotY = ((te.rotPrevY - te.rotDestY) / 2) * MathHelper.cos((float) (subtractedMillis * Math.PI / transitionTimeMaxY)) + (te.rotDestY + te.rotPrevY) / 2;
			} else rotY = te.rotDestY;
		}
		GlStateManager.rotate(rotY, 0, 1, 0);

		Minecraft.getMinecraft().getBlockRendererDispatcher().getBlockModelRenderer().renderModelBrightnessColor(
				modelArms, 1.0F, 1, 1, 1);

		GlStateManager.translate(0, 0.5, 0);

		if (te.transitionX) {
			if (subtractedMillis < transitionTimeMaxX) {
				if (Math.round(te.rotDestX) > Math.round(te.rotPrevX))
					rotX = -((te.rotDestX - te.rotPrevX) / 2) * MathHelper.cos((float) (subtractedMillis * Math.PI / transitionTimeMaxX)) + (te.rotDestX + te.rotPrevX) / 2;
				else
					rotX = ((te.rotPrevX - te.rotDestX) / 2) * MathHelper.cos((float) (subtractedMillis * Math.PI / transitionTimeMaxX)) + (te.rotDestX + te.rotPrevX) / 2;
			} else rotX = te.rotDestX;

		}
		GlStateManager.rotate(rotX, 1, 0, 0);

		Minecraft.getMinecraft().getBlockRendererDispatcher().getBlockModelRenderer().renderModelBrightnessColor(
				modelMirror, 1.0F, 1, 1, 1);

		GlStateManager.disableBlend();
		GlStateManager.popMatrix();

		if (te.transitionX || te.transitionY) {
			Matrix4 matrix = new Matrix4();
			matrix.rotate(Math.toRadians(rotY), new Vec3d(0, 1, 0));
			matrix.rotate(Math.toRadians(rotX), new Vec3d(1, 0, 0));

			Vec3d normal = matrix.apply(new Vec3d(0, 1, 0));

			BeamRenderer.getBeams(te.getPos().toLong() + "").forEach(beamRenderInfo -> {
				Beam beam = beamRenderInfo.beam;
				if (beam.originalSlope == null) return;

				Vec3d incomingDir = beam.originalSlope;
				if (incomingDir.dotProduct(normal) > 0) return;

				Vec3d outgoingDir = incomingDir.subtract(normal.scale(incomingDir.dotProduct(normal) * 2));
				beam.slope = outgoingDir;
				RayTraceResult result = Beam.runRawTrace(te.getWorld(), outgoingDir, beam.origin, beam.range, beam.ignoreEntities, beam.entitySkipList);

				if (result.hitVec == null) return;
				beam.endLoc = result.hitVec;
			});
		}
	}
}
