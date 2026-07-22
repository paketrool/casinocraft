package com.paketrool.casinocraft.loot;

import com.paketrool.casinocraft.item.CasinocraftItems;
import net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents;
import net.fabricmc.fabric.api.loot.v3.LootTableEvents;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.predicates.LootItemRandomChanceCondition;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;

import java.util.HashMap;
import java.util.Map;

public final class CasinocraftLoot {
	private CasinocraftLoot() {}

	private static final Map<ResourceKey<LootTable>, Float> CHIP_DROPS = new HashMap<>();

	static {
		// 1%
		put(EntityType.ZOMBIE, 0.01f);
		put(EntityType.ZOMBIE_VILLAGER, 0.01f);
		put(EntityType.HUSK, 0.01f);
		put(EntityType.DROWNED, 0.01f);
		put(EntityType.SLIME, 0.01f);
		put(EntityType.SILVERFISH, 0.01f);
		put(EntityType.ENDERMITE, 0.01f);
		// 2%
		put(EntityType.SKELETON, 0.02f);
		put(EntityType.STRAY, 0.02f);
		put(EntityType.BOGGED, 0.02f);
		put(EntityType.SPIDER, 0.02f);
		put(EntityType.CAVE_SPIDER, 0.02f);
		put(EntityType.MAGMA_CUBE, 0.02f);
		put(EntityType.ZOMBIFIED_PIGLIN, 0.02f);
		put(EntityType.PHANTOM, 0.02f);
		// 3%
		put(EntityType.CREEPER, 0.03f);
		put(EntityType.PIGLIN, 0.03f);
		put(EntityType.HOGLIN, 0.03f);
		put(EntityType.ZOGLIN, 0.03f);
		put(EntityType.VEX, 0.03f);
		put(EntityType.PILLAGER, 0.03f);
		put(EntityType.GUARDIAN, 0.03f);
		// 5%
		put(EntityType.ENDERMAN, 0.05f);
		put(EntityType.WITCH, 0.05f);
		put(EntityType.BLAZE, 0.05f);
		put(EntityType.GHAST, 0.05f);
		put(EntityType.VINDICATOR, 0.05f);
		put(EntityType.BREEZE, 0.05f);
		// 8%
		put(EntityType.EVOKER, 0.08f);
		put(EntityType.PIGLIN_BRUTE, 0.08f);
		// 10%
		put(EntityType.WITHER_SKELETON, 0.10f);
		put(EntityType.RAVAGER, 0.10f);
		// 15%
		put(EntityType.SHULKER, 0.15f);
		put(EntityType.ELDER_GUARDIAN, 0.15f);
		// 30%
		put(EntityType.WARDEN, 0.30f);
	}

	private static void put(EntityType<?> type, float chance) {
		type.getDefaultLootTable().ifPresent(key -> CHIP_DROPS.put(key, chance));
	}

	public static void init() {
		LootTableEvents.MODIFY.register((key, tableBuilder, source, registries) -> {
			if (!source.isBuiltin()) return;
			Float chance = CHIP_DROPS.get(key);
			if (chance == null) return;
			LootPool pool = LootPool.lootPool()
				.setRolls(ConstantValue.exactly(1))
				.add(LootItem.lootTableItem(CasinocraftItems.CASINO_CHIP))
				.when(LootItemRandomChanceCondition.randomChance(chance))
				.build();
			tableBuilder.pool(pool);
		});

		ServerLivingEntityEvents.AFTER_DEATH.register((entity, damageSource) -> {
			if (!(entity instanceof ServerPlayer victim)) return;
			if (!(damageSource.getEntity() instanceof ServerPlayer killer)) return;
			if (killer == victim) return;
			ItemEntity drop = new ItemEntity(
				victim.level(),
				victim.getX(), victim.getY(), victim.getZ(),
				new ItemStack(CasinocraftItems.CASINO_CHIP)
			);
			victim.level().addFreshEntity(drop);
		});
	}
}
