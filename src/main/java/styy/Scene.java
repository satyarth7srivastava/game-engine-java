package styy;

import java.util.ArrayList;
import java.util.List;

public abstract class Scene {

    protected  Camera camera;
    protected List<GameObject> gameObjects = new ArrayList<>();
    private boolean isRunning = false;

    public Scene(){

    }

    public void init(){

    }

    public void start(){
        for(GameObject go : this.gameObjects){
            go.start();
        }
        isRunning = true;
    }
    public void addGameObjectToScene(GameObject go){
        if(isRunning){
            gameObjects.add(go);
            go.start();
        }else{
            gameObjects.add(go);
        }
    }

    public abstract void update(float dt);
}
