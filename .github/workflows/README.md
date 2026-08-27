# .github/workflows/

GitHub Actions workflows for the `main` branch.

## `build.yml`

- **Trigger:** push and pull requests targeting `main`.
- **What it does:** installs JDK 21 (Temurin), runs `./gradlew build`, and uploads the produced jars from `build/libs/` as an artifact.
- **Purpose:** validates that every commit and PR to `main` compiles cleanly.

> The multi-version build matrix and the tagged-release workflow live on the [`stonecutter`](https://github.com/paketrool/casinocraft/tree/stonecutter/.github/workflows) branch, together with the code that actually supports multiple Minecraft versions.
