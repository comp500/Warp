package link.infra.warp.mixin.client;

import link.infra.warp.RiftManager;
import link.infra.warp.RiftManagerClient;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.chunk.BlockBufferBuilderStorage;
import net.minecraft.client.render.chunk.ChunkBuilder;
import net.minecraft.client.render.chunk.ChunkOcclusionDataBuilder;
import net.minecraft.util.math.ChunkSectionPos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Set;

@Environment(EnvType.CLIENT)
@Mixin(ChunkBuilder.BuiltChunk.RebuildTask.class)
public abstract class RebuildTaskMixin {
	@SuppressWarnings("ShadowTarget")
	@Shadow
	ChunkBuilder.BuiltChunk field_20839;

	@Inject(method = "render", at = @At("HEAD"), cancellable = true)
	private void warp_onRender(float cameraX, float cameraY, float cameraZ, ChunkBuilder.ChunkData data, BlockBufferBuilderStorage buffers, CallbackInfoReturnable<Set<BlockEntity>> cir) {
		RiftManagerClient riftManager = (RiftManagerClient) RiftManager.getInstance(MinecraftClient.getInstance().world);
		RiftManager.Rift rift = riftManager.getCurrentRift();
		if (rift != null) {
			if (rift.replacedRegions.contains(ChunkSectionPos.from(field_20839.getOrigin()))) {
				ChunkOcclusionDataBuilder chunkOcclusionDataBuilder = new ChunkOcclusionDataBuilder();
				((ChunkDataAccessor) data).setOcclusionGraph(chunkOcclusionDataBuilder.build());
				cir.setReturnValue(Set.of());
			}
		}
	}
}
