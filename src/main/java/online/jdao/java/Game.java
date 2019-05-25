package online.jdao.java;

import online.jdao.java.render.*;
import org.joml.AxisAngle4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import org.lwjgl.glfw.GLFWKeyCallbackI;

import java.io.IOException;

import static org.lwjgl.glfw.GLFW.*;

public class Game implements MouseInput {
    Window w;
    FrequencyLimiter fpsLimiter, upsLimiter;
    final float moveV = 0.3f;
    FileItem gun;

    Game() {
        w = new Window("Shooting Game");

        fpsLimiter = new FrequencyLimiter(0);
        upsLimiter = new FrequencyLimiter(1.0 / 20);
    }


    public void run() throws IOException {
        w.start();

        w.setMouseCallback(this);
        gun = new FileItem("Pistola38.obj");
        gun.ratio = 1.5f;
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
                gun.rot.setAngleAxis(Math.toRadians(w.cam.Yaw - 90), 0, -1, 0);
            }
            if (upsLimiter.limit())
                tick();

            System.out.print("\rfps: " + fpsLimiter.getCPS() + "\tups: " + upsLimiter.getCPS());
        }
        w.dispose();
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

    @Override
    public void onMouseMove(double xpos, double ypos) {
        double xoffset = xpos - lastX;
        double yoffset = lastY - ypos; // 注意这里是相反的，因为y坐标是从底部往顶部依次增大的
        lastX = xpos;
        lastY = ypos;

        float sensitivity = 0.05f;
        xoffset *= sensitivity;
        yoffset *= sensitivity;

        w.cam.Pitch += yoffset;
        w.cam.Yaw += xoffset;
    }
}
