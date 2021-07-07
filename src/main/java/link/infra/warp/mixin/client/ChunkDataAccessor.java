package link.infra.warp.mixin.client;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.chunk.ChunkBuilder;
import net.minecraft.client.render.chunk.ChunkOcclusionData;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Environment(EnvType.CLIENT)
@Mixin(ChunkBuilder.ChunkData.class)
public interface ChunkDataAccessor {
	@Accessor
	void setOcclusionGraph(ChunkOcclusionData data);
}
