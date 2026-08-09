package com.paketrool.casinocraft;

import com.paketrool.casinocraft.attachment.CasinocraftAttachments;
import com.paketrool.casinocraft.block.CasinocraftBlocks;
import com.paketrool.casinocraft.block.entity.CasinocraftBlockEntities;
import com.paketrool.casinocraft.event.CasinocraftEvents;
import com.paketrool.casinocraft.item.CasinocraftCreativeTab;
import com.paketrool.casinocraft.item.CasinocraftItems;
import com.paketrool.casinocraft.loot.CasinocraftLoot;
import com.paketrool.casinocraft.menu.CasinocraftMenus;
import net.fabricmc.api.ModInitializer;
import net.minecraft.resources.ResourceLocation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Casinocraft implements ModInitializer {
	public static final String MOD_ID = "casinocraft";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {
		CasinocraftBlocks.init();
		CasinocraftBlockEntities.init();
		CasinocraftItems.init();
		CasinocraftMenus.init();
		CasinocraftCreativeTab.init();
		CasinocraftAttachments.init();
		CasinocraftLoot.init();
		CasinocraftEvents.init();
		LOGGER.info("Casinocraft: bootstrap");
	}

	public static ResourceLocation id(String path) {
		return ResourceLocation.fromNamespaceAndPath(MOD_ID, path);
	}
}
