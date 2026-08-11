package com.paketrool.casinocraft.enchantment;

import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;
import org.jetbrains.annotations.Nullable;

public final class ChorusPortalData {
	public static final String POS_KEY = "CroupierChorusPos";
	public static final String DIM_KEY = "CroupierChorusDim";

	private ChorusPortalData() {}

	public static void writePos(ItemStack stack, BlockPos pos, String dim) {
		CustomData current = stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY);
		CompoundTag tag = current.copyTag();
		tag.putLong(POS_KEY, pos.asLong());
		tag.putString(DIM_KEY, dim);
		stack.set(DataComponents.CUSTOM_DATA, CustomData.of(tag));
	}

	@Nullable
	public static BlockPos readPos(ItemStack stack) {
		CustomData custom = stack.get(DataComponents.CUSTOM_DATA);
		if (custom == null) return null;
		CompoundTag tag = custom.copyTag();
		long v = tag.getLongOr(POS_KEY, Long.MIN_VALUE);
		if (v == Long.MIN_VALUE) return null;
		return BlockPos.of(v);
	}

	public static String readDim(ItemStack stack) {
		CustomData custom = stack.get(DataComponents.CUSTOM_DATA);
		if (custom == null) return "";
		CompoundTag tag = custom.copyTag();
		return tag.getStringOr(DIM_KEY, "");
	}
}
