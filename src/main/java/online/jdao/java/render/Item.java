package online.jdao.java.render;


import org.joml.*;

public interface Item {

    Vector3f pos = new Vector3f(0, 0, 0);
    Quaternionf rot = new Quaternionf(new AxisAngle4f());

    public void Draw();
    public float ratio();

}
