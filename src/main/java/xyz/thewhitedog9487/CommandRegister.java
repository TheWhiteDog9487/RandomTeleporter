package xyz.thewhitedog9487;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.block.Blocks;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.command.argument.Vec2ArgumentType;
import net.minecraft.entity.Entity;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec2f;
import org.jetbrains.annotations.Nullable;

import java.util.HashSet;
import java.util.SplittableRandom;

import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

public class CommandRegister {

    /**
     * 世界边界
     * <br>
     * @see <a href="https://zh.minecraft.wiki/w/%E4%B8%96%E7%95%8C%E8%BE%B9%E7%95%8C#%E5%A4%A7%E5%B0%8F">Minecraft Wiki (中文)</a>
     * @see <a href="https://minecraft.wiki/w/World_border#General_information">Minecraft Wiki (English)</a>
     */
    final static Integer WorldBorder = (int) 2.9e7;

    /**
     * 执行命令所需权限等级
     * @see net.minecraft.server.command.TeleportCommand
     */
    final static byte PermissionLevel = 2;

    /**
     * 使用Fabric API向游戏内注册命令
     * @param Name 根命令名
     * <br>
     * @see <a href="https://docs.fabricmc.net/zh_cn/develop/commands/basics">Fabric Wiki (新样式，中文)</a>
     * @see <a href="https://wiki.fabricmc.net/zh_cn:tutorial:commands">Fabric Wiki (旧样式，中文)</a>
     * @see <a href="https://docs.fabricmc.net/develop/commands/basics">Fabric Wiki (New style,English)</a>
     * @see <a href="https://wiki.fabricmc.net/tutorial:commands">Fabric Wiki (Old style,English)</a>
     */
    public static void Register(String Name){
        CommandRegistrationCallback.EVENT
                .register((dispatcher, registryAccess, environment) ->{
                    dispatcher.register(literal(Name)
                            // /rtp
                            .requires(source -> source.hasPermissionLevel(PermissionLevel))
                            .executes(context -> ExecuteCommand(
                                    context.getSource(),null,null, null))
                            // /rtp <Radius(半径)>
                            .then(argument("Radius(半径)", IntegerArgumentType.integer(0))
                                    .requires(source -> source.hasPermissionLevel(PermissionLevel))
                                    .executes(context -> ExecuteCommand(
                                            context.getSource(),
                                            IntegerArgumentType.getInteger(context, "Radius(半径)"),
                                            null,
                                            null)))
                            // /rtp <被传送玩家名(PlayerID)>
                            .then(argument("被传送玩家名(PlayerID)", EntityArgumentType.entity())
                                    .requires(source -> source.hasPermissionLevel(PermissionLevel))
                                    .executes(context -> ExecuteCommand(
                                            context.getSource(),
                                            null,
                                            EntityArgumentType.getEntity(context,"被传送玩家名(PlayerID)"),
                                            null)))
                            // /rtp <Radius(半径)> <被传送玩家名(PlayerID)>
                            .then(argument("Radius(半径)", IntegerArgumentType.integer(0))
                                    .then(argument("被传送玩家名(PlayerID)", EntityArgumentType.entity())
                                            .requires(source -> source.hasPermissionLevel(PermissionLevel))
                                            .executes(context -> ExecuteCommand(
                                                    context.getSource(),
                                                    IntegerArgumentType.getInteger(context, "Radius(半径)"),
                                                    EntityArgumentType.getEntity(context,"被传送玩家名(PlayerID)"),
                                                    null))))
                            // /rtp <被传送玩家名(PlayerID)> <Radius(半径)>
                            .then(argument("被传送玩家名(PlayerID)", EntityArgumentType.entity())
                                    .then(argument("Radius(半径)", IntegerArgumentType.integer(0))
                                            .requires(source -> source.hasPermissionLevel(PermissionLevel))
                                            .executes(context -> ExecuteCommand(
                                                    context.getSource(),
                                                    IntegerArgumentType.getInteger(context, "Radius(半径)"),
                                                    EntityArgumentType.getEntity(context,"被传送玩家名(PlayerID)"),
                                                    null))))
//                          // /rtp <Radius(半径)> <Origin(随机中心)>
//                          .then(argument("Radius(半径)", LongArgumentType.longArg())
//                                  .then(argument("Origin(随机中心)",EntityArgumentType.player())
//                                          .requires(source -> source.hasPermissionLevel(PermissionLevel))
//                                          .executes(context -> execute_command_origin(
//                                                  context.getSource(),
//                                                  LongArgumentType.getLong(context, "Radius(半径)"),
//                                                  null,
//                                                  EntityArgumentType.getEntity(context,"Origin(随机中心)")))))
                            // /rtp <Radius(半径)> <OriginPos(随机中心，坐标)>
                            .then(argument("Radius(半径)", IntegerArgumentType.integer(0))
                                    .then(argument("OriginPos(随机中心，坐标)",Vec2ArgumentType.vec2())
                                            .requires(source -> source.hasPermissionLevel(PermissionLevel))
                                            .executes(context -> ExecuteCommand(
                                                    context.getSource(),
                                                    IntegerArgumentType.getInteger(context, "Radius(半径)"),
                                                    null,
                                                    Vec2ArgumentType.getVec2(context,"OriginPos(随机中心，坐标)")))))
                            // /rtp <Radius(半径)> <被传送玩家名(PlayerID)> <OriginEntity(随机中心，实体)>
                            .then(argument("Radius(半径)", IntegerArgumentType.integer(0))
                                    .then(argument("被传送玩家名(PlayerID)", EntityArgumentType.entity())
                                            .then(argument("OriginEntity(随机中心，实体)",EntityArgumentType.entity())
                                                    .requires(source -> source.hasPermissionLevel(PermissionLevel))
                                                    .executes(context -> ExecuteCommand(
                                                            context.getSource(),
                                                            IntegerArgumentType.getInteger(context, "Radius(半径)"),
                                                            EntityArgumentType.getEntity(context,"被传送玩家名(PlayerID)"),
                                                            new Vec2f( (float) EntityArgumentType.getEntity( context,"OriginEntity(随机中心，实体)").getEntityPos().x,
                                                                    (float) EntityArgumentType.getEntity( context,"OriginEntity(随机中心，实体)").getEntityPos().z ))))))
                           // /rtp <Radius(半径)> <被传送玩家名(PlayerID)> <OriginPos(随机中心，坐标)>
                           .then(argument("Radius(半径)", IntegerArgumentType.integer(0))
                                   .then(argument("被传送玩家名(PlayerID)", EntityArgumentType.entity())
                                           .then(argument("OriginPos(随机中心，坐标)",Vec2ArgumentType.vec2())
                                                   .requires(source -> source.hasPermissionLevel(PermissionLevel))
                                                   .executes(context -> ExecuteCommand(
                                                           context.getSource(),
                                                           IntegerArgumentType.getInteger(context, "Radius(半径)"),
                                                           EntityArgumentType.getEntity(context,"被传送玩家名(PlayerID)"),
                                                           Vec2ArgumentType.getVec2(context,"OriginPos(随机中心，坐标)"))))))
                           // /rtp <被传送玩家名(PlayerID)> <Radius(半径)> <OriginEntity(随机中心，实体)>
                           .then(argument("被传送玩家名(PlayerID)", EntityArgumentType.entity())
                                   .then(argument("Radius(半径)", IntegerArgumentType.integer(0))
                                           .then(argument("OriginEntity(随机中心，实体)",EntityArgumentType.entity())
                                                   .requires(source -> source.hasPermissionLevel(PermissionLevel))
                                                   .executes(context -> ExecuteCommand(
                                                           context.getSource(),
                                                           IntegerArgumentType.getInteger(context, "Radius(半径)"),
                                                           EntityArgumentType.getEntity(context,"被传送玩家名(PlayerID)"),
                                                           new Vec2f( (float) EntityArgumentType.getEntity( context,"OriginEntity(随机中心，实体)").getEntityPos().x,
                                                                   (float) EntityArgumentType.getEntity( context,"OriginEntity(随机中心，实体)").getEntityPos().z ))))))
                           // /rtp <被传送玩家名(PlayerID)> <Radius(半径)> <OriginPos(随机中心，坐标)>
                           .then(argument("被传送玩家名(PlayerID)", EntityArgumentType.entity())
                                   .then(argument("Radius(半径)", IntegerArgumentType.integer(0))
                                           .then(argument("OriginPos(随机中心，坐标)",Vec2ArgumentType.vec2())
                                                   .requires(source -> source.hasPermissionLevel(PermissionLevel))
                                                   .executes(context -> ExecuteCommand(
                                                           context.getSource(),
                                                           IntegerArgumentType.getInteger(context, "Radius(半径)"),
                                                           EntityArgumentType.getEntity(context,"被传送玩家名(PlayerID)"),
                                                           Vec2ArgumentType.getVec2(context,"OriginPos(随机中心，坐标)")))))) );});}

