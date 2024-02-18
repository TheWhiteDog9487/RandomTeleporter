package xyz.thewhitedog9487;

import com.mojang.brigadier.arguments.LongArgumentType;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.block.Blocks;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.command.argument.Vec3ArgumentType;
import net.minecraft.entity.Entity;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import org.jetbrains.annotations.Nullable;

import java.util.SplittableRandom;

import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

public class CommandRegister {
    final static long WorldBorder = (long) 2.9e7;
    static byte Retry = 0;
    static byte PermissionLevel = 2;
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
                            .then(argument("Radius(半径)", LongArgumentType.longArg())
                                    .requires(source -> source.hasPermissionLevel(PermissionLevel))
                                    .executes(context -> execute_command(
                                            context.getSource(),
                                            LongArgumentType.getLong(context, "Radius(半径)"),
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
                            .then(argument("Radius(半径)", LongArgumentType.longArg())
                                    .then(argument("被传送玩家名(PlayerID)", EntityArgumentType.entity())
                                            .requires(source -> source.hasPermissionLevel(PermissionLevel))
                                            .executes(context -> execute_command(
                                                    context.getSource(),
                                                    LongArgumentType.getLong(context, "Radius(半径)"),
                                                    EntityArgumentType.getEntity(context,"被传送玩家名(PlayerID)"),
                                                    null)))));});

