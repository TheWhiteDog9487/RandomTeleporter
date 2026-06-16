package xyz.thewhitedog9487

import net.fabricmc.api.ModInitializer
import org.slf4j.Logger
import org.slf4j.LoggerFactory

const val ModID = "randomteleporter"
const val FriendlyModID = "RandomTeleporter"
val ModLogger: Logger = LoggerFactory.getLogger(FriendlyModID)

object RandomTeleporter : ModInitializer {

	override fun onInitialize() {
		// This code runs as soon as Minecraft is in a mod-load-ready state.
		// However, some things (like resources) may still be uninitialized.
		// Proceed with mild caution.

		// This code runs as soon as Minecraft is in a mod-load-ready state.
		// However, some things (like resources) may still be uninitialized.
		// Proceed with mild caution.

		ServerLifecycleListenerRegister()
		ResourceReloaderListenerRegister()
		CommandRegister()
		ModLogger.info("RandomTeleporter已写入命令注册回调，目标命令将会在该注册的时候被注册")	}
}