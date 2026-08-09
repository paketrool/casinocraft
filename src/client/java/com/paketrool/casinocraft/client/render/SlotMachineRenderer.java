package com.paketrool.casinocraft.client.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.paketrool.casinocraft.Casinocraft;
import com.paketrool.casinocraft.block.entity.SlotMachineBlockEntity;
import com.paketrool.casinocraft.slot.SlotSymbol;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix4f;

import static com.paketrool.casinocraft.block.SlotMachineBlock.HALF;

public class SlotMachineRenderer implements BlockEntityRenderer<SlotMachineBlockEntity> {

	private static final ResourceLocation[] SYMBOL_TEX = {
		ResourceLocation.fromNamespaceAndPath(Casinocraft.MOD_ID, "textures/gui/symbol/coal.png"),
		ResourceLocation.fromNamespaceAndPath(Casinocraft.MOD_ID, "textures/gui/symbol/iron.png"),
		ResourceLocation.fromNamespaceAndPath(Casinocraft.MOD_ID, "textures/gui/symbol/gold.png"),
		ResourceLocation.fromNamespaceAndPath(Casinocraft.MOD_ID, "textures/gui/symbol/lapis.png"),
		ResourceLocation.fromNamespaceAndPath(Casinocraft.MOD_ID, "textures/gui/symbol/diamond.png"),
		ResourceLocation.fromNamespaceAndPath(Casinocraft.MOD_ID, "textures/gui/symbol/seven.png")
	};

	private static final int SYMBOL_CYCLE_TICKS = 2;
	private static final int FULL_BRIGHT = LightTexture.pack(15, 15);

	public SlotMachineRenderer(BlockEntityRendererProvider.Context ctx) {}

	@Override
	public void render(SlotMachineBlockEntity be, float partialTick, PoseStack ps, MultiBufferSource buffer, int light, int overlay, Vec3 cameraPos) {
		if (be.getLevel() == null) return;
		BlockState state = be.getBlockState();
		if (state.getValue(HALF) != DoubleBlockHalf.LOWER) return;

		Direction facing = state.getValue(HorizontalDirectionalBlock.FACING);
		boolean spinning = be.isSpinning();
		long time = be.getLevel().getGameTime();

		float rot = switch (facing) {
			case NORTH -> 0f;
			case EAST  -> -90f;
			case SOUTH -> 180f;
			case WEST  -> 90f;
			default    -> 0f;
		};

		ps.pushPose();
		ps.translate(0.5, 0.0, 0.5);
		ps.mulPose(com.mojang.math.Axis.YP.rotationDegrees(rot));
		ps.translate(-0.5, 0.0, -0.5);

		float cell = 4.0f / 16.0f;
		float gridLeft = 2.0f / 16.0f;
		float gridBottom = 16.0f / 16.0f;
		float frontZ = -0.01f;

		for (int col = 0; col < 3; col++) {
			for (int row = 0; row < 3; row++) {
				int ordinal;
				if (spinning) {
					long stepTime = (time - be.getSpinStartTime()) / SYMBOL_CYCLE_TICKS;
					ordinal = (int)((stepTime + col * 7L + row * 3L) % 6);
				} else {
					ordinal = be.getLastGridSymbol(col, row);
					if (ordinal < 0 || ordinal >= SlotSymbol.VALUES.length) ordinal = 0;
				}

				float x = gridLeft + col * cell;
				float y = gridBottom + (2 - row) * cell;

				drawSymbolQuad(ps, buffer, x, y, frontZ, cell, cell, SYMBOL_TEX[ordinal]);
			}
		}

		ps.popPose();
	}

	private static void drawSymbolQuad(PoseStack ps, MultiBufferSource buffer,
									   float x, float y, float z, float w, float h, ResourceLocation tex) {
		VertexConsumer vc = buffer.getBuffer(RenderType.entityCutoutNoCull(tex));
		Matrix4f m = ps.last().pose();

		vc.addVertex(m, x,     y,     z).setColor(255, 255, 255, 255).setUv(0f, 1f).setOverlay(OverlayTexture.NO_OVERLAY).setLight(FULL_BRIGHT).setNormal(0f, 0f, -1f);
		vc.addVertex(m, x + w, y,     z).setColor(255, 255, 255, 255).setUv(1f, 1f).setOverlay(OverlayTexture.NO_OVERLAY).setLight(FULL_BRIGHT).setNormal(0f, 0f, -1f);
		vc.addVertex(m, x + w, y + h, z).setColor(255, 255, 255, 255).setUv(1f, 0f).setOverlay(OverlayTexture.NO_OVERLAY).setLight(FULL_BRIGHT).setNormal(0f, 0f, -1f);
		vc.addVertex(m, x,     y + h, z).setColor(255, 255, 255, 255).setUv(0f, 0f).setOverlay(OverlayTexture.NO_OVERLAY).setLight(FULL_BRIGHT).setNormal(0f, 0f, -1f);
	}
}
