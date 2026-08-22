plugins {
	id("dev.kikugie.stonecutter")
}

stonecutter active "26.2"

// See https://stonecutter.kikugie.dev/wiki/config/params
// Version-specific `//? if` blocks get added here (and inline in source) as real
// API differences surface while porting 26.2 - see stonecutter.properties.toml.
stonecutter parameters {
	swaps["mod_version"] = "\"${property("mod.version")}\""
	swaps["minecraft"] = "\"${node.metadata.version}\""
	// Combined - chaining two separate swap markers in one expression corrupts everything
	// between them, so anywhere both are needed together (e.g. an annotation argument) uses this.
	swaps["mod_full_version"] = "\"${property("mod.version")}+${node.metadata.version}\""
}
