package com.paketrool.casinocraft.attachment;

import com.mojang.serialization.Codec;
import com.paketrool.casinocraft.Casinocraft;
import net.fabricmc.fabric.api.attachment.v1.AttachmentRegistry;
import net.fabricmc.fabric.api.attachment.v1.AttachmentType;

public final class CasinocraftAttachments {
	private CasinocraftAttachments() {}

	public static final AttachmentType<Boolean> GUIDE_GIVEN =
		AttachmentRegistry.<Boolean>builder()
			.initializer(() -> false)
			.persistent(Codec.BOOL)
			.buildAndRegister(Casinocraft.id("guide_given"));

	public static void init() {
	}
}
