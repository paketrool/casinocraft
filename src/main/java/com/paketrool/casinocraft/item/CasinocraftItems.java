package com.paketrool.casinocraft.item;

import com.paketrool.casinocraft.Casinocraft;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;

import java.util.function.Function;

public final class CasinocraftItems {
	private CasinocraftItems() {}

	public static final Item CASINO_CHIP = register(
		"casino_chip",
		Item::new,
		new Item.Properties()
	);

	public static final Item GOLDEN_TALISMAN = register(
		"golden_talisman",
		Item::new,
		new Item.Properties().stacksTo(16).rarity(Rarity.RARE)
	);

	public static final Item CASINO_GUIDE = register(
		"casino_guide",
		Item::new,
		new Item.Properties().stacksTo(1)
	);

	public static void init() {
	}

	private static Item register(String path, Function<Item.Properties, Item> factory, Item.Properties props) {
		ResourceLocation id = Casinocraft.id(path);
		ResourceKey<Item> key = ResourceKey.create(Registries.ITEM, id);
		Item item = factory.apply(props.setId(key));
		return Registry.register(BuiltInRegistries.ITEM, key, item);
	}
}
