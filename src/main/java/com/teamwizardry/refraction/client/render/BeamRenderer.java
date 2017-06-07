package com.teamwizardry.refraction.client.render;

import com.teamwizardry.librarianlib.features.network.PacketHandler;
import com.teamwizardry.librarianlib.features.sprite.Sprite;
import com.teamwizardry.refraction.Refraction;
import com.teamwizardry.refraction.common.network.PacketBeamRenderTick;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.VertexBuffer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.util.HashMap;

import static org.lwjgl.opengl.GL11.GL_ONE;
import static org.lwjgl.opengl.GL11.GL_SRC_ALPHA;

/**
 * Created by LordSaad.
 */
public class BeamRenderer {

	private static Sprite streakBase = new Sprite(new ResourceLocation(Refraction.MOD_ID, "textures/particles/streak_base.png"));

	public static BeamRenderer INSTANCE = new BeamRenderer();

	private HashMap<BeamRenderInfo, Integer> beams = new HashMap<>();

	private BeamRenderer() {
		MinecraftForge.EVENT_BUS.register(this);
	}

	@SubscribeEvent
	public void renderWorldLast(RenderWorldLastEvent event) {
		GlStateManager.pushMatrix();

		EntityPlayer rootPlayer = Minecraft.getMinecraft().player;
		double x = rootPlayer.lastTickPosX + (rootPlayer.posX - rootPlayer.lastTickPosX) * event.getPartialTicks();
		double y = rootPlayer.lastTickPosY + (rootPlayer.posY - rootPlayer.lastTickPosY) * event.getPartialTicks();
		double z = rootPlayer.lastTickPosZ + (rootPlayer.posZ - rootPlayer.lastTickPosZ) * event.getPartialTicks();
		GlStateManager.translate(-x, -y, -z);

		GlStateManager.enableBlend();
		GlStateManager.alphaFunc(GL11.GL_GEQUAL, 1f / 255f);

		beams.keySet().forEach(beamRenderInfo -> {
			Color color = beamRenderInfo.getColor();
			Vec3d start = beamRenderInfo.getOrigin();
			Vec3d end = beamRenderInfo.getTarget();

			GlStateManager.disableCull();
			GlStateManager.enableBlend();
			GlStateManager.blendFunc(GL_SRC_ALPHA, GL_ONE);
			GlStateManager.depthMask(false);

			Vec3d d = new Vec3d(0, (0.25 * color.getAlpha() / 255f) / 2.0, 0);
			Vec3d d2 = new Vec3d((0.25 * color.getAlpha() / 255f) / 2.0, 0, 0);
			Vec3d d3 = new Vec3d(0, 0, (0.25 * color.getAlpha() / 255f) / 2.0);

			streakBase.bind();

			double vMin = 0, vMax = 1;
			double uMin = 0, uMax = 1;

			Tessellator tessellator = Tessellator.getInstance();
			VertexBuffer vb = tessellator.getBuffer();

			vb.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX_COLOR);
			pos(vb, start.add(d)).tex(uMin, vMin).color(color.getRed(), color.getGreen(), color.getBlue(), Math.max(128, color.getAlpha())).endVertex();
			pos(vb, start.subtract(d)).tex(uMin, vMax).color(color.getRed(), color.getGreen(), color.getBlue(), Math.max(128, color.getAlpha())).endVertex();
			pos(vb, end.subtract(d)).tex(uMax, vMax).color(color.getRed(), color.getGreen(), color.getBlue(), Math.max(128, color.getAlpha())).endVertex();
			pos(vb, end.add(d)).tex(uMax, vMin).color(color.getRed(), color.getGreen(), color.getBlue(), Math.max(128, color.getAlpha())).endVertex();
			tessellator.draw();

			vb.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX_COLOR);
			pos(vb, start.add(d2)).tex(uMin, vMin).color(color.getRed(), color.getGreen(), color.getBlue(), Math.max(128, color.getAlpha())).endVertex();
			pos(vb, start.subtract(d2)).tex(uMin, vMax).color(color.getRed(), color.getGreen(), color.getBlue(), Math.max(128, color.getAlpha())).endVertex();
			pos(vb, end.subtract(d2)).tex(uMax, vMax).color(color.getRed(), color.getGreen(), color.getBlue(), Math.max(128, color.getAlpha())).endVertex();
			pos(vb, end.add(d2)).tex(uMax, vMin).color(color.getRed(), color.getGreen(), color.getBlue(), Math.max(128, color.getAlpha())).endVertex();
			tessellator.draw();

			vb.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX_COLOR);
			pos(vb, start.add(d3)).tex(uMin, vMin).color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).endVertex();
			pos(vb, start.subtract(d3)).tex(uMin, vMax).color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).endVertex();
			pos(vb, end.subtract(d3)).tex(uMax, vMax).color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).endVertex();
			pos(vb, end.add(d3)).tex(uMax, vMin).color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).endVertex();
			tessellator.draw();
		});

		GlStateManager.disableBlend();
		GlStateManager.disableAlpha();

		GlStateManager.popMatrix();
	}

	public void update() {
		beams.entrySet().removeIf((e) -> {
			if (e.getValue() <= 0) return true;
			else {
				e.setValue(e.getValue() - 1);
				return false;
			}
		});
	}

	public void addBeam(BeamRenderInfo info) {
		beams.put(info, 1);
	}

	@SubscribeEvent
	public void eventServerTick(TickEvent.ServerTickEvent event) {
		if (event.phase == TickEvent.Phase.START) {
			PacketHandler.NETWORK.sendToAll(new PacketBeamRenderTick());
		}
	}

	private static VertexBuffer pos(VertexBuffer vb, Vec3d pos) {
		return vb.pos(pos.xCoord, pos.yCoord, pos.zCoord);
	}
}
