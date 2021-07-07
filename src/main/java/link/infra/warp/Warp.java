package link.infra.warp;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemGroup;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class Warp implements ModInitializer {
	public static final WandItem WAND_ITEM = new WandItem(new FabricItemSettings().group(ItemGroup.TRANSPORTATION));
	public static final AnchorBlock ANCHOR_BLOCK = new AnchorBlock();
	public static final String MODID = "warp";
	public static final String MODNAME = "Warp";

	@Override
	public void onInitialize() {
		Registry.register(Registry.ITEM, new Identifier(MODID, "wand"), WAND_ITEM);
		Registry.register(Registry.BLOCK, new Identifier(MODID, "anchor"), ANCHOR_BLOCK);
		Registry.register(Registry.ITEM, new Identifier(MODID, "anchor"), new BlockItem(ANCHOR_BLOCK, new FabricItemSettings().group(ItemGroup.TRANSPORTATION)));

		ServerTickEvents.START_WORLD_TICK.register(world -> {
			RiftManager riftMgr = RiftManager.getInstance(world);
			if (riftMgr != null) {
				riftMgr.tick();
			}
		});

		ClientTickEvents.START_WORLD_TICK.register(world -> {
			RiftManager riftMgr = RiftManager.getInstance(world);
			if (riftMgr != null) {
				riftMgr.tick();
			}
		});
	}
}
