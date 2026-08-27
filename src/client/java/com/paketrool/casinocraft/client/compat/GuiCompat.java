package com.paketrool.casinocraft.client.compat;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;

/**
 * Обёртка над GuiGraphics.blit. В 1.21.6 сменили сигнатуру:
 * RenderType::guiTextured → RenderPipelines.GUI_TEXTURED.
 */
public final class GuiCompat {
	private GuiCompat() {}

	public static void blitTextured(GuiGraphics g, ResourceLocation tex,
	                                int x, int y, float u, float v,
	                                int w, int h, int tw, int th) {
		//? if >=1.21.6 {
		/*g.blit(net.minecraft.client.renderer.RenderPipelines.GUI_TEXTURED, tex, x, y, u, v, w, h, tw, th);*/
		//?} else
		g.blit(net.minecraft.client.renderer.RenderType::guiTextured, tex, x, y, u, v, w, h, tw, th);
	}
}
