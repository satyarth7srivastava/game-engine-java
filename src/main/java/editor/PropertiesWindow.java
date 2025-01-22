package editor;

import Nova.GameObject;
import Nova.MouseListner;
import imgui.ImGui;
import renderer.PickingTexture;
import scenes.Scene;

import static org.lwjgl.glfw.GLFW.GLFW_MOUSE_BUTTON_LEFT;

public class PropertiesWindow {
    private GameObject activeGameObject = null;
    private PickingTexture pickingTexture;
    private float debounce = 0.2f;

    public PropertiesWindow(PickingTexture pickingTexture){
        this.pickingTexture = pickingTexture;
    }

    public GameObject getActiveGameObject() {
        return activeGameObject;
    }

    public void update(float dt, Scene currentScene){
        debounce -= dt;

        if (MouseListner.mouseButtonDown(GLFW_MOUSE_BUTTON_LEFT) && debounce < 0){
            int x = (int) MouseListner.getScreenX();
            int y = (int) MouseListner.getScreenY();
            int gameObjectId = pickingTexture.readPixel(x, y);
            activeGameObject = currentScene.getGameObject(gameObjectId);
            this.debounce = 0.2f;
        }
    }
    public void imgui(){
        if(activeGameObject != null){
            ImGui.begin("Properties");
            activeGameObject.imgui();
            ImGui.end();
        }
    }
}
