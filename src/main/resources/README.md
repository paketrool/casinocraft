# resources/

Non-code files packaged into the mod jar.

## Top-level files

- **`fabric.mod.json`** — Fabric mod manifest. Declares mod id, entry points, mixin configs, and dependencies. `${version}` and `${minecraft_dependency}` are expanded at build time by `processResources` from per-version `gradle.properties`.
- **`casinocraft.mixins.json`** — common mixin config. Individual mixin classes are guarded per Minecraft version via Stonecutter `//? if` wraps.
- **`casinocraft.client.mixins.json`** — client-only mixin config.

## `assets/casinocraft/`

Client-facing content — anything the player sees, hears, or reads.

- **`lang/`** — localization JSONs (`en_us.json`, `ru_ru.json`, ...).
- **`models/`** — block and item models (JSON, Blockbench-exported).
- **`textures/`** — PNG textures for blocks, items, and GUI elements.
- **`blockstates/`** — mappings from block states to models.
- **`items/`** — item model overrides.
- **`icon.png`** — the mod icon shown in the mod list.
- **`sounds.json` + `sounds/`** (when present) — SFX definitions and audio files.

## `data/casinocraft/`

Server-side data, structured like a datapack. Loaded via the vanilla resource loader.

- **`enchantment/`** — data-driven "Croupier" enchantment definitions (one JSON per enchantment).
- **`recipe/`** — crafting recipes.
- **`advancement/`** — advancement definitions.
- **`loot_table/`** — loot tables for blocks and drops.
- **`tags/`** (when present) — item and block tags.
