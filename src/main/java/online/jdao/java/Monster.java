package online.jdao.java;

import online.jdao.java.render.FileItem;
import online.jdao.java.render.Window;
import org.joml.Vector3f;

public class Monster extends FileItem {
    public Monster() {
        super("ding.obj");
        ratio = 1;
        scale = 0.1f;

    }

    public void join(Window w) {
        w.sence.items.add(this);
    }

    @Override
    public void Draw() {
        super.Draw();
        if (ratio < 1.3f)
            ratio += 0.003;
    }

    final static Vector3f up = new Vector3f(0, 1, 0);

    public void tick(Vector3f target) {
        pos.add(target.sub(pos).normalize().mul(0.5f));
    }

    public AABB box() {
        return new AABB(
                new Vector3f(pos).add(0, 1.5f, 3),
                6);
    }
}
