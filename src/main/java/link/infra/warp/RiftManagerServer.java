package link.infra.warp;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.HashMap;
import java.util.Map;

public class RiftManagerServer extends RiftManager {
	public RiftManagerServer(World world) {
		super(world);
	}

	private final Map<PlayerEntity, Rift> currentRifts = new HashMap<>();

	@Override
	public Rift create(BlockPos target, PlayerEntity player) throws InvalidRiftException {
		// TODO: validate?
		Rift rift = new Rift(target, player);
		currentRifts.put(player, rift);
		return rift;
	}

	@Override
	public void tick() {
		for (Rift rift : currentRifts.values()) {
			rift.tick();
		}
	}
}
