# Casino Royale MC

Fabric-мод для Minecraft, добавляющий игровое казино: слот-машина 3×3 с 8 линиями, ставки казино-фишками, джекпот с кастомными зачарованиями «Крупье» на предметы.

**Modrinth:** https://modrinth.com/mod/casino-royale-mc

## Возможности

- **Слот-машина** — двух-блочный автомат с 3D-моделью, анимация барабанов, звуки, лампы
- **Ставки** — фишки казино как валюта, ×1 и ×10
- **6 символов** — уголь / железо / золото / лазурит / алмаз / **семёрка** (джекпот ×50)
- **Бонус-слот** — положи туда предмет и на джекпоте получишь **кастомное зачарование «Крупье»**:
  - Меч / инструмент / броня — своё зачарование под тип
  - **Эндер-перл** → бесконечные броски
  - **Хорус** → сохранить точку (Sneak+ПКМ) и телепорт туда (ПКМ)
  - **Факел** → авто-поджог горючих блоков рядом (землю не жжёт)
  - **Снежок** → бесконечные броски + 30% шанс заморозить цель
- **Гайд-книга** — открывается при взаимодействии, объясняет символы и правила

## Стек

- Minecraft **1.21.2 — 1.21.8** (сборка через [Stonecutter](https://stonecutter.kikugie.dev/))
- Fabric Loader 0.19.3+
- Fabric API
- Java 21

## Сборка

```bash
# Собрать все версии сразу
./gradlew build

# Собрать конкретную версию
./gradlew :1.21.5:build

# Запустить клиент разработки
./gradlew :1.21.5:runClient
```

Готовые jar-ы — в `versions/<v>/build/libs/`.

## Ветки

- **main** — стабильный релиз
- **stonecutter** — мультиверсионная сборка (актуальная разработка)

## Лицензия

CC0-1.0 — свободно используй, форки и дистрибутивы разрешены.

## Багрепорты и предложения

Через [Issues](https://github.com/paketrool/casinocraft/issues) — есть шаблоны для багов и фич.

---

## Project structure (English)

Top-level layout of the repository.

```
casinocraft/
├── .github/
│   ├── workflows/            # GitHub Actions CI: build matrix + tagged release
│   │   ├── build.yml         # Per-push / per-PR build across all supported MC versions
│   │   └── release.yml       # On tag v* — builds all versions, publishes to GitHub Releases and Modrinth
│   ├── ISSUE_TEMPLATE/       # Bug report / feature request templates and config
│   └── dependabot.yml        # Weekly Gradle and Actions dependency updates
│
├── build.gradle.kts          # Root build script (Kotlin DSL) — used by every Stonecutter subproject
├── settings.gradle.kts       # Registers Stonecutter and Fabric Loom back-compat plugins
├── stonecutter.gradle.kts    # Stonecutter multi-version configuration (declares MC versions)
├── gradle.properties         # Shared Gradle properties (mod version, group, loader version, defaults)
├── gradlew / gradlew.bat     # Gradle wrapper scripts
├── LICENSE                   # CC0-1.0 license text
├── README.md                 # This file
│
├── versions/                 # Per-Minecraft-version subprojects created by Stonecutter
│   ├── 1.21.2/gradle.properties   # Override fabric_api_version and minecraft_dependency for 1.21.2
│   ├── 1.21.3/gradle.properties
│   ├── 1.21.4/gradle.properties
│   ├── 1.21.5/gradle.properties
│   ├── 1.21.6/gradle.properties
│   ├── 1.21.7/gradle.properties
│   └── 1.21.8/gradle.properties
│
└── src/
    ├── main/                                          # Common (server + client) code and resources
    │   ├── java/com/paketrool/casinocraft/
    │   │   ├── Casinocraft.java                       # Fabric mod entrypoint — registers items, blocks, menus, events
    │   │   │
    │   │   ├── block/                                 # Blocks
    │   │   │   ├── SlotMachineBlock.java              # Two-block-tall slot machine (double block half, facing)
    │   │   │   ├── CasinocraftBlocks.java             # Block registry
    │   │   │   └── entity/                            # BlockEntities
    │   │   │       ├── SlotMachineBlockEntity.java    # Reel state, pool + bonus slot, jackpot logic, NBT save/load
    │   │   │       └── CasinocraftBlockEntities.java  # BlockEntityType registry
    │   │   │
    │   │   ├── item/                                  # Items (casino chip, talisman, guidebook) + registry
    │   │   ├── menu/                                  # Screen-handler (server side of GUI): slots and quick-move
    │   │   │
    │   │   ├── slot/                                  # Pure game logic (no MC deps beyond RandomSource)
    │   │   │   ├── SlotSymbol.java                    # Enum: coal / iron / gold / lapis / diamond / seven + weights
    │   │   │   ├── SlotMachineLogic.java              # Roll grid, evaluate 8 winning lines, jackpot detection
    │   │   │   └── SlotSpinResult.java                # DTO returned from a spin (grid + payouts)
    │   │   │
    │   │   ├── enchantment/                           # Custom "Croupier" enchantments
    │   │   │   ├── CasinocraftEnchantments.java       # ResourceKeys for all 7 enchants + pickForItem() dispatcher
    │   │   │   └── ChorusPortalData.java              # Persistent saved position stored in the chorus fruit ItemStack
    │   │   │
    │   │   ├── event/                                 # Fabric API event handlers (server-side ticks, item use)
    │   │   │   └── CasinocraftEvents.java             # Torch auto-ignition of flammable blocks; other passives
    │   │   │
    │   │   ├── mixin/                                 # SpongePowered mixins — behavior overrides for vanilla items
    │   │   │   ├── EnderpearlItemMixin.java           # Infinite ender-pearl throws when enchanted
    │   │   │   ├── SnowballItemMixin.java             # Infinite snowballs when enchanted
    │   │   │   ├── SnowballMixin.java                 # 30% freeze-on-hit chance
    │   │   │   └── TeleportRandomlyConsumeEffectMixin.java  # Chorus fruit — teleport to saved point instead of random
    │   │   │
    │   │   └── compat/                                # Stonecutter compatibility helpers
    │   │       └── NbtCompat.java                     # getIntOr / getLongOr / getStringOr shims for the NBT API change in 1.21.5+
    │   │
    │   └── resources/
    │       ├── fabric.mod.json                        # Fabric mod manifest; placeholders expanded at build time
    │       ├── casinocraft.mixins.json                # Common mixin config (guarded per-version via //? if wraps)
    │       ├── casinocraft.client.mixins.json         # Client-only mixin config
    │       ├── assets/casinocraft/                    # Client assets — visible/audible content
    │       │   ├── lang/                              # Localization JSONs (ru_ru, en_us, ...)
    │       │   ├── models/                            # Block and item models
    │       │   ├── textures/                          # Block, item, and GUI textures
    │       │   ├── sounds/, sounds.json               # Slot machine SFX, jackpot chime
    │       │   ├── blockstates/                       # Block state → model mappings
    │       │   └── icon.png                           # Mod icon shown in the mod list
    │       │
    │       └── data/casinocraft/                      # Server data — datapack-style content
    │           ├── enchantment/                       # Data-driven "Croupier" enchantments (JSON)
    │           ├── recipe/                            # Crafting recipes
    │           ├── advancement/                       # Advancement definitions
    │           ├── loot_table/                        # Loot tables for blocks and drops
    │           └── tags/                              # Item and block tags
    │
    └── client/                                        # Client-only source set (split by Loom)
        └── java/com/paketrool/casinocraft/client/
            ├── CasinocraftClient.java                 # ClientModInitializer — registers screens and renderers
            ├── screen/                                # GUI screens (client-side of Menus)
            │   ├── SlotMachineScreen.java             # Slot machine window: 3×3 grid, bet buttons, pool + bonus slots
            │   └── SlotGuideScreen.java               # In-book guide: symbols, multipliers, rules
            ├── render/                                # BlockEntityRenderer
            │   └── SlotMachineRenderer.java           # Draws animated 3D reels on the machine's front face
            └── compat/                                # Client-side stonecutter compatibility shims
                └── GuiCompat.java                     # blitTextured wrap for the RenderType → RenderPipelines change in 1.21.6+
```

### Notes

- Files under `src/**` may contain `//? if <MC>` comment directives — those are [Stonecutter](https://stonecutter.kikugie.dev/) preprocessor blocks that toggle code per Minecraft version.
- Per-version `gradle.properties` files override the defaults from the root `gradle.properties` (Fabric API version and Minecraft dependency string).
- All 7 `versions/<mc>/build/libs/*.jar` outputs are produced by a single `./gradlew build` run.

