package online.jdao.java;

import org.joml.LineSegmentd;
import org.joml.Rectangled;
import org.joml.Rectanglef;
import org.joml.Vector2d;

public class RayAABB {
    /**
     * 判定矩形是否相交
     */
    public static boolean isRectIntersect(double r1ax, double r1ay, double r1bx, double r1by,
                                          double r2ax, double r2ay, double r2bx, double r2by) {
        return (Math.max(r1ax, r1bx) >= Math.min(r2ax, r2bx) &&
                Math.max(r2ax, r2bx) >= Math.min(r1ax, r1bx) &&
                Math.max(r1ay, r1by) >= Math.min(r2ay, r2by) &&
                Math.max(r2ay, r2by) >= Math.min(r1ay, r1by));
    }

    public static boolean isLineSegmentExclusive(double r1ax, double r1ay, double r1bx, double r1by,
                                                 double r2ax, double r2ay, double r2bx, double r2by) {
        if (isRectIntersect(r1ax, r1ay, r1bx, r1by, r2ax, r2ay, r2bx, r2by))
            return false;

        Vector2d p1q1 = new Vector2d(r1ax, r1ay).sub(new Vector2d(r1bx, r1by));
        Vector2d p2q1 = new Vector2d(r2ax, r2ay).sub(new Vector2d(r1bx, r1by));
        Vector2d q2q1 = new Vector2d(r2bx, r2by).sub(new Vector2d(r1bx, r1by));

        Vector2d q1p1 = new Vector2d(r2bx, r2by).sub(new Vector2d(r1bx, r1by));
        Vector2d q2p1 = new Vector2d(r2bx, r2by).sub(new Vector2d(r1bx, r1by));
        Vector2d p2p1 = new Vector2d(r1bx, r1by).sub(new Vector2d(r2ax, r2bx));

        double p1xq = crossProduct(p1q1.x, p1q1.y, p2q1.x, p2q1.y);
        double p2xq = crossProduct(p2q1.x, p2q1.y, q2q1.x, q2q1.y);
        double q1xp = crossProduct(q1p1.x, q1p1.y, p2p1.x, p2p1.y);
        double q2xp = crossProduct(q2p1.x, q2p1.y, p2p1.x, p2p1.y);

        return (p1xq * p2xq <= 0.0) && (q1xp * q2xp <= 0.0);
    }

    public static double crossProduct(double x1, double y1, double x2, double y2) {
        return x1 * y2 - x2 * y1;
    }

    double maxX, maxY, maxZ;
    double minX, minY, minZ;


}
