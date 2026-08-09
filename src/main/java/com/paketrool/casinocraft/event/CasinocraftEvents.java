package com.paketrool.casinocraft.event;

import com.paketrool.casinocraft.attachment.CasinocraftAttachments;
import com.paketrool.casinocraft.item.CasinocraftItems;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;

public final class CasinocraftEvents {
	private CasinocraftEvents() {}

	public static void init() {
		ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> {
			ServerPlayer player = handler.player;
			if (Boolean.TRUE.equals(player.getAttachedOrCreate(CasinocraftAttachments.GUIDE_GIVEN))) return;
			player.setAttached(CasinocraftAttachments.GUIDE_GIVEN, true);
			ItemStack guide = new ItemStack(CasinocraftItems.CASINO_GUIDE);
			if (!player.getInventory().add(guide)) {
				player.drop(guide, false);
			}
		});
	}
}
