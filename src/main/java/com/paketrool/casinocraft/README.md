# com.paketrool.casinocraft

Common (server + client) Java code for the mod.

## Entry point

- **`Casinocraft.java`** — `ModInitializer` implementation. Registers items, blocks, block entities, menus, and event handlers.

## Packages

- **`block/`** — `SlotMachineBlock` (double-block-tall, facing) and `CasinocraftBlocks` registry.
  - **`block/entity/`** — `SlotMachineBlockEntity` (reel state, pool + bonus slot, jackpot logic, NBT save/load) and its type registry.
- **`item/`** — items (casino chip, talisman, guidebook) and their registry.
- **`menu/`** — server-side of the GUI: `AbstractContainerMenu` subclasses, slot layout, quick-move logic.
- **`slot/`** — pure gameplay logic, no Minecraft dependencies beyond `RandomSource`. Contains `SlotSymbol`, `SlotMachineLogic`, `SlotSpinResult`.
- **`enchantment/`** — custom "Croupier" enchantments. `CasinocraftEnchantments` exposes `ResourceKey<Enchantment>` constants and `pickForItem()` — the dispatcher that decides which enchantment lands on a given item on jackpot. `ChorusPortalData` stores the saved teleport point in a chorus fruit stack's `CustomData` component.
- **`event/`** — Fabric API event handlers (`UseItemCallback`, `ServerTickEvents`). Currently drives the torch auto-ignition passive.
- **`mixin/`** — [SpongePowered mixins](https://github.com/SpongePowered/Mixin) that override vanilla item behavior when the corresponding Croupier enchantment is present.
- **`compat/`** — Stonecutter compatibility shims for API differences across Minecraft versions.
