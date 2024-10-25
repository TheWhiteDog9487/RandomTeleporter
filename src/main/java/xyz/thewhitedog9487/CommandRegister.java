package xyz.thewhitedog9487;

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
    final static Integer WorldBorder = (int) 2.9e7;
    // ↑
    // https://zh.minecraft.wiki/w/%E4%B8%96%E7%95%8C%E8%BE%B9%E7%95%8C#%E5%A4%A7%E5%B0%8F
    // https://minecraft.wiki/w/World_border#General_information
    final static byte PermissionLevel = 2;
    public static void Register(String Name){
        // /rtp
        CommandRegistrationCallback.EVENT
                .register((dispatcher, registryAccess, environment) ->{
                    dispatcher.register(literal(Name)
                            .requires(source -> source.hasPermissionLevel(PermissionLevel))
                            .executes(context -> execute_command(
                                    context.getSource(),null,null, null)));});

        // /rtp <Radius(半径)>
        CommandRegistrationCallback.EVENT
                .register((dispatcher, registryAccess, environment) -> {
                    dispatcher.register(literal(Name)
                            .then(argument("Radius(半径)", IntegerArgumentType.integer())
                                    .requires(source -> source.hasPermissionLevel(PermissionLevel))
                                    .executes(context -> execute_command(
                                            context.getSource(),
                                            IntegerArgumentType.getInteger(context, "Radius(半径)"),
                                            null,
                                            null))));});

        // /rtp <被传送玩家名(PlayerID)>
        CommandRegistrationCallback.EVENT
                .register((dispatcher, registryAccess, environment) -> {
                    dispatcher.register(literal(Name)
                            .then(argument("被传送玩家名(PlayerID)", EntityArgumentType.entity())
                                    .requires(source -> source.hasPermissionLevel(PermissionLevel))
                                    .executes(context -> execute_command(
                                            context.getSource(),
                                            null,
                                            EntityArgumentType.getEntity(context,"被传送玩家名(PlayerID)"),
                                            null))));});

        // /rtp <Radius(半径)> <被传送玩家名(PlayerID)>
        CommandRegistrationCallback.EVENT
                .register((dispatcher, registryAccess, environment) -> {
                    dispatcher.register(literal(Name)
                            .then(argument("Radius(半径)", IntegerArgumentType.integer())
                                    .then(argument("被传送玩家名(PlayerID)", EntityArgumentType.entity())
                                            .requires(source -> source.hasPermissionLevel(PermissionLevel))
                                            .executes(context -> execute_command(
                                                    context.getSource(),
                                                    IntegerArgumentType.getInteger(context, "Radius(半径)"),
                                                    EntityArgumentType.getEntity(context,"被传送玩家名(PlayerID)"),
                                                    null)))));});

        // /rtp <被传送玩家名(PlayerID)> <Radius(半径)>
        CommandRegistrationCallback.EVENT
                .register((dispatcher, registryAccess, environment) -> {
                    dispatcher.register(literal(Name)
                            .then(argument("被传送玩家名(PlayerID)", EntityArgumentType.entity())
                                .then(argument("Radius(半径)", IntegerArgumentType.integer())
                                            .requires(source -> source.hasPermissionLevel(PermissionLevel))
                                            .executes(context -> execute_command(
                                                    context.getSource(),
                                                    IntegerArgumentType.getInteger(context, "Radius(半径)"),
                                                    EntityArgumentType.getEntity(context,"被传送玩家名(PlayerID)"),
                                                    null)))));});

//        // /rtp <Radius(半径)> <Origin(随机中心)>
//        CommandRegistrationCallback.EVENT
//                .register((dispatcher, registryAccess, environment) -> {
//                    dispatcher.register(literal(Name)
//                            .then(argument("Radius(半径)", LongArgumentType.longArg())
//                                    .then(argument("Origin(随机中心)",EntityArgumentType.player())
//                                        .requires(source -> source.hasPermissionLevel(PermissionLevel))
//                                        .executes(context -> execute_command_origin(
//                                                context.getSource(),
//                                                LongArgumentType.getLong(context, "Radius(半径)"),
//                                                null,
//                                                EntityArgumentType.getEntity(context,"Origin(随机中心)"))))));});
        // /rtp <Radius(半径)> <OriginPos(随机中心，坐标)>
        CommandRegistrationCallback.EVENT
                .register((dispatcher, registryAccess, environment) -> {
                    dispatcher.register(literal(Name)
                            .then(argument("Radius(半径)", IntegerArgumentType.integer())
                                    .then(argument("OriginPos(随机中心，坐标)",Vec2ArgumentType.vec2())
                                            .requires(source -> source.hasPermissionLevel(PermissionLevel))
                                            .executes(context -> execute_command(
                                                    context.getSource(),
                                                    IntegerArgumentType.getInteger(context, "Radius(半径)"),
                                                    null,
                                                    Vec2ArgumentType.getVec2(context,"OriginPos(随机中心，坐标)"))))));});

        // /rtp <Radius(半径)> <被传送玩家名(PlayerID)> <OriginEntity(随机中心，实体)>
        CommandRegistrationCallback.EVENT
                .register((dispatcher, registryAccess, environment) -> {
                    dispatcher.register(literal(Name)
                            .then(argument("Radius(半径)", IntegerArgumentType.integer())
                                    .then(argument("被传送玩家名(PlayerID)", EntityArgumentType.entity())
                                            .then(argument("OriginEntity(随机中心，实体)",EntityArgumentType.entity())
                                                    .requires(source -> source.hasPermissionLevel(PermissionLevel))
                                                    .executes(context -> execute_command(
                                                            context.getSource(),
                                                            IntegerArgumentType.getInteger(context, "Radius(半径)"),
                                                            EntityArgumentType.getEntity(context,"被传送玩家名(PlayerID)"),
                                                            new Vec2f( (float) EntityArgumentType.getEntity( context,"OriginEntity(随机中心，实体)").getPos().x,
                                                                    (float) EntityArgumentType.getEntity( context,"OriginEntity(随机中心，实体)").getPos().y )))))));});

        // /rtp <Radius(半径)> <被传送玩家名(PlayerID)> <OriginPos(随机中心，坐标)>
        CommandRegistrationCallback.EVENT
                .register((dispatcher, registryAccess, environment) -> {
                    dispatcher.register(literal(Name)
                            .then(argument("Radius(半径)", IntegerArgumentType.integer())
                                    .then(argument("被传送玩家名(PlayerID)", EntityArgumentType.entity())
                                            .then(argument("OriginPos(随机中心，坐标)",Vec2ArgumentType.vec2())
                                                    .requires(source -> source.hasPermissionLevel(PermissionLevel))
                                                    .executes(context -> execute_command(
                                                            context.getSource(),
                                                            IntegerArgumentType.getInteger(context, "Radius(半径)"),
                                                            EntityArgumentType.getEntity(context,"被传送玩家名(PlayerID)"),
                                                            Vec2ArgumentType.getVec2(context,"OriginPos(随机中心，坐标)")))))));});

        // /rtp <被传送玩家名(PlayerID)> <Radius(半径)> <OriginEntity(随机中心，实体)>
        CommandRegistrationCallback.EVENT
                .register((dispatcher, registryAccess, environment) -> {
                    dispatcher.register(literal(Name)
                            .then(argument("被传送玩家名(PlayerID)", EntityArgumentType.entity())
                                    .then(argument("Radius(半径)", IntegerArgumentType.integer())
                                            .then(argument("OriginEntity(随机中心，实体)",EntityArgumentType.entity())
                                                    .requires(source -> source.hasPermissionLevel(PermissionLevel))
                                                    .executes(context -> execute_command(
                                                            context.getSource(),
                                                            IntegerArgumentType.getInteger(context, "Radius(半径)"),
                                                            EntityArgumentType.getEntity(context,"被传送玩家名(PlayerID)"),
                                                            new Vec2f( (float) EntityArgumentType.getEntity( context,"OriginEntity(随机中心，实体)").getPos().x,
                                                                    (float) EntityArgumentType.getEntity( context,"OriginEntity(随机中心，实体)").getPos().y )))))));});

        // /rtp <被传送玩家名(PlayerID)> <Radius(半径)> <OriginPos(随机中心，坐标)>
        CommandRegistrationCallback.EVENT
                .register((dispatcher, registryAccess, environment) -> {
                    dispatcher.register(literal(Name)
                            .then(argument("被传送玩家名(PlayerID)", EntityArgumentType.entity())
                                    .then(argument("Radius(半径)", IntegerArgumentType.integer())
                                            .then(argument("OriginPos(随机中心，坐标)",Vec2ArgumentType.vec2())
                                                    .requires(source -> source.hasPermissionLevel(PermissionLevel))
                                                    .executes(context -> execute_command(
                                                            context.getSource(),
                                                            IntegerArgumentType.getInteger(context, "Radius(半径)"),
                                                            EntityArgumentType.getEntity(context,"被传送玩家名(PlayerID)"),
                                                            Vec2ArgumentType.getVec2(context,"OriginPos(随机中心，坐标)")))))));});}

    public static void Register(){
        Register("随机传送");
        Register("rtp");}
    static int execute_command(ServerCommandSource Source, @Nullable Integer Radius, @Nullable Entity Player, @Nullable Vec2f Origin){
        Entity entity = Player == null ? Source.getPlayer() : Player;
        if (entity == null) {
            Source.sendFeedback(()->{ return  Text.translatable("error.not_player"); }, true);
            return -1;}
        if (Radius == null){Radius = (int) (WorldBorder - 1e4);}
        // ↑ 远离世界边界
        Radius = Math.abs(Radius);
        int Coordinate_X;
        int Coordinate_Z;
        if (Origin == null){
            Coordinate_X = new SplittableRandom().nextInt(-Radius, Radius);
            Coordinate_Z = new SplittableRandom().nextInt(-Radius, Radius);}
        else{
            Coordinate_X = new SplittableRandom().nextInt((int) Origin.x - Radius, (int) Origin.x + Radius);
            Coordinate_Z = new SplittableRandom().nextInt((int) Origin.y - Radius, (int) Origin.y + Radius);}
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
        int finalCoordinate_Y = Coordinate_Y;
        Source.sendFeedback(()->{ return  Text.translatable("info.success", entity.getName(), Coordinate_X, finalCoordinate_Y, Coordinate_Z); },true);
        return 0;}
}