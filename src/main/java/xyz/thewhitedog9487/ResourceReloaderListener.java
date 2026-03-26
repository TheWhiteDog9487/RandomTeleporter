package xyz.thewhitedog9487;

import net.fabricmc.fabric.api.resource.v1.ResourceLoader;
import net.fabricmc.fabric.api.resource.v1.reloader.SimpleReloadListener;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.resources.PreparableReloadListener;
import org.jspecify.annotations.NonNull;

import static net.minecraft.server.packs.PackType.CLIENT_RESOURCES;
import static xyz.thewhitedog9487.RandomTeleporter.MOD_ID;

/**
 * 在游戏资源重新加载之后刷新翻译文本
 */
public class ResourceReloaderListener {
    public static String CommandArgumentName_Radius = "Radius(半径)";
    public static String CommandArgumentName_Target = "PlayerID(被传送玩家名)";
    public static String CommandArgumentName_OriginPosition = "OriginPos(随机中心，坐标)";
    public static String CommandArgumentName_OriginEntity = "OriginEntity(随机中心，实体)";
    public static String CommandArgumentName_RegionFromPosition = "RegionFrom(随机范围起始位置，坐标)";
    public static String CommandArgumentName_RegionToPosition = "RegionTo(随机范围结束位置，坐标)";
    public static String CommandArgumentName_RegionFromEntity = "RegionFrom(随机范围起始位置，实体)";
    public static String CommandArgumentName_RegionToEntity = "RegionTo(随机范围结束位置，实体)";

    public static void Register(){
        ResourceLoader.get(CLIENT_RESOURCES).registerReloadListener(Identifier.fromNamespaceAndPath(MOD_ID, "translate_text_apply"), new SimpleReloadListener<Void>() {
            /**
             * 用不到，不用管
             */
            @Override
            protected Void prepare(PreparableReloadListener.@NonNull SharedState store) {
                return null; }

            @Override
            protected void apply(Void prepared, PreparableReloadListener.@NonNull SharedState store) {
                CommandArgumentName_Radius = Component.translatableWithFallback("command.argument.radius", "Radius(半径)").getString();
                CommandArgumentName_Target = Component.translatableWithFallback("command.argument.target", "PlayerID(被传送玩家名)").getString();
                CommandArgumentName_OriginPosition = Component.translatableWithFallback("command.argument.origin_pos", "OriginPos(随机中心，坐标)").getString();
                CommandArgumentName_OriginEntity = Component.translatableWithFallback("command.argument.origin_entity", "OriginEntity(随机中心，实体)").getString();
                CommandArgumentName_RegionFromPosition = Component.translatableWithFallback("command.argument.region_from_pos", "RegionFrom(随机范围起始位置，坐标)").getString();
                CommandArgumentName_RegionToPosition = Component.translatableWithFallback("command.argument.region_to_pos", "RegionTo(随机范围结束位置，坐标)").getString();
                CommandArgumentName_RegionFromEntity = Component.translatableWithFallback("command.argument.region_from_entity", "RegionFrom(随机范围起始位置，实体)").getString();
                CommandArgumentName_RegionToEntity = Component.translatableWithFallback("command.argument.region_to_entity", "RegionTo(随机范围结束位置，实体)").getString(); } } ); } }