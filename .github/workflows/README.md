# .github/workflows/

GitHub Actions workflows for CI and release automation.

## `build.yml`

- **Trigger:** push and pull requests targeting the `stonecutter` branch.
- **What it does:** builds every supported Minecraft version in parallel via a matrix (`1.21.2` – `1.21.8`), using `./gradlew :<mc>:build`. Uploads each version's jar as a separate artifact.
- **Purpose:** every commit and PR is validated across all supported versions before it is merged.

## `release.yml`

- **Trigger:** pushing a git tag matching `v*` (for example, `v1.0.0`). Also available as a manual dispatch for dry-runs.
- **What it does:**
  1. Builds every supported Minecraft version.
  2. Downloads the produced jars and creates a **GitHub Release** with them attached.
  3. Publishes every jar to **Modrinth** via [`Kir-Antipov/mc-publish`](https://github.com/Kir-Antipov/mc-publish), using the `MODRINTH_TOKEN` repository secret.
- **Purpose:** one `git tag && git push` cuts a full multi-version release across GitHub and Modrinth.

## Secrets used

- **`MODRINTH_TOKEN`** — a Modrinth Personal Access Token with the `Create versions` scope. Set under Settings → Secrets and variables → Actions.

The `GITHUB_TOKEN` used for creating the GitHub Release is provided automatically by Actions.
