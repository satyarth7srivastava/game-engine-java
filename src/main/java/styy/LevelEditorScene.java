package styy;

import Components.SpriteRenderer;
import org.joml.Vector2f;
import org.joml.Vector4f;
import util.AssetPool;

public class LevelEditorScene extends Scene{

    public LevelEditorScene(){

    }

    @Override
    public void init() {
        this.camera = new Camera(new Vector2f());
        GameObject obj1 = new GameObject("Ob1", new Transform(new Vector2f(100, 100), new Vector2f(256,256)));
        obj1.addComponent(new SpriteRenderer(AssetPool.getTexture("assets/images/testImage_Mario.png")));
        this.addGameObjectToScene(obj1);

        GameObject obj2 = new GameObject("Ob2", new Transform(new Vector2f(400, 100), new Vector2f(256,256)));
        obj2.addComponent(new SpriteRenderer(AssetPool.getTexture("assets/images/testImage_Heart.jpg")));
        this.addGameObjectToScene(obj2);
    }


    private void loadResources(){
        AssetPool.getShader("assets/shaders/default.glsl");
    }

    @Override
    public void update(float dt) {
//        System.out.println("FPS: " + ((float) 1/dt));

        for(GameObject go : this.gameObjects){
            go.update(dt);
        }
        this.renderer.render();
    }
}