    /**
     * 向游戏内注册命令
     * <br>
     * 是 {@link CommandRegister#Register(String)} 的包装器
     * @see CommandRegister#Register(String)
     */
    public static void Register(){
        Register("随机传送");
        Register("rtp");
    }

    /**
     *
     * @param Source 命令执行者
     * @param Radius 随机选择的目的坐标距离参数 {@code Origin} 的最大距离
     * @param Player 被传送的玩家
     * @param Origin 随机选择的目的坐标的中心
     * @return 命令运行是否成功
     */
    static int execute_command(ServerCommandSource Source, @Nullable Integer Radius, @Nullable Entity Player, @Nullable Vec2f Origin){
        Entity entity = Player == null ? Source.getPlayer() : Player;
        /*
            ↑
            Entity entity = null;
            if (Player == null){
                entity = Source.getPlayer();}
            else{
                entity = Player;}
         */
        if (entity == null) {
            Source.sendFeedback(()->{ return  Text.translatableWithFallback("error.no_target","不存在被传送目标，由非玩家物体执行命令时请显式指定被传送玩家ID"); }, true);
            return -1;}
        if (Radius == null){Radius = (int) (WorldBorder - 1e4);}
        // ↑ 远离世界边界
        int Coordinate_X = 0;
        int Coordinate_Z = 0;
        try {
            if (Origin == null){
                Coordinate_X = new SplittableRandom().nextInt(-Radius, Radius);
                Coordinate_Z = new SplittableRandom().nextInt(-Radius, Radius);}
            else{
                Coordinate_X = new SplittableRandom().nextInt((int) Origin.x - Radius, (int) Origin.x + Radius);
                Coordinate_Z = new SplittableRandom().nextInt((int) Origin.y - Radius, (int) Origin.y + Radius);}}
        catch (IllegalArgumentException e) {
            // 半径为零
            if (Origin == null) {
                Source.sendFeedback(()->{ return Text.translatableWithFallback("warning.radius_equal_zero_no_target", "由于你设置的随机半径为0，并且未设置随机中心点坐标，因此什么都不会发生"); },true);
                return -1;}
            else {
                Coordinate_X = (int) Origin.x;
                Coordinate_Z = (int) Origin.y;
                int finalCoordinate_X1 = Coordinate_X;
                int finalCoordinate_Z1 = Coordinate_Z;
                // ↑ "lambda 表达式中使用的变量应为 final 或有效 final"
                Source.sendFeedback(()->{ return Text.translatableWithFallback("warning.radius_equal_zero", "警告：由于你设置的随机半径为0，因此在选择出合适高度之后将直接把你传送至%d %d", finalCoordinate_X1, finalCoordinate_Z1); },true);}}
        int Coordinate_Y = 320;
        for (var CurrentBlock = Source.getWorld().getBlockState(new BlockPos(Coordinate_X, Coordinate_Y, Coordinate_Z)).getBlock();
             // 从世界顶层往下找，直到遇到一个非空气方块
             Blocks.AIR == CurrentBlock ||
             Blocks.VOID_AIR == CurrentBlock ||
             Blocks.CAVE_AIR == CurrentBlock
                ;CurrentBlock = Source.getWorld().getBlockState(new BlockPos(Coordinate_X, Coordinate_Y, Coordinate_Z)).getBlock()){
            Coordinate_Y--;}
            for (int x = -1; x <= 1; x++) {
                for (int z = -1; z <= 1; z++) {
                    // 如果传送到的位置周围一圈是空气、水或岩浆，将其替换为玻璃
                    var BlockPos = new BlockPos(Coordinate_X - x, Coordinate_Y, Coordinate_Z - z);
                    var CurrentBlock = Source.getWorld().getBlockState(BlockPos).getBlock();
                    if ( CurrentBlock == Blocks.AIR ||
                        CurrentBlock == Blocks.VOID_AIR ||
                        CurrentBlock == Blocks.CAVE_AIR ||
                        CurrentBlock == Blocks.WATER ||
                        CurrentBlock == Blocks.LAVA ){
                        // 只替换空气、水和岩浆，其余保留
                            Source.getWorld().setBlockState(BlockPos, Blocks.GLASS.getDefaultState());}}}
//        if ( String.valueOf(entity.getWorld().getBiome(new BlockPos(Math.toIntExact(Coordinate_X), Coordinate_Y, Math.toIntExact(Coordinate_Z))).getKey()).equals("minecraft:the_void") ) {
//            Coordinate_Y++;}
        Coordinate_Y++;
        // ↑ 高一层，人别站在土里了
        entity.teleport(Source.getWorld(),Coordinate_X + 0.5, Coordinate_Y, Coordinate_Z + 0.5, new HashSet<>(), entity.getYaw(), entity.getPitch(), false);
        int finalCoordinate_X = Coordinate_X;
        int finalCoordinate_Y = Coordinate_Y;
        int finalCoordinate_Z = Coordinate_Z;
        // ↑ "lambda 表达式中使用的变量应为 final 或有效 final"
        final var FeedbackFallbackString = String.format("已将玩家%s传送到%d %d %d", TargetEntity.getName().getString(), Coordinate_X, Coordinate_Y, Coordinate_Z);
        Source.sendFeedback(()->{ return  Text.translatableWithFallback("info.success", FeedbackFallbackString, TargetEntity.getName(), finalCoordinate_X, Coordinate_Y, finalCoordinate_Z); },true);
        return Command.SINGLE_SUCCESS;}
}