package online.jdao.java;

import org.joml.*;

public class AABB {
    double maxX, maxY, maxZ;
    double minX, minY, minZ;

    public AABB(Vector3f pos, float size) {
        size /= 2;
        maxX = pos.x + size;
        minX = pos.x - size;

        maxY = pos.y + size;
        minY = pos.y - size;

        maxZ = pos.z + size;
        minZ = pos.z - size;
    }

//    public boolean rayCross(Vector3f pos, Vector3f dir) {
//
//    }

    public boolean inside(Vector3f pos) {
        return (pos.x < maxX && pos.x > minX) &&
                (pos.y < maxY && pos.y > minY) &&
                (pos.z < maxZ && pos.z > minZ);
    }
}
