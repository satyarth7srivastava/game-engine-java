package Components;


import Nova.GameObject;
import Nova.MouseListner;
import Nova.Window;

import static org.lwjgl.glfw.GLFW.GLFW_MOUSE_BUTTON_LEFT;

public class MouseControls extends Component {
    GameObject holdingObj = null;

    public void pickUpObj(GameObject obj){
        this.gameObject = obj;
        Window.getScene().addGameObjectToScene(obj);
        holdingObj = obj;
    }

    public void place(){
        this.holdingObj = null;
    }

    @Override
    public void update(float dt) {
        if(holdingObj != null){
            holdingObj.transform.position.x = MouseListner.getOrthoX() - 16;
            holdingObj.transform.position.y = MouseListner.getOrthoY() - 16;

            if(MouseListner.mouseButtonDown(GLFW_MOUSE_BUTTON_LEFT)){
                place();
            }
        }
    }
}
