package link.infra.warp.client;

import link.infra.warp.RiftManager;
import link.infra.warp.RiftManagerClient;
import link.infra.warp.WandHud;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.minecraft.client.MinecraftClient;
import net.minecraft.resource.ResourceType;

@Environment(EnvType.CLIENT)
public class WarpClient implements ClientModInitializer {
	@Override
	public void onInitializeClient() {
		WorldRenderEvents.BEFORE_ENTITIES.register(ctx ->
			RiftRenderer.render((RiftManagerClient) RiftManager.getInstance(ctx.world()), ctx.world(), ctx.matrixStack(), ctx.projectionMatrix(), ctx.camera(), ctx.tickDelta()));

		WorldRenderEvents.BEFORE_BLOCK_OUTLINE.register((ctx, hitResult) ->
			WandHud.render((RiftManagerClient) RiftManager.getInstance(ctx.world()), ctx.world(), ctx.matrixStack(), ctx.consumers(), MinecraftClient.getInstance().player));

		ResourceManagerHelper.get(ResourceType.CLIENT_RESOURCES).registerReloadListener(RiftShaders.INSTANCE);
	}
}
