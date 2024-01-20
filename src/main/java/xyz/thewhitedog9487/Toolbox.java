package xyz.thewhitedog9487;

import net.minecraft.util.math.Vec3d;

public class Toolbox {
    static public double Distance(Vec3d A, Vec3d B){
        return Math.sqrt(
                Math.pow(A.getX() - B.getX(), 2) +
                Math.pow(A.getY() - B.getY(), 2));}
}