package Components;

import Nova.Camera;
import Nova.KeyListener;
import Nova.MouseListner;
import org.joml.Vector2f;

import java.util.Vector;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.glfw.GLFW.GLFW_MOUSE_BUTTON_RIGHT;

public class EditorCamera extends Component{

    private float dragDebounce = 0.032f;
    private float dragSensitivity = 30.0f;
    private float scrollSensitivity = 0.25f;

    private Camera levelEditorCamera;
    private Vector2f clickOrigin;

    public EditorCamera(Camera levelEditorCamera){
        this.levelEditorCamera = levelEditorCamera;
        this.clickOrigin = new Vector2f();
    }

    @Override
    public void editorUpdate(float dt){
        if (MouseListner.mouseButtonDown(GLFW_MOUSE_BUTTON_RIGHT) && dragDebounce > 0){
            this.clickOrigin = new Vector2f(MouseListner.getOrthoX(), MouseListner.getOrthoY());
            dragDebounce -= dt;
            return;
        } else if (MouseListner.mouseButtonDown(GLFW_MOUSE_BUTTON_RIGHT)) {
            Vector2f mousePos = new Vector2f(MouseListner.getOrthoX(), MouseListner.getOrthoY());
            Vector2f delta = new Vector2f(mousePos)
                    .sub(this.clickOrigin);
            levelEditorCamera.position.sub(delta.mul(dt).mul(dragSensitivity));
            this.clickOrigin.lerp(mousePos, dt);
        }

        if (dragDebounce <= 0.0f && !MouseListner.mouseButtonDown(GLFW_MOUSE_BUTTON_RIGHT)){
            dragDebounce = 0.1f;
        }

        if(MouseListner.getScrollY() != 0.0f && KeyListener.isKeyPressed(GLFW_KEY_LEFT_CONTROL)){
            float addValue = (float) Math.pow(Math.abs(MouseListner.getScrollY() * scrollSensitivity),
                    1/levelEditorCamera.getZoom());
            addValue *= Math.signum(MouseListner.getScrollY());
            levelEditorCamera.addZoom(addValue);
        }
    }

    @Override
    public void update(float dt){
        if (MouseListner.mouseButtonDown(GLFW_MOUSE_BUTTON_RIGHT) && dragDebounce > 0){
            this.clickOrigin = new Vector2f(MouseListner.getOrthoX(), MouseListner.getOrthoY());
            dragDebounce -= dt;
            return;
        } else if (MouseListner.mouseButtonDown(GLFW_MOUSE_BUTTON_RIGHT)) {
            Vector2f mousePos = new Vector2f(MouseListner.getOrthoX(), MouseListner.getOrthoY());
            Vector2f delta = new Vector2f(mousePos)
                    .sub(this.clickOrigin);
            levelEditorCamera.position.sub(delta.mul(dt).mul(dragSensitivity));
            this.clickOrigin.lerp(mousePos, dt);
        }

        if (dragDebounce <= 0.0f && !MouseListner.mouseButtonDown(GLFW_MOUSE_BUTTON_RIGHT)){
            dragDebounce = 0.1f;
        }

        if(MouseListner.getScrollY() != 0.0f && KeyListener.isKeyPressed(GLFW_KEY_LEFT_CONTROL)){
            float addValue = (float) Math.pow(Math.abs(MouseListner.getScrollY() * scrollSensitivity),
                    1/levelEditorCamera.getZoom());
            addValue *= Math.signum(MouseListner.getScrollY());
            levelEditorCamera.addZoom(addValue);
        }

        //TODO: Add camera reset position system lecture 33 at 22:00
    }
}
