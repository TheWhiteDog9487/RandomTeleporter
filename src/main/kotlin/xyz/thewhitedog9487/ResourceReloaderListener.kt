package xyz.thewhitedog9487

import net.fabricmc.fabric.api.resource.v1.ResourceLoader
import net.fabricmc.fabric.api.resource.v1.reloader.SimpleReloadListener
import net.minecraft.network.chat.Component
import net.minecraft.resources.Identifier
import net.minecraft.server.packs.PackType
import net.minecraft.server.packs.resources.PreparableReloadListener

var CommandArgumentName_Radius = "Radius(半径)"
var CommandArgumentName_Target = "PlayerID(被传送玩家名)"
var CommandArgumentName_OriginPosition = "OriginPos(随机中心，坐标)"
var CommandArgumentName_OriginEntity = "OriginEntity(随机中心，实体)"
var CommandArgumentName_RegionFromPosition = "RegionFrom(随机范围起始位置，坐标)"
var CommandArgumentName_RegionToPosition = "RegionTo(随机范围结束位置，坐标)"
var CommandArgumentName_RegionFromEntity = "RegionFrom(随机范围起始位置，实体)"
var CommandArgumentName_RegionToEntity = "RegionTo(随机范围结束位置，实体)"

fun ResourceReloaderListenerRegister() {
    ResourceLoader.get(PackType.CLIENT_RESOURCES).registerReloadListener(
        Identifier.fromNamespaceAndPath(ModID,
            "translate_text_apply"), object: SimpleReloadListener<Unit>() {
            override fun prepare(p0: PreparableReloadListener.SharedState) {}

            override fun apply(p0: Unit, p1: PreparableReloadListener.SharedState) {
                CommandArgumentName_Radius =
                    Component.translatableWithFallback("command.argument.radius", "Radius(半径)").string
                CommandArgumentName_Target =
                    Component.translatableWithFallback("command.argument.target", "PlayerID(被传送玩家名)").string
                CommandArgumentName_OriginPosition =
                    Component.translatableWithFallback("command.argument.origin_pos", "OriginPos(随机中心，坐标)").string
                CommandArgumentName_OriginEntity =
                    Component.translatableWithFallback("command.argument.origin_entity", "OriginEntity(随机中心，实体)").string
                CommandArgumentName_RegionFromPosition = Component.translatableWithFallback(
                    "command.argument.region_from_pos",
                    "RegionFrom(随机范围起始位置，坐标)").string
                CommandArgumentName_RegionToPosition = Component.translatableWithFallback(
                    "command.argument.region_to_pos",
                    "RegionTo(随机范围结束位置，坐标)").string
                CommandArgumentName_RegionFromEntity = Component.translatableWithFallback(
                    "command.argument.region_from_entity",
                    "RegionFrom(随机范围起始位置，实体)").string
                CommandArgumentName_RegionToEntity = Component.translatableWithFallback(
                    "command.argument.region_to_entity",
                    "RegionTo(随机范围结束位置，实体)").string } } ) }