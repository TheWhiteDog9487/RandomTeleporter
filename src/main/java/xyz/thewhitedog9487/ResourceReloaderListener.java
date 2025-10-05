package xyz.thewhitedog9487;

import net.fabricmc.fabric.api.resource.v1.ResourceLoader;
import net.fabricmc.fabric.api.resource.v1.reloader.SimpleResourceReloader;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import static net.minecraft.resource.ResourceType.CLIENT_RESOURCES;
import static xyz.thewhitedog9487.RandomTeleporter.MOD_ID;

/**
 * 在游戏资源重新加载之后刷新翻译文本
 */
public class ResourceReloaderListener {
    public static String CommandArgumentName_Radius = "Radius(半径)";
    public static String CommandArgumentName_Target = "被传送玩家名(PlayerID)";
    public static String CommandArgumentName_OriginPosition = "OriginPos(随机中心，坐标)";
    public static String CommandArgumentName_OriginEntity = "OriginEntity(随机中心，实体)";

    public static void Register(){
        ResourceLoader.get(CLIENT_RESOURCES).registerReloader(Identifier.of(MOD_ID, "translate_text_apply"), new SimpleResourceReloader<Void>() {
            /**
             * 用不到，不用管
             */
            @Override
            protected Void prepare(Store store) {
                return null; }

            @Override
            protected void apply(Void prepared, Store store) {
                CommandArgumentName_Radius = Text.translatableWithFallback("command.argument.radius", "Radius(半径)").getString();
                CommandArgumentName_Target = Text.translatableWithFallback("command.argument.target", "被传送玩家名(PlayerID)").getString();
                CommandArgumentName_OriginPosition = Text.translatableWithFallback("command.argument.origin_pos", "OriginPos(随机中心，坐标)").getString();
                CommandArgumentName_OriginEntity = Text.translatableWithFallback("command.argument.origin_entity", "OriginEntity(随机中心，实体)").getString(); } } ); } }