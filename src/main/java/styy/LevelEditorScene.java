package styy;


import static org.lwjgl.opengl.GL20.*;

public class LevelEditorScene extends Scene{

    private String vertexShaderSrc = "#version 330 core\n" +
            "layout (location=0) in vec3 aPos;\n" +
            "layout (location=1) in vec4 aColor;\n" +
            "\n" +
            "out vec4 fColor;\n" +
            "\n" +
            "void main(){\n" +
            "    fColor = aColor;\n" +
            "    gl_Position = vec4(aPos, 1.0);\n" +
            "}";
    private String fragmentShaderSrc = "#version 330 core\n" +
            "\n" +
            "in vec4 fColor;\n" +
            "\n" +
            "out vec4 color;\n" +
            "\n" +
            "void main(){\n" +
            "    color = fColor;\n" +
            "}";

    private int vertexID, fragmentID, shaderProgram;

    public LevelEditorScene(){
    }

    @Override
    public void init(){
        //Compiling and linking shaders
        //First load and compile the vertex shader
        vertexID = glCreateShader(GL_VERTEX_SHADER);
        //pass the shader src code to GPU
        glShaderSource(vertexID, vertexShaderSrc);
        glCompileShader(vertexID);

        //checking for errors
        int success = glGetShaderi(vertexID, GL_COMPILE_STATUS);
        if(success == GL_FALSE){
            int len = glGetShaderi(vertexID, GL_INFO_LOG_LENGTH);
            System.out.println("ERROR:: 'defaultShader.glsl'\n\tVertex Shader compilation failed.");
            System.out.println(glGetShaderInfoLog(vertexID, len));
            assert false : "";
        }

        //Compiling and linking shaders
        //First load and compile the Fragment shader
        fragmentID = glCreateShader(GL_FRAGMENT_SHADER);
        //pass the shader src code to GPU
        glShaderSource(fragmentID, fragmentShaderSrc);
        glCompileShader(fragmentID);

        //checking for errors
        success = glGetShaderi(fragmentID, GL_COMPILE_STATUS);
        if(success == GL_FALSE){
            int len = glGetShaderi(fragmentID, GL_INFO_LOG_LENGTH);
            System.out.println("ERROR:: 'defaultShader.glsl'\n\tFragment Shader compilation failed.");
            System.out.println(glGetShaderInfoLog(fragmentID, len));
            assert false : "";
        }

        //Linking shaders and error checking
        shaderProgram = glCreateProgram();
        glAttachShader(shaderProgram, vertexID);
        glAttachShader(shaderProgram, fragmentID);
        glLinkProgram(shaderProgram);

        //error checking
        success = glGetProgrami(shaderProgram, GL_LINK_STATUS);
        if(success == GL_FALSE){
            int len = glGetProgrami(shaderProgram, GL_INFO_LOG_LENGTH);
            System.out.println("ERROR:: 'defaultShader.glsl'\n\tShader Linking Failed.");
            System.out.println(glGetProgramInfoLog(shaderProgram, len));
            assert false : "";
        }
    }
    @Override
    public void update(float dt) {

    }
}
