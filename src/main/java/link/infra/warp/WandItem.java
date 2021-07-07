package link.infra.warp;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.world.World;

public class WandItem extends Item {
	public WandItem(Settings settings) {
		super(settings);
	}

	@Override
	public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
		HitResult result = user.raycast(16.0, 0f, false);
		if (result.getType() == HitResult.Type.BLOCK) {
			BlockHitResult blockHitResult = (BlockHitResult) result;
			// TODO: send to server separately instead of creating rift on both sides?
			if (world.getBlockState(blockHitResult.getBlockPos()).isOf(Warp.ANCHOR_BLOCK)) {
				try {
					RiftManager.getInstance(world).create(blockHitResult.getBlockPos(), user);
				} catch (RiftManager.InvalidRiftException e) {
					return TypedActionResult.fail(user.getStackInHand(hand));
				}
				return TypedActionResult.success(user.getStackInHand(hand));
			}
		}
		return TypedActionResult.pass(user.getStackInHand(hand));
	}
}
