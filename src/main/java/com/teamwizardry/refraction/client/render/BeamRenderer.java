package com.teamwizardry.refraction.client.render;

import com.teamwizardry.librarianlib.core.client.ClientTickHandler;
import com.teamwizardry.librarianlib.features.forgeevents.CustomWorldRenderEvent;
import com.teamwizardry.librarianlib.features.network.PacketHandler;
import com.teamwizardry.librarianlib.features.sprite.Sprite;
import com.teamwizardry.refraction.Refraction;
import com.teamwizardry.refraction.api.utils.Utils;
import com.teamwizardry.refraction.common.network.PacketUpdateBeamRender;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.function.Predicate;

import static org.lwjgl.opengl.GL11.GL_ONE;
import static org.lwjgl.opengl.GL11.GL_SRC_ALPHA;

/**
 * Created by Demoniaque.
 */
@Mod.EventBusSubscriber(modid = Refraction.MOD_ID)
public class BeamRenderer {

	private static Set<BeamRenderInfo> beams = new HashSet<>();

	@SideOnly(Side.CLIENT)
	private static BufferBuilder pos(BufferBuilder vb, Vec3d pos) {
		return vb.pos(pos.x, pos.y, pos.z);
	}

	@SideOnly(Side.CLIENT)
	public static void addBeam(World world, Vec3d origin, Vec3d target, Color color, UUID uuid) {
		for (BeamRenderInfo info : beams) {
			if (info.uuid.equals(uuid)) {
				info.lastTime = world.getTotalWorldTime();
				info.color = color;
				info.origin = origin;
				info.target = target;
				return;
			}
		}
		beams.add(new BeamRenderInfo(world, origin, target, color, uuid));
	}

	public static void update() {
		if (Minecraft.getMinecraft().world == null) return;

		beams.removeIf(beamRenderInfo -> {
			long currentTick = Minecraft.getMinecraft().world.getTotalWorldTime();
			long lastTick = beamRenderInfo.lastTime;

			return (currentTick - lastTick) > 1;
		});
	}

	/**
	 * We're updating the client beam render ticks on the server because
	 * if the server desyncs from the client for less than a tick, the beam flickers.
	 * So we only update the beam rendering from the server side and tell the client to
	 * update accordingly.
	 * The packet is stupid small which is good.
	 */
	@SubscribeEvent
	public static void serverTick(TickEvent.ServerTickEvent event) {
		if (event.phase != TickEvent.Phase.END) return;

		PacketHandler.NETWORK.sendToAll(new PacketUpdateBeamRender());
	}

	@SideOnly(Side.CLIENT)
	@SubscribeEvent
	public static void renderWorldLast(CustomWorldRenderEvent event) {
		if (Minecraft.getMinecraft().world == null) return;

		GlStateManager.pushMatrix();

		GlStateManager.enableBlend();
		GlStateManager.alphaFunc(GL11.GL_GEQUAL, 1f / 255f);

		GlStateManager.enableBlend();
		GlStateManager.depthMask(false);
		GlStateManager.disableCull();
		GlStateManager.enableAlpha();
		GlStateManager.enableBlend();
		GlStateManager.shadeModel(GL11.GL_SMOOTH);
		GlStateManager.blendFunc(GL_SRC_ALPHA, GL_ONE);
		GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
		GlStateManager.color(1, 1, 1, 1);
		GlStateManager.disableTexture2D();
		GlStateManager.enableColorMaterial();

		beams.forEach(beamRenderInfo -> {
			GlStateManager.pushMatrix();

			Color color = beamRenderInfo.color;
			Vec3d start = beamRenderInfo.origin;
			Vec3d end = beamRenderInfo.target;
			Vec3d diff = end.subtract(start);

			Vec3d look = Minecraft.getMinecraft().player.getLook(0);
			float rY = Utils.signAngle(new Vec3d(0, 0, 1), (new Vec3d(look.x, 0, look.z)).normalize(), new Vec3d(0, 1, 0));
			float rX = Utils.signAngle(new Vec3d(0, 1, 0), look, null);

			GlStateManager.translate(start.x, start.y, start.z);
			//GlStateManager.rotate(rY, 0f, 1f, 0f);
			//GlStateManager.rotate(90 + rX, 1f, 0f, 0f);

			Vec3d d = new Vec3d(0, (0.25 * color.getAlpha() / 255f) / 2.0, 0);
			Vec3d d2 = new Vec3d((0.25 * color.getAlpha() / 255f) / 2.0, 0, 0);
			Vec3d d3 = new Vec3d(0, 0, (0.25 * color.getAlpha() / 255f) / 2.0);

			double vMin = 0, vMax = 1;
			double uMin = 0, uMax = 1;

			Tessellator tessellator = Tessellator.getInstance();
			BufferBuilder vb = tessellator.getBuffer();

			vb.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_COLOR);
			vb.pos(0, 0, 0).color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).endVertex();
			vb.pos(0, 0.25, 0).color(color.getRed(), color.getGreen(), color.getBlue(), 0).endVertex();
			vb.pos(diff.x, diff.y + 0.25, diff.z).color(color.getRed(), color.getGreen(), color.getBlue(), 0).endVertex();
			vb.pos(diff.x, diff.y, diff.z).color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).endVertex();
			tessellator.draw();

			if (false) {
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
			}
			GlStateManager.popMatrix();
		});

		GlStateManager.disableBlend();
		GlStateManager.enableDepth();
		GlStateManager.enableAlpha();
		GlStateManager.enableTexture2D();
		GlStateManager.disableColorMaterial();
		GlStateManager.depthMask(true);

		GlStateManager.popMatrix();
	}
}
