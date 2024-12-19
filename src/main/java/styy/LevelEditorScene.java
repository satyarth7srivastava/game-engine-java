package styy;

import java.awt.event.KeyEvent;

public class LevelEditorScene extends Scene{
    private boolean changingScene = false;
    private float timeToChange = 2.0f;

    public LevelEditorScene(){
        System.out.println("Inside Level Editor Scene");
    }

    @Override
    public void update(float dt) {
        if(KeyListener.isKeyPressed(KeyEvent.VK_SPACE) && !changingScene){
            changingScene = true;
        }
        if(changingScene && timeToChange > 0){
            timeToChange -= dt;
            Window.get().r -= dt * 5.0f;
            Window.get().g -= dt * 5.0f;
            Window.get().b -= dt * 5.0f;
            Window.get().a -= dt * 5.0f;
        }else if(changingScene){
            Window.changeScene(1);
        }
    }
}
