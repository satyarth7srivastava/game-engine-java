package Components;


import Nova.GameObject;
import Nova.MouseListner;
import Nova.Window;
import util.Settings;

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
    public void editorUpdate(float dt) {
        if (this.holdingObj != null)
        {
            holdingObj.transform.position.x = MouseListner.getOrthoX();
            holdingObj.transform.position.y = MouseListner.getOrthoY();
            if (holdingObj.transform.position.x >= 0.0f)
                holdingObj.transform.position.x = (int)(holdingObj.transform.position.x/ Settings.GRID_WIDTH) * Settings.GRID_WIDTH;
            else
                holdingObj.transform.position.x = (int)(holdingObj.transform.position.x/ Settings.GRID_WIDTH) * Settings.GRID_WIDTH - Settings.GRID_WIDTH;
            if (holdingObj.transform.position.y >= 0.0f)
                holdingObj.transform.position.y = (int)(holdingObj.transform.position.y/ Settings.GRID_HEIGHT) * Settings.GRID_HEIGHT;
            else
                holdingObj.transform.position.y = (int)(holdingObj.transform.position.y/ Settings.GRID_HEIGHT) * Settings.GRID_HEIGHT - Settings.GRID_HEIGHT;


            holdingObj.transform.position.x += Settings.GRID_WIDTH * 0.5f;
            holdingObj.transform.position.y += Settings.GRID_HEIGHT * 0.5f;
            if (MouseListner.mouseButtonDown(GLFW_MOUSE_BUTTON_LEFT))
                place();
        }
    }
}
