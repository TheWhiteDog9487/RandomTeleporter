package xyz.thewhitedog9487

import com.mojang.brigadier.Command
import com.mojang.brigadier.CommandDispatcher
import com.mojang.brigadier.arguments.IntegerArgumentType
import com.mojang.brigadier.tree.LiteralCommandNode
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback
import net.minecraft.commands.CommandSourceStack
import net.minecraft.commands.Commands
import net.minecraft.commands.arguments.EntityArgument
import net.minecraft.commands.arguments.coordinates.Vec2Argument
import net.minecraft.core.BlockPos
import net.minecraft.network.chat.Component
import net.minecraft.server.commands.TeleportCommand
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.entity.Entity
import net.minecraft.world.level.block.Blocks
import net.minecraft.world.level.chunk.status.ChunkStatus
import net.minecraft.world.level.levelgen.Heightmap.Types.MOTION_BLOCKING_NO_LEAVES
import net.minecraft.world.phys.Vec2
import net.minecraft.world.phys.Vec3
import java.util.*
import kotlin.math.absoluteValue
import kotlin.math.max
import kotlin.math.min
import kotlin.random.Random
import kotlin.random.nextInt

/**
 * 传送后用于生成保护平台的方块
 */
val TargetBlock = Blocks.GLASS

/**
 * 传送后会被 [TargetBlock] 替换掉的方块
 * <br></br>
 * 替换中心：被传送目标脚下方块
 * <br></br>
 * 替换范围：替换中心周围半径为一的正方形区域
 */
val ReplaceToTargetBlock = setOf(
    Blocks.AIR,
    Blocks.VOID_AIR,
    Blocks.CAVE_AIR,
    Blocks.WATER,
    Blocks.LAVA,
    Blocks.SHORT_GRASS,
    Blocks.VINE )

/**
 * 世界边界
 * <br></br>
 * @see <a href="https://zh.minecraft.wiki/w/%E4%B8%96%E7%95%8C%E8%BE%B9%E7%95%8C#%E5%A4%A7%E5%B0%8F">Minecraft Wiki (中文)</a>
 * @see <a href="https://minecraft.wiki/w/World_border#General_information">Minecraft Wiki (English)</a>
 **/
const val WorldBorder = 2.9e7.toInt()

/**
 * 执行命令所需权限等级
 * @see TeleportCommand
 * @see Commands
 * @see <a href="https://zh.minecraft.wiki/w/%E6%9D%83%E9%99%90%E7%AD%89%E7%BA%A7">Minecraft Wiki (中文)</a>
 * @see <a href="https://minecraft.wiki/w/Permission_level">Minecraft Wiki (English)</a>
 */
val PermissionLevel = Commands.LEVEL_GAMEMASTERS

/**
 * 在传送之前记录当前位置，以支持传送回去
 */
var OldPositions: MutableMap<UUID, Vec3> = mutableMapOf()

/**
 * 命令执行失败时的返回值
 * @see <a href="https://zh.minecraft.wiki/w/%E5%91%BD%E4%BB%A4#%E7%BB%93%E6%9E%9C">Minecraft Wiki (中文)</a>
 * @see <a href="https://minecraft.wiki/w/Commands">Minecraft Wiki (English)</a>
 */
const val CommandExecuteFailure = 0

/**
 * 根命令名
 */
val CommandRootNodeName = setOf(
    "随机传送",
    "rtp" )

val CommandNodes: MutableSet<LiteralCommandNode<CommandSourceStack>> = mutableSetOf()

/**
 * 是否注册过Fabric API的命令注册回调监听器
 */
var CommandRegistrationCallbackEventHasBeenRegistered = false

