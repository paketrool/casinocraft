package com.paketrool.casinocraft.enchantment;

import com.paketrool.casinocraft.Casinocraft;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Level;

public final class CasinocraftEnchantments {
	private CasinocraftEnchantments() {}

	// generic (fallback by tags)
	public static final ResourceKey<Enchantment> CROUPIERS_EDGE =
		ResourceKey.create(Registries.ENCHANTMENT, Casinocraft.id("croupiers_edge"));
	public static final ResourceKey<Enchantment> CROUPIERS_HASTE =
		ResourceKey.create(Registries.ENCHANTMENT, Casinocraft.id("croupiers_haste"));
	public static final ResourceKey<Enchantment> CROUPIERS_WARD =
		ResourceKey.create(Registries.ENCHANTMENT, Casinocraft.id("croupiers_ward"));

	// per-item specials (mixin/event-driven behavior)
	public static final ResourceKey<Enchantment> CROUPIERS_PEARL =
		ResourceKey.create(Registries.ENCHANTMENT, Casinocraft.id("croupiers_pearl"));
	public static final ResourceKey<Enchantment> CROUPIERS_CHORUS =
		ResourceKey.create(Registries.ENCHANTMENT, Casinocraft.id("croupiers_chorus"));
	public static final ResourceKey<Enchantment> CROUPIERS_TORCH =
		ResourceKey.create(Registries.ENCHANTMENT, Casinocraft.id("croupiers_torch"));
	public static final ResourceKey<Enchantment> CROUPIERS_FLURRY =
		ResourceKey.create(Registries.ENCHANTMENT, Casinocraft.id("croupiers_flurry"));

	@org.jetbrains.annotations.Nullable
	public static ResourceKey<Enchantment> pickForItem(ItemStack stack) {
		// specific items win over generic tag categories
		if (stack.is(Items.ENDER_PEARL)) return CROUPIERS_PEARL;
		if (stack.is(Items.CHORUS_FRUIT)) return CROUPIERS_CHORUS;
		if (stack.is(Items.TORCH)) return CROUPIERS_TORCH;
		if (stack.is(Items.SNOWBALL)) return CROUPIERS_FLURRY;

		if (stack.is(ItemTags.WEAPON_ENCHANTABLE) || stack.is(ItemTags.SWORD_ENCHANTABLE)) {
			return CROUPIERS_EDGE;
		}
		if (stack.is(ItemTags.MINING_ENCHANTABLE)) {
			return CROUPIERS_HASTE;
		}
		if (stack.is(ItemTags.ARMOR_ENCHANTABLE)) {
			return CROUPIERS_WARD;
		}
		return null;
	}

	public static boolean has(ItemStack stack, ResourceKey<Enchantment> key, Level level) {
		if (stack.isEmpty()) return false;
		HolderLookup.RegistryLookup<Enchantment> lookup = level.registryAccess().lookup(Registries.ENCHANTMENT).orElse(null);
		if (lookup == null) return false;
		Holder<Enchantment> holder = lookup.get(key).orElse(null);
		if (holder == null) return false;
		return EnchantmentHelper.getItemEnchantmentLevel(holder, stack) > 0;
	}
}
