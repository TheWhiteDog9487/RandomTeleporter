package xyz.thewhitedog9487

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents

fun ServerLifecycleListenerRegister() {
    ServerLifecycleEvents.SERVER_STARTED.register { server ->
        OldPositions.clear()
        ModLogger.info("已清空传送历史记录") } }