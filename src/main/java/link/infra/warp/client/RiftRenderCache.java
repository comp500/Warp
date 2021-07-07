package link.infra.warp.client;

import it.unimi.dsi.fastutil.objects.ObjectArraySet;
import link.infra.warp.RiftManager;
import net.minecraft.client.gl.VertexBuffer;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.util.math.BlockPos;

import java.util.Map;
import java.util.Set;

public class RiftRenderCache {
	// TODO: needs to be deleted on world unload?
	public final Map<RenderLayer, VertexBuffer> buffers = LayerMap.makeVertexBuffers();
	// TODO: rebuilds
	public RiftManager.Rift currentRift = null;
	public final Set<RenderLayer> layers = new ObjectArraySet<>();
	public BlockPos origin = null;

	public void reset() {
		layers.clear();
		currentRift = null;
		origin = null;
	}

	public VertexBuffer getBuffer(RenderLayer renderLayer) {
		return buffers.get(renderLayer);
	}

	public BlockPos getOrigin() {
		return origin;
	}
}
