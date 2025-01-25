package Nova;

import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector4f;

import static org.lwjgl.glfw.GLFW.GLFW_PRESS;
import static org.lwjgl.glfw.GLFW.GLFW_RELEASE;

public class MouseListner {
    private static MouseListner instance;
    private double scrollX, scrollY;
    private double xPos, yPos, lastY, lastX, worldX, worldY, lastWorldX, lastWorldY;
    private boolean mouseButtonPressed[] = new boolean[3];
    private boolean isDragging;
    private Vector2f gameViewPos = new Vector2f();
    private Vector2f gameViewSize = new Vector2f();

    private int mouseButtonDown = 0;

    private MouseListner(){
        this.scrollX = 0.0;
        this.scrollY = 0.0;
        this.xPos = 0.0;
        this.yPos = 0.0;
        this.lastX = 0.0;
        this.lastY = 0.0;
    }


    public static MouseListner get(){
        if(MouseListner.instance == null){
            MouseListner.instance = new MouseListner();
        }
        return MouseListner.instance;
    }

    private static void calcOrthoX(){
        float cX = getX() - get().gameViewPos.x;
        cX = (cX / get().gameViewSize.x) * 2f - 1f;
        Vector4f tmp = new Vector4f(cX, 0, 0, 1);
        Camera camera = Window.getScene().getCamera();
        Matrix4f viewProjection = new Matrix4f();
        camera.getInverseView().mul(camera.getInverseProjection(), viewProjection);
        tmp.mul(viewProjection);
        cX = tmp.x;
        get().worldX = cX;
    }
    private static void calcOrthoY(){
        float cY = getY() - get().gameViewPos.y;
        cY = -((cY / get().gameViewSize.y) * 2f - 1f);
        Vector4f tmp = new Vector4f(0, cY, 0, 1);
        Camera camera = Window.getScene().getCamera();
        Matrix4f viewProjection = new Matrix4f();
        camera.getInverseView().mul(camera.getInverseProjection(), viewProjection);
        tmp.mul(viewProjection);
        cY = tmp.y;
        get().worldY = cY;
    }

    public static float getOrthoX(){
        return (float)get().worldX;
    }

    public static float getOrthoY(){
        return (float) get().worldY;
    }

    public static void mousePosCallback(long window, double xpos, double ypos){
        if (get().mouseButtonDown > 0){
            get().isDragging = true;
        }

        get().lastX = get().xPos;
        get().lastY = get().yPos;
        get().lastWorldX = get().worldX;
        get().lastWorldY = get().worldY;
        get().xPos = xpos;
        get().yPos = ypos;
        calcOrthoX();
        calcOrthoY();

    }

    public static void mouseButtonCallback(long window, int button, int action, int mods){
        if (action == GLFW_PRESS) {
            get().mouseButtonDown++;

            if(button < get().mouseButtonPressed.length) {
                get().mouseButtonPressed[button] = true;
            }
        } else if(action == GLFW_RELEASE){
            get().mouseButtonDown--;

            if(button < get().mouseButtonPressed.length) {
                get().mouseButtonPressed[button] = false;
                get().isDragging = false;
            }
        }
    }

    public static void mouseScrollCallback(long window, double xoffset, double yoffset){
        get().scrollX = xoffset;
        get().scrollY = yoffset;
    }

    public static void endFrame(){
        get().scrollX = 0;
        get().scrollY = 0;
        get().lastY = get().yPos;
        get().lastX = get().xPos;
        get().lastWorldY = get().worldY;
        get().lastWorldX = get().worldX;
    }

    public static float getX(){
        return (float) get().xPos;
    }
    public static float getY(){
        return (float) get().yPos;
    }

    public static float getDx(){
        return (float) (get().lastX - get().xPos);
    }

    public static float getWorldDx(){
        return (float) (get().lastWorldX - get().worldX);
    }

    public static float getDy(){
        return (float) (get().lastY - get().yPos);
    }

    public static float getWorldDy(){
        return (float) (get().lastWorldY - get().worldY);
    }

    public static float getScrollX(){
        return (float) get().scrollX;
    }
    public static float getScrollY(){
        return (float) get().scrollY;
    }

    public static boolean isDragging(){
        return get().isDragging;
    }
    public static boolean mouseButtonDown(int button){
        if(button < get().mouseButtonPressed.length){
            return get().mouseButtonPressed[button];
        }else{
            return false;
        }
    }

    public static float getScreenX(){
        float cX = getX() - get().gameViewPos.x;
        cX = (cX / get().gameViewSize.x) * 2560f;
        return cX;
    }

    public static float getScreenY(){
        float cY = getY() - get().gameViewPos.y;
        cY = 1440f - ((cY / get().gameViewSize.y) * 1440f);
        return cY;
    }

    public static void setGameViewPos(Vector2f gameViewPos) {
        get().gameViewPos.set(gameViewPos);
    }

    public static void setGameViewSize(Vector2f gameViewSize) {
        get().gameViewSize.set(gameViewSize);
    }
}
