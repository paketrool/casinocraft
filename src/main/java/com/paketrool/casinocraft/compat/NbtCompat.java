package com.paketrool.casinocraft.compat;

import net.minecraft.nbt.CompoundTag;

import java.util.function.Consumer;

/**
 * Обёртка над CompoundTag-геттерами. В 1.21.5 появились getIntOr/getLongOr/getStringOr,
 * а старые getInt/getLong/getString в 1.21.5 возвращают Optional. До 1.21.5 API обратное.
 * Stonecutter переключает реализацию под активную версию.
 */
public final class NbtCompat {
	private NbtCompat() {}

	public static int getIntOr(CompoundTag tag, String key, int def) {
		//? if >=1.21.5 {
		return tag.getIntOr(key, def);
		//?} else
		/*return tag.contains(key) ? tag.getInt(key) : def;*/
	}

	public static long getLongOr(CompoundTag tag, String key, long def) {
		//? if >=1.21.5 {
		return tag.getLongOr(key, def);
		//?} else
		/*return tag.contains(key) ? tag.getLong(key) : def;*/
	}

	public static String getStringOr(CompoundTag tag, String key, String def) {
		//? if >=1.21.5 {
		return tag.getStringOr(key, def);
		//?} else
		/*return tag.contains(key) ? tag.getString(key) : def;*/
	}

	public static void ifCompoundPresent(CompoundTag tag, String key, Consumer<CompoundTag> action) {
		//? if >=1.21.5 {
		tag.getCompound(key).ifPresent(action);
		//?} else
		/*if (tag.contains(key)) action.accept(tag.getCompound(key));*/
	}

	public static int[] getIntArrayOr(CompoundTag tag, String key, int[] def) {
		//? if >=1.21.5 {
		return tag.getIntArray(key).orElse(def);
		//?} else
		/*return tag.contains(key) ? tag.getIntArray(key) : def;*/
	}
}
