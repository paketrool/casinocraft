package com.paketrool.casinocraft.client;

import com.paketrool.casinocraft.block.entity.CasinocraftBlockEntities;
import com.paketrool.casinocraft.client.render.SlotMachineRenderer;
import com.paketrool.casinocraft.client.screen.SlotMachineScreen;
import com.paketrool.casinocraft.menu.CasinocraftMenus;
import net.fabricmc.api.ClientModInitializer;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderers;

public class CasinocraftClient implements ClientModInitializer {
	@Override
	public void onInitializeClient() {
		MenuScreens.register(CasinocraftMenus.SLOT_MACHINE, SlotMachineScreen::new);
		BlockEntityRenderers.register(CasinocraftBlockEntities.SLOT_MACHINE, SlotMachineRenderer::new);
	}
}
