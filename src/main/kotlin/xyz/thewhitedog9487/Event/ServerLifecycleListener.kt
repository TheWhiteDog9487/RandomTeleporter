package xyz.thewhitedog9487.Event

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents
import net.minecraft.server.MinecraftServer
import xyz.thewhitedog9487.ModLogger
import xyz.thewhitedog9487.OldPositions

var MinecraftServerInstanceBackendField: MinecraftServer? = null
val MinecraftServerInstance get() =
    MinecraftServerInstanceBackendField ?: throw NullPointerException("当前服务器实例不可用")

fun ServerLifecycleListenerRegister() {
    ServerLifecycleEvents.SERVER_STARTED.register { MinecraftServerInstance ->
        MinecraftServerInstanceBackendField = MinecraftServerInstance
        OldPositions.clear()
        ModLogger.info("已清空传送历史记录") }
ServerLifecycleEvents.SERVER_STOPPED.register { MinecraftServerInstance ->
    MinecraftServerInstanceBackendField = null } }