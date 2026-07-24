import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
	// Applies the fabric-loom variant matching the active Minecraft version (Yarn pre-26.1, Mojang 26.1+).
	id("dev.kikugie.loom-back-compat")
	`maven-publish`
	id("org.jetbrains.kotlin.jvm") version "2.3.21"
}

fun prop(key: String): String = sc.properties[key] as String

// DO NOT set group = ... - stonecutter applies it from stonecutter.properties.toml's mod.group.
version = "${prop("mod.version")}+${sc.current.version}"
base.archivesName = prop("mod.id")

val requiredJava: JavaVersion = JavaVersion.toVersion(prop("deps.java"))

repositories {
	maven("https://maven.fabricmc.net")
	maven("https://pkgs.dev.azure.com/djtheredstoner/DevAuth/_packaging/public/maven/v1")
	maven("https://maven.teamresourceful.com/repository/maven-public/")
	maven("https://maven.terraformersmc.com/")
}

dependencies {
	minecraft("com.mojang:minecraft:${sc.current.version}")
	// Applies Mojang mappings on every version, including Yarn-mapped ones (via loom-back-compat).
	loomx.applyMojangMappings()

	// Use `mod*` configurations even on 26.1+ - loom-back-compat converts them as needed.
	modImplementation("net.fabricmc:fabric-loader:${prop("deps.fabric_loader")}")
	modImplementation("net.fabricmc.fabric-api:fabric-api:${prop("deps.fabric_api")}")
	modImplementation("net.fabricmc:fabric-language-kotlin:${prop("deps.fabric_kotlin")}")

	// Modmenu
	modCompileOnly("com.terraformersmc:modmenu:${prop("deps.modmenu")}")

	// Resourceful Config (artifact id carries the Minecraft version, see stonecutter.properties.toml)
	modImplementation("com.teamresourceful.resourcefulconfig:${prop("deps.resourcefulconfig_artifact")}:${prop("deps.resourcefulconfig")}")
	// No resourcefulconfigkt build exists for 26.2 yet (verified against maven.teamresourceful.com);
	// that branch uses resourcefulconfig's plain Java annotation API instead - see Settings.kt.
	if (sc.current.parsed < "26.2") {
		modImplementation("com.teamresourceful.resourcefulconfigkt:${prop("deps.resourcefulconfigkt_artifact")}:${prop("deps.resourcefulconfigkt")}")
	}

	// DevAuth
	modRuntimeOnly("me.djtheredstoner:DevAuth-fabric:${prop("deps.devauth")}")
}

loom {
	fabricModJsonPath = rootProject.file("src/main/resources/fabric.mod.json")

	runConfigs.all {
		preferGradleTask = true
		generateRunConfig = true
		runDirectory = rootProject.file("run") // Shared between all versions.
	}
}

tasks.withType<JavaCompile>().configureEach {
	options.release = requiredJava.majorVersion.toInt()
}

kotlin {
	compilerOptions {
		jvmTarget = JvmTarget.fromTarget(requiredJava.majorVersion)
	}
}

java {
	// Loom will automatically attach sourcesJar to a RemapSourcesJar task and to the "build" task
	// if it is present.
	withSourcesJar()

	sourceCompatibility = requiredJava
	targetCompatibility = requiredJava
}

tasks.processResources {
	val props = mapOf(
		"id" to prop("mod.id"),
		"name" to prop("mod.name"),
		"version" to version.toString(),
		"minecraft" to prop("mod.mc_compat"),
	)
	inputs.properties(props)

	filesMatching("fabric.mod.json") { expand(props) }

	val mixinJava = "JAVA_${requiredJava.majorVersion}"
	inputs.property("mixinJava", mixinJava)
	filesMatching("*.mixins.json") { expand("java" to mixinJava) }
}

tasks.jar {
	val projectName = prop("mod.id")
	inputs.property("projectName", projectName)

	from(rootProject.file("LICENSE")) {
		rename { "${it}_$projectName" }
	}
}

tasks.register<Copy>("buildAndCollect") {
	group = "build"
	description = "Builds mod jars for the active version and copies them to build/libs/{mc version}/"

	inputs.property("version", sc.current.version)
	from(loomx.modJar.flatMap { it.archiveFile }, loomx.modSourcesJar.flatMap { it.archiveFile })
	into(rootProject.layout.buildDirectory.file("libs/${sc.current.version}"))
}

// configure the maven publication
publishing {
	publications {
		register<MavenPublication>("mavenJava") {
			from(components["java"])
		}
	}

	// See https://docs.gradle.org/current/userguide/publishing_maven.html for information on how to set up publishing.
	repositories {
		// Add repositories to publish to here.
	}
}
