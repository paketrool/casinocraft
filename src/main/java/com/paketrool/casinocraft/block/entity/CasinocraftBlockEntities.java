package com.paketrool.casinocraft.block.entity;

import com.paketrool.casinocraft.Casinocraft;
import com.paketrool.casinocraft.block.CasinocraftBlocks;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.block.entity.BlockEntityType;

public final class CasinocraftBlockEntities {
	private CasinocraftBlockEntities() {}

	public static final BlockEntityType<SlotMachineBlockEntity> SLOT_MACHINE = Registry.register(
		BuiltInRegistries.BLOCK_ENTITY_TYPE,
		Casinocraft.id("slot_machine"),
		FabricBlockEntityTypeBuilder.create(SlotMachineBlockEntity::new, CasinocraftBlocks.SLOT_MACHINE).build()
	);

	public static void init() {
	}
}
