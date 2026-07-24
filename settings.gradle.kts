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
	// Manages the multi-version subprojects. See https://stonecutter.kikugie.dev/wiki/start/
	id("dev.kikugie.stonecutter") version "0.9.7"

	// Lets the same source target both Yarn-mapped (pre-26.1) and Mojang-mapped (26.1+) Minecraft.
	// https://codeberg.org/KikuGie/loom-back-compat
	id("dev.kikugie.loom-back-compat") version "0.4.1"

	// Resolves a matching JDK toolchain automatically.
	id("org.gradle.toolchains.foojay-resolver-convention") version "1.0.0"
}

stonecutter {
	create(rootProject) {
		versions("1.21.11", "26.1.2", "26.2")
		vcsVersion = "26.1.2"
	}
}

rootProject.name = "dyeaddons"
