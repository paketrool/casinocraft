package com.paketrool.casinocraft.menu;

import com.paketrool.casinocraft.Casinocraft;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.inventory.MenuType;

public final class CasinocraftMenus {
	private CasinocraftMenus() {}

	public static final MenuType<SlotMachineMenu> SLOT_MACHINE = Registry.register(
		BuiltInRegistries.MENU,
		Casinocraft.id("slot_machine"),
		new MenuType<>(SlotMachineMenu::new, FeatureFlags.VANILLA_SET)
	);

	public static void init() {
	}
}