val CommandArgumentName_Radius = Component.translatableWithFallback("command.argument.radius", "Radius(半径)")
val CommandArgumentName_Target = Component.translatableWithFallback("command.argument.target", "PlayerID(被传送玩家名)")
val CommandArgumentName_OriginPosition = Component.translatableWithFallback("command.argument.origin_pos", "OriginPos(随机中心，坐标)")
val CommandArgumentName_OriginEntity = Component.translatableWithFallback("command.argument.origin_entity", "OriginEntity(随机中心，实体)")
val CommandArgumentName_RegionFromPosition = Component.translatableWithFallback(
    "command.argument.region_from_pos",
    "RegionFrom(随机范围起始位置，坐标)")
val CommandArgumentName_RegionToPosition = Component.translatableWithFallback(
    "command.argument.region_to_pos",
    "RegionTo(随机范围结束位置，坐标)")
val CommandArgumentName_RegionFromEntity = Component.translatableWithFallback(
    "command.argument.region_from_entity",
    "RegionFrom(随机范围起始位置，实体)")
val CommandArgumentName_RegionToEntity = Component.translatableWithFallback(
    "command.argument.region_to_entity",
    "RegionTo(随机范围结束位置，实体)")

/**
 * 使用Fabric API向游戏内注册命令
 * <br>
 * @see <a href="https://docs.fabricmc.net/zh_cn/develop/commands/basics">Fabric Docs (中文)</a>
 * @see <a href="https://docs.fabricmc.net/develop/commands/basics">Fabric Docs (English)</a>
 */
