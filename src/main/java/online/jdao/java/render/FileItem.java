package online.jdao.java.render;


import org.joml.AxisAngle4f;
import org.joml.Quaternionf;
import org.joml.Quaternionfc;
import org.joml.Vector3f;
import org.lwjgl.assimp.*;
import org.lwjgl.system.MemoryUtil;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import static org.lwjgl.opengl.GL33C.*;
import static org.lwjgl.assimp.Assimp.*;


public class FileItem implements Item {
    int vao, vbo, ebo;
    int numVert, numIndex;

    public FileItem(String name) {
        AIScene scene = this.getScene(name);
        AIMesh mesh = AIMesh.create(scene.mMeshes().get());
        System.out.println(mesh.mNumFaces());
        numVert = mesh.mNumVertices();
        numIndex = mesh.mNumFaces() * 3;

        //将网格的顶点数据读入FloatBuffer

        FloatBuffer vertices = MemoryUtil.memAllocFloat(numVert * 3);
        for (AIVector3D v : mesh.mVertices())
            vertices.put(v.x()).put(v.y()).put(v.z());
        vertices.flip();

        IntBuffer indexes = MemoryUtil.memAllocInt(numIndex);
        for (AIFace face : mesh.mFaces())
            indexes.put(face.mIndices());
        indexes.flip();

        //将FloatBuffer载入VBO
        vao = glGenVertexArrays();
        vbo = glGenBuffers();
        ebo = glGenBuffers();

        glBindVertexArray(vao);

        glBindBuffer(GL_ARRAY_BUFFER, vbo);
        glBufferData(GL_ARRAY_BUFFER, vertices, GL_STATIC_DRAW);

        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, ebo);
        glBufferData(GL_ELEMENT_ARRAY_BUFFER, indexes, GL_STATIC_DRAW);

        glVertexAttribPointer(0, 3, GL_FLOAT, false, 3 * 4, 0);
        glEnableVertexAttribArray(0);
    }

    public void Draw() {
        glBindVertexArray(vao);
        glDrawElements(GL_TRIANGLES, numIndex, GL_UNSIGNED_INT, 0);
    }

    private AIScene getScene(String name) {
        AIScene scene = aiImportFile(this.getClass().getResource("/modules/" + name).getPath(),
                aiProcess_Triangulate);//自动转为三角面
        if (scene == null) {
            System.err.println(aiGetErrorString());
            System.exit(2);
        }
        return scene;
    }
}
