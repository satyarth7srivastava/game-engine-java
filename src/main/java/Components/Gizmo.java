package Components;

import Nova.*;
import editor.PropertiesWindow;
import org.joml.Vector2f;
import org.joml.Vector4f;

import static org.lwjgl.glfw.GLFW.*;

public class Gizmo extends Component{
    private Vector4f xAxisColor = new Vector4f(1, 0, 0, 1);
    private Vector4f xAxisColorHover = new Vector4f();
    private Vector4f yAxisColor = new Vector4f(0, 1, 0, 1);
    private Vector4f yAxisColorHover = new Vector4f();

    private float gizmoWidth = 16f / 80f;
    private float gizmoHeight = 48f / 80f;
    protected boolean xAxisActive = false;
    protected boolean yAxisActive = false;
    private boolean using = false;

    private Vector2f xAxisOffset = new Vector2f(24f / 80f, -6f / 80f);
    private Vector2f yAxisOffset = new Vector2f(-7 / 80f, 21 / 80f);

    private GameObject xAxisObject;
    private GameObject yAxisObject;
    private SpriteRenderer xAxisSprite;
    private SpriteRenderer yAxisSprite;
    protected GameObject activeGameObject = null;
    private PropertiesWindow propertiesWindow;

    public Gizmo(Sprite arrowSprite, PropertiesWindow propertiesWindow){
        this.xAxisObject = Prefabs.generateSpriteObject(arrowSprite, gizmoWidth, gizmoHeight, 100);
        this.yAxisObject = Prefabs.generateSpriteObject(arrowSprite, gizmoWidth, gizmoHeight, 100);
        this.xAxisSprite = this.xAxisObject.getComponent(SpriteRenderer.class);
        this.yAxisSprite = this.yAxisObject.getComponent(SpriteRenderer.class);
        this.xAxisObject.addComponent(new NonPickable());
        this.yAxisObject.addComponent(new NonPickable());

        this.propertiesWindow = propertiesWindow;

        Window.getScene().addGameObjectToScene(this.xAxisObject);
        Window.getScene().addGameObjectToScene(this.yAxisObject);
    }

    @Override
    public void start(){
        xAxisObject.transform.rotation = 90;
        yAxisObject.transform.rotation = 180;
        this.xAxisObject.transform.zIndex = 100;
        this.yAxisObject.transform.zIndex = 100;
        this.xAxisObject.setNoSerialize();
        this.yAxisObject.setNoSerialize();
    }

    @Override
    public void update(float dt){
        if(using){
            setInactive();
        }
    }

    @Override
    public void editorUpdate(float dt){
        if(!using){
            return;
        }

        this.activeGameObject = this.propertiesWindow.getActiveGameObject();
        if (this.activeGameObject != null) {
            this.setActive();

            //todo: move this to keybinding class or similar
            if (KeyListener.isKeyPressed(GLFW_KEY_LEFT_CONTROL) &&
                    KeyListener.keyBeginPress(GLFW_KEY_D)){
                GameObject newObj = this.activeGameObject.copy();
                Window.getScene().addGameObjectToScene(newObj);
                newObj.transform.position.add(0.1f, 0.1f);
                this.propertiesWindow.setActiveGameObject(newObj);
                return;
            } else if(KeyListener.keyBeginPress(GLFW_KEY_DELETE)){
                activeGameObject.destroy();
                this.setInactive();
                this.propertiesWindow.setActiveGameObject(null);
            }
        }else{
            this.setInactive();
            return;
        }


        if (this.activeGameObject != null){
            this.xAxisObject.transform.position.set(this.activeGameObject.transform.position);
            this.yAxisObject.transform.position.set(this.activeGameObject.transform.position);
            this.xAxisObject.transform.position.add(xAxisOffset);
            this.yAxisObject.transform.position.add(yAxisOffset);
        }
        boolean xAxisHot = checkXHover();
        boolean yAxisHot = checkYHover();

        if ((xAxisHot || xAxisActive) && MouseListner.isDragging() && MouseListner.mouseButtonDown(GLFW_MOUSE_BUTTON_LEFT)){
            xAxisActive = true;
            yAxisActive = false;
        } else if ((yAxisHot || yAxisActive) && MouseListner.isDragging() && MouseListner.mouseButtonDown(GLFW_MOUSE_BUTTON_LEFT)){
            xAxisActive = false;
            yAxisActive = true;
        } else {
            xAxisActive = false;
            yAxisActive = false;
        }

    }

    private void setActive(){
        this.xAxisSprite.setColor(xAxisColor);
        this.yAxisSprite.setColor(yAxisColor);
    }
    private boolean checkXHover(){
        Vector2f mousePos = new Vector2f(MouseListner.getOrthoX(), MouseListner.getOrthoY());
        if (mousePos.x <= xAxisObject.transform.position.x + (gizmoHeight / 2f) &&
                mousePos.x >= xAxisObject.transform.position.x - (gizmoHeight / 2f) &&
                mousePos.y >= xAxisObject.transform.position.y - (gizmoWidth / 2f) &&
                mousePos.y <= xAxisObject.transform.position.y + (gizmoWidth / 2f)){
            xAxisSprite.setColor(xAxisColorHover);
            return true;
        }
        xAxisSprite.setColor(xAxisColor);
        return false;
    }
    private boolean checkYHover(){
        Vector2f mousePos = new Vector2f(MouseListner.getOrthoX(), MouseListner.getOrthoY());
        if (mousePos.x <= yAxisObject.transform.position.x + (gizmoWidth / 2f)&&
                mousePos.x >= yAxisObject.transform.position.x - (gizmoWidth / 2f) &&
                mousePos.y <= yAxisObject.transform.position.y + (gizmoHeight / 2f) &&
                mousePos.y >= yAxisObject.transform.position.y - (gizmoHeight / 2f)){
            yAxisSprite.setColor(yAxisColorHover);
            return true;
        }
        yAxisSprite.setColor(yAxisColor);
        return false;
    }
    private void setInactive(){
        this.activeGameObject = null;
        this.xAxisSprite.setColor(new Vector4f(0,0,0,0));
        this.yAxisSprite.setColor(new Vector4f(0,0,0,0));
    }

    public void setUsing(){
        this.using = true;
    }
    public void unsetUsing(){
        this.using = false;
        this.setInactive();
    }
}
