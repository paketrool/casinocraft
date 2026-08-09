package com.paketrool.casinocraft.client.screen;

import com.paketrool.casinocraft.Casinocraft;
import com.paketrool.casinocraft.menu.SlotMachineMenu;
import com.paketrool.casinocraft.slot.SlotSymbol;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

public class SlotMachineScreen extends AbstractContainerScreen<SlotMachineMenu> {

	private static final ResourceLocation BG_TEXTURE =
		ResourceLocation.fromNamespaceAndPath(Casinocraft.MOD_ID, "textures/gui/slot_machine.png");

	private static final ResourceLocation[] SYMBOL_TEX = {
		ResourceLocation.fromNamespaceAndPath(Casinocraft.MOD_ID, "textures/gui/symbol/coal.png"),
		ResourceLocation.fromNamespaceAndPath(Casinocraft.MOD_ID, "textures/gui/symbol/iron.png"),
		ResourceLocation.fromNamespaceAndPath(Casinocraft.MOD_ID, "textures/gui/symbol/gold.png"),
		ResourceLocation.fromNamespaceAndPath(Casinocraft.MOD_ID, "textures/gui/symbol/lapis.png"),
		ResourceLocation.fromNamespaceAndPath(Casinocraft.MOD_ID, "textures/gui/symbol/diamond.png"),
		ResourceLocation.fromNamespaceAndPath(Casinocraft.MOD_ID, "textures/gui/symbol/seven.png")
	};

	private static final int GRID_X = 68;
	private static final int GRID_Y = 8;
	private static final int CELL = 24;
	private static final int SYMBOL_SIZE = 16;

	private static final int TEXT_LIGHT = 0xFFF0E6C0;

	public SlotMachineScreen(SlotMachineMenu menu, Inventory playerInventory, Component title) {
		super(menu, playerInventory, title);
		this.imageWidth = 176;
		this.imageHeight = 188;
		this.inventoryLabelY = 96;
		this.titleLabelY = -100;
	}

	@Override
	protected void init() {
		super.init();
		int cx = this.leftPos;
		int cy = this.topPos;

		addRenderableWidget(Button.builder(Component.literal("×1"), b -> click(SlotMachineMenu.BUTTON_BET_1))
			.bounds(cx + 8, cy + 18, 34, 18).build());
		addRenderableWidget(Button.builder(Component.literal("×5"), b -> click(SlotMachineMenu.BUTTON_BET_5))
			.bounds(cx + 8, cy + 38, 34, 18).build());
		addRenderableWidget(Button.builder(Component.literal("×10"), b -> click(SlotMachineMenu.BUTTON_BET_10))
			.bounds(cx + 8, cy + 58, 34, 18).build());

		addRenderableWidget(Button.builder(Component.literal("СПИН"), b -> click(SlotMachineMenu.BUTTON_SPIN))
			.bounds(cx + 88, cy + 82, 28, 28).build());
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
		g.blit(net.minecraft.client.renderer.RenderType::guiTextured, BG_TEXTURE, x, y, 0f, 0f, imageWidth, imageHeight, 256, 256);

		int spinLeft = menu.getSpinTicksLeft();
		for (int col = 0; col < 3; col++) {
			for (int row = 0; row < 3; row++) {
				int ordinal;
				if (spinLeft > 0) {
					int step = spinLeft / 2;
					ordinal = Math.floorMod(step + col * 7 + row * 3, SlotSymbol.VALUES.length);
				} else {
					ordinal = menu.getLastGridSymbol(col, row);
					if (ordinal < 0 || ordinal >= SlotSymbol.VALUES.length) ordinal = 0;
				}
				int sx = x + GRID_X + col * CELL + (CELL - SYMBOL_SIZE) / 2;
				int sy = y + GRID_Y + row * CELL + (CELL - SYMBOL_SIZE) / 2;
				g.blit(net.minecraft.client.renderer.RenderType::guiTextured, SYMBOL_TEX[ordinal], sx, sy, 0f, 0f, SYMBOL_SIZE, SYMBOL_SIZE, SYMBOL_SIZE, SYMBOL_SIZE);
			}
		}
	}

	@Override
	protected void renderLabels(GuiGraphics g, int mouseX, int mouseY) {
		g.drawString(this.font, this.title, 8, 6, TEXT_LIGHT, false);
		g.drawString(this.font, Component.translatable("container.inventory"), 8, this.inventoryLabelY, TEXT_LIGHT, false);
		g.drawString(this.font, Component.literal("×" + menu.getBet()), 44, 84, TEXT_LIGHT, false);
		g.drawString(this.font, Component.literal("П:" + menu.getChipsPool()), 8, 84, TEXT_LIGHT, false);
		int payout = menu.getLastPayout();
		if (payout > 0) {
			g.drawString(this.font, Component.literal("+" + payout), 128, 84, 0xFFF7D848, false);
		}
		int jackpot = menu.getLastJackpot();
		if (jackpot > 0) {
			g.drawString(this.font, Component.literal("ДЖЕКПОТ!"), 8, 94, 0xFFE84040, true);
		}
	}

	@Override
	public void render(GuiGraphics g, int mouseX, int mouseY, float partialTick) {
		super.render(g, mouseX, mouseY, partialTick);
		this.renderTooltip(g, mouseX, mouseY);
	}
}
