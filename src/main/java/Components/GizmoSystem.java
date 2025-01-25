package Components;

import Nova.KeyListener;
import Nova.Window;

import static org.lwjgl.glfw.GLFW.GLFW_KEY_E;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_R;

public class GizmoSystem extends Component{
    private SpriteSheet gizmos;
    private int usingGizmo = 0;

    public GizmoSystem(SpriteSheet gizmoSprites){
        this.gizmos = gizmoSprites;
    }

    @Override
    public void start(){
        System.out.println("Inside the adding components for the gizmo system");
        System.out.println("Current game object = " + gameObject.name);
        gameObject.addComponent(new TranslateGizmo(gizmos.getSprite(1), Window.getImGuiLayer().getPropertiesWindow()));
        gameObject.addComponent(new ScaleGizmo(gizmos.getSprite(2), Window.getImGuiLayer().getPropertiesWindow()));
        System.out.println("adding of translate and scale done");
    }

    @Override
    public void update(float dt){
        if (usingGizmo == 0){
            gameObject.getComponent(TranslateGizmo.class).setUsing();
            gameObject.getComponent(ScaleGizmo.class).unsetUsing();
        } else if (usingGizmo == 1) {
            gameObject.getComponent(TranslateGizmo.class).unsetUsing();
            gameObject.getComponent(ScaleGizmo.class).setUsing();
        }

        if (KeyListener.isKeyPressed(GLFW_KEY_E)){
            usingGizmo = 0;
        } else if (KeyListener.isKeyPressed(GLFW_KEY_R)) {
            usingGizmo = 1;
        }
    }
}
