package styy;


import org.joml.Vector2f;
import org.lwjgl.BufferUtils;
import renderer.Shader;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.*;

public class LevelEditorScene extends Scene{

    private int vertexID, fragmentID, shaderProgram, vaoID, vboID, eboID;

    private float[] vertexArray = {
            //position             //color
            //(x,y,z)              //(r,g,b,a)
             10.0f, -100.0f, 0.0f,      1.0f, 0.0f, 0.0f, 1.0f, //Bottom Right
            -100.0f, 100.0f,  0.0f,      0.0f, 1.0f, 0.0f, 1.0f, //Top Left
             10.0f, 100.0f,  0.0f,      1.0f, 0.0f, 1.0f, 1.0f, //Top Right
            -100.0f, -100.0f, 0.0f,      1.0f, 1.0f, 0.0f, 1.0f, //Bottom Left
    };

    //IMPORTANT: Must be in counter-clockwise order
    private int[] elementArray = {
            /*
                    x       x


                    x       x
             */
            0, 2, 1, //upper right triangle
            0, 1, 3, //bottom left triangle
    };

    private Shader defaultShader;


    public LevelEditorScene(){

    }

    @Override
    public void init(){
        this.camera = new Camera(new Vector2f());
        defaultShader = new Shader("assets/shaders/default.glsl");
        defaultShader.compile();
        // =========================================
        // Generating VAO, VBO and EBO buffer objects and sending them to GPU
        // =========================================

        vaoID = glGenVertexArrays();
        glBindVertexArray(vaoID);

        //Create a float buffer of vertices
        FloatBuffer vertexBuffer = BufferUtils.createFloatBuffer(vertexArray.length);
        vertexBuffer.put(vertexArray).flip();

        //create VBO upload the vertex buffer
        vboID = glGenBuffers();
        glBindBuffer(GL_ARRAY_BUFFER, vboID);
        glBufferData(GL_ARRAY_BUFFER, vertexBuffer, GL_STATIC_DRAW);

        //creating indices and upload
        IntBuffer elementBuffer = BufferUtils.createIntBuffer(elementArray.length);
        elementBuffer.put(elementArray).flip();

        eboID = glGenBuffers();
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, eboID);
        glBufferData(GL_ELEMENT_ARRAY_BUFFER, elementBuffer, GL_STATIC_DRAW);

        //Add the vertex attribute pointers
        int positionSize = 3;
        int colorSize = 4;
        int floatSizeBytes = 4;
        int vertexSizeBytes = (positionSize + colorSize) * floatSizeBytes;
        glVertexAttribPointer(0, positionSize, GL_FLOAT,false, vertexSizeBytes, 0);
        glEnableVertexAttribArray(0);

        glVertexAttribPointer(1, colorSize, GL_FLOAT, false, vertexSizeBytes, positionSize*floatSizeBytes);
        glEnableVertexAttribArray(1);
    }
    @Override
    public void update(float dt) {
        camera.position.x -= dt * 50.0f;

        //Using and uploading shaders
        defaultShader.use();
        defaultShader.uploadMat4f("uProjection", camera.getProjectionMatrix());
        defaultShader.uploadMat4f("uView", camera.getViewMatrix());
        //Bind VAO that we are using
        glBindVertexArray(vaoID);

        //enable the vertex attribute pointers
        glEnableVertexAttribArray(0);
        glEnableVertexAttribArray(1);

        glDrawElements(GL_TRIANGLES, elementArray.length, GL_UNSIGNED_INT, 0);

        //unbinding everything
        glDisableVertexAttribArray(0);
        glDisableVertexAttribArray(1);

        glBindVertexArray(0);

        defaultShader.detach();

    }
}
