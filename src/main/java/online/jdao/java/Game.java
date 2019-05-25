package online.jdao.java;

import online.jdao.java.render.*;
import org.joml.AxisAngle4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import org.lwjgl.glfw.GLFWCursorPosCallbackI;
import org.lwjgl.glfw.GLFWKeyCallbackI;
import org.lwjgl.glfw.GLFWMouseButtonCallbackI;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import static org.lwjgl.glfw.GLFW.*;

public class Game implements AutoCloseable {
    Window w;
    FrequencyLimiter fpsLimiter, upsLimiter;
    final float moveV = 0.3f;
    FileItem gun;
    Set<Monster> monsters = new HashSet<Monster>();
    Set<Bullet> bullets = new HashSet<Bullet>();

    public Game() {
        w = new Window("Shooting Game");
        fpsLimiter = new FrequencyLimiter(0);
        upsLimiter = new FrequencyLimiter(1.0 / 20);
    }


    final Vector3f up = new Vector3f(0, 1, 0);
    float gunRot = 0;

    public void run() throws IOException {
        w.start();
        w.setMouseMoveCallback(mouseMoveListener);
        w.setMouseButtonCallback(mouseButtonListener);
        gun = new FileItem("Pistola38.obj");
        gun.ratio = 1.5f;
        gun.scale = 0.01f;

        w.sence = new Sence();
        w.sence.items.add(gun);
//        w.sence.items.add(new TestItem());
        w.sence.skybox = new SkyBox(
                "right.jpg",
                "left.jpg",
                "top.jpg",
                "bottom.jpg",
                "front.jpg",
                "back.jpg"
        );


        w.cam.pos.add(0, 3, 20);

        gun.rot.rotateLocalX((float) Math.toRadians(90)).rotateLocalY((float) Math.toRadians(-140));
        gun.pos.add(0, -10f, 0);


        Vector3f lastGunFront = new Vector3f(gun.pos);
        while (true) {                      //The Game Loop
            if (fpsLimiter.limit()) {
                if (w.render()) break;
                input();

                if (gunRot > 0) gunRot -= 1f;
                gun.rot.setAngleAxis(Math.toRadians(w.cam.Yaw - 90), 0, -1, 0).
                        rotateX((float) Math.toRadians(90 - gunRot));
                Vector3f front = new Vector3f(w.cam.front).mul(0.5f);
                front.add(lastGunFront).div(2);
                lastGunFront = front;
                Vector3f right = new Vector3f(front).cross(up);
                gun.pos.set(w.cam.pos).
                        add(front).                 //向前
                        add(right.mul(0.3f)).       //向右一点
                        add(0, -0.2f, 0);   //向下一点

            }
            if (upsLimiter.limit())
                tick();

            System.out.print("\rfps: " + fpsLimiter.getCPS() + "\tups: " + upsLimiter.getCPS());
        }
    }


    private void tick() {
        if (Math.random() < 1.0 / 40) {//刷怪
            Monster m = new Monster();
            monsters.add(m);
            m.join(w);

            m.pos = new Vector3f(w.cam.pos).
                    add((float) Math.random() * 50 + 50, 0, (float) Math.random() * 50 + 50);
        }
        for (Monster m : monsters) {//怪物移动
            m.tick(new Vector3f(w.cam.pos).sub(0, 3, 0));
        }
        Object[] buls = bullets.toArray();
        for (Object b : buls){
            ((Bullet) b).tick();
            if ( new Vector3f(((Bullet) b).pos).sub(w.cam.pos).length()>100)
            {
                bullets.remove(b);
                w.sence.items.remove(b);
            }
        }
        Object[] mons = monsters.toArray();
        buls = bullets.toArray();
        for (Object m : mons) {
            AABB box = ((Monster) m).box();
            for (Object b : buls)
                if (box.inside(((Bullet) b).pos)) {
                    bullets.remove(b);
                    monsters.remove(m);
                    w.sence.items.remove(b);
                    w.sence.items.remove(m);
                }
        }
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

    private GLFWCursorPosCallbackI mouseMoveListener = (window, xpos, ypos) -> {
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

    private GLFWMouseButtonCallbackI mouseButtonListener = (window, button, action, mods) -> {
        if (button == GLFW_MOUSE_BUTTON_LEFT && action == GLFW_PRESS) {
            gunRot = 30;
            Bullet b = new Bullet(w.cam.pos, w.cam.front);
            bullets.add(b);
            w.sence.items.add(b);
        }
    };

    public void close() {
        w.close();
    }
}
