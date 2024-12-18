package styy;

import org.lwjgl.Version;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.opengl.GL;

import static org.lwjgl.glfw.Callbacks.glfwFreeCallbacks;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.system.MemoryUtil.NULL;


public class Window {
    private int width, height;
    private String title;
    private long glfwWindow;

    //for testing only
    private float r, g, b, a;
    private boolean fade = false;


    private static Window window = null;

    private Window(){
        this.width = 1920;
        this.height = 1080;
        this.title = "Main";
        r = 1;
        g = 1;
        b = 1;
        a = 1;
    }

    public static  Window get(){
        if(Window.window == null){
            Window.window = new Window();
        }
        return Window.window;
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
    }

    public void loop(){
        while(!glfwWindowShouldClose(glfwWindow)){
            //POLL events
            glfwPollEvents();

            glClearColor(r, g, b, a);
            glClear(GL_COLOR_BUFFER_BIT);

            //testing
            if(fade){
                r = Math.max(r - 0.01f, (float) 0.5);
                g = Math.max(g - 0.01f, 0);
                b = Math.max(b - 0.01f, 0);
            }
            if (KeyListener.isKeyPressed(GLFW_KEY_SPACE)){
                fade = true;
            }

            glfwSwapBuffers(glfwWindow);
        }
    }
}