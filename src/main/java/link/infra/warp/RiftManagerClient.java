package link.infra.warp;

import link.infra.warp.client.RiftRenderBuilder;
import link.infra.warp.client.RiftRenderCache;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkSectionPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class RiftManagerClient extends RiftManager {
	public RiftManagerClient(World world) {
		super(world);
	}

	private Rift currentRift;
	// TODO: should be coupled to world renderer instead of world?
	private final RiftRenderCache renderCache = new RiftRenderCache();
	private final RiftRenderBuilder renderBuilder = new RiftRenderBuilder();

	@Override
	public Rift create(BlockPos target, PlayerEntity player) throws InvalidRiftException {
		Rift oldRift = currentRift;
		// TODO: send to server?
		currentRift = new Rift(target, player);
		// Schedule rerenders of rift'd chunksections, and old chunksections where necessary
		List<ChunkSectionPos> rerenderRegions = new ArrayList<>(currentRift.replacedRegions);
		if (oldRift != null) {
			rerenderRegions.addAll(oldRift.replacedRegions);
		}
		for (ChunkSectionPos region : rerenderRegions) {
			MinecraftClient.getInstance().worldRenderer.scheduleBlockRender(region.getX(), region.getY(), region.getZ());
		}
		return currentRift;
	}

	@Nullable
	public Rift getCurrentRift() {
		return currentRift;
	}

	public boolean hasRift() {
		return currentRift != null;
	}

	public boolean shouldRebuild() {
		// TODO: normal chunk rebuilding
		return renderCache.currentRift != currentRift;
	}

	public RiftRenderCache getRenderCache() {
		return renderCache;
	}

	public RiftRenderBuilder getRenderBuilder() {
		return renderBuilder;
	}

	@Override
	public void tick() {
		if (currentRift != null) {
			currentRift.tick();
		}
	}
}
