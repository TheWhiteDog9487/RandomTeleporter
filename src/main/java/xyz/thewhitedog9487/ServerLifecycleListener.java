package xyz.thewhitedog9487;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;

public class ServerLifecycleListener {
    public static void Register() {
        ServerLifecycleEvents.SERVER_STARTED.register(server -> {
            CommandRegister.OldPositions.clear();
            RandomTeleporter.LOGGER.info("已清空传送历史记录"); } ); } }