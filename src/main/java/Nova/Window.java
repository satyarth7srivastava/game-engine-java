package Nova;

import org.lwjgl.Version;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.opengl.GL;
import util.Time;

import static org.lwjgl.glfw.Callbacks.glfwFreeCallbacks;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.system.MemoryUtil.NULL;


public class Window {
    private int width, height;
    private String title;
    private long glfwWindow;
    private ImGuiLayer imGuiLayer;

    //for testing only
    public float r, g, b, a;
    private boolean fade = false;


    private static Window window = null;

    //We want window to change between Scenes so we declare it here
    private static Scene currentScene = null;

    private Window(){
        this.width = 1920;
        this.height = 1080;
        this.title = "Main";
        r = 1;
        g = 1;
        b = 1;
        a = 1;
    }

    public static void changeScene(int newScene){
        switch (newScene){
            case 0:
                currentScene = new LevelEditorScene();
                currentScene.init();
                currentScene.start();
                break;
            case 1:
                currentScene = new LevelScene();
                currentScene.init();
                currentScene.start();
                break;
            default:
                assert false: "Unknown Scene";
        }
    }

    public static  Window get(){
        if(Window.window == null){
            Window.window = new Window();
        }
        return Window.window;
    }

    public static Scene getScene(){
        return get().currentScene;
    }

    public void run(){
        System.out.println("Hello LWJGL: " + Version.getVersion() + " @");

        init();
        loop();

        //Free the memory
        glfwFreeCallbacks(glfwWindow);
        glfwSetErrorCallback(null).free();
    }

    public void init(){
        //Setting up error callback
        GLFWErrorCallback.createPrint(System.err).set();

        //Initialize GLFW
        if(!glfwInit()){
            throw new IllegalStateException("Unable to init GLFW.");
        }

        // configuring GLFW
        glfwDefaultWindowHints();
        glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE);
        glfwWindowHint(GLFW_RESIZABLE, GLFW_TRUE);
        glfwWindowHint(GLFW_MAXIMIZED, GLFW_TRUE);

        //creating the window
        glfwWindow = glfwCreateWindow(this.width, this.height, this.title, NULL, NULL);
        if(glfwWindow == NULL){
            throw new IllegalStateException("Unable to init GLFW.");
        }

        //setting up callbacks for mouse
        glfwSetCursorPosCallback(glfwWindow, MouseListner::mousePosCallback);
        glfwSetMouseButtonCallback(glfwWindow, MouseListner::mouseButtonCallback);
        glfwSetScrollCallback(glfwWindow, MouseListner::mouseScrollCallback);

        //setting up callbacks for keys
        glfwSetKeyCallback(glfwWindow, KeyListener::keyCallback);

        //setting up callback for window reSizing
        glfwSetWindowSizeCallback(glfwWindow, (w, newWidth, newHeight) -> {
            Window.setWidth(newWidth);
            Window.setHeight(newHeight);
        });

        //Making the OpenGL context current
        glfwMakeContextCurrent(glfwWindow);
        //Enabling V-sync
        glfwSwapInterval(1);

        //Making the window visible
        glfwShowWindow(glfwWindow);

        // This line is critical for LWJGL's interoperation with GLFW's
        // OpenGL context, or any context that is managed externally.
        // LWJGL detects the context that is current in the current thread,
        // creates the GLCapabilities instance and makes the OpenGL
        // bindings available for use.
        GL.createCapabilities();

        glEnable(GL_BLEND);
        glBlendFunc(GL_ONE, GL_ONE_MINUS_SRC_ALPHA);

        this.imGuiLayer = new ImGuiLayer(glfwWindow);
        this.imGuiLayer.initImGui();

        Window.changeScene(0);
    }

    public void loop(){
        float beginTime = Time.getTime();
        float endTime;
        float dt = -1.0f; // It is the time per frame

        while(!glfwWindowShouldClose(glfwWindow)){
            //POLL events
            glfwPollEvents();

            glClearColor(r, g, b, a);
            glClear(GL_COLOR_BUFFER_BIT);

            //testing our dt as well as scene update
            if(dt >= 0) {
                currentScene.update(dt);
            }

            this.imGuiLayer.update(dt);

            //testing
//            if(fade){
//                r = Math.max(r - 0.01f, (float) 0.5);
//                g = Math.max(g - 0.01f, 0);
//                b = Math.max(b - 0.01f, 0);
//            }
//            if (KeyListener.isKeyPressed(GLFW_KEY_SPACE)){
//                fade = true;
//            }


            glfwSwapBuffers(glfwWindow);

            endTime = Time.getTime();
            dt = endTime - beginTime;
            beginTime = endTime;
        }
    }

    public static int getWidth(){
        return get().width;
    }
    public static int getHeight(){
        return get().height;
    }
    public static void setWidth(int newWidth){
        get().width = newWidth;
    }
    public static void setHeight(int newHeight){
        get().height = newHeight;
    }
}
