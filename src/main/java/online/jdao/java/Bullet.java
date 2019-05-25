package online.jdao.java;

import online.jdao.java.render.Item;
import online.jdao.java.render.TestItem;
import org.joml.Vector3f;

public class Bullet extends TestItem implements Item {
    Vector3f velocity;

    public Bullet(Vector3f pos, Vector3f dir) {
        this.pos = new Vector3f(pos);
        velocity = new Vector3f(dir).normalize().mul(3);

    }

    public void tick() {
        pos.add(velocity);

    }

}
