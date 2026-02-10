package xyz.thewhitedog9487;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.commands.Commands;
import net.minecraft.server.commands.TeleportCommand;
import net.minecraft.server.permissions.PermissionCheck;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.commands.arguments.coordinates.Vec2Argument;
import net.minecraft.world.entity.Entity;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.network.chat.Component;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.chunk.status.ChunkStatus;
import org.jetbrains.annotations.Nullable;

import java.util.HashSet;
import java.util.Set;
import java.util.SplittableRandom;

import static net.minecraft.commands.Commands.argument;
import static net.minecraft.commands.Commands.literal;
import static net.minecraft.world.level.levelgen.Heightmap.Types.MOTION_BLOCKING_NO_LEAVES;
import static xyz.thewhitedog9487.ResourceReloaderListener.*;

public class CommandRegister {

    /**
     * 随机数生成器
     */
    final static SplittableRandom SR = new SplittableRandom();

    /**
     * 传送后用于生成保护平台的方块
     */
    final static Block TargetBlock = Blocks.GLASS;

    /**
     * 传送后会被 {@link CommandRegister#TargetBlock} 替换掉的方块
     * <br>
     * 替换中心：被传送目标脚下方块
     * <br>
     * 替换范围：替换中心周围半径为一的正方形区域
     */
    final static Set<Block> ReplaceToTargetBlock = Set.of(
            Blocks.AIR,
            Blocks.VOID_AIR,
            Blocks.CAVE_AIR,
            Blocks.WATER,
            Blocks.LAVA,
            Blocks.SHORT_GRASS,
            Blocks.VINE );

    /**
     * 世界边界
     * <br>
     * @see <a href="https://zh.minecraft.wiki/w/%E4%B8%96%E7%95%8C%E8%BE%B9%E7%95%8C#%E5%A4%A7%E5%B0%8F">Minecraft Wiki (中文)</a>
     * @see <a href="https://minecraft.wiki/w/World_border#General_information">Minecraft Wiki (English)</a>
     */
    final static Integer WorldBorder = (int) 2.9e7;

