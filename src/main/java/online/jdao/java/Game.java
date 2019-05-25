package online.jdao.java;

import online.jdao.java.render.*;
import org.joml.AxisAngle4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import org.lwjgl.glfw.GLFWCursorPosCallbackI;
import org.lwjgl.glfw.GLFWKeyCallbackI;

import java.io.IOException;

import static org.lwjgl.glfw.GLFW.*;

public class Game implements AutoCloseable {
    Window w;
    FrequencyLimiter fpsLimiter, upsLimiter;
    final float moveV = 0.3f;
    FileItem gun;

    public Game() {
        w = new Window("Shooting Game");
        fpsLimiter = new FrequencyLimiter(0);
        upsLimiter = new FrequencyLimiter(1.0 / 20);
    }


    final Vector3f up = new Vector3f(0,1,0);
    public void run() throws IOException {
        w.start();
        w.setMouseCallback(mouseListener);
        gun = new FileItem("Pistola38.obj");
        gun.ratio = 1.5f;
        gun.scale = 0.01f;
        w.sence = new Sence();
        w.sence.items.add(gun);
        w.sence.skybox = new SkyBox(
                "right.jpg",
                "left.jpg",
                "top.jpg",
                "bottom.jpg",
                "front.jpg",
                "back.jpg"
        );

        w.cam.pos.add(0, 0, -33);
        gun.rot.rotateLocalX((float) Math.toRadians(90)).rotateLocalY((float) Math.toRadians(-140));
        gun.pos.add(0, -10f, 0);

        while (true) {                      //The Game Loop
            if (fpsLimiter.limit()) {
                if (w.render()) break;

                input();
                gun.rot.setAngleAxis(Math.toRadians(w.cam.Yaw - 90), 0, -1, 0).
                        rotateX((float) Math.toRadians(90));
                Vector3f front = new Vector3f(w.cam.front).mul(0.5f);
                Vector3f right = new Vector3f(front).cross(up);
                gun.pos.set(w.cam.pos).
                        add(front).                 //向前
                        add(right.mul(0.3f)).       //向右一点
                        add(0,-0.2f,0);   //向下一点

            }
            if (upsLimiter.limit())
                tick();

            System.out.print("\rfps: " + fpsLimiter.getCPS() + "\tups: " + upsLimiter.getCPS());
        }
    }

    private void tick() {
    }

    private void input() {
        Vector3f move = new Vector3f();
        if (w.getKey(GLFW_KEY_W) == GLFW_PRESS)
            move.add(w.cam.front);
        if (w.getKey(GLFW_KEY_S) == GLFW_PRESS)
            move.add(new Vector3f(w.cam.front).mul(-1));
        if (w.getKey(GLFW_KEY_A) == GLFW_PRESS)
            move.add(new Vector3f(w.cam.front).cross(w.cam.up).normalize().mul(-1));
        if (w.getKey(GLFW_KEY_D) == GLFW_PRESS)
            move.add(new Vector3f(w.cam.front).cross(w.cam.up).normalize());

        move.y = 0;
        move.mul(moveV);
        w.cam.pos.add(move);
    }

    double lastX = 400, lastY = 300;

    private GLFWCursorPosCallbackI mouseListener = (window, xpos, ypos) -> {
        double xoffset = xpos - lastX;
        double yoffset = lastY - ypos; // 注意这里是相反的，因为y坐标是从底部往顶部依次增大的
        lastX = xpos;
        lastY = ypos;

        float sensitivity = 0.05f;
        xoffset *= sensitivity;
        yoffset *= sensitivity;

        w.cam.Pitch += yoffset;
        w.cam.Yaw += xoffset;
    };

    public void close() {
        w.close();
    }
}
