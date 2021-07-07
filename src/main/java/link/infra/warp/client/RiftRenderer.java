package link.infra.warp.client;

import com.mojang.blaze3d.systems.RenderSystem;
import link.infra.warp.RiftManagerClient;
import net.minecraft.client.gl.GlUniform;
import net.minecraft.client.gl.VertexBuffer;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.Shader;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.*;
import net.minecraft.world.World;

public class RiftRenderer {
	public static void render(RiftManagerClient riftManager, World world, MatrixStack modelViewMatrix, Matrix4f projectionMatrix, Camera camera, float tickDelta) {
		if (riftManager.hasRift()) {
			if (riftManager.shouldRebuild()) {
				riftManager.getRenderBuilder().build(riftManager.getRenderCache(), riftManager.getCurrentRift(), world);
			} else {
				RiftRenderCache renderCache = riftManager.getRenderCache();
				Vec3d cameraPos = camera.getPos();
				double cameraX = cameraPos.getX();
				double cameraY = cameraPos.getY();
				double cameraZ = cameraPos.getZ();

				for (RenderLayer layer : renderCache.layers) {
					layer.startDrawing();

					// TODO: translucency sorting?
					Shader shader = RenderSystem.getShader();

					GlUniform offsetUniform = shader.chunkOffset;
					BlockPos origin = renderCache.getOrigin();
					if (offsetUniform != null && origin != null) {
						offsetUniform.set((float) ((double) origin.getX() - cameraX), (float) ((double) origin.getY() - cameraY), (float) ((double) origin.getZ() - cameraZ));

						shader.getUniformOrDefault("RiftCastDir").set(
							(float) renderCache.currentRift.rayDirection.getX(),
							(float) renderCache.currentRift.rayDirection.getZ());
						shader.getUniformOrDefault("RiftCastOrigin").set(
							(float) (renderCache.currentRift.source.getX() - (origin.getX())),
							(float) (renderCache.currentRift.source.getZ() - (origin.getZ())));
						shader.getUniformOrDefault("RiftSourceDist").set(renderCache.currentRift.rayLength);
						float rayLengthMinusOne = renderCache.currentRift.rayLength - 1f;
						shader.getUniformOrDefault("RiftTargetDist").set(1f +
							(rayLengthMinusOne - MathHelper.lerp(tickDelta, renderCache.currentRift.scaleProgressLast, renderCache.currentRift.scaleProgress) * rayLengthMinusOne));
					}

					VertexBuffer vertexBuffer = renderCache.getBuffer(layer);
					// Actually calls draw?!
					vertexBuffer.setShader(modelViewMatrix.peek().getModel(), projectionMatrix, shader);

					if (offsetUniform != null) {
						offsetUniform.set(Vec3f.ZERO);
					}

					layer.endDrawing();
				}
			}
		}
	}
}
