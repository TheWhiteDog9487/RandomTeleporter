package xyz.thewhitedog9487.Event

import net.fabricmc.fabric.api.resource.v1.ResourceLoader
import net.fabricmc.fabric.api.resource.v1.reloader.SimpleReloadListener
import net.minecraft.resources.Identifier
import net.minecraft.server.packs.PackType
import net.minecraft.server.packs.resources.PreparableReloadListener
import xyz.thewhitedog9487.CommandNodes
import xyz.thewhitedog9487.CommandRegister
import xyz.thewhitedog9487.ModID

fun ResourceReloaderListenerRegister() {
    ResourceLoader.get(PackType.CLIENT_RESOURCES).registerReloadListener(
        Identifier.fromNamespaceAndPath(
            ModID,
            "translate_text_apply"),
        object: SimpleReloadListener<Unit>() {
            override fun prepare(p0: PreparableReloadListener.SharedState) {}

            override fun apply(p0: Unit, p1: PreparableReloadListener.SharedState) {
                val TempSet = CommandNodes.toSet()
                CommandNodes.clear()
                for (Node in TempSet) {
                    MinecraftServerInstanceBackendField
                        ?.commands
                        ?.dispatcher
                        ?.root
                        ?.children
                        ?.remove(Node) }
                CommandRegister(MinecraftServerInstanceBackendField?.commands?.dispatcher)
                MinecraftServerInstanceBackendField
                    ?.playerList
                    ?.players
                    ?.forEach { SPE ->
                        MinecraftServerInstance.commands.sendCommands(SPE) } } } ) }