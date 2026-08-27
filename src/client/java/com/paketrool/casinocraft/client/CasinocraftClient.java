package com.paketrool.casinocraft.client;

import com.paketrool.casinocraft.block.entity.CasinocraftBlockEntities;
import com.paketrool.casinocraft.client.render.SlotMachineRenderer;
import com.paketrool.casinocraft.client.screen.SlotGuideScreen;
import com.paketrool.casinocraft.client.screen.SlotMachineScreen;
import com.paketrool.casinocraft.item.CasinocraftItems;
import com.paketrool.casinocraft.menu.CasinocraftMenus;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.event.player.UseItemCallback;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderers;
import net.minecraft.world.InteractionResult;

public class CasinocraftClient implements ClientModInitializer {
	@Override
	public void onInitializeClient() {
		MenuScreens.register(CasinocraftMenus.SLOT_MACHINE, SlotMachineScreen::new);
		BlockEntityRenderers.register(CasinocraftBlockEntities.SLOT_MACHINE, SlotMachineRenderer::new);

		UseItemCallback.EVENT.register((player, level, hand) -> {
			if (player.getItemInHand(hand).is(CasinocraftItems.CASINO_GUIDE)) {
				if (level.isClientSide()) {
					Minecraft.getInstance().setScreen(new SlotGuideScreen());
				}
				return InteractionResult.SUCCESS;
			}
			return InteractionResult.PASS;
		});
	}
}
