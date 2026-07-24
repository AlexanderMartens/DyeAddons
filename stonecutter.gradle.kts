plugins {
	id("dev.kikugie.stonecutter")
}

stonecutter active "26.1.2"

// See https://stonecutter.kikugie.dev/wiki/config/params
// Version-specific `//? if` blocks get added here (and inline in source) as real
// API differences surface while porting 1.21.11 and 26.2 - see stonecutter.properties.toml.
stonecutter parameters {
	swaps["mod_version"] = "\"${property("mod.version")}\""
	swaps["minecraft"] = "\"${node.metadata.version}\""
	// Combined - chaining two separate swap markers in one expression corrupts everything
	// between them, so anywhere both are needed together (e.g. an annotation argument) uses this.
	swaps["mod_full_version"] = "\"${property("mod.version")}+${node.metadata.version}\""

	replacements {
		// Pre-26.1 has no GuiGraphicsExtractor class, and AbstractWidget/Screen/HudElement
		// still use their old immediate-render method names. Rewrite both mechanically instead
		// of hand-editing every override across ~20 GUI files.
		string(current.parsed < "26.1") {
			replace(
				"import net.minecraft.client.gui.GuiGraphicsExtractor",
				"import anlg.dyeaddons.utils.extensions.GuiGraphicsExtractor"
			)
			replace("extractWidgetRenderState", "renderWidget")
			// NOTE: HudElement's extractRenderState/render is handled per-file with `//? if`
			// (gui/overlay/*.kt) instead of a global replace - "render" is too generic a
			// substring and corrupts unrelated text like "rendering" package segments.
			replace("addClientSystemMessage", "addMessage")
			replace(".centeredText(", ".drawCenteredString(")
			replace(".textWithWordWrap(", ".drawWordWrap(")
			replace(".outline(", ".renderOutline(")
			replace(".text(", ".drawString(")

			// Fabric API renamed these; both class and constant/method name changed.
			replace("ClientLevelEvents", "ClientWorldEvents")
			replace("AFTER_CLIENT_LEVEL_CHANGE", "AFTER_CLIENT_WORLD_CHANGE")
			replace("ClientCommands", "ClientCommandManager")

			// Vanilla renamed the container-click parameter type.
			replace("ContainerInput", "ClickType")
		}
	}
}
