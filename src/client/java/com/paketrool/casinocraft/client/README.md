# com.paketrool.casinocraft.client

Client-only Java code. This entire source set is stripped from server jars.

## Entry point

- **`CasinocraftClient.java`** — `ClientModInitializer`. Binds `Screen` classes to `MenuType`s and registers the block-entity renderer.

## Packages

- **`screen/`** — GUI screens (client side of `AbstractContainerMenu`s).
  - `SlotMachineScreen` — the slot machine window: 3×3 grid, bet buttons (×1 / ×10), pool slot, bonus slot, spin button.
  - `SlotGuideScreen` — the in-book guide showing symbols, multipliers, and rules.
- **`render/`** — `BlockEntityRenderer` implementations.
  - `SlotMachineRenderer` — draws the animated 3D reels on the machine's front face, oriented by block-state facing.
- **`compat/`** — client-side Stonecutter compatibility shims.
  - `GuiCompat.blitTextured` — thin wrap over the `RenderType` → `RenderPipelines` GUI-texture API change in 1.21.6+.
