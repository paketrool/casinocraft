package com.paketrool.casinocraft.block;

import com.paketrool.casinocraft.Casinocraft;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;

import java.util.function.Function;

public final class CasinocraftBlocks {
	private CasinocraftBlocks() {}

	public static final Block SLOT_MACHINE = register(
		"slot_machine",
		SlotMachineBlock::new,
		BlockBehaviour.Properties.of()
			.strength(3.0F, 6.0F)
			.sound(SoundType.WOOD)
	);

	public static void init() {
	}

	private static Block register(String path, Function<BlockBehaviour.Properties, Block> factory, BlockBehaviour.Properties props) {
		ResourceLocation id = Casinocraft.id(path);
		ResourceKey<Block> blockKey = ResourceKey.create(Registries.BLOCK, id);
		Block block = factory.apply(props.setId(blockKey));
		Registry.register(BuiltInRegistries.BLOCK, blockKey, block);

		ResourceKey<Item> itemKey = ResourceKey.create(Registries.ITEM, id);
		BlockItem blockItem = new BlockItem(block, new Item.Properties().setId(itemKey).useBlockDescriptionPrefix());
		Registry.register(BuiltInRegistries.ITEM, itemKey, blockItem);
		return block;
	}
}
