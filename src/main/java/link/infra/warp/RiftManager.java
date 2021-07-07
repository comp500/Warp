package link.infra.warp;

import link.infra.warp.util.RotatedBox;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.*;
import net.minecraft.world.World;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class RiftManager {
	// TODO: needs to be cleaned up on world unload?
	private static final Map<World, RiftManager> INSTANCES = new HashMap<>();

	public static RiftManager getInstance(World world) {
		synchronized (INSTANCES) {
			if (world.isClient()) {
				return INSTANCES.computeIfAbsent(world, RiftManagerClient::new);
			} else {
				return INSTANCES.computeIfAbsent(world, RiftManagerServer::new);
			}
		}
	}

	protected final World world;

	public RiftManager(World world) {
		this.world = world;
	}

	public static class Rift {
		public final Vec3d source;
		public final Vec3d playerPos;
		public final Vec3d targetPos;
		public final BlockPos targetBlock;
		public final PlayerEntity player;

		public final Vec3d rayDirection;
		public final float rayLength;
		public final RotatedBox targetBox;
		public final RotatedBox squashBox;
		public final List<ChunkSectionPos> replacedRegions;

		public float scaleProgress = 0;
		public float scaleProgressLast = 0;
		public boolean expanding = true;
		private final static float scaleStep = 0.1f;

		protected Rift(BlockPos target, PlayerEntity player) throws InvalidRiftException {
			this.playerPos = player.getPos();
			this.targetPos = Vec3d.ofCenter(target);
			this.targetBlock = target;
			this.player = player;
			Vec3d ray = targetPos.subtract(playerPos).multiply(1, 0, 1); // Get rid of y component
			rayDirection = ray.normalize();
			// Use a length 2 blocks shorter than the ray length
			rayLength = (float) ray.length() - 2f;
			// If the resulting length is less than 1.5, fail creating the rift
			if (rayLength < 1.5f) {
				throw new InvalidRiftException();
			}
			// Start the rift slightly in front of the player
			source = playerPos.add(rayDirection);

			Matrix4f transformMatrix = new Matrix4f(); // TODO: get from ray?
			transformMatrix.loadIdentity();
			transformMatrix.multiplyByTranslation((float) playerPos.getX(), (float) playerPos.getY(), (float) playerPos.getZ());
			transformMatrix.multiply(Vec3f.POSITIVE_Y.getDegreesQuaternion(player.getYaw()));
			transformMatrix.multiplyByTranslation((float) -playerPos.getX(), (float) -playerPos.getY(), (float) -playerPos.getZ());
			targetBox = new RotatedBox(source, 24, 8, ray.length() + 24, transformMatrix);
			squashBox = new RotatedBox(source, 24, 8, 1, transformMatrix);

			this.replacedRegions = List.copyOf(targetBox.coveredSections());
		}

		public void tick() {
			scaleProgressLast = scaleProgress;
			Vec3d playerPos = player.getPos();
			if (expanding) {
				if (targetBox.contains(playerPos) && !squashBox.contains(playerPos)) {
					expanding = false;
					// TODO: remove rift when contracted?
				}
			}

			if (expanding) {
				if (scaleProgress < 1f) {
					scaleProgress = MathHelper.clamp(scaleProgress + scaleStep, 0f, 1f);
				}
			} else {
				if (scaleProgress > 0f) {
					scaleProgress = MathHelper.clamp(scaleProgress - scaleStep, 0f, 1f);
					// Move the player when the rift contracts!
					player.setPosition(playerPos.add(rayDirection.multiply(scaleStep * rayLength)));
					if (player.isLogicalSideForUpdatingMovement()) {
						player.updateTrackedPosition(player.getPos());
					}
				}
			}
		}
	}

	public abstract Rift create(BlockPos target, PlayerEntity player) throws InvalidRiftException;

	public abstract void tick();

	public static class InvalidRiftException extends Exception {
	}
}
