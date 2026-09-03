plugins {
	id("fabric-loom") version "1.17.20"
	`maven-publish`
}

val modVersion: String = project.property("mod_version") as String
val mavenGroup: String = project.property("maven_group") as String
val minecraftVersion: String = stonecutter.current.version
val loaderVersion: String = project.property("loader_version") as String
val fabricApiVersion: String = project.property("fabric_api_version") as String

version = "$modVersion+$minecraftVersion"
group = mavenGroup

base {
	archivesName.set("casinocraft")
}

loom {
	splitEnvironmentSourceSets()

	mods {
		create("casinocraft") {
			sourceSet(sourceSets.main.get())
			sourceSet(sourceSets["client"])
		}
	}
}

repositories {
	mavenCentral()
	maven("https://maven.fabricmc.net/") { name = "Fabric" }
}

dependencies {
	minecraft("com.mojang:minecraft:$minecraftVersion")
	mappings(loom.officialMojangMappings())
	modImplementation("net.fabricmc:fabric-loader:$loaderVersion")
	modImplementation("net.fabricmc.fabric-api:fabric-api:$fabricApiVersion")
}

tasks.processResources {
	val version = project.version.toString()
	inputs.property("version", version)
	filesMatching("fabric.mod.json") {
		expand(mapOf("version" to version))
	}
}

tasks.withType<JavaCompile>().configureEach {
	options.release.set(21)
}

java {
	withSourcesJar()
	sourceCompatibility = JavaVersion.VERSION_21
	targetCompatibility = JavaVersion.VERSION_21
}

tasks.jar {
	from("LICENSE") {
		rename { "${it}_${base.archivesName.get()}" }
	}
}

publishing {
	publications {
		create<MavenPublication>("mavenJava") {
			from(components["java"])
		}
	}
}
