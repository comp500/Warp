package link.infra.warp.client;

import link.infra.warp.Warp;
import net.fabricmc.fabric.api.resource.SimpleSynchronousResourceReloadListener;
import net.minecraft.client.render.Shader;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;

import java.io.IOException;

public class RiftShaders implements SimpleSynchronousResourceReloadListener {
	public static RiftShaders INSTANCE = new RiftShaders();

	public Shader SOLID;
	public Shader CUTOUT_MIPPED;
	public Shader CUTOUT;
	public Shader TRANSLUCENT;

	@Override
	public void reload(ResourceManager manager) {
		closeIfNotNull(SOLID);
		closeIfNotNull(CUTOUT_MIPPED);
		closeIfNotNull(CUTOUT);
		closeIfNotNull(TRANSLUCENT);
		try {
			SOLID = new Shader(manager, "rendertype_" + Warp.MODID + "_rift_solid", VertexFormats.POSITION_COLOR_TEXTURE_LIGHT_NORMAL);
			CUTOUT_MIPPED = new Shader(manager, "rendertype_" + Warp.MODID + "_rift_cutout_mipped", VertexFormats.POSITION_COLOR_TEXTURE_LIGHT_NORMAL);
			CUTOUT = new Shader(manager, "rendertype_" + Warp.MODID + "_rift_cutout", VertexFormats.POSITION_COLOR_TEXTURE_LIGHT_NORMAL);
			TRANSLUCENT = new Shader(manager, "rendertype_" + Warp.MODID + "_rift_translucent", VertexFormats.POSITION_COLOR_TEXTURE_LIGHT_NORMAL);
		} catch (IOException e) {
			throw new RuntimeException("Failed to load " + Warp.MODNAME + " shaders");
		}
	}

	private static void closeIfNotNull(Shader shader) {
		if (shader != null) {
			shader.close();
		}
	}

	@Override
	public Identifier getFabricId() {
		return new Identifier(Warp.MODID, "rift_shaders");
	}

}
