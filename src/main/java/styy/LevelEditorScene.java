package styy;


import Components.FontRenderer;
import Components.SpriteRenderer;
import org.joml.Vector2f;
import org.lwjgl.BufferUtils;
import renderer.Shader;
import renderer.Texture;
import util.Time;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.*;

public class LevelEditorScene extends Scene{

    private int vertexID, fragmentID, shaderProgram, vaoID, vboID, eboID;

    private float[] vertexArray = {
            //position             //color                          // UV coord
            //(x,y,z)              //(r,g,b,a)                      // x, y
             100.0f, -100.0f,  0.0f,      1.0f, 0.0f, 0.0f, 1.0f,    1, 0,//Bottom Right
            -100.0f, 100.0f,  0.0f,      0.0f, 1.0f, 0.0f, 1.0f,    0, 1,//Top Left
             100.0f,  100.0f,  0.0f,      1.0f, 0.0f, 1.0f, 1.0f,    1, 1,//Top Right
            -100.0f, -100.0f, 0.0f,      1.0f, 1.0f, 0.0f, 1.0f,    0, 0,//Bottom Left
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
    private Texture testTexture;

    private GameObject testObj;
    private boolean firstTime = false;

    public LevelEditorScene(){

    }

    @Override
    public void init(){
        System.out.println("Creating test obj");
        this.testObj = new GameObject("Test Obj");
        this.testObj.addComponent(new SpriteRenderer());
        this.testObj.addComponent(new FontRenderer());
        this.addGameObjectToScene(this.testObj);

        this.camera = new Camera(new Vector2f());

        defaultShader = new Shader("assets/shaders/default.glsl");
        defaultShader.compile();

        this.testTexture = new Texture("assets/images/testImage_Mario.png");

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
        int uvSize = 2;
        int floatSizeBytes = Float.BYTES;
        int vertexSizeBytes = (positionSize + colorSize + uvSize) * floatSizeBytes;

        glVertexAttribPointer(0, positionSize, GL_FLOAT,false, vertexSizeBytes, 0);
        glEnableVertexAttribArray(0);

        glVertexAttribPointer(1, colorSize, GL_FLOAT, false, vertexSizeBytes, positionSize*floatSizeBytes);
        glEnableVertexAttribArray(1);

        glVertexAttribPointer(2, uvSize, GL_FLOAT, false, vertexSizeBytes, (positionSize + colorSize)*floatSizeBytes);
        glEnableVertexAttribArray(2);
    }
    @Override
    public void update(float dt) {
        camera.position.x = -500.0f;
        camera.position.y = -500.0f;

        //Using and uploading shaders
        defaultShader.use();

        //uploading the texture
        defaultShader.uploadTexture("TEX_SAMPLER", 0); //telling shaders that we will use slot 0 for texture
        glActiveTexture(GL_TEXTURE0); //making gl use tex slot 0
        testTexture.bind(); //uploading our tex to gl

        defaultShader.uploadMat4f("uProjection", camera.getProjectionMatrix());
        defaultShader.uploadMat4f("uView", camera.getViewMatrix());
        defaultShader.uploadFloat("uTime", Time.getTime());




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

        if(!firstTime) {
            System.out.println("Creating game obj 2");
            GameObject go = new GameObject("Game test 2");
            go.addComponent(new SpriteRenderer());
            this.addGameObjectToScene(go);
            firstTime = true;
        }

        for(GameObject go : this.gameObjects){
            go.update(dt);
        }

    }
}
