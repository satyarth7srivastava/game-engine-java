package renderer;

import Components.SpriteRenderer;
import org.joml.Vector4f;
import styy.Window;
import util.AssetPool;

import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.glBindVertexArray;
import static org.lwjgl.opengl.GL30.glGenVertexArrays;

public class RenderBatch {
    //Vertex
    //Pos(x,y)      Color(r,g,b,a)
    //f, f          f, f, f, f
    private final int POS_SIZE = 2;
    private final int COLOR_SIZE = 4;
    private final int POS_OFFSET = 0;
    private final int COLOR_OFFSET = (POS_OFFSET + POS_SIZE) * Float.BYTES;
    private final int VERTEX_SIZE = 6;
    private final int VERTEX_SIZE_BYTES = VERTEX_SIZE*Float.BYTES;

    private SpriteRenderer[] sprites;
    private int numSprites;
    private boolean hasRoom;
    private float[] vertices;

    private int vaoID, vboID;
    private int maxBatchSize;
    private Shader shader;

    public RenderBatch(int maxBatchSize){
        this.shader = AssetPool.getShader("assets/shaders/default.glsl");
        this.shader.compile();
        this.sprites = new SpriteRenderer[maxBatchSize];
        this.maxBatchSize = maxBatchSize;

        this.vertices = new float[maxBatchSize * VERTEX_SIZE * 4]; // 4 vertex per quad

        this.numSprites = 0;
        this.hasRoom = true;
    }

    public void start(){
        //Generate and bind Vertex Array Object
        vaoID = glGenVertexArrays();
        glBindVertexArray(vaoID);

        //Allocating space for vertices
        vboID = glGenBuffers();
        glBindBuffer(GL_ARRAY_BUFFER, vboID);
        glBufferData(GL_ARRAY_BUFFER, (long) vertices.length * Float.BYTES, GL_DYNAMIC_DRAW);

        //Create and upload indices buffer
        int eboID = glGenBuffers();
        int[] indices = generateIndices();
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, eboID);
        glBufferData(GL_ELEMENT_ARRAY_BUFFER, indices, GL_STATIC_DRAW);

        //Enable the buffer attribute pointers
        glVertexAttribPointer(0, POS_SIZE, GL_FLOAT, false, VERTEX_SIZE_BYTES, POS_OFFSET);
        glEnableVertexAttribArray(0);
        glVertexAttribPointer(1, COLOR_SIZE, GL_FLOAT, false, VERTEX_SIZE_BYTES, COLOR_OFFSET);
        glEnableVertexAttribArray(1);


    }

    public void addSprite(SpriteRenderer spr){
        //get index and add renderObj
        int index = this.numSprites;
        this.sprites[index] = spr;
        this.numSprites++;

        //add the properties
        loadVertexProperties(index);

        if(numSprites >= this.maxBatchSize){
            this.hasRoom = false;
        }
    }

    public void render(){
        //For now, we will re-buffer all data every frame
        glBindBuffer(GL_ARRAY_BUFFER, vboID);
        glBufferSubData(GL_ARRAY_BUFFER, 0, vertices);

        //use shader
        shader.use();
        shader.uploadMat4f("uProjection", Window.getScene().getCamera().getProjectionMatrix());
        shader.uploadMat4f("uView", Window.getScene().getCamera().getViewMatrix());

        glBindVertexArray(vaoID);
        glEnableVertexAttribArray(0);
        glEnableVertexAttribArray(1);

        glDrawElements(GL_TRIANGLES, this.numSprites * 6, GL_UNSIGNED_INT, 0);

        glDisableVertexAttribArray(0);
        glDisableVertexAttribArray(1);
        glBindVertexArray(0);
        shader.detach();
    }

    public boolean hasRoom(){
        return hasRoom;
    }

    private void loadVertexProperties(int ind){
        SpriteRenderer spr = this.sprites[ind];

        //find offset within array (4 ver per spr)
        int offset = ind * 4 * VERTEX_SIZE;

        Vector4f color = spr.getColor();

        //Add vertices with the appropriate properties
        float xAdd = 1.0f;
        float yAdd = 1.0f;
        for(int i = 0; i < 4; i++){
            if(i == 1){
                yAdd = 0.0f;
            }else if(i == 2){
                xAdd = 0.0f;
            }else if(i == 3){
                yAdd = 1.0f;
            }

            //loading position
            vertices[offset] = spr.gameObject.transform.position.x + (xAdd * spr.gameObject.transform.scale.x);
            vertices[offset + 1] = spr.gameObject.transform.position.y + (yAdd * spr.gameObject.transform.scale.y);

            //loading colors
            vertices[offset + 2] = color.x;
            vertices[offset + 3] = color.y;
            vertices[offset + 4] = color.z;
            vertices[offset + 5] = color.w;

            offset += VERTEX_SIZE;
        }

    }
    private int[] generateIndices(){
        int[] elements = new int[6 * maxBatchSize];
        for(int i =0 ; i< maxBatchSize; i++){
            loadElementIndices(elements, i);
        }
        return elements;
    }
    private void loadElementIndices(int[] elements, int i){
        int offsetArrayIndex = 6 * i;
        int offset = 4 * i;
        // 3, 2, 0, 0, 2, 1
        elements[offsetArrayIndex] = offset + 3;
        elements[offsetArrayIndex + 1] = offset + 2;
        elements[offsetArrayIndex + 2] = offset;
        elements[offsetArrayIndex + 3] = offset;
        elements[offsetArrayIndex + 4] = offset + 2;
        elements[offsetArrayIndex + 5] = offset + 1;
    }
}
