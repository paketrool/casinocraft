# src/

Source root — split into two sets by Fabric Loom's `splitEnvironmentSourceSets()`.

## Layout

- **`main/`** — common code and resources, loaded on both server and client.
  - `java/` — Java sources (blocks, items, menus, mixins, game logic).
  - `resources/` — `fabric.mod.json`, mixin configs, assets, and datapack-style server data.
- **`client/`** — client-only code (screens, block-entity renderers). Never loaded on a dedicated server.

## Stonecutter directives

Files under `main/` and `client/` may contain [Stonecutter](https://stonecutter.kikugie.dev/) preprocessor comments such as `//? if >=1.21.6`. They toggle blocks of code depending on the Minecraft version being built, so a single source tree can produce every jar in `versions/*/build/libs/`.
