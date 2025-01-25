package Components;

import Nova.MouseListner;
import editor.PropertiesWindow;

public class TranslateGizmo extends Gizmo{

    public TranslateGizmo(Sprite arrowSprite, PropertiesWindow propertiesWindow){
        super(arrowSprite, propertiesWindow);
    }

    @Override
    public void update(float dt){

        if (activeGameObject != null){
            if (xAxisActive && !yAxisActive){
                activeGameObject.transform.position.x -= MouseListner.getWorldDx();
            } else if (yAxisActive && !xAxisActive) {
                activeGameObject.transform.position.y -= MouseListner.getWorldDy();
            }
        }

        super.update(dt);
    }

}
