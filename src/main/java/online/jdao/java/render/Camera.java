package online.jdao.java.render;

import org.joml.Math;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;

public class Camera {
    Matrix4f projection;
    public float Roll, Yaw, Pitch;
    public Vector3f pos;
    public Vector3f front;
    public Vector3f up = new Vector3f(0, 1, 0);

    public Camera(int width, int height) {
        projection = new Matrix4f().setPerspective(
                (float) Math.toRadians(45),
                (float) width / height,
                0.1f, 100.0f
        );
        pos = new Vector3f();
//        Pitch = 1;
    }

    public void updateSize(int width, int height) {
        projection = new Matrix4f().setPerspective(
                (float) Math.toRadians(45),
                (float) width / height,
                0.1f, 100.0f
        );
    }

    public Matrix4f getView() {
        front = new Vector3f(
                (float) (Math.cos(Math.toRadians(Pitch)) * Math.cos(Math.toRadians(Yaw))),
                (float) Math.sin(Math.toRadians(Pitch)),
                (float) (Math.cos(Math.toRadians(Pitch)) * Math.sin(Math.toRadians(Yaw)))
        ).normalize();
        return new Matrix4f().lookAt(pos, new Vector3f(pos).add(front), up);
    }

    public Matrix4f getProjection() {
        return projection;
    }
}
