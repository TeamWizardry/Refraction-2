package com.teamwizardry.refraction.client.render;

import com.teamwizardry.librarianlib.features.forgeevents.CustomWorldRenderEvent;
import com.teamwizardry.librarianlib.features.network.PacketHandler;
import com.teamwizardry.refraction.Refraction;
import com.teamwizardry.refraction.common.network.PacketUpdateBeamRender;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

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
	public static void addBeam(World world, Vec3d origin, Vec3d target, int red, int green, int blue, UUID uuid) {
		for (BeamRenderInfo info : beams) {
			if (info.uuid.equals(uuid)) {
				info.lastTime = world.getTotalWorldTime();
				info.red = red;
				info.green = green;
				info.blue = blue;
				info.origin = origin;
				info.target = target;
				return;
			}
		}
		beams.add(new BeamRenderInfo(world, origin, target, red, green, blue, uuid));
	}

	public static void update() {
		if (Minecraft.getMinecraft().world == null) return;

		beams.removeIf(beamRenderInfo -> {
			long currentTick = Minecraft.getMinecraft().world.getTotalWorldTime();
			long lastTick = beamRenderInfo.lastTime;

			return (currentTick - lastTick) > 2;
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

			// TODO: FIX COLOR OVERFLOW
			Color color = new Color(
					MathHelper.clamp(beamRenderInfo.red, 0, 255),
					MathHelper.clamp(beamRenderInfo.green, 0, 255),
					MathHelper.clamp(beamRenderInfo.blue, 0, 255));

			Vec3d start = beamRenderInfo.origin;
			Vec3d end = beamRenderInfo.target;
			Vec3d diff = end.subtract(start);
			double diameter = 0.15;
			double radius = diameter / 2.0;

			GlStateManager.translate(start.x, start.y, start.z);

			Tessellator tessellator = Tessellator.getInstance();
			BufferBuilder vb = tessellator.getBuffer();

			vb.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_COLOR);
			vb.pos(0, 0, 0).color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).endVertex();
			vb.pos(0, radius, 0).color(color.getRed(), color.getGreen(), color.getBlue(), 0).endVertex();
			vb.pos(diff.x, diff.y + radius, diff.z).color(color.getRed(), color.getGreen(), color.getBlue(), 0).endVertex();
			vb.pos(diff.x, diff.y, diff.z).color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).endVertex();

			vb.pos(0, 0, 0).color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).endVertex();
			vb.pos(0, -radius, 0).color(color.getRed(), color.getGreen(), color.getBlue(), 0).endVertex();
			vb.pos(diff.x, diff.y - radius, diff.z).color(color.getRed(), color.getGreen(), color.getBlue(), 0).endVertex();
			vb.pos(diff.x, diff.y, diff.z).color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).endVertex();

			vb.pos(0, 0, 0).color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).endVertex();
			vb.pos(0, 0, radius).color(color.getRed(), color.getGreen(), color.getBlue(), 0).endVertex();
			vb.pos(diff.x, diff.y, diff.z + radius).color(color.getRed(), color.getGreen(), color.getBlue(), 0).endVertex();
			vb.pos(diff.x, diff.y, diff.z).color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).endVertex();

			vb.pos(0, 0, 0).color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).endVertex();
			vb.pos(0, 0, -radius).color(color.getRed(), color.getGreen(), color.getBlue(), 0).endVertex();
			vb.pos(diff.x, diff.y, diff.z - radius).color(color.getRed(), color.getGreen(), color.getBlue(), 0).endVertex();
			vb.pos(diff.x, diff.y, diff.z).color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).endVertex();

			vb.pos(0, 0, 0).color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).endVertex();
			vb.pos(radius, 0, 0).color(color.getRed(), color.getGreen(), color.getBlue(), 0).endVertex();
			vb.pos(diff.x + radius, diff.y, diff.z).color(color.getRed(), color.getGreen(), color.getBlue(), 0).endVertex();
			vb.pos(diff.x, diff.y, diff.z).color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).endVertex();

			vb.pos(0, 0, 0).color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).endVertex();
			vb.pos(-radius, 0, 0).color(color.getRed(), color.getGreen(), color.getBlue(), 0).endVertex();
			vb.pos(diff.x - radius, diff.y, diff.z).color(color.getRed(), color.getGreen(), color.getBlue(), 0).endVertex();
			vb.pos(diff.x, diff.y, diff.z).color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).endVertex();

			color = new Color(1f, 1f, 1f, 1f);
			radius = radius / 4.0;

			vb.pos(0, 0, 0).color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).endVertex();
			vb.pos(0, radius, 0).color(color.getRed(), color.getGreen(), color.getBlue(), 0).endVertex();
			vb.pos(diff.x, diff.y + radius, diff.z).color(color.getRed(), color.getGreen(), color.getBlue(), 0).endVertex();
			vb.pos(diff.x, diff.y, diff.z).color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).endVertex();

			vb.pos(0, 0, 0).color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).endVertex();
			vb.pos(0, -radius, 0).color(color.getRed(), color.getGreen(), color.getBlue(), 0).endVertex();
			vb.pos(diff.x, diff.y - radius, diff.z).color(color.getRed(), color.getGreen(), color.getBlue(), 0).endVertex();
			vb.pos(diff.x, diff.y, diff.z).color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).endVertex();

			vb.pos(0, 0, 0).color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).endVertex();
			vb.pos(0, 0, radius).color(color.getRed(), color.getGreen(), color.getBlue(), 0).endVertex();
			vb.pos(diff.x, diff.y, diff.z + radius).color(color.getRed(), color.getGreen(), color.getBlue(), 0).endVertex();
			vb.pos(diff.x, diff.y, diff.z).color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).endVertex();

			vb.pos(0, 0, 0).color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).endVertex();
			vb.pos(0, 0, -radius).color(color.getRed(), color.getGreen(), color.getBlue(), 0).endVertex();
			vb.pos(diff.x, diff.y, diff.z - radius).color(color.getRed(), color.getGreen(), color.getBlue(), 0).endVertex();
			vb.pos(diff.x, diff.y, diff.z).color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).endVertex();

			vb.pos(0, 0, 0).color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).endVertex();
			vb.pos(radius, 0, 0).color(color.getRed(), color.getGreen(), color.getBlue(), 0).endVertex();
			vb.pos(diff.x + radius, diff.y, diff.z).color(color.getRed(), color.getGreen(), color.getBlue(), 0).endVertex();
			vb.pos(diff.x, diff.y, diff.z).color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).endVertex();

			vb.pos(0, 0, 0).color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).endVertex();
			vb.pos(-radius, 0, 0).color(color.getRed(), color.getGreen(), color.getBlue(), 0).endVertex();
			vb.pos(diff.x - radius, diff.y, diff.z).color(color.getRed(), color.getGreen(), color.getBlue(), 0).endVertex();
			vb.pos(diff.x, diff.y, diff.z).color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).endVertex();

			tessellator.draw();

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
