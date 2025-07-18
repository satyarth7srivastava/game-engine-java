package renderer;

import Components.SpriteRenderer;
import Nova.GameObject;
import org.jetbrains.annotations.NotNull;
import org.joml.Math;
import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector4f;
import Nova.Window;
import util.AssetPool;

import java.util.ArrayList;
import java.util.List;

import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.glBindVertexArray;
import static org.lwjgl.opengl.GL30.glGenVertexArrays;

public class RenderBatch implements Comparable<RenderBatch>{
    //Vertex
    //Pos(x,y)      Color(r,g,b,a)          tex coords      tex_id
    //f, f,          f, f, f, f,              f,f,             f
    private final int POS_SIZE = 2;
    private final int COLOR_SIZE = 4;
    private final int POS_OFFSET = 0;
    private final int COLOR_OFFSET = POS_OFFSET + POS_SIZE * Float.BYTES;
    private final int ENTITY_ID_SIZE = 1;
    private final int VERTEX_SIZE = 10;
    private final int VERTEX_SIZE_BYTES = VERTEX_SIZE*Float.BYTES;

    private final int TEX_COORDS_SIZE = 2;
    private final int TEX_ID_SIZE = 1;
    private final int TEX_COORDS_OFFSET = COLOR_OFFSET + COLOR_SIZE * Float.BYTES;
    private final int TEX_ID_OFFSET = TEX_COORDS_OFFSET + TEX_COORDS_SIZE * Float.BYTES;
    private final int ENTITY_ID_OFFSET = TEX_ID_OFFSET + TEX_ID_SIZE*Float.BYTES;

    private SpriteRenderer[] sprites;
    private int numSprites;
    private boolean hasRoom;
    private float[] vertices;
    private List<Texture> textures;
    private  int[] texSlots = {0, 1, 2, 3, 4, 5, 6, 7};

    private int vaoID, vboID;
    private int maxBatchSize;
    private int zIndex;
    private Renderer renderer;

    public RenderBatch(int maxBatchSize, int zIndex, Renderer renderer){
        this.sprites = new SpriteRenderer[maxBatchSize];
        this.maxBatchSize = maxBatchSize;
        this.textures = new ArrayList<>();
        this.zIndex = zIndex;
        this.renderer = renderer;

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

        glVertexAttribPointer(2, TEX_COORDS_SIZE, GL_FLOAT, false, VERTEX_SIZE_BYTES, TEX_COORDS_OFFSET);
        glVertexAttribPointer(3, TEX_ID_SIZE, GL_FLOAT, false, VERTEX_SIZE_BYTES, TEX_ID_OFFSET);
        glEnableVertexAttribArray(2);
        glEnableVertexAttribArray(3);

        glVertexAttribPointer(4, ENTITY_ID_SIZE, GL_FLOAT, false, VERTEX_SIZE_BYTES, ENTITY_ID_OFFSET);
        glEnableVertexAttribArray(4);

    }

    public void addSprite(SpriteRenderer spr){
        //get index and add renderObj
        int index = this.numSprites;
        this.sprites[index] = spr;
        this.numSprites++;

        if(spr.getTexture() != null){
            if(!textures.contains(spr.getTexture())){
                textures.add(spr.getTexture());
            }
        }

        //add the properties
        loadVertexProperties(index);

        if(numSprites >= this.maxBatchSize){
            this.hasRoom = false;
        }
    }

    public void render(){
        boolean reBufferData = false;
        for(int i = 0; i < numSprites; i++){
            SpriteRenderer spr = sprites[i];
            if(spr.isDirty()){
                loadVertexProperties(i);
                spr.setClean();
                reBufferData = true;
            }
            if (spr.gameObject.transform.zIndex != this.zIndex){
                destroyIfExist(spr.gameObject);

                renderer.add(spr.gameObject);

                i--;
            }
        }

        if(reBufferData) { // look at here only the batch updating is getting rendered here do some de bugging
            glBindBuffer(GL_ARRAY_BUFFER, vboID);
            glBufferSubData(GL_ARRAY_BUFFER, 0, vertices);
        }
        //use shader
        Shader shader = Renderer.getBoundShader();
        shader.uploadMat4f("uProjection", Window.getScene().getCamera().getProjectionMatrix());
        shader.uploadMat4f("uView", Window.getScene().getCamera().getViewMatrix());
        for (int i = 0; i < textures.size(); i++) {
            glActiveTexture(GL_TEXTURE0 + i + 1);
            textures.get(i).bind();
        }
        shader.uploadIntArray("uTextures", texSlots);

        glBindVertexArray(vaoID);
        glEnableVertexAttribArray(0);
        glEnableVertexAttribArray(1);

        glDrawElements(GL_TRIANGLES, this.numSprites * 6, GL_UNSIGNED_INT, 0);

        glDisableVertexAttribArray(0);
        glDisableVertexAttribArray(1);
        glBindVertexArray(0);

        for (int i = 0; i < textures.size(); i++) {
            glActiveTexture(GL_TEXTURE0 + i + 1);
            textures.get(i).unBind();
        }

        shader.detach();
    }

    public boolean destroyIfExist(GameObject go){
        SpriteRenderer sprite = go.getComponent(SpriteRenderer.class);
        for (int i = 0; i < numSprites; i++) {
            if (sprites[i] == sprite) {
                for (int j = i; j < numSprites - 1; j++) {
                    sprites[j] = sprites[j + 1];
                    sprites[j].setDirty();
                }
                numSprites--;
                return true;
            }
        }
        return false;
    }

    public boolean hasRoom(){
        return hasRoom;
    }

    private void loadVertexProperties(int ind){
        SpriteRenderer spr = this.sprites[ind];

        //find offset within array (4 ver per spr)
        int offset = ind * 4 * VERTEX_SIZE;

        Vector4f color = spr.getColor();
        Vector2f[] texCoords = spr.getTexCoords();

        int texId = 0;
        if(spr.getTexture() != null){
            for(int i = 0; i < textures.size(); i++){
                if(textures.get(i).equals(spr.getTexture())){
                    texId = i + 1;
                    break;
                }
            }
        }

        boolean isRotated = spr.gameObject.transform.rotation != 0.0f;
        Matrix4f transformMatrix = new Matrix4f().identity();
        if (isRotated){
            transformMatrix.translate(spr.gameObject.transform.position.x,
                            spr.gameObject.transform.position.y, 0);
            transformMatrix.rotate((float) Math.toRadians(spr.gameObject.transform.rotation), 0, 0, 1);
            transformMatrix.scale(spr.gameObject.transform.scale.x, spr.gameObject.transform.scale.y, 0);
        }

        //Add vertices with the appropriate properties
        float xAdd = 0.5f;
        float yAdd = 0.5f;
        for(int i = 0; i < 4; i++){
            if(i == 1){
                yAdd = -0.5f;
            }else if(i == 2){
                xAdd = -0.5f;
            }else if(i == 3){
                yAdd = 0.5f;
            }

            Vector4f currentPos = new Vector4f(spr.gameObject.transform.position.x + (xAdd * spr.gameObject.transform.scale.x),
                    spr.gameObject.transform.position.y + (yAdd * spr.gameObject.transform.scale.y), 0, 1);

            if (isRotated){
                currentPos = new Vector4f(xAdd, yAdd, 0, 1).mul(transformMatrix);
            }
            //loading position
            vertices[offset] = currentPos.x;
            vertices[offset + 1] = currentPos.y;

            //loading colors
            vertices[offset + 2] = color.x;
            vertices[offset + 3] = color.y;
            vertices[offset + 4] = color.z;
            vertices[offset + 5] = color.w;

            //loading tex_coords
            vertices[offset + 6] = texCoords[i].x;
            vertices[offset + 7] = texCoords[i].y;
            //loading tex_id
            vertices[offset + 8] = texId;
            //loading entityId
            vertices[offset + 9] = spr.gameObject.getUid() + 1;

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

    public boolean hasTextureRoom(){
        return this.textures.size() < 8;
    }

    public boolean hasTexture(Texture tx){
        return this.textures.contains(tx);
    }

    public int getzIndex(){
        return zIndex;
    }

    @Override
    public int compareTo(@NotNull RenderBatch o) {
        return Integer.compare(this.zIndex, o.zIndex);
    }
}
