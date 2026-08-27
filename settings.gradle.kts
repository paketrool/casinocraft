pluginManagement {
	repositories {
		mavenCentral()
		gradlePluginPortal()
		maven("https://maven.fabricmc.net/") { name = "Fabric" }
		maven("https://maven.kikugie.dev/releases") { name = "KikuGie Releases" }
		maven("https://maven.kikugie.dev/snapshots") { name = "KikuGie Snapshots" }
	}
}

plugins {
	id("dev.kikugie.stonecutter") version "0.9.7"
	id("dev.kikugie.loom-back-compat") version "0.4.1"
}

stonecutter {
	kotlinController = true
	shared {
		versions(
			"1.21.2",
			"1.21.3",
			"1.21.4",
			"1.21.5",
			"1.21.6",
			"1.21.7",
			"1.21.8"
		)
		vcsVersion = "1.21.5"
	}
	create(rootProject)
}

rootProject.name = "casinocraft"
