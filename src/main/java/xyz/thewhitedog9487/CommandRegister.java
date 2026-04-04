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
import net.minecraft.world.phys.Vec3;
import org.jspecify.annotations.Nullable;

import module java.base;

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
     * @see Commands
     * @see <a href="https://zh.minecraft.wiki/w/%E6%9D%83%E9%99%90%E7%AD%89%E7%BA%A7">Minecraft Wiki (中文)</a>
     * @see <a href="https://minecraft.wiki/w/Permission_level">Minecraft Wiki (English)</a>
     */
    final static PermissionCheck PermissionLevel = Commands.LEVEL_GAMEMASTERS;

    /**
     * 在传送之前记录当前位置，以支持传送回去
     */
    static Map<UUID, Vec3> OldPositions = new HashMap<>();

    /**
     * 命令执行失败时的返回值
     * @see <a href="https://zh.minecraft.wiki/w/%E5%91%BD%E4%BB%A4#%E7%BB%93%E6%9E%9C">Minecraft Wiki (中文)</a>
     * @see <a href="https://minecraft.wiki/w/Commands">Minecraft Wiki (English)</a>
     */
    final static int CommandExecuteFailure = 0;

    /**
     * 使用Fabric API向游戏内注册命令
     * @param Name 根命令名
     * <br>
     * @see <a href="https://docs.fabricmc.net/zh_cn/develop/commands/basics">Fabric Docs (中文)</a>
     * @see <a href="https://wiki.fabricmc.net/zh_cn:tutorial:commands">Fabric Wiki (中文)</a>
     * @see <a href="https://docs.fabricmc.net/develop/commands/basics">Fabric Docs (English)</a>
     * @see <a href="https://wiki.fabricmc.net/tutorial:commands">Fabric Wiki (English)</a>
     */
    public static void Register(String Name){
        CommandRegistrationCallback.EVENT
                .register((dispatcher, registryAccess, environment) ->{
                    dispatcher.register(literal(Name)
                            // /rtp
                            .requires(Commands.hasPermission(PermissionLevel))
                            .executes(context -> ExecuteCommand(
                                    context.getSource(),null,null, null, null, null))
                            // /rtp back
                            .then(literal("back")
                                    .requires(Commands.hasPermission(PermissionLevel))
                                    .executes(context -> TeleportBack(
                                            context.getSource(), null)))
                            // /rtp back <PlayerID(被传送玩家名)>
                            .then(literal("back")
                                    .then(argument(CommandArgumentName_Target, EntityArgument.entity())
                                            .requires(Commands.hasPermission(PermissionLevel))
                                            .executes(context -> TeleportBack(
                                                    context.getSource(),
                                                    EntityArgument.getEntity(context,CommandArgumentName_Target)))))
                            // /rtp <PlayerID(被传送玩家名)> back
                            .then(argument(CommandArgumentName_Target, EntityArgument.entity())
                                    .then(literal("back")
                                            .requires(Commands.hasPermission(PermissionLevel))
                                            .executes(context -> TeleportBack(
                                                    context.getSource(),
                                                    EntityArgument.getEntity(context,CommandArgumentName_Target)))))
                            // /rtp <Radius(半径)>
                            .then(argument(CommandArgumentName_Radius, IntegerArgumentType.integer(0))
                                    .requires(Commands.hasPermission(PermissionLevel))
                                    .executes(context -> ExecuteCommand(
                                            context.getSource(),
                                            IntegerArgumentType.getInteger(context, CommandArgumentName_Radius),
                                            null,
                                            null,
                                            null,
                                            null)))
                            // /rtp <PlayerID(被传送玩家名)>
                            .then(argument(CommandArgumentName_Target, EntityArgument.entity())
                                    .requires(Commands.hasPermission(PermissionLevel))
                                    .executes(context -> ExecuteCommand(
                                            context.getSource(),
                                            null,
                                            EntityArgument.getEntity(context,CommandArgumentName_Target),
                                            null,
                                            null,
                                            null)))
                            // /rtp <Radius(半径)> <PlayerID(被传送玩家名)>
                            .then(argument(CommandArgumentName_Radius, IntegerArgumentType.integer(0))
                                    .then(argument(CommandArgumentName_Target, EntityArgument.entity())
                                            .requires(Commands.hasPermission(PermissionLevel))
                                            .executes(context -> ExecuteCommand(
                                                    context.getSource(),
                                                    IntegerArgumentType.getInteger(context, CommandArgumentName_Radius),
                                                    EntityArgument.getEntity(context,CommandArgumentName_Target),
                                                    null,
                                                    null,
                                                    null))))
                            // /rtp <PlayerID(被传送玩家名)> <Radius(半径)>
                            .then(argument(CommandArgumentName_Target, EntityArgument.entity())
                                    .then(argument(CommandArgumentName_Radius, IntegerArgumentType.integer(0))
                                            .requires(Commands.hasPermission(PermissionLevel))
                                            .executes(context -> ExecuteCommand(
                                                    context.getSource(),
                                                    IntegerArgumentType.getInteger(context, CommandArgumentName_Radius),
                                                    EntityArgument.getEntity(context,CommandArgumentName_Target),
                                                    null,
                                                    null,
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
                                                    Vec2Argument.getVec2(context,CommandArgumentName_OriginPosition),
                                                    null,
                                                    null))))
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
                                                                    (float) EntityArgument.getEntity( context,CommandArgumentName_OriginEntity).position().z ),
                                                            null,
                                                            null)))))
                           // /rtp <Radius(半径)> <PlayerID(被传送玩家名)> <OriginPos(随机中心，坐标)>
                           .then(argument(CommandArgumentName_Radius, IntegerArgumentType.integer(0))
                                   .then(argument(CommandArgumentName_Target, EntityArgument.entity())
                                           .then(argument(CommandArgumentName_OriginPosition, Vec2Argument.vec2())
                                                   .requires(Commands.hasPermission(PermissionLevel))
                                                   .executes(context -> ExecuteCommand(
                                                           context.getSource(),
                                                           IntegerArgumentType.getInteger(context, CommandArgumentName_Radius),
                                                           EntityArgument.getEntity(context,CommandArgumentName_Target),
                                                           Vec2Argument.getVec2(context,CommandArgumentName_OriginPosition),
                                                           null,
                                                           null)))))
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
                                                                   (float) EntityArgument.getEntity( context,CommandArgumentName_OriginEntity).position().z ),
                                                           null,
                                                           null)))))
                           // /rtp <PlayerID(被传送玩家名)> <Radius(半径)> <OriginPos(随机中心，坐标)>
                           .then(argument(CommandArgumentName_Target, EntityArgument.entity())
                                   .then(argument(CommandArgumentName_Radius, IntegerArgumentType.integer(0))
                                           .then(argument(CommandArgumentName_OriginPosition, Vec2Argument.vec2())
                                                   .requires(Commands.hasPermission(PermissionLevel))
                                                   .executes(context -> ExecuteCommand(
                                                           context.getSource(),
                                                           IntegerArgumentType.getInteger(context, CommandArgumentName_Radius),
                                                           EntityArgument.getEntity(context,CommandArgumentName_Target),
                                                           Vec2Argument.getVec2(context,CommandArgumentName_OriginPosition),
                                                           null,
                                                           null)))))
                           // /rtp <RegionFrom(随机区域起点，坐标)> <RegionTo(随机区域终点，坐标)>
                           .then(argument(CommandArgumentName_RegionFromPosition, Vec2Argument.vec2())
                                   .then(argument(CommandArgumentName_RegionToPosition, Vec2Argument.vec2())
                                           .requires(Commands.hasPermission(PermissionLevel))
                                           .executes(context -> ExecuteCommand(
                                                   context.getSource(),
                                                   null,
                                                   null,
                                                   null,
                                                   Vec2Argument.getVec2(context, CommandArgumentName_RegionFromPosition),
                                                   Vec2Argument.getVec2(context, CommandArgumentName_RegionToPosition)))))
                           // /rtp <RegionFrom(随机区域起点，坐标)> <RegionTo(随机区域终点，坐标)> <PlayerID(被传送玩家名)>
                           .then(argument(CommandArgumentName_RegionFromPosition, Vec2Argument.vec2())
                                   .then(argument(CommandArgumentName_RegionToPosition, Vec2Argument.vec2())
                                           .then(argument(CommandArgumentName_Target, EntityArgument.entity())
                                                   .requires(Commands.hasPermission(PermissionLevel))
                                                   .executes(context -> ExecuteCommand(
                                                           context.getSource(),
                                                           null,
                                                           EntityArgument.getEntity(context,CommandArgumentName_Target),
                                                           null,
                                                           Vec2Argument.getVec2(context, CommandArgumentName_RegionFromPosition),
                                                           Vec2Argument.getVec2(context, CommandArgumentName_RegionToPosition))))))
                           // /rtp <PlayerID(被传送玩家名)> <RegionFrom(随机区域起点，坐标)> <RegionTo(随机区域终点，坐标)>
                           .then(argument(CommandArgumentName_Target, EntityArgument.entity())
                                   .then(argument(CommandArgumentName_RegionFromPosition, Vec2Argument.vec2())
                                           .then(argument(CommandArgumentName_RegionToPosition, Vec2Argument.vec2())
                                                   .requires(Commands.hasPermission(PermissionLevel))
                                                   .executes(context -> ExecuteCommand(
                                                           context.getSource(),
                                                           null,
                                                           EntityArgument.getEntity(context,CommandArgumentName_Target),
                                                           null,
                                                           Vec2Argument.getVec2(context, CommandArgumentName_RegionFromPosition),
                                                           Vec2Argument.getVec2(context, CommandArgumentName_RegionToPosition))))))
                           // /rtp <RegionFrom(随机区域起点，实体)> <RegionTo(随机区域终点，实体)>
                           .then(argument(CommandArgumentName_RegionFromEntity, EntityArgument.entity())
                                   .then(argument(CommandArgumentName_RegionToEntity, EntityArgument.entity())
                                           .requires(Commands.hasPermission(PermissionLevel))
                                           .executes(context -> ExecuteCommand(
                                                   context.getSource(),
                                                   null,
                                                   null,
                                                   null,
                                                    new Vec2( (float) EntityArgument.getEntity( context,CommandArgumentName_RegionFromEntity).position().x,
                                                              (float) EntityArgument.getEntity( context,CommandArgumentName_RegionFromEntity).position().z ),
                                                    new Vec2( (float) EntityArgument.getEntity( context,CommandArgumentName_RegionToEntity).position().x,
                                                              (float) EntityArgument.getEntity( context,CommandArgumentName_RegionToEntity).position().z ) ) )))
                           // /rtp <RegionFrom(随机区域起点，实体)> <RegionTo(随机区域终点，实体)> <PlayerID(被传送玩家名)>
                           .then(argument(CommandArgumentName_RegionFromEntity, EntityArgument.entity())
                                   .then(argument(CommandArgumentName_RegionToEntity, EntityArgument.entity())
                                           .then(argument(CommandArgumentName_Target, EntityArgument.entity())
                                                   .requires(Commands.hasPermission(PermissionLevel))
                                                   .executes(context -> ExecuteCommand(
                                                           context.getSource(),
                                                           null,
                                                           EntityArgument.getEntity(context,CommandArgumentName_Target),
                                                           null,
                                                           new Vec2( (float) EntityArgument.getEntity( context,CommandArgumentName_RegionFromEntity).position().x,
                                                                     (float) EntityArgument.getEntity( context,CommandArgumentName_RegionFromEntity).position().z ),
                                                           new Vec2( (float) EntityArgument.getEntity( context,CommandArgumentName_RegionToEntity).position().x,
                                                                     (float) EntityArgument.getEntity( context,CommandArgumentName_RegionToEntity).position().z )))))) );});}

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
     * @param RegionFrom 选择随机坐标的范围的起始坐标
     * @param RegionTo 选择随机坐标的范围的结束坐标
     * @return 命令运行是否成功
     */
    static int ExecuteCommand(CommandSourceStack Source, @Nullable Integer Radius, @Nullable Entity Entity, @Nullable Vec2 Origin, @Nullable Vec2 RegionFrom, @Nullable Vec2 RegionTo){
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
            return CommandExecuteFailure; }
        int Coordinate_X = 0;
        int Coordinate_Z = 0;
        if (RegionFrom == null || RegionTo == null) { // ← 按半径和中心取随机
            if (Radius == null) { Radius = (int) (WorldBorder - 1e4); }
            // ↑ 远离世界边界
            try {
                if (Origin == null) {
                    Coordinate_X = SR.nextInt(-Radius, Radius + 1);
                    Coordinate_Z = SR.nextInt(-Radius, Radius + 1); }
                else {
                    Coordinate_X = SR.nextInt((int) Origin.x - Radius, (int) Origin.x + Radius + 1);
                    Coordinate_Z = SR.nextInt((int) Origin.y - Radius, (int) Origin.y + Radius + 1); } }
            catch (IllegalArgumentException e) {
                // 半径为零
                if (Origin == null) {
                    Source.sendFailure(Component.translatableWithFallback("warning.radius_equal_zero_no_target", "由于你设置的随机半径为0，并且未设置随机中心点坐标，因此什么都不会发生"));
                    return CommandExecuteFailure; }
                else {
                    Coordinate_X = (int) Origin.x;
                    Coordinate_Z = (int) Origin.y;
                    int finalCoordinate_X1 = Coordinate_X;
                    int finalCoordinate_Z1 = Coordinate_Z;
                    // ↑ "lambda 表达式中使用的变量应为 final 或有效 final"
                    Source.sendSuccess(() -> {
                        return Component.translatableWithFallback("warning.radius_equal_zero", "警告：由于你设置的随机半径为0，因此在选择出合适高度之后将直接把你传送至%d %d", finalCoordinate_X1, finalCoordinate_Z1); }, true); } } }
        else { // ← 按范围取随机
            Coordinate_X = SR.nextInt( Math.min( (int)RegionFrom.x, (int)RegionTo.x ), Math.max( (int)RegionFrom.x, (int)RegionTo.x ) + 1 );
            Coordinate_Z = SR.nextInt( Math.min( (int)RegionFrom.y, (int)RegionTo.y ), Math.max( (int)RegionFrom.y, (int)RegionTo.y ) + 1 ); }
        if (Coordinate_X >= WorldBorder){ Coordinate_X = (int) (WorldBorder - 1e4); }
        else if (Coordinate_X <= -WorldBorder){ Coordinate_X = (int) (-WorldBorder + 1e4); }
        if (Coordinate_Z >= WorldBorder){ Coordinate_Z = (int) (WorldBorder - 1e4); }
        else if (Coordinate_Z <= -WorldBorder){ Coordinate_Z = (int) (-WorldBorder + 1e4); }
        // ↑ 远离世界边界
        Level EntityWorld = TargetEntity.level();
        EntityWorld.getChunk(Coordinate_X >> 4, Coordinate_Z >> 4, ChunkStatus.FULL, true);
        // ↑ 加载目标区块，不然下面获取到的Coordinate_Y一定是-64
        // 按位右移4和除以16在被除数小于0时的结果不一致，一个是向上取整，一个是向下取整
        // 因此必须是 >> 4 ，不能是 / 16
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
        OldPositions.put(TargetEntity.getUUID(), TargetEntity.position());
        TargetEntity.teleportTo((ServerLevel) EntityWorld,Coordinate_X + 0.5, Coordinate_Y, Coordinate_Z + 0.5, new HashSet<>(), TargetEntity.getYRot(), TargetEntity.getXRot(), false);
        int finalCoordinate_X = Coordinate_X;
        int finalCoordinate_Z = Coordinate_Z;
        // ↑ "lambda 表达式中使用的变量应为 final 或有效 final"
        final var FeedbackFallbackString = String.format("已将玩家%s传送到%d %d %d", TargetEntity.getName().getString(), Coordinate_X, Coordinate_Y, Coordinate_Z);
        Source.sendSuccess(()->{ return  Component.translatableWithFallback("info.success", FeedbackFallbackString, TargetEntity.getName(), finalCoordinate_X, Coordinate_Y, finalCoordinate_Z); },true);
        return Command.SINGLE_SUCCESS; }

    static int TeleportBack(CommandSourceStack Source, @Nullable Entity TargetEntity) {
        TargetEntity = TargetEntity != null ? TargetEntity : Source.getPlayer();
        /*
            ↑
            if (TargetEntity == null){
                TargetEntity = Source.getPlayer(); }
         */
        if (TargetEntity == null) {
            Source.sendFailure(Component.translatableWithFallback("error.no_target", "不存在被传送目标，由非玩家物体执行命令时请显式指定被传送玩家ID"));
            return CommandExecuteFailure; }
        var OldPos =  OldPositions.get(TargetEntity.getUUID());
        if (OldPos == null) {
            Source.sendFailure(Component.translatableWithFallback("error.no_old_position", "%s还未使用过随机传送，因此无法回溯", TargetEntity.getName().getString()));
            return CommandExecuteFailure; }
        else {
            TargetEntity.teleportTo(( ServerLevel) TargetEntity.level(), OldPos.x, OldPos.y, OldPos.z, new HashSet<>(), TargetEntity.getYRot(), TargetEntity.getXRot(), false);
            Entity finalTargetEntity = TargetEntity;
            // ↑ "lambda 表达式中使用的变量应为 final 或有效 final"
            Source.sendSuccess(() -> Component.translatableWithFallback("info.success.back", "已将玩家%s传送回原位置", finalTargetEntity.getName().getString()), true);
            return Command.SINGLE_SUCCESS; } }
}