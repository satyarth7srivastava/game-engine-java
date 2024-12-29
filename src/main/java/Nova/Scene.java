package Nova;

import renderer.Renderer;

import java.util.ArrayList;
import java.util.List;

public abstract class Scene {

    protected  Camera camera;
    protected List<GameObject> gameObjects = new ArrayList<>();
    protected Renderer renderer = new Renderer();
    private boolean isRunning = false;

    public Scene(){
//        this.renderer = new Renderer();
    }

    public void init(){

    }

    public void start(){
        for(GameObject go : this.gameObjects){
            go.start();
            this.renderer.add(go);
        }
        isRunning = true;
    }
    public void addGameObjectToScene(GameObject go){
        if(isRunning){
            gameObjects.add(go);
            go.start();
            this.renderer.add(go);
        }else{
            gameObjects.add(go);
        }
    }

    public abstract void update(float dt);

    public Camera getCamera(){
        return this.camera;
    }
}
