package com.paketrool.casinocraft.slot;

public enum SlotSymbol {
	COAL(30, 2),
	IRON(25, 3),
	GOLD(20, 5),
	LAPIS(12, 8),
	DIAMOND(10, 15),
	SEVEN(3, 50);

	public final int weight;
	public final int multiplier;

	SlotSymbol(int weight, int multiplier) {
		this.weight = weight;
		this.multiplier = multiplier;
	}

	public static final SlotSymbol[] VALUES = values();
	public static final int TOTAL_WEIGHT;

	static {
		int t = 0;
		for (SlotSymbol s : VALUES) t += s.weight;
		TOTAL_WEIGHT = t;
	}
}