    /**
     * 执行命令所需权限等级
     * @see TeleportCommand
     */
    final static PermissionCheck PermissionLevel = Commands.LEVEL_GAMEMASTERS;

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
                            .requires(Commands.hasPermission(PermissionLevel))
                            .executes(context -> ExecuteCommand(
                                    context.getSource(),null,null, null))
                            // /rtp <Radius(半径)>
                            .then(argument(CommandArgumentName_Radius, IntegerArgumentType.integer(0))
                                    .requires(Commands.hasPermission(PermissionLevel))
                                    .executes(context -> ExecuteCommand(
                                            context.getSource(),
                                            IntegerArgumentType.getInteger(context, CommandArgumentName_Radius),
                                            null,
                                            null)))
                            // /rtp <PlayerID(被传送玩家名)>
                            .then(argument(CommandArgumentName_Target, EntityArgument.entity())
                                    .requires(Commands.hasPermission(PermissionLevel))
                                    .executes(context -> ExecuteCommand(
                                            context.getSource(),
                                            null,
                                            EntityArgument.getEntity(context,CommandArgumentName_Target),
                                            null)))
                            // /rtp <Radius(半径)> <PlayerID(被传送玩家名)>
                            .then(argument(CommandArgumentName_Radius, IntegerArgumentType.integer(0))
                                    .then(argument(CommandArgumentName_Target, EntityArgument.entity())
                                            .requires(Commands.hasPermission(PermissionLevel))
                                            .executes(context -> ExecuteCommand(
                                                    context.getSource(),
                                                    IntegerArgumentType.getInteger(context, CommandArgumentName_Radius),
                                                    EntityArgument.getEntity(context,CommandArgumentName_Target),
                                                    null))))
                            // /rtp <PlayerID(被传送玩家名)> <Radius(半径)>
                            .then(argument(CommandArgumentName_Target, EntityArgument.entity())
                                    .then(argument(CommandArgumentName_Radius, IntegerArgumentType.integer(0))
                                            .requires(Commands.hasPermission(PermissionLevel))
                                            .executes(context -> ExecuteCommand(
                                                    context.getSource(),
                                                    IntegerArgumentType.getInteger(context, CommandArgumentName_Radius),
                                                    EntityArgument.getEntity(context,CommandArgumentName_Target),
                                                    null))))
//                          // /rtp <Radius(半径)> <Origin(随机中心)>
//                          .then(argument("Radius(半径)", LongArgumentType.longArg())
//                                  .then(argument("Origin(随机中心)",EntityArgumentType.player())
//                                          .requires(Commands.hasPermission(PermissionLevel))
//                                          .executes(context -> execute_command_origin(
//                                                  context.getSource(),
//                                                  LongArgumentType.getLong(context, "Radius(半径)"),
//                                                  null,
//                                                  EntityArgumentType.getEntity(context,"Origin(随机中心)")))))
                            // /rtp <Radius(半径)> <OriginPos(随机中心，坐标)>
                            .then(argument(CommandArgumentName_Radius, IntegerArgumentType.integer(0))
                                    .then(argument(CommandArgumentName_OriginPosition, Vec2Argument.vec2())
                                            .requires(Commands.hasPermission(PermissionLevel))
                                            .executes(context -> ExecuteCommand(
                                                    context.getSource(),
                                                    IntegerArgumentType.getInteger(context, CommandArgumentName_Radius),
                                                    null,
                                                    Vec2Argument.getVec2(context,CommandArgumentName_OriginPosition)))))
                            // /rtp <Radius(半径)> <被传送玩家名(PlayerID)> <OriginEntity(随机中心，实体)>
                            // /rtp <Radius(半径)> <PlayerID(被传送玩家名)> <OriginEntity(随机中心，实体)>
                            .then(argument(CommandArgumentName_Radius, IntegerArgumentType.integer(0))
                                    .then(argument(CommandArgumentName_Target, EntityArgument.entity())
                                            .then(argument(CommandArgumentName_OriginEntity, EntityArgument.entity())
                                                    .requires(Commands.hasPermission(PermissionLevel))
                                                    .executes(context -> ExecuteCommand(
                                                            context.getSource(),
                                                            IntegerArgumentType.getInteger(context, CommandArgumentName_Radius),
                                                            EntityArgument.getEntity(context,CommandArgumentName_Target),
                                                            new Vec2( (float) EntityArgument.getEntity( context,CommandArgumentName_OriginEntity).position().x,
                                                                    (float) EntityArgument.getEntity( context,CommandArgumentName_OriginEntity).position().z ))))))
                           // /rtp <Radius(半径)> <被传送玩家名(PlayerID)> <OriginPos(随机中心，坐标)>
                           // /rtp <Radius(半径)> <PlayerID(被传送玩家名)> <OriginPos(随机中心，坐标)>
                           .then(argument(CommandArgumentName_Radius, IntegerArgumentType.integer(0))
                                   .then(argument(CommandArgumentName_Target, EntityArgument.entity())
                                           .then(argument(CommandArgumentName_OriginPosition, Vec2Argument.vec2())
                                                   .requires(Commands.hasPermission(PermissionLevel))
                                                   .executes(context -> ExecuteCommand(
                                                           context.getSource(),
                                                           IntegerArgumentType.getInteger(context, CommandArgumentName_Radius),
                                                           EntityArgument.getEntity(context,CommandArgumentName_Target),
                                                           Vec2Argument.getVec2(context,CommandArgumentName_OriginPosition))))))
                           // /rtp <被传送玩家名(PlayerID)> <Radius(半径)> <OriginEntity(随机中心，实体)>
                           // /rtp <PlayerID(被传送玩家名)> <Radius(半径)> <OriginEntity(随机中心，实体)>
                           .then(argument(CommandArgumentName_Target, EntityArgument.entity())
                                   .then(argument(CommandArgumentName_Radius, IntegerArgumentType.integer(0))
                                           .then(argument(CommandArgumentName_OriginEntity, EntityArgument.entity())
                                                   .requires(Commands.hasPermission(PermissionLevel))
                                                   .executes(context -> ExecuteCommand(
                                                           context.getSource(),
                                                           IntegerArgumentType.getInteger(context, CommandArgumentName_Radius),
                                                           EntityArgument.getEntity(context,CommandArgumentName_Target),
                                                           new Vec2( (float) EntityArgument.getEntity( context,CommandArgumentName_OriginEntity).position().x,
                                                                   (float) EntityArgument.getEntity( context,CommandArgumentName_OriginEntity).position().z ))))))
                           // /rtp <被传送玩家名(PlayerID)> <Radius(半径)> <OriginPos(随机中心，坐标)>
                           // /rtp <PlayerID(被传送玩家名)> <Radius(半径)> <OriginPos(随机中心，坐标)>
                           .then(argument(CommandArgumentName_Target, EntityArgument.entity())
                                   .then(argument(CommandArgumentName_Radius, IntegerArgumentType.integer(0))
                                           .then(argument(CommandArgumentName_OriginPosition, Vec2Argument.vec2())
                                                   .requires(Commands.hasPermission(PermissionLevel))
                                                   .executes(context -> ExecuteCommand(
                                                           context.getSource(),
                                                           IntegerArgumentType.getInteger(context, CommandArgumentName_Radius),
                                                           EntityArgument.getEntity(context,CommandArgumentName_Target),
                                                           Vec2Argument.getVec2(context,CommandArgumentName_OriginPosition)))))) );});}

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
     * @param Entity 被传送的实体
     * @param Origin 随机选择的目的坐标的中心
     * @return 命令运行是否成功
     */
    static int ExecuteCommand(CommandSourceStack Source, @Nullable Integer Radius, @Nullable Entity Entity, @Nullable Vec2 Origin){
        Entity TargetEntity = Entity == null ? Source.getPlayer() : Entity;
        /*
            ↑
            Entity TargetEntity = null;
            if (TargetEntity == null){
                TargetEntity = Source.getPlayer();}
            else{
                TargetEntity = Entity;}
         */
        if (TargetEntity == null) {
            Source.sendSuccess(()->{ return  Component.translatableWithFallback("error.no_target","不存在被传送目标，由非玩家物体执行命令时请显式指定被传送玩家ID"); }, true);
            return -1;}
        if (Radius == null){Radius = (int) (WorldBorder - 1e4);}
        // ↑ 远离世界边界
        int Coordinate_X = 0;
        int Coordinate_Z = 0;
        try {
            if (Origin == null){
                Coordinate_X = SR.nextInt(-Radius, Radius + 1);
                Coordinate_Z = SR.nextInt(-Radius, Radius + 1); }
            else{
                Coordinate_X = SR.nextInt((int) Origin.x - Radius, (int) Origin.x + Radius + 1);
                Coordinate_Z = SR.nextInt((int) Origin.y - Radius, (int) Origin.y + Radius + 1); } }
        catch (IllegalArgumentException e) {
            // 半径为零
            if (Origin == null) {
                Source.sendSuccess(()->{ return Component.translatableWithFallback("warning.radius_equal_zero_no_target", "由于你设置的随机半径为0，并且未设置随机中心点坐标，因此什么都不会发生"); },true);
                return -1;}
            else {
                Coordinate_X = (int) Origin.x;
                Coordinate_Z = (int) Origin.y;
                int finalCoordinate_X1 = Coordinate_X;
                int finalCoordinate_Z1 = Coordinate_Z;
                // ↑ "lambda 表达式中使用的变量应为 final 或有效 final"
                Source.sendSuccess(()->{ return Component.translatableWithFallback("warning.radius_equal_zero", "警告：由于你设置的随机半径为0，因此在选择出合适高度之后将直接把你传送至%d %d", finalCoordinate_X1, finalCoordinate_Z1); },true);}}
        Level EntityWorld = TargetEntity.level();
        EntityWorld.getChunk(Coordinate_X >> 4, Coordinate_Z >> 4, ChunkStatus.FULL, true);
        // ↑ 加载目标区块，不然下面获取到的Coordinate_Y一定是-64
        // 必须是 >> 4 ，不能是 / 16
        int Coordinate_Y = EntityWorld.getHeight(MOTION_BLOCKING_NO_LEAVES, Coordinate_X, Coordinate_Z);
        BlockPos.MutableBlockPos BlockPos = new BlockPos.MutableBlockPos();
        for (int x = -1; x <= 1; x++) {
            for (int z = -1; z <= 1; z++) {
                // 如果传送到的位置周围一圈是ReplaceToTargetBlock的方块，将其替换为TargetBlock
                BlockPos.set(Coordinate_X - x, Coordinate_Y - 1, Coordinate_Z - z);
                var CurrentBlock = EntityWorld.getBlockState(BlockPos).getBlock();
                if ( ReplaceToTargetBlock.contains(CurrentBlock) ) {
                    // 只替换ReplaceToTargetBlock内的方块，其余保留
                    EntityWorld.setBlockAndUpdate(BlockPos, TargetBlock.defaultBlockState());}}}
//        if ( String.valueOf(TargetEntity.getWorld().getBiome(new BlockPos(Math.toIntExact(Coordinate_X), Coordinate_Y, Math.toIntExact(Coordinate_Z))).getKey()).equals("minecraft:the_void") ) {
//            Coordinate_Y++;}
        TargetEntity.teleportTo((ServerLevel) EntityWorld,Coordinate_X + 0.5, Coordinate_Y, Coordinate_Z + 0.5, new HashSet<>(), TargetEntity.getYRot(), TargetEntity.getXRot(), false);
        int finalCoordinate_X = Coordinate_X;
        int finalCoordinate_Z = Coordinate_Z;
        // ↑ "lambda 表达式中使用的变量应为 final 或有效 final"
        final var FeedbackFallbackString = String.format("已将玩家%s传送到%d %d %d", TargetEntity.getName().getString(), Coordinate_X, Coordinate_Y, Coordinate_Z);
        Source.sendSuccess(()->{ return  Component.translatableWithFallback("info.success", FeedbackFallbackString, TargetEntity.getName(), finalCoordinate_X, Coordinate_Y, finalCoordinate_Z); },true);
        return Command.SINGLE_SUCCESS;}
}