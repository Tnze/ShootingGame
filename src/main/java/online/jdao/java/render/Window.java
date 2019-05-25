package online.jdao.java.render;

import org.joml.*;
import org.lwjgl.BufferUtils;
import org.lwjgl.glfw.*;
import org.lwjgl.opengl.*;
import org.lwjgl.system.*;

import java.io.IOException;
import java.io.InputStream;
import java.nio.*;


import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL33C.*;
import static org.lwjgl.system.MemoryStack.*;
import static org.lwjgl.system.MemoryUtil.*;

public class Window implements AutoCloseable {
    final boolean PolygonMode = false;

    long window;
    int width, height;
    public Sence sence;
    public Camera cam;

    FloatBuffer fb = BufferUtils.createFloatBuffer(16);


    public Window(String title) {
        init(title, 800, 600);
    }

    Window(String title, int width, int height) {
        init(title, width, height);
    }

    /**
     * Init GLFW and create windows. Create OpenGL context.
     *
     * @param title  is the window's title.
     * @param width  is the window's width.
     * @param height is the window's height.
     */
    private void init(String title, int width, int height) {
        if (!glfwInit())
            throw new IllegalStateException("Unable to initialize GLFW");

        // Configure GLFW
        glfwDefaultWindowHints(); // optional, the current window hints are already the default
        glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE); // the window will stay hidden after creation
        glfwWindowHint(GLFW_RESIZABLE, GLFW_TRUE); // the window will be resizable
        glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR, 3);//使用OpenGL 3.3
        glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR, 3);
        glfwWindowHint(GLFW_OPENGL_PROFILE, GLFW_OPENGL_CORE_PROFILE);
        glfwWindowHint(GLFW_OPENGL_FORWARD_COMPAT, GLFW_TRUE);

        window = glfwCreateWindow(width, height, title, NULL, NULL);
        if (window == NULL)
            throw new RuntimeException("Failed to create the GLFW window");

        // Setup a key callback. It will be called every time a key is pressed, repeated or released.
        glfwSetKeyCallback(window, (window, key, scancode, action, mods) -> {
            if (key == GLFW_KEY_ESCAPE && action == GLFW_RELEASE)
                glfwSetWindowShouldClose(window, true); // We will detect this in the rendering loop
        });
        glfwSetInputMode(window, GLFW_CURSOR, GLFW_CURSOR_DISABLED);
        glfwSetFramebufferSizeCallback(window, (window, w, h) -> {
            glViewport(0, 0, w, h);
            this.width = w;
            this.height = h;
            if (cam != null)
                cam.updateSize(w, h);
        });

        // Get the thread stack and push a new frame
        try (MemoryStack stack = stackPush()) {
            IntBuffer pWidth = stack.mallocInt(1); // int*
            IntBuffer pHeight = stack.mallocInt(1); // int*

            // Get the window size passed to glfwCreateWindow
            glfwGetWindowSize(window, pWidth, pHeight);

            // Get the resolution of the primary monitor
            GLFWVidMode vidmode = glfwGetVideoMode(glfwGetPrimaryMonitor());


            // Center the window
            glfwSetWindowPos(
                    window,
                    (vidmode.width() - pWidth.get(0)) / 2,
                    (vidmode.height() - pHeight.get(0)) / 2
            );

            this.width = pWidth.get();
            this.height = pHeight.get();
        } // the stack frame is popped automatically

        // Make the OpenGL context current
        glfwMakeContextCurrent(window);
        // Enable v-sync
        glfwSwapInterval(1);

        // Make the window visible
        glfwShowWindow(window);
    }

    public void start() throws IOException {
        GL.createCapabilities();
        if (PolygonMode)
            glPolygonMode(GL_FRONT_AND_BACK, GL_LINE);//线框模式

        glEnable(GL_DEPTH_TEST);
        glClearColor(0.2f, 0.3f, 0.3f, 1.0f);

        cam = new Camera(width, height);
    }

    public void setMouseMoveCallback(GLFWCursorPosCallbackI input) {
        glfwSetCursorPosCallback(window, input);
    }

    public void setMouseButtonCallback(GLFWMouseButtonCallbackI input) {
        glfwSetMouseButtonCallback(window, input);
    }

    /**
     * Render the game and fresh the window.
     *
     * @return if game should continue.
     */
    public boolean render() {
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT); // clear the framebuffer

        sence.render(cam);

        glfwSwapBuffers(window);
        glfwPollEvents();
        return glfwWindowShouldClose(window);
    }

    public void close() {
        glfwDestroyWindow(window);
        glfwTerminate();
    }

    public int getKey(int key) {
        return glfwGetKey(window, key);
    }
}

class Shader {
    public int id;

    Shader(int type, String source) {
        id = glCreateShader(type);
        glShaderSource(id, source);
        glCompileShader(id);

        int status = glGetShaderi(id, GL_COMPILE_STATUS);
        if (status != GL_TRUE) {
            throw new RuntimeException(glGetShaderInfoLog(id));
        }
    }

    public void delete() {
        glDeleteShader(id);
    }
}

class ShaderProgram {
    public int id;

    ShaderProgram(Shader... shaders) {
        id = glCreateProgram();
        for (Shader shader : shaders)
            glAttachShader(id, shader.id);
        glBindFragDataLocation(id, 0, "FragColor");
        glLinkProgram(id);
    }

    public void Use() {
        glUseProgram(id);
    }

    public int GetUniformLocation(String name) {
        return glGetUniformLocation(id, name);
    }
}
