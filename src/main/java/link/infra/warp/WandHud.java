package link.infra.warp;

import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.world.ClientWorld;

public class WandHud {
	public static boolean render(RiftManagerClient riftManager, ClientWorld world, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, ClientPlayerEntity player) {
		if (player == null) {
			return true;
		}

		if (player.getMainHandStack().isOf(Warp.WAND_ITEM) || player.getOffHandStack().isOf(Warp.WAND_ITEM)) {
			// TODO: render overlay
			return false;
		}
		return true;
	}
}
