package com.paketrool.casinocraft.slot;

import net.minecraft.util.RandomSource;

public final class SlotMachineLogic {
	private SlotMachineLogic() {}

	private static final int[][][] LINES = {
		{{0,0},{1,0},{2,0}},
		{{0,1},{1,1},{2,1}},
		{{0,2},{1,2},{2,2}},
		{{0,0},{1,1},{2,2}},
		{{0,2},{1,1},{2,0}},
		{{0,0},{1,0},{2,1}},
		{{0,2},{1,2},{2,1}},
		{{0,0},{1,2},{2,0}}
	};

	public static SlotSpinResult spin(RandomSource random, int bet) {
		SlotSymbol[][] grid = new SlotSymbol[SlotSpinResult.COLS][SlotSpinResult.ROWS];
		for (int c = 0; c < SlotSpinResult.COLS; c++) {
			for (int r = 0; r < SlotSpinResult.ROWS; r++) {
				grid[c][r] = rollSymbol(random);
			}
		}

		int chipsPayout = 0;
		int talismansPayout = 0;
		for (int[][] line : LINES) {
			SlotSymbol a = grid[line[0][0]][line[0][1]];
			SlotSymbol b = grid[line[1][0]][line[1][1]];
			SlotSymbol c = grid[line[2][0]][line[2][1]];
			if (a == b && b == c) {
				chipsPayout += a.multiplier * bet;
				if (a == SlotSymbol.SEVEN) {
					talismansPayout += 1;
				}
			}
		}
		return new SlotSpinResult(grid, chipsPayout, talismansPayout);
	}

	private static SlotSymbol rollSymbol(RandomSource random) {
		int roll = random.nextInt(SlotSymbol.TOTAL_WEIGHT);
		int acc = 0;
		for (SlotSymbol s : SlotSymbol.VALUES) {
			acc += s.weight;
			if (roll < acc) return s;
		}
		return SlotSymbol.COAL;
	}
}
