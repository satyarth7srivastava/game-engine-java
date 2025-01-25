package Components;

import Nova.MouseListner;
import editor.PropertiesWindow;

public class ScaleGizmo extends Gizmo{
    public ScaleGizmo(Sprite scaleSprite, PropertiesWindow propertiesWindow){
        super(scaleSprite, propertiesWindow);
    }

    @Override
    public void update(float dt){

        if (activeGameObject != null){
            if (xAxisActive && !yAxisActive){
                activeGameObject.transform.scale.x -= MouseListner.getWorldDx();
            } else if (yAxisActive && !xAxisActive) {
                activeGameObject.transform.scale.y -= MouseListner.getWorldDy();
            }
        }

        super.update(dt);
    }
}