        // /rtp <被传送玩家名(PlayerID)> <Radius(半径)>
        CommandRegistrationCallback.EVENT
                .register((dispatcher, registryAccess, environment) -> {
                    dispatcher.register(literal(Name)
                            .then(argument("被传送玩家名(PlayerID)", EntityArgumentType.entity())
                                .then(argument("Radius(半径)", LongArgumentType.longArg())
                                            .requires(source -> source.hasPermissionLevel(PermissionLevel))
                                            .executes(context -> execute_command(
                                                    context.getSource(),
                                                    LongArgumentType.getLong(context, "Radius(半径)"),
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
                            .then(argument("Radius(半径)", LongArgumentType.longArg())
                                    .then(argument("OriginPos(随机中心，坐标)",Vec3ArgumentType.vec3())
                                            .requires(source -> source.hasPermissionLevel(PermissionLevel))
                                            .executes(context -> execute_command(
                                                    context.getSource(),
                                                    LongArgumentType.getLong(context, "Radius(半径)"),
                                                    null,
                                                    Vec3ArgumentType.getVec3(context,"OriginPos(随机中心，坐标)"))))));});

        // /rtp <Radius(半径)> <被传送玩家名(PlayerID)> <OriginEntity(随机中心，实体)>
        CommandRegistrationCallback.EVENT
                .register((dispatcher, registryAccess, environment) -> {
                    dispatcher.register(literal(Name)
                            .then(argument("Radius(半径)", LongArgumentType.longArg())
                                    .then(argument("被传送玩家名(PlayerID)", EntityArgumentType.entity())
                                            .then(argument("OriginEntity(随机中心，实体)",EntityArgumentType.entity())
                                                    .requires(source -> source.hasPermissionLevel(PermissionLevel))
                                                    .executes(context -> execute_command_origin(
                                                            context.getSource(),
                                                            LongArgumentType.getLong(context, "Radius(半径)"),
                                                            EntityArgumentType.getEntity(context,"被传送玩家名(PlayerID)"),
                                                            EntityArgumentType.getEntity(context,"OriginEntity(随机中心，实体)")))))));});

        // /rtp <Radius(半径)> <被传送玩家名(PlayerID)> <OriginPos(随机中心，坐标)>
        CommandRegistrationCallback.EVENT
                .register((dispatcher, registryAccess, environment) -> {
                    dispatcher.register(literal(Name)
                            .then(argument("Radius(半径)", LongArgumentType.longArg())
                                    .then(argument("被传送玩家名(PlayerID)", EntityArgumentType.entity())
                                            .then(argument("OriginPos(随机中心，坐标)",Vec3ArgumentType.vec3())
                                                    .requires(source -> source.hasPermissionLevel(PermissionLevel))
                                                    .executes(context -> execute_command(
                                                            context.getSource(),
                                                            LongArgumentType.getLong(context, "Radius(半径)"),
                                                            EntityArgumentType.getEntity(context,"被传送玩家名(PlayerID)"),
                                                            Vec3ArgumentType.getVec3(context,"OriginPos(随机中心，坐标)")))))));});

        // /rtp <被传送玩家名(PlayerID)> <Radius(半径)> <OriginEntity(随机中心，实体)>
        CommandRegistrationCallback.EVENT
                .register((dispatcher, registryAccess, environment) -> {
                    dispatcher.register(literal(Name)
                            .then(argument("被传送玩家名(PlayerID)", EntityArgumentType.entity())
                                    .then(argument("Radius(半径)", LongArgumentType.longArg())
                                            .then(argument("OriginEntity(随机中心，实体)",EntityArgumentType.entity())
                                                    .requires(source -> source.hasPermissionLevel(PermissionLevel))
                                                    .executes(context -> execute_command_origin(
                                                            context.getSource(),
                                                            LongArgumentType.getLong(context, "Radius(半径)"),
                                                            EntityArgumentType.getEntity(context,"被传送玩家名(PlayerID)"),
                                                            EntityArgumentType.getEntity(context,"OriginEntity(随机中心，实体)")))))));});

        // /rtp <被传送玩家名(PlayerID)> <Radius(半径)> <OriginPos(随机中心，坐标)>
        CommandRegistrationCallback.EVENT
                .register((dispatcher, registryAccess, environment) -> {
                    dispatcher.register(literal(Name)
                            .then(argument("被传送玩家名(PlayerID)", EntityArgumentType.entity())
                                    .then(argument("Radius(半径)", LongArgumentType.longArg())
                                            .then(argument("OriginPos(随机中心，坐标)",Vec3ArgumentType.vec3())
                                                    .requires(source -> source.hasPermissionLevel(PermissionLevel))
                                                    .executes(context -> execute_command(
                                                            context.getSource(),
                                                            LongArgumentType.getLong(context, "Radius(半径)"),
                                                            EntityArgumentType.getEntity(context,"被传送玩家名(PlayerID)"),
                                                            Vec3ArgumentType.getVec3(context,"OriginPos(随机中心，坐标)")))))));});}
    public static void Register(){
        Register("随机传送");
        Register("rtp");}
    static int execute_command(ServerCommandSource Source, @Nullable Long Radius, @Nullable Entity Player, @Nullable Vec3d Origin){
        Entity entity = Player == null ? Source.getPlayer() : Player;
        if (entity == null) {
            Source.sendFeedback(()->{ return  Text.translatable("error.twd.rtp.not_player"); }, true);
            return -1;}
        if (Radius == null){Radius = WorldBorder - (long) 1e4;}
        Radius = Math.abs(Radius);
        long Coordinate_X;
        long Coordinate_Z;
        if (Origin == null){
            Coordinate_X = new SplittableRandom().nextLong(-Radius, Radius);
            Coordinate_Z = new SplittableRandom().nextLong(-Radius, Radius);}
        else{
            Coordinate_X = new SplittableRandom().nextLong(Math.round(Origin.getX() - Radius), Math.round(Origin.getX() + Radius));
            Coordinate_Z = new SplittableRandom().nextLong(Math.round(Origin.getZ() - Radius), Math.round(Origin.getZ() + Radius));}
        int Coordinate_Y = 320;
        for (;
             Blocks.AIR == Source.getWorld().getBlockState(new BlockPos(Math.toIntExact(Coordinate_X), Coordinate_Y, Math.toIntExact(Coordinate_Z))).getBlock() ||
             Blocks.VOID_AIR == Source.getWorld().getBlockState(new BlockPos(Math.toIntExact(Coordinate_X), Coordinate_Y, Math.toIntExact(Coordinate_Z))).getBlock() ||
             Blocks.CAVE_AIR == Source.getWorld().getBlockState(new BlockPos(Math.toIntExact(Coordinate_X), Coordinate_Y, Math.toIntExact(Coordinate_Z))).getBlock()
                ;Coordinate_Y--){}
        if (Blocks.WATER == Source.getWorld().getBlockState(new BlockPos(Math.toIntExact(Coordinate_X), Coordinate_Y, Math.toIntExact(Coordinate_Z))).getBlock() ||
            Blocks.LAVA == Source.getWorld().getBlockState(new BlockPos(Math.toIntExact(Coordinate_X), Coordinate_Y, Math.toIntExact(Coordinate_Z))).getBlock()){
            for (int x = -1; x <= 1; x++) {
                for (int z = -1; z <= 1; z++) {
                    Source.getWorld().setBlockState(new BlockPos(Math.toIntExact(Coordinate_X - x), Coordinate_Y, Math.toIntExact(Coordinate_Z - z)), Blocks.GLASS.getDefaultState());}}}
//        if ( String.valueOf(entity.getWorld().getBiome(new BlockPos(Math.toIntExact(Coordinate_X), Coordinate_Y, Math.toIntExact(Coordinate_Z))).getKey()).equals("minecraft:the_void") ) {
//            Coordinate_Y++;}
        Coordinate_Y++;
        Vec3d Coordinate = new Vec3d(Coordinate_X, Coordinate_Y, Coordinate_Z);
        if (Radius == WorldBorder && Retry < 126 && entity.getPos().distanceTo(Coordinate) < 1e5){
            Retry++;
            execute_command(Source, Radius,null, Origin);
            return 0;}
        if (Retry >= 126){
            Source.sendFeedback(()->{ return  Text.translatable("warning.twd.rtp.retry"); }, true);}
        entity.teleport(Coordinate_X + 0.5, Coordinate_Y, Coordinate_Z + 0.5);
        final long FinalCoordinate_X = Coordinate_X;
        final int FinalCoordinate_Y = Coordinate_Y;
        final long FinalCoordinate_Z = Coordinate_Z;
        Source.sendFeedback(()->{ return  Text.translatable("info.twd.rtp.success", entity.getName(), FinalCoordinate_X, FinalCoordinate_Y, FinalCoordinate_Z); },true);
        return 0;}
    static int execute_command_origin(ServerCommandSource Source, @Nullable Long Radius, @Nullable Entity Player, Entity Origin){
        return execute_command(Source, Radius, Player, Origin.getPos());}
}