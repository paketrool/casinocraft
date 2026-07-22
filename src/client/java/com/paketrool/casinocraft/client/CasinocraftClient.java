package com.paketrool.casinocraft.client;

import com.paketrool.casinocraft.client.screen.SlotMachineScreen;
import com.paketrool.casinocraft.menu.CasinocraftMenus;
import net.fabricmc.api.ClientModInitializer;
import net.minecraft.client.gui.screens.MenuScreens;

public class CasinocraftClient implements ClientModInitializer {
	@Override
	public void onInitializeClient() {
		MenuScreens.register(CasinocraftMenus.SLOT_MACHINE, SlotMachineScreen::new);
	}
}
