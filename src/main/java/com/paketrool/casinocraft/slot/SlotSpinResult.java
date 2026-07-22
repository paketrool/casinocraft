package com.paketrool.casinocraft.slot;

public record SlotSpinResult(SlotSymbol[][] grid, int chipsPayout, int talismansPayout) {
	public static final int COLS = 3;
	public static final int ROWS = 3;
}
