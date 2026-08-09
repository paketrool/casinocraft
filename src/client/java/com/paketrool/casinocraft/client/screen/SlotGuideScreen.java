package com.paketrool.casinocraft.client.screen;

import com.paketrool.casinocraft.Casinocraft;
import com.paketrool.casinocraft.slot.SlotSymbol;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

public class SlotGuideScreen extends Screen {

	private static final ResourceLocation[] SYMBOL_TEX = {
		ResourceLocation.fromNamespaceAndPath(Casinocraft.MOD_ID, "textures/gui/symbol/coal.png"),
		ResourceLocation.fromNamespaceAndPath(Casinocraft.MOD_ID, "textures/gui/symbol/iron.png"),
		ResourceLocation.fromNamespaceAndPath(Casinocraft.MOD_ID, "textures/gui/symbol/gold.png"),
		ResourceLocation.fromNamespaceAndPath(Casinocraft.MOD_ID, "textures/gui/symbol/lapis.png"),
		ResourceLocation.fromNamespaceAndPath(Casinocraft.MOD_ID, "textures/gui/symbol/diamond.png"),
		ResourceLocation.fromNamespaceAndPath(Casinocraft.MOD_ID, "textures/gui/symbol/seven.png")
	};

	private static final String[] SYMBOL_KEY = {
		"casinocraft.guide.symbol.coal",
		"casinocraft.guide.symbol.iron",
		"casinocraft.guide.symbol.gold",
		"casinocraft.guide.symbol.lapis",
		"casinocraft.guide.symbol.diamond",
		"casinocraft.guide.symbol.seven"
	};

	private static final int PANEL_BG = 0xF0201510;
	private static final int PANEL_BORDER = 0xFFE1B437;
	private static final int PANEL_BORDER_DARK = 0xFF966815;
	private static final int TEXT_LIGHT = 0xFFF0E6C0;
	private static final int TEXT_GOLD = 0xFFF7D848;
	private static final int TEXT_RED = 0xFFE84040;
	private static final int TEXT_DIM = 0xFFB89A6A;
	private static final int DIVIDER = 0xFF483014;

	private static final int PANEL_W = 240;
	private static final int PANEL_H = 220;

	public SlotGuideScreen() {
		super(Component.translatable("casinocraft.guide.title"));
	}

	@Override
	protected void init() {
		super.init();
		int px = (this.width - PANEL_W) / 2;
		int py = (this.height - PANEL_H) / 2;
		addRenderableWidget(Button.builder(CommonComponents.GUI_DONE, b -> onClose())
			.bounds(px + PANEL_W / 2 - 40, py + PANEL_H - 22, 80, 18)
			.build());
	}

	@Override
	public void render(GuiGraphics g, int mouseX, int mouseY, float partialTick) {
		super.render(g, mouseX, mouseY, partialTick);
		int px = (this.width - PANEL_W) / 2;
		int py = (this.height - PANEL_H) / 2;

		g.fill(px, py, px + PANEL_W, py + PANEL_H, PANEL_BG);
		g.fill(px - 1, py - 1, px + PANEL_W + 1, py, PANEL_BORDER);
		g.fill(px - 1, py + PANEL_H, px + PANEL_W + 1, py + PANEL_H + 1, PANEL_BORDER);
		g.fill(px - 1, py, px, py + PANEL_H, PANEL_BORDER);
		g.fill(px + PANEL_W, py, px + PANEL_W + 1, py + PANEL_H, PANEL_BORDER);

		g.drawCenteredString(this.font, Component.translatable("casinocraft.guide.title"), px + PANEL_W / 2, py + 8, TEXT_GOLD);
		g.drawCenteredString(this.font, Component.translatable("casinocraft.guide.subtitle"), px + PANEL_W / 2, py + 22, TEXT_DIM);

		int headerY = py + 40;
		g.fill(px + 8, headerY - 2, px + PANEL_W - 8, headerY - 1, DIVIDER);
		g.drawString(this.font, Component.translatable("casinocraft.guide.symbols_header"), px + 12, headerY, TEXT_LIGHT, false);

		int rowY = py + 56;
		int rowStep = 18;
		int iconX = px + 16;
		int nameX = px + 42;
		int multX = px + PANEL_W - 80;

		for (int i = 0; i < SlotSymbol.VALUES.length; i++) {
			SlotSymbol sym = SlotSymbol.VALUES[i];
			int y = rowY + i * rowStep;
			g.blit(net.minecraft.client.renderer.RenderType::guiTextured, SYMBOL_TEX[i], iconX, y - 2, 0f, 0f, 16, 16, 16, 16);
			g.drawString(this.font, Component.translatable(SYMBOL_KEY[i]), nameX, y + 2, sym == SlotSymbol.SEVEN ? TEXT_RED : TEXT_LIGHT, false);
			g.drawString(this.font, Component.translatable("casinocraft.guide.multiplier", sym.multiplier), multX, y + 2, TEXT_GOLD, false);
			if (sym == SlotSymbol.SEVEN) {
				g.drawString(this.font, Component.translatable("casinocraft.guide.jackpot"), multX + 30, y + 2, TEXT_RED, false);
			}
		}

		int notesY = rowY + SlotSymbol.VALUES.length * rowStep + 8;
		g.fill(px + 8, notesY - 2, px + PANEL_W - 8, notesY - 1, DIVIDER);
		g.drawString(this.font, Component.translatable("casinocraft.guide.bet_note"), px + 12, notesY + 2, TEXT_DIM, false);
		g.drawString(this.font, Component.translatable("casinocraft.guide.drop_note"), px + 12, notesY + 14, TEXT_DIM, false);
	}

	@Override
	public boolean isPauseScreen() {
		return false;
	}
}
