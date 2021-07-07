package link.infra.warp.client;

import link.infra.warp.RiftManager;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.*;
import net.minecraft.client.render.block.BlockModelRenderer;
import net.minecraft.client.render.block.BlockRenderManager;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkSectionPos;
import net.minecraft.world.World;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class RiftRenderBuilder {
	private final Map<RenderLayer, BufferBuilder> buffers = new HashMap<>();
	private final MinecraftClient client = MinecraftClient.getInstance();
	private final BlockRenderManager renderManager = client.getBlockRenderManager();

	public RiftRenderBuilder() {
		for (RenderLayer layer : LayerMap.getCustomShaderLayers()) {
			buffers.put(layer, new BufferBuilder(layer.getExpectedBufferSize()));
		}
	}

	public void build(RiftRenderCache renderCache, RiftManager.Rift currentRift, World world) {
		for (BufferBuilder buf : buffers.values()) {
			buf.reset();
		}
		renderCache.reset();

		BlockPos.Mutable pos = new BlockPos.Mutable();
		Random random = new Random();
		MatrixStack matrices = new MatrixStack();

		if (currentRift.replacedRegions.size() > 0) {
			BlockModelRenderer.enableBrightnessCache();
			BlockPos mainOrigin = currentRift.replacedRegions.get(0).getMinPos();
			for (ChunkSectionPos section : currentRift.replacedRegions) {
				BlockPos sectionOrigin = section.getMinPos();
				BlockPos originOffset = sectionOrigin.subtract(mainOrigin);
				// TODO: set chunkoffset origin stuff??

				for (int y = 0; y < 16; y++) {
					for (int z = 0; z < 16; z++) {
						for (int x = 0; x < 16; x++) {
							pos.set(sectionOrigin.getX() + x, sectionOrigin.getY() + y, sectionOrigin.getZ() + z);

							BlockState state = world.getBlockState(pos);
							if (state.getRenderType() != BlockRenderType.INVISIBLE) {
								RenderLayer layer = LayerMap.getWithCustomShader(RenderLayers.getBlockLayer(state));
								BufferBuilder buf = buffers.get(layer);
								if (renderCache.layers.add(layer)) {
									buf.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_COLOR_TEXTURE_LIGHT_NORMAL);
								}

								matrices.push();
								matrices.translate(originOffset.getX() + x, originOffset.getY() + y, originOffset.getZ() + z);
								if (renderManager.renderBlock(state, pos, world, matrices, buf, true, random)) {
									// TODO: mark non empty, mark layers non-empty?
								}
								matrices.pop();
							}
							// TODO: fluids, BEs?
						}
					}
				}
			}
			BlockModelRenderer.disableBrightnessCache();

			for (RenderLayer layer : renderCache.layers) {
				BufferBuilder buffer = buffers.get(layer);
				buffer.end();
				renderCache.buffers.get(layer).upload(buffer);
			}

			renderCache.origin = mainOrigin;
		}

		renderCache.currentRift = currentRift;
	}
}
