package link.infra.warp.client;

import it.unimi.dsi.fastutil.objects.Object2ObjectArrayMap;
import link.infra.warp.Warp;
import link.infra.warp.mixin.client.RenderPhaseAccessor;
import net.minecraft.client.gl.VertexBuffer;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.RenderPhase;
import net.minecraft.client.render.VertexFormat;
import net.minecraft.client.render.VertexFormats;

import java.util.List;
import java.util.Map;

public class LayerMap {
	private static final Map<RenderLayer, RenderLayer> LAYER_MAP = new Object2ObjectArrayMap<>();
	public static final List<RenderLayer> CUSTOM_SHADER_LAYERS;

	private static final RenderPhase.Shader SOLID_SHADER = new RenderPhase.Shader(() -> RiftShaders.INSTANCE.SOLID);
	private static final RenderPhase.Shader CUTOUT_MIPPED_SHADER = new RenderPhase.Shader(() -> RiftShaders.INSTANCE.CUTOUT_MIPPED);
	private static final RenderPhase.Shader CUTOUT_SHADER = new RenderPhase.Shader(() -> RiftShaders.INSTANCE.CUTOUT);
	private static final RenderPhase.Shader TRANSLUCENT_SHADER = new RenderPhase.Shader(() -> RiftShaders.INSTANCE.TRANSLUCENT);

	private static final RenderLayer SOLID = RenderLayer.
		of(Warp.MODID + "_rift_solid", VertexFormats.POSITION_COLOR_TEXTURE_LIGHT_NORMAL, VertexFormat.DrawMode.QUADS, 2097152,
			true, false, RenderLayer.MultiPhaseParameters.builder()
				.lightmap(RenderPhaseAccessor.getENABLE_LIGHTMAP())
				.shader(SOLID_SHADER)
				.texture(RenderPhaseAccessor.getMIPMAP_BLOCK_ATLAS_TEXTURE())
				.build(true));
	private static final RenderLayer CUTOUT_MIPPED = RenderLayer.
		of(Warp.MODID + "_rift_cutout_mipped", VertexFormats.POSITION_COLOR_TEXTURE_LIGHT_NORMAL, VertexFormat.DrawMode.QUADS, 131072,
			true, false, RenderLayer.MultiPhaseParameters.builder()
				.lightmap(RenderPhaseAccessor.getENABLE_LIGHTMAP())
				.shader(CUTOUT_MIPPED_SHADER)
				.texture(RenderPhaseAccessor.getMIPMAP_BLOCK_ATLAS_TEXTURE())
				.build(true));
	private static final RenderLayer CUTOUT = RenderLayer.
		of(Warp.MODID + "_rift_cutout", VertexFormats.POSITION_COLOR_TEXTURE_LIGHT_NORMAL, VertexFormat.DrawMode.QUADS, 131072,
			true, false, RenderLayer.MultiPhaseParameters.builder()
				.lightmap(RenderPhaseAccessor.getENABLE_LIGHTMAP())
				.shader(CUTOUT_SHADER)
				.texture(RenderPhaseAccessor.getBLOCK_ATLAS_TEXTURE())
				.build(true));
	private static final RenderLayer TRANSLUCENT = RenderLayer.
		of(Warp.MODID + "_rift_translucent", VertexFormats.POSITION_COLOR_TEXTURE_LIGHT_NORMAL, VertexFormat.DrawMode.QUADS, 2097152,
			true, true, RenderLayer.MultiPhaseParameters.builder()
				.lightmap(RenderPhaseAccessor.getENABLE_LIGHTMAP())
				.shader(TRANSLUCENT_SHADER)
				.texture(RenderPhaseAccessor.getMIPMAP_BLOCK_ATLAS_TEXTURE())
				.transparency(RenderPhaseAccessor.getTRANSLUCENT_TRANSPARENCY())
				.target(RenderPhaseAccessor.getTRANSLUCENT_TARGET())
				.build(true));

	static {
		LAYER_MAP.put(RenderLayer.getSolid(), SOLID);
		LAYER_MAP.put(RenderLayer.getCutoutMipped(), CUTOUT_MIPPED);
		LAYER_MAP.put(RenderLayer.getCutout(), CUTOUT);
		LAYER_MAP.put(RenderLayer.getTranslucent(), TRANSLUCENT);
		// I don't really care about whatever blending issue tripwires have, just use cutout
		// TODO: test tripwires
		LAYER_MAP.put(RenderLayer.getTripwire(), LAYER_MAP.get(RenderLayer.getCutout()));

		CUSTOM_SHADER_LAYERS = List.of(SOLID, CUTOUT_MIPPED, CUTOUT, TRANSLUCENT);
	}

	public static RenderLayer getWithCustomShader(RenderLayer layer) {
		return LAYER_MAP.get(layer);
	}

	public static Iterable<RenderLayer> getCustomShaderLayers() {
		return CUSTOM_SHADER_LAYERS;
	}

	public static Map<RenderLayer, VertexBuffer> makeVertexBuffers() {
		return Map.of(
			SOLID, new VertexBuffer(),
			CUTOUT_MIPPED, new VertexBuffer(),
			CUTOUT, new VertexBuffer(),
			TRANSLUCENT, new VertexBuffer()
		);
	}
}