fun CommandRegister(InGameCommandDispatcherInstance: CommandDispatcher<CommandSourceStack>? = null) {
    val Register: (CommandDispatcherInstance: CommandDispatcher<CommandSourceStack>, NodeName: String) -> Unit = { CommandDispatcherInstance, NodeName ->
        val Node = CommandDispatcherInstance.register(Commands.literal(NodeName)
            // /rtp
            .requires(Commands.hasPermission(PermissionLevel))
            .executes{ commandContext -> ExecuteCommand(commandContext.source) }

            // /rtp back
            .then(Commands.literal("back")
                .requires(Commands.hasPermission(PermissionLevel))
                .executes { commandContext -> TeleportBack(commandContext.source) } )

            // /rtp back <PlayerID(被传送玩家名)>
            .then(Commands.literal("back")
                .then(Commands.argument(CommandArgumentName_Target.string, EntityArgument.entity())
                    .requires(Commands.hasPermission(PermissionLevel))
                    .executes { commandContext -> TeleportBack(commandContext.source,
                        EntityArgument.getEntity(commandContext, CommandArgumentName_Target.string)) } ) )

            // /rtp <PlayerID(被传送玩家名)> back
            .then(Commands.argument(CommandArgumentName_Target.string, EntityArgument.entity())
                .then(Commands.literal("back")
                    .requires(Commands.hasPermission(PermissionLevel))
                    .executes { commandContext -> TeleportBack(commandContext.source,
                        EntityArgument.getEntity(commandContext, CommandArgumentName_Target.string)) } ) )

            // /rtp <Radius(半径)>
            .then(Commands.argument(CommandArgumentName_Radius.string, IntegerArgumentType.integer(0))
                .requires(Commands.hasPermission(PermissionLevel))
                .executes { commandContext -> ExecuteCommand(commandContext.source,
                    Radius = IntegerArgumentType.getInteger(commandContext, CommandArgumentName_Radius.string)) } )

            // /rtp <PlayerID(被传送玩家名)>
            .then(Commands.argument(CommandArgumentName_Target.string, EntityArgument.entity())
                .requires(Commands.hasPermission(PermissionLevel))
                .executes { commandContext -> ExecuteCommand(commandContext.source,
                    Entity = EntityArgument.getEntity(commandContext, CommandArgumentName_Target.string)) } )

            // /rtp <Radius(半径)> <PlayerID(被传送玩家名)>
            .then(Commands.argument(CommandArgumentName_Radius.string, IntegerArgumentType.integer(0))
                .then(Commands.argument(CommandArgumentName_Target.string, EntityArgument.entity())
                    .requires(Commands.hasPermission(PermissionLevel))
                    .executes { commandContext -> ExecuteCommand(commandContext.source,
                        Radius = IntegerArgumentType.getInteger(commandContext, CommandArgumentName_Radius.string),
                        Entity = EntityArgument.getEntity(commandContext, CommandArgumentName_Target.string)) } ) )

            // /rtp <PlayerID(被传送玩家名)> <Radius(半径)>
            .then(Commands.argument(CommandArgumentName_Target.string, EntityArgument.entity())
                .then(Commands.argument(CommandArgumentName_Radius.string, IntegerArgumentType.integer(0))
                    .requires(Commands.hasPermission(PermissionLevel))
                    .executes { commandContext -> ExecuteCommand(commandContext.source,
                        Radius = IntegerArgumentType.getInteger(commandContext, CommandArgumentName_Radius.string),
                        Entity = EntityArgument.getEntity(commandContext, CommandArgumentName_Target.string)) } ) )

            // /rtp <Radius(半径)> <OriginPos(随机中心，坐标)>
            .then(Commands.argument(CommandArgumentName_Radius.string, IntegerArgumentType.integer(0))
                .then(Commands.argument(CommandArgumentName_OriginPosition.string, Vec2Argument.vec2())
                    .requires(Commands.hasPermission(PermissionLevel))
                    .executes { commandContext -> ExecuteCommand(commandContext.source,
                        Radius = IntegerArgumentType.getInteger(commandContext, CommandArgumentName_Radius.string),
                        Origin = Vec2Argument.getVec2(commandContext, CommandArgumentName_OriginPosition.string)) } ) )

            // /rtp <Radius(半径)> <PlayerID(被传送玩家名)> <OriginEntity(随机中心，实体)>
            .then(Commands.argument(CommandArgumentName_Radius.string, IntegerArgumentType.integer(0))
                .then(Commands.argument(CommandArgumentName_Target.string, EntityArgument.entity())
                    .then(Commands.argument(CommandArgumentName_OriginEntity.string, EntityArgument.entity())
                        .requires(Commands.hasPermission(PermissionLevel))
                        .executes { commandContext -> ExecuteCommand(commandContext.source,
                            Radius = IntegerArgumentType.getInteger(commandContext, CommandArgumentName_Radius.string),
                            Entity = EntityArgument.getEntity(commandContext, CommandArgumentName_Target.string),
                            Origin = Vec2(
                                EntityArgument.getEntity(commandContext, CommandArgumentName_OriginEntity.string).position().x.toFloat(),
                                EntityArgument.getEntity(commandContext, CommandArgumentName_OriginEntity.string).position().z.toFloat() ) ) } ) ) )

            // /rtp <Radius(半径)> <PlayerID(被传送玩家名)> <OriginPos(随机中心，坐标)>
            .then(Commands.argument(CommandArgumentName_Radius.string, IntegerArgumentType.integer(0))
                .then(Commands.argument(CommandArgumentName_Target.string, EntityArgument.entity())
                    .then(Commands.argument(CommandArgumentName_OriginPosition.string, Vec2Argument.vec2())
                        .requires(Commands.hasPermission(PermissionLevel))
                        .executes { commandContext -> ExecuteCommand(commandContext.source,
                            Radius = IntegerArgumentType.getInteger(commandContext, CommandArgumentName_Radius.string),
                            Entity = EntityArgument.getEntity(commandContext, CommandArgumentName_Target.string),
                            Origin = Vec2Argument.getVec2(commandContext, CommandArgumentName_OriginPosition.string) ) } ) ) )

            // /rtp <PlayerID(被传送玩家名)> <Radius(半径)> <OriginEntity(随机中心，实体)>
            .then(Commands.argument(CommandArgumentName_Target.string, EntityArgument.entity())
                .then(Commands.argument(CommandArgumentName_Radius.string, IntegerArgumentType.integer(0))
                    .then(Commands.argument(CommandArgumentName_OriginEntity.string, EntityArgument.entity())
                        .requires(Commands.hasPermission(PermissionLevel))
                        .executes { commandContext -> ExecuteCommand(commandContext.source,
                            Radius = IntegerArgumentType.getInteger(commandContext, CommandArgumentName_Radius.string),
                            Entity = EntityArgument.getEntity(commandContext, CommandArgumentName_Target.string),
                            Origin = Vec2(
                                EntityArgument.getEntity(commandContext, CommandArgumentName_OriginEntity.string).position().x.toFloat(),
                                EntityArgument.getEntity(commandContext, CommandArgumentName_OriginEntity.string).position().z.toFloat() ) ) } ) ) )

            // /rtp <PlayerID(被传送玩家名)> <Radius(半径)> <OriginPos(随机中心，坐标)>
            .then(Commands.argument(CommandArgumentName_Target.string, EntityArgument.entity())
                .then(Commands.argument(CommandArgumentName_Radius.string, IntegerArgumentType.integer(0))
                    .then(Commands.argument(CommandArgumentName_OriginPosition.string, Vec2Argument.vec2())
                        .requires(Commands.hasPermission(PermissionLevel))
                        .executes { commandContext -> ExecuteCommand(commandContext.source,
                            Radius = IntegerArgumentType.getInteger(commandContext, CommandArgumentName_Radius.string),
                            Entity = EntityArgument.getEntity(commandContext, CommandArgumentName_Target.string),
                            Origin = Vec2Argument.getVec2(commandContext, CommandArgumentName_OriginPosition.string) ) } ) ) )

            // /rtp <RegionFrom(随机区域起点，坐标)> <RegionTo(随机区域终点，坐标)>
            .then(Commands.argument(CommandArgumentName_RegionFromPosition.string, Vec2Argument.vec2())
                .then(Commands.argument(CommandArgumentName_RegionToPosition.string, Vec2Argument.vec2())
                    .requires(Commands.hasPermission(PermissionLevel))
                    .executes { commandContext -> ExecuteCommand(commandContext.source,
                        RegionFrom = Vec2Argument.getVec2(commandContext, CommandArgumentName_RegionFromPosition.string),
                        RegionTo = Vec2Argument.getVec2(commandContext, CommandArgumentName_RegionToPosition.string) ) } ) )

            // /rtp <RegionFrom(随机区域起点，坐标)> <RegionTo(随机区域终点，坐标)> <PlayerID(被传送玩家名)>
            .then(Commands.argument(CommandArgumentName_RegionFromPosition.string, Vec2Argument.vec2())
                .then(Commands.argument(CommandArgumentName_RegionToPosition.string, Vec2Argument.vec2())
                    .then(Commands.argument(CommandArgumentName_Target.string, EntityArgument.entity())
                        .requires(Commands.hasPermission(PermissionLevel))
                        .executes { commandContext -> ExecuteCommand(commandContext.source,
                            Entity = EntityArgument.getEntity(commandContext, CommandArgumentName_Target.string),
                            RegionFrom = Vec2Argument.getVec2(commandContext, CommandArgumentName_RegionFromPosition.string),
                            RegionTo = Vec2Argument.getVec2(commandContext, CommandArgumentName_RegionToPosition.string) ) } ) ) )

            // /rtp <PlayerID(被传送玩家名)> <RegionFrom(随机区域起点，坐标)> <RegionTo(随机区域终点，坐标)>
            .then(Commands.argument(CommandArgumentName_Target.string, EntityArgument.entity())
                .then(Commands.argument(CommandArgumentName_RegionFromPosition.string, Vec2Argument.vec2())
                    .then(Commands.argument(CommandArgumentName_RegionToPosition.string, Vec2Argument.vec2())
                        .requires(Commands.hasPermission(PermissionLevel))
                        .executes { commandContext -> ExecuteCommand(commandContext.source,
                            Entity = EntityArgument.getEntity(commandContext, CommandArgumentName_Target.string),
                            RegionFrom = Vec2Argument.getVec2(commandContext, CommandArgumentName_RegionFromPosition.string),
                            RegionTo = Vec2Argument.getVec2(commandContext, CommandArgumentName_RegionToPosition.string) ) } ) ) )

            // /rtp <RegionFrom(随机区域起点，实体)> <RegionTo(随机区域终点，实体)>
            .then(Commands.argument(CommandArgumentName_RegionFromEntity.string, EntityArgument.entity())
                .then(Commands.argument(CommandArgumentName_RegionToEntity.string, EntityArgument.entity())
                    .requires(Commands.hasPermission(PermissionLevel))
                    .executes { commandContext -> ExecuteCommand(commandContext.source,
                        RegionFrom = Vec2(
                            EntityArgument.getEntity(commandContext, CommandArgumentName_RegionFromEntity.string).position().x.toFloat(),
                            EntityArgument.getEntity(commandContext, CommandArgumentName_RegionFromEntity.string).position().z.toFloat() ),
                        RegionTo = Vec2(
                            EntityArgument.getEntity(commandContext, CommandArgumentName_RegionToEntity.string).position().x.toFloat(),
                            EntityArgument.getEntity(commandContext, CommandArgumentName_RegionToEntity.string).position().z.toFloat() ) ) } ) )

            // /rtp <RegionFrom(随机区域起点，实体)> <RegionTo(随机区域终点，实体)> <PlayerID(被传送玩家名)>
            .then(Commands.argument(CommandArgumentName_RegionFromEntity.string, EntityArgument.entity())
                .then(Commands.argument(CommandArgumentName_RegionToEntity.string, EntityArgument.entity())
                    .then(Commands.argument(CommandArgumentName_Target.string, EntityArgument.entity())
                        .requires(Commands.hasPermission(PermissionLevel))
                        .executes { commandContext -> ExecuteCommand(commandContext.source,
                            Entity = EntityArgument.getEntity(commandContext, CommandArgumentName_Target.string),
                            RegionFrom = Vec2(
                                EntityArgument.getEntity(commandContext, CommandArgumentName_RegionFromEntity.string).position().x.toFloat(),
                                EntityArgument.getEntity(commandContext, CommandArgumentName_RegionFromEntity.string).position().z.toFloat() ),
                            RegionTo = Vec2(
                                EntityArgument.getEntity(commandContext, CommandArgumentName_RegionToEntity.string).position().x.toFloat(),
                                EntityArgument.getEntity(commandContext, CommandArgumentName_RegionToEntity.string).position().z.toFloat() ) ) } ) ) ) )
        CommandNodes.add(Node) }
    for (RootNodeName in CommandRootNodeName) {
        InGameCommandDispatcherInstance?.let {
            Register(it, RootNodeName) } }
    if (CommandRegistrationCallbackEventHasBeenRegistered) return
    for (RootNodeName in CommandRootNodeName) {
        CommandRegistrationCallback.EVENT.register { dispatcher, context, selection ->
            Register(dispatcher, RootNodeName) } }
    CommandRegistrationCallbackEventHasBeenRegistered = true }

