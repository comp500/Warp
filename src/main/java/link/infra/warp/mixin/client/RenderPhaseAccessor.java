package link.infra.warp.mixin.client;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.RenderPhase;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Environment(EnvType.CLIENT)
@Mixin(RenderPhase.class)
public interface RenderPhaseAccessor {
	@Accessor
	static RenderPhase.Lightmap getENABLE_LIGHTMAP() {
		throw new RuntimeException("Mixin not applied");
	}

	@Accessor
	static RenderPhase.Texture getMIPMAP_BLOCK_ATLAS_TEXTURE() {
		throw new RuntimeException("Mixin not applied");
	}

	@Accessor
	static RenderPhase.Transparency getTRANSLUCENT_TRANSPARENCY() {
		throw new RuntimeException("Mixin not applied");
	}

	@Accessor
	static RenderPhase.Target getTRANSLUCENT_TARGET() {
		throw new RuntimeException("Mixin not applied");
	}

	@Accessor
	static RenderPhase.Texture getBLOCK_ATLAS_TEXTURE() {
		throw new RuntimeException("Mixin not applied");
	}
}
