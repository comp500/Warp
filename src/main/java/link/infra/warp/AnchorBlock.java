package link.infra.warp;

import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.block.Block;
import net.minecraft.block.Material;

public class AnchorBlock extends Block {
	public AnchorBlock() {
		super(FabricBlockSettings.of(Material.METAL).strength(4.0F));
	}
}
