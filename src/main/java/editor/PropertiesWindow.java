package editor;

import Components.NonPickable;
import Nova.GameObject;
import Nova.MouseListner;
import imgui.ImGui;
import physics2D.components.Box2DCollider;
import physics2D.components.CircleCollider;
import physics2D.components.RigidBody2D;
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

    public void setActiveGameObject(GameObject activeGameObject) {
        this.activeGameObject = activeGameObject;
    }

    public void update(float dt, Scene currentScene){
        debounce -= dt;

        if (MouseListner.mouseButtonDown(GLFW_MOUSE_BUTTON_LEFT) && debounce < 0){
            int x = (int) MouseListner.getScreenX();
            int y = (int) MouseListner.getScreenY();
            int gameObjectId = pickingTexture.readPixel(x, y);
            GameObject pickObj = currentScene.getGameObject(gameObjectId);
            if (pickObj != null && pickObj.getComponent(NonPickable.class) == null){
                activeGameObject = pickObj;
            } else if (pickObj == null && !MouseListner.isDragging()) {
                activeGameObject = null;
            }
            this.debounce = 0.2f;
        }
    }
    public void imgui(){
        if(activeGameObject != null){
            ImGui.begin("Properties");

            if (ImGui.beginPopupContextWindow("Component Adder")){
                if (ImGui.menuItem("Add RigidBody")) {
                    if (activeGameObject.getComponent(RigidBody2D.class) == null) {
                        activeGameObject.addComponent(new RigidBody2D());
                    }
                }

                if (ImGui.menuItem("Add Box Collider")){
                    if (activeGameObject.getComponent(Box2DCollider.class) == null){
                        activeGameObject.addComponent(new Box2DCollider());
                    }
                }

                if (ImGui.menuItem("Add Circle Collider")){
                    if (activeGameObject.getComponent(CircleCollider.class) == null){
                        activeGameObject.addComponent(new CircleCollider());
                    }
                }

                ImGui.endPopup();
            }

            activeGameObject.imgui();
            ImGui.end();
        }
    }
}