fun ExecuteCommand(
    Source: CommandSourceStack,
    Radius: Int = WorldBorder - 1e4.toInt(),
    Entity: Entity? = Source.player,
    Origin: Vec2? = null,
    RegionFrom: Vec2? = null,
    RegionTo: Vec2? = null ): Int {

    val TargetEntity = Entity ?: run {
            Source.sendFailure( Component.translatableWithFallback("error.no_target","不存在被传送目标，由非玩家物体执行命令时请显式指定被传送玩家ID") )
            return CommandExecuteFailure }
    val RandomRadius = Radius.absoluteValue.toFloat()

    var (RandomRegionFrom, RandomRegionTo) = if (RegionFrom == null && RegionTo == null) Pair(
        Vec2(-RandomRadius, -RandomRadius), Vec2(RandomRadius, RandomRadius) )
    else Pair(
        Vec2(min(RegionFrom!!.x, RegionTo!!.x), min(RegionFrom.y, RegionTo.y)),
        Vec2(max(RegionFrom.x, RegionTo.x), max(RegionFrom.y, RegionTo.y)))

    if (Origin != null) {
        RandomRegionFrom = Vec2(Origin.x - RandomRadius, Origin.y - RandomRadius)
        RandomRegionTo = Vec2(Origin.x + RandomRadius, Origin.y + RandomRadius) }

    var (TargetX, TargetZ) = Pair(
        Random.nextInt(RandomRegionFrom.x.toInt() until RandomRegionTo.x.toInt() + 1),
        Random.nextInt(RandomRegionFrom.y.toInt() until RandomRegionTo.y.toInt() + 1) )
    TargetX = Math.clamp(TargetX.toLong(), -(WorldBorder - 1e4).toInt(), WorldBorder - 1e4.toInt() )
    TargetZ = Math.clamp(TargetZ.toLong(), -(WorldBorder - 1e4).toInt(), WorldBorder - 1e4.toInt() )

    val EntityWorld = TargetEntity.level()
    EntityWorld.getChunk(TargetX shr 4, TargetZ shr 4, ChunkStatus.FULL, true)
    val TargetY = EntityWorld.getHeight(MOTION_BLOCKING_NO_LEAVES, TargetX, TargetZ)
    val BlockPosInstance = BlockPos.MutableBlockPos()
    for (x in -1..1) {
        for (z in -1..1) {
            BlockPosInstance.set(TargetX - x, TargetY - 1, TargetZ - z)
            val CurrentBlock = EntityWorld.getBlockState(BlockPosInstance).block
            if (ReplaceToTargetBlock.contains(CurrentBlock)) {
                EntityWorld.setBlockAndUpdate(BlockPosInstance, TargetBlock.defaultBlockState()) } } }
    OldPositions[TargetEntity.uuid] = TargetEntity.position()
    TargetEntity.teleportTo(EntityWorld as ServerLevel, TargetX + 0.5, TargetY.toDouble(), TargetZ + 0.5, setOf(), TargetEntity.yRot, TargetEntity.xRot, false)
    val FeedbackFallbackString = String.format("已将玩家%s传送到%d %d %d", TargetEntity.name.string, TargetX, TargetY, TargetZ)
    Source.sendSuccess({ Component.translatableWithFallback("info.success", FeedbackFallbackString, TargetEntity.name, TargetX, TargetY, TargetZ) }, true)
    return Command.SINGLE_SUCCESS }

fun TeleportBack(Source: CommandSourceStack,
                 TargetEntity: Entity? = Source.player): Int {

    if (TargetEntity == null) {
        Source.sendFailure(
            Component.translatableWithFallback(
                "error.no_target",
                "不存在被传送目标，由非玩家物体执行命令时请显式指定被传送玩家ID"))
        return CommandExecuteFailure }
    OldPositions[TargetEntity.uuid].also {
        if (it == null) {
            Source.sendFailure(
                Component.translatableWithFallback(
                    "error.no_old_position",
                    "%s还未使用过随机传送，因此无法回溯",
                    TargetEntity.name.string ) )
            return@TeleportBack CommandExecuteFailure }

        TargetEntity.teleportTo(
            TargetEntity.level() as ServerLevel,
            it.x,
            it.y,
            it.z,
            setOf(),
            TargetEntity.yRot,
            TargetEntity.xRot,
            false)
        Source.sendSuccess({
            Component.translatableWithFallback(
                "info.success.back",
                "已将玩家%s传送回原位置",
                TargetEntity.name.string ) }, true)
        return@TeleportBack Command.SINGLE_SUCCESS } }