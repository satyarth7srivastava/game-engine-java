package renderer;

import org.lwjgl.BufferUtils;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.stb.STBImage.*;

public class Texture {

    private  String filePath;
    private int texID;
    private int width, height;

    public int getTexID() {
        return texID;
    }

    public  Texture(){

    }

    public void init(String filepath){

        this.filePath = filepath;

        //Generating tex over the gpu
        this.texID = glGenTextures();
        glBindTexture(GL_TEXTURE_2D, texID);

        //Setting the tex parameters
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_REPEAT);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_REPEAT);

        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);

        //loading the image
        IntBuffer width = BufferUtils.createIntBuffer(1);
        IntBuffer height = BufferUtils.createIntBuffer(1);
        IntBuffer channels = BufferUtils.createIntBuffer(1);
        stbi_set_flip_vertically_on_load(true); // flipping the image
        ByteBuffer image = stbi_load(filepath, width, height, channels, 0);

        if(image != null){
            this.width = width.get(0);
            this.height = height.get(0);
            if(channels.get(0) == 3){
                glTexImage2D(GL_TEXTURE_2D, 0, GL_RGB, width.get(0), height.get(0), 0, GL_RGB, GL_UNSIGNED_BYTE, image);
            }else if(channels.get(0) == 4){
                glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, width.get(0), height.get(0), 0, GL_RGBA, GL_UNSIGNED_BYTE, image);
            }else{
                assert false: "Error: (Texture) Unknown number of channels: " + channels.get(0);
            }

        }else{
            assert false: "Error:(Texture) could not load the image from: " +  filepath;
        }

        //freeing up the memory (It's actually a C library)
        stbi_image_free(image);
    }
    public void bind(){
       glBindTexture(GL_TEXTURE_2D, texID);
    }

    public void unBind(){
        glBindTexture(GL_TEXTURE_2D, 0);
    }

    public int getHeight() {
        return this.height;
    }
    public int getWidth(){
        return this.width;
    }
    public String getFilePath(){ return this.filePath;}

    @Override
    public boolean equals(Object o){
        if (o == null) return false;
        if (!(o instanceof Texture)) return false;
        Texture oTex = (Texture) o;
        return oTex.getWidth() == this.width &&
                oTex.getHeight() == this.height &&
                oTex.getTexID() == this.texID &&
                oTex.getFilePath().equals(this.filePath);
    }
}
