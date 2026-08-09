package com.paketrool.casinocraft.item;

import com.paketrool.casinocraft.Casinocraft;
import com.paketrool.casinocraft.block.CasinocraftBlocks;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;

public final class CasinocraftCreativeTab {
	private CasinocraftCreativeTab() {}

	public static final ResourceKey<CreativeModeTab> TAB_KEY =
		ResourceKey.create(Registries.CREATIVE_MODE_TAB, Casinocraft.id("main"));

	public static void init() {
		CreativeModeTab tab = FabricItemGroup.builder()
			.title(Component.translatable("itemGroup.casinocraft.main"))
			.icon(() -> new ItemStack(CasinocraftItems.CASINO_CHIP))
			.displayItems((params, output) -> {
				output.accept(CasinocraftBlocks.SLOT_MACHINE);
				output.accept(CasinocraftItems.CASINO_CHIP);
				output.accept(CasinocraftItems.GOLDEN_TALISMAN);
				output.accept(CasinocraftItems.CASINO_GUIDE);
			})
			.build();
		Registry.register(BuiltInRegistries.CREATIVE_MODE_TAB, TAB_KEY, tab);
	}
}
