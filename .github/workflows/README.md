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

## Secrets and variables used

- **`MODRINTH_TOKEN`** (secret) — a Modrinth Personal Access Token with the `Create versions` scope.
- **`MODRINTH_ID`** (repository variable, not secret) — the Modrinth project slug or id (e.g. `N7SalUTv`).
- **`CURSEFORGE_TOKEN`** (secret) — a CurseForge API token, created at https://legacy.curseforge.com/account/api-tokens or in the new Authors panel. Needed only after the CurseForge project has passed initial moderation.
- **`CURSEFORGE_ID`** (repository variable, not secret) — the CurseForge project id (visible in the project URL once approved). Set under Settings → Secrets and variables → Actions → Variables.

Both CurseForge- and Modrinth-publishing steps skip themselves if their respective credentials are missing, so partial setup is safe.

The `GITHUB_TOKEN` used for creating the GitHub Release is provided automatically by Actions.
