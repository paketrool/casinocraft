package com.paketrool.casinocraft.client.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import com.paketrool.casinocraft.menu.SlotMachineMenu;
import com.paketrool.casinocraft.slot.SlotSymbol;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;

public class SlotMachineScreen extends AbstractContainerScreen<SlotMachineMenu> {

	private static final int PANEL_COLOR = 0xFF2A1A0A;
	private static final int BORDER_COLOR = 0xFFD4AF37;
	private static final int SCREEN_BG = 0xFF15100A;
	private static final int CELL_BG = 0xFF201810;
	private static final int TEXT_LIGHT = 0xFFF0E6C0;

	private static final int GRID_SIZE = 3;
	private static final int CELL = 24;
	private static final int GRID_LEFT = 64;
	private static final int GRID_TOP = 20;

	private static final int[] SYMBOL_COLORS = {
		0xFF303030,
		0xFFB5B5B5,
		0xFFF7D848,
		0xFF3B77CF,
		0xFF6BE0FF,
		0xFFE84040
	};

	private Button btn1, btn5, btn10, btnSpin;

	public SlotMachineScreen(SlotMachineMenu menu, Inventory playerInventory, Component title) {
		super(menu, playerInventory, title);
		this.imageWidth = 176;
		this.imageHeight = 188;
		this.inventoryLabelY = 94;
	}

	@Override
	protected void init() {
		super.init();
		int cx = this.leftPos;
		int cy = this.topPos;

		btn1 = Button.builder(Component.literal("×1"), b -> click(SlotMachineMenu.BUTTON_BET_1))
			.bounds(cx + 8, cy + 20, 32, 16).build();
		btn5 = Button.builder(Component.literal("×5"), b -> click(SlotMachineMenu.BUTTON_BET_5))
			.bounds(cx + 8, cy + 40, 32, 16).build();
		btn10 = Button.builder(Component.literal("×10"), b -> click(SlotMachineMenu.BUTTON_BET_10))
			.bounds(cx + 8, cy + 60, 32, 16).build();
		btnSpin = Button.builder(Component.literal("КРУТИТЬ"), b -> click(SlotMachineMenu.BUTTON_SPIN))
			.bounds(cx + 72, cy + 88, 60, 16).build();

		addRenderableWidget(btn1);
		addRenderableWidget(btn5);
		addRenderableWidget(btn10);
		addRenderableWidget(btnSpin);
	}

	private void click(int id) {
		Minecraft mc = Minecraft.getInstance();
		if (mc.gameMode != null && mc.player != null) {
			mc.gameMode.handleInventoryButtonClick(menu.containerId, id);
		}
	}

	@Override
	protected void renderBg(GuiGraphics g, float partialTick, int mouseX, int mouseY) {
		int x = this.leftPos;
		int y = this.topPos;

		g.fill(x, y, x + imageWidth, y + imageHeight, PANEL_COLOR);
		g.fill(x, y, x + imageWidth, y + 1, BORDER_COLOR);
		g.fill(x, y + imageHeight - 1, x + imageWidth, y + imageHeight, BORDER_COLOR);
		g.fill(x, y, x + 1, y + imageHeight, BORDER_COLOR);
		g.fill(x + imageWidth - 1, y, x + imageWidth, y + imageHeight, BORDER_COLOR);

		int gLeft = x + GRID_LEFT;
		int gTop = y + GRID_TOP;
		int gRight = gLeft + GRID_SIZE * CELL + 4;
		int gBot = gTop + GRID_SIZE * CELL + 4;
		g.fill(gLeft - 2, gTop - 2, gRight, gBot, SCREEN_BG);
		g.fill(gLeft - 3, gTop - 3, gRight + 1, gTop - 2, BORDER_COLOR);
		g.fill(gLeft - 3, gBot, gRight + 1, gBot + 1, BORDER_COLOR);
		g.fill(gLeft - 3, gTop - 3, gLeft - 2, gBot + 1, BORDER_COLOR);
		g.fill(gRight, gTop - 3, gRight + 1, gBot + 1, BORDER_COLOR);

		for (int col = 0; col < GRID_SIZE; col++) {
			for (int row = 0; row < GRID_SIZE; row++) {
				int cellX = gLeft + col * CELL;
				int cellY = gTop + row * CELL;
				g.fill(cellX, cellY, cellX + CELL - 1, cellY + CELL - 1, CELL_BG);
				int ordinal = menu.getLastGridSymbol(col, row);
				SlotSymbol sym = SlotSymbol.VALUES[Math.min(ordinal, SlotSymbol.VALUES.length - 1)];
				int color = SYMBOL_COLORS[sym.ordinal()];
				g.fill(cellX + 6, cellY + 6, cellX + CELL - 7, cellY + CELL - 7, color);
				if (sym == SlotSymbol.SEVEN) {
					g.drawString(this.font, "7", cellX + CELL / 2 - 2, cellY + CELL / 2 - 3, 0xFFFFFFFF, true);
				}
			}
		}

		// slot for chips (single input)
		int slotX = x + 26;
		int slotY = y + 82;
		g.fill(slotX - 1, slotY - 1, slotX + 17, slotY + 17, BORDER_COLOR);
		g.fill(slotX, slotY, slotX + 16, slotY + 16, CELL_BG);
	}

	@Override
	protected void renderLabels(GuiGraphics g, int mouseX, int mouseY) {
		g.drawString(this.font, this.title, 8, 6, TEXT_LIGHT, false);
		g.drawString(this.font, Component.translatable("container.inventory"), 8, this.inventoryLabelY, TEXT_LIGHT, false);
		g.drawString(this.font, Component.literal("Ставка: ×" + menu.getBet()), 44, 22, TEXT_LIGHT, false);
		g.drawString(this.font, Component.literal("Пул: " + menu.getChipsPool()), 44, 82, TEXT_LIGHT, false);
		int payout = menu.getLastPayout();
		if (payout > 0) {
			g.drawString(this.font, Component.literal("Выигрыш: +" + payout), 76, 108, 0xFFF7D848, false);
		}
		int jackpot = menu.getLastJackpot();
		if (jackpot > 0) {
			g.drawString(this.font, Component.literal("ДЖЕКПОТ! +" + jackpot + " талисман"), 50, 118, 0xFFE84040, true);
		}
	}

	@Override
	public void render(GuiGraphics g, int mouseX, int mouseY, float partialTick) {
		super.render(g, mouseX, mouseY, partialTick);
		this.renderTooltip(g, mouseX, mouseY);
	}
}
