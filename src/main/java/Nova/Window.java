package Nova;

import observers.EventSystem;
import observers.Observer;
import observers.events.Event;
import observers.events.EventType;
import org.lwjgl.Version;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.opengl.GL;
import renderer.*;
import scenes.LevelEditorSceneInitializer;
import scenes.Scene;
import scenes.SceneInitializer;
import util.AssetPool;
import util.Time;

import static org.lwjgl.glfw.Callbacks.glfwFreeCallbacks;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.system.MemoryUtil.NULL;


public class Window implements Observer {
    private int width, height;
    private String title;
    private long glfwWindow;
    private ImGuiLayer imGuiLayer;
    private FrameBuffer frameBuffer;
    private PickingTexture pickingTexture;



    private static Window window = null;

    //We want window to change between Scenes so we declare it here
    private static Scene currentScene = null;

    private Window(){
        this.width = 1920;
        this.height = 1080;
        this.title = "Nova";

        EventSystem.addObserver(this);
    }

    public static void changeScene(SceneInitializer sceneInitializer){

        if (currentScene != null){
            //destroy it
        }

        currentScene = new Scene(sceneInitializer);

        currentScene.load();
        currentScene.init();
        currentScene.start();
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


        this.frameBuffer = new FrameBuffer(2560, 1440);
        this.pickingTexture = new PickingTexture(2560, 1440);

        this.imGuiLayer = new ImGuiLayer(glfwWindow, pickingTexture);
        this.imGuiLayer.initImGui();
        glViewport(0, 0,2560, 1440);


        Window.changeScene(new LevelEditorSceneInitializer());
    }

    public void loop(){
        float beginTime = Time.getTime();
        float endTime;
        float dt = -1.0f; // It is the time per frame

        Shader defaultShader = AssetPool.getShader("assets/shaders/default.glsl");
        Shader pickingShader = AssetPool.getShader("assets/shaders/pickingShader.glsl");
        while(!glfwWindowShouldClose(glfwWindow)){
            //POLL events
            glfwPollEvents();

            //Render pass 1. Render to picking texture

            glDisable(GL_BLEND);
            pickingTexture.enableWriting();

            glViewport(0, 0, 2560, 1440);
            glClearColor(0,0,0,0);
            glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

            Renderer.bindShader(pickingShader);
            currentScene.render();

            pickingTexture.disableWriting();
            glEnable(GL_BLEND);


            //Render pass 2. Render actual game texture
            DebugDraw.beginFrame();

            this.frameBuffer.bind();
            glClearColor(1, 1, 1, 1);
            glClear(GL_COLOR_BUFFER_BIT);

            //testing our dt as well as scene update
            if(dt >= 0) {
                Renderer.bindShader(defaultShader);
                DebugDraw.draw();
                currentScene.update(dt);
                currentScene.render();
            }
            this.frameBuffer.unbind();

            this.imGuiLayer.update(dt, currentScene);


            glfwSwapBuffers(glfwWindow);

            MouseListner.endFrame();

            endTime = Time.getTime();
            dt = endTime - beginTime;
            beginTime = endTime;
        }
        currentScene.saveExit();
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

    public static FrameBuffer getFrameBuffer() {
        return get().frameBuffer;
    }

    public static float getTargetAspectRatio(){
        return 16.0f / 9.0f;
    }

    public static ImGuiLayer getImGuiLayer() {
        return get().imGuiLayer;
    }

    @Override
    public void onNotify(GameObject gameObject, Event event) {
        if (event.type == EventType.GameEngineStartPlay){
            System.out.println("Game engine started");
        }
        if (event.type == EventType.GameEngineStopPlay){
            System.out.println("Game engine has been stopped");
        }
    }
}
