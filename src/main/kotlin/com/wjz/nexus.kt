package com.wjz

import net.fabricmc.api.ModInitializer
import org.apache.logging.log4j.LogManager
object Nexus : ModInitializer {
    private val logger = LogManager.getLogger("wjz-nexus")
	override fun onInitialize() {
		// This code runs as soon as Minecraft is in a mod-load-ready state.
		// However, some things (like resources) may still be uninitialized.
		// Proceed with mild caution.
		logger.info("Hello Fabric world!")
	}
}