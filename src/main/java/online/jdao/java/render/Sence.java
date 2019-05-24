package online.jdao.java.render;

import org.joml.*;
import org.joml.Math;
import org.lwjgl.BufferUtils;

import java.io.IOException;
import java.io.InputStream;
import java.nio.FloatBuffer;
import java.util.HashSet;
import java.util.Set;

import static org.lwjgl.opengl.GL33C.*;

public class Sence {
    ShaderProgram sp, sky;
    public Item skybox;
    int skyUniProjection, skyUniView;
    int uniProjection, uniView, uniModel;
    public Set<Item> items = new HashSet<Item>();
    FloatBuffer fb = BufferUtils.createFloatBuffer(16);

    public Sence() throws IOException {
        init();
    }

    public void init() throws IOException {
        Shader vertShader, fragShader;

        vertShader = new Shader(GL_VERTEX_SHADER, getCode("main.vert"));
        fragShader = new Shader(GL_FRAGMENT_SHADER, getCode("main.frag"));

        sp = new ShaderProgram(vertShader, fragShader);
        vertShader.delete();
        fragShader.delete();


        vertShader = new Shader(GL_VERTEX_SHADER, getCode("sky.vert"));
        fragShader = new Shader(GL_FRAGMENT_SHADER, getCode("sky.frag"));

        sky = new ShaderProgram(vertShader, fragShader);
        vertShader.delete();
        fragShader.delete();

        uniProjection = sp.GetUniformLocation("projection");
        uniView = sp.GetUniformLocation("view");
        uniModel = sp.GetUniformLocation("model");

        skyUniProjection = sky.GetUniformLocation("projection");
        skyUniView = sky.GetUniformLocation("view");
    }

    protected void render(Camera cam) {
        sp.Use();
        glUniformMatrix4fv(uniProjection, false, cam.getProjection().get(fb));
        glUniformMatrix4fv(uniView, false, cam.getView().get(fb));
        for (Item item : items) {
            Matrix4f model = new Matrix4f().
                    rotate(item.rot).
                    translateLocal(item.pos);
            glUniformMatrix4fv(uniModel, false, model.get(fb));
            item.Draw();
        }

        sky.Use();
        glUniformMatrix4fv(skyUniProjection, false, cam.getProjection().get(fb));
        glUniformMatrix4fv(skyUniView, false, new Matrix4f(new Matrix3f(cam.getView())).get(fb));
        skybox.Draw();

    }


    public String getCode(String name) throws IOException {
        try (InputStream is = this.getClass().getResourceAsStream("/glsl/" + name)) {
            String code = new String(is.readAllBytes());
            is.close();
            return code;
        }
    }

}
