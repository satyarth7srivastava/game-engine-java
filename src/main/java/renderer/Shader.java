package renderer;

import org.joml.Matrix4f;
import org.lwjgl.BufferUtils;

import java.io.IOException;
import java.nio.FloatBuffer;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.lwjgl.opengl.GL11.GL_FALSE;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL20.glGetShaderInfoLog;

public class Shader {

    private int shaderProgramID;

    private String vertexSrc;
    private String fragmentSrc;
    private String filePath;

    public Shader(String filepath) {
        this.filePath = filepath;
        try{
            String src = new String(Files.readAllBytes(Paths.get(filepath)));
            String[] splitString = src.split("(#type)( )+([a-zA-z]+)");

            int index = src.indexOf("#type") + 6;
            int eol = src.indexOf("\r\n", index);
            String firstP = src.substring(index, eol).trim();

            index = src.indexOf("#type", eol) + 6;
            eol = src.indexOf("\r\n", index);
            String secondP = src.substring(index, eol).trim();
            
            if(firstP.equals("vertex")){
                vertexSrc = splitString[1];
            } else if (firstP.equals("fragment")) {
                fragmentSrc = splitString[1];
            } else {
                throw new IOException("Unexpected token: "+firstP);
            }

            if(secondP.equals("vertex")){
                vertexSrc = splitString[2];
            } else if (secondP.equals("fragment")) {
                fragmentSrc = splitString[2];
            } else {
                throw new IOException("Unexpected token: "+secondP);
            }

        }catch (IOException e){
            e.printStackTrace();
            assert false : "ERROR: Could not open file for shader: " + filepath + " ";
        }
    }

    public void compile(){
        // ===================================
        // Compiling and linking shaders
        // ===================================

        int vertexID, fragmentID;
        //First load and compile the vertex shader
        vertexID = glCreateShader(GL_VERTEX_SHADER);
        //pass the shader src code to GPU
        glShaderSource(vertexID, vertexSrc);
        glCompileShader(vertexID);

        //checking for errors
        int success = glGetShaderi(vertexID, GL_COMPILE_STATUS);
        if(success == GL_FALSE){
            int len = glGetShaderi(vertexID, GL_INFO_LOG_LENGTH);
            System.out.println("ERROR:: "+filePath+"\n\tVertex Shader compilation failed.");
            System.out.println(glGetShaderInfoLog(vertexID, len));
            assert false : "";
        }

        //Compiling and linking shaders
        //First load and compile the Fragment shader
        fragmentID = glCreateShader(GL_FRAGMENT_SHADER);
        //pass the shader src code to GPU
        glShaderSource(fragmentID, fragmentSrc);
        glCompileShader(fragmentID);

        //checking for errors
        success = glGetShaderi(fragmentID, GL_COMPILE_STATUS);
        if(success == GL_FALSE){
            int len = glGetShaderi(fragmentID, GL_INFO_LOG_LENGTH);
            System.out.println("ERROR:: "+filePath+"\n\tFragment Shader compilation failed.");
            System.out.println(glGetShaderInfoLog(fragmentID, len));
            assert false : "";
        }

        //Linking shaders and error checking
        shaderProgramID = glCreateProgram();
        glAttachShader(shaderProgramID, vertexID);
        glAttachShader(shaderProgramID, fragmentID);
        glLinkProgram(shaderProgramID);

        //error checking
        success = glGetProgrami(shaderProgramID, GL_LINK_STATUS);
        if(success == GL_FALSE){
            int len = glGetProgrami(shaderProgramID, GL_INFO_LOG_LENGTH);
            System.out.println("ERROR:: "+ filePath +"\n\tShader Linking Failed.");
            System.out.println(glGetProgramInfoLog(shaderProgramID, len));
            assert false : "";
        }

    }

    public void use(){
        //Bind shader program
        glUseProgram(shaderProgramID);

    }
    public void detach(){
        glUseProgram(0);
    }

    public void uploadMat4f(String varName, Matrix4f mat4){
        int varLocation = glGetUniformLocation(shaderProgramID, varName);
        FloatBuffer matBuffer = BufferUtils.createFloatBuffer(16);
        mat4.get(matBuffer);
        glUniformMatrix4fv(varLocation, false, matBuffer);
    }
}
