package xyz.thewhitedog9487;

import com.mojang.brigadier.arguments.LongArgumentType;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.block.Blocks;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

import java.util.SplittableRandom;

import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;
import static xyz.thewhitedog9487.Toolbox.Distance;

public class CommandRegister {
    final static long WorldBorder = (long) 2.9e7;
    static byte Retry = 0;
    public static void Register(String Name){
        CommandRegistrationCallback.EVENT
                .register((dispatcher, registryAccess, environment) ->{
                    dispatcher.register(literal(Name)
                            .requires(source -> source.hasPermissionLevel(4))
                            .executes(context -> execute_command_radius(
                                    context.getSource(),null)));});
        CommandRegistrationCallback.EVENT
                .register((dispatcher, registryAccess, environment) -> {
                    dispatcher.register(literal(Name)
                            .then(argument("Radius(半径)", LongArgumentType.longArg())
                                    .requires(source -> source.hasPermissionLevel(4))
                                    .executes(context -> execute_command_radius(
                                            context.getSource(),
                                            LongArgumentType.getLong(context, "Radius(半径)")))));});}
    public static void Register(){
        Register("随机传送");
        Register("rtp");}
    static int execute_command_radius(ServerCommandSource Source, Long Radius){
        if (Source.getPlayer() == null) {
            Source.sendFeedback(Text.translatable("error.twd.rtp.not_player"), true);
            return -1;}
        if (Radius == null){Radius = WorldBorder - (long) 1e4;}
        Radius = Math.abs(Radius);
        long Coordinate_X = new SplittableRandom().nextLong(-Radius, Radius);
        long Coordinate_Z = new SplittableRandom().nextLong(-Radius, Radius);
        int Coordinate_Y = 320;
        for (;
             Blocks.AIR == Source.getWorld().getBlockState(new BlockPos(Coordinate_X, Coordinate_Y, Coordinate_Z)).getBlock() ||
             Blocks.VOID_AIR == Source.getWorld().getBlockState(new BlockPos(Coordinate_X, Coordinate_Y, Coordinate_Z)).getBlock() ||
             Blocks.CAVE_AIR == Source.getWorld().getBlockState(new BlockPos(Coordinate_X, Coordinate_Y, Coordinate_Z)).getBlock()
                ;Coordinate_Y--){}
        Coordinate_Y++;
        Vec3d Coordinate = new Vec3d(Coordinate_X, Coordinate_Y, Coordinate_Z);
        if (Radius == WorldBorder && Retry < 126 && Distance(Source.getPlayer().getPos(), Coordinate) < 1e5){
            Retry++;
            execute_command_radius(Source, Radius);
            return 0;}
        if (Retry >= 126){
            Source.sendFeedback(Text.translatable("warning.twd.rtp.retry"), true);}
        Source.getPlayer().teleport(Source.getWorld(), Coordinate_X, Coordinate_Y, Coordinate_Z, 0, 0);
        Source.sendFeedback(Text.translatable("info.twd.rtp.success", Source.getPlayer().getName(), Coordinate_X, Coordinate_Y, Coordinate_Z),true);
        return 0;}
}