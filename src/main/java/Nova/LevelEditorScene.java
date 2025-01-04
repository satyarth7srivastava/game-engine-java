package Nova;

import Components.SpriteRenderer;
import Components.SpriteSheet;
import imgui.ImGui;
import org.joml.Vector2f;
import org.joml.Vector4f;
import util.AssetPool;

public class LevelEditorScene extends Scene{
    private GameObject obj1;
    private GameObject obj2;
    private SpriteSheet sprites;

    public LevelEditorScene(){

    }

    @Override
    public void init() {
        loadResources();

        this.camera = new Camera(new Vector2f());
        if(loadedLevel) return;

        this.sprites = AssetPool.getSpriteSheet("assets/images/spritesheet.png");
        this.obj1 = new GameObject("Ob1", new Transform(new Vector2f(100, 100), new Vector2f(256, 256)), 1);
        SpriteRenderer obj1Sprite = new SpriteRenderer();
        this.obj1.addComponent(obj1Sprite);
        obj1Sprite.setColor(new Vector4f(1,1,0,1));
        this.addGameObjectToScene(this.obj1);
        this.activeGameObject = obj1;

        this.obj2 = new GameObject("Ob2", new Transform(new Vector2f(400, 100), new Vector2f(256, 256)), 1);
        SpriteRenderer obj2Sprite = new SpriteRenderer();
        obj2.addComponent(obj2Sprite);
        obj2Sprite.setSprite(sprites.getSprite(0));
        this.addGameObjectToScene(obj2);
    }


    private void loadResources(){
        AssetPool.getShader("assets/shaders/default.glsl");
        AssetPool.addSpriteSheet("assets/images/spritesheet.png",
                new SpriteSheet(AssetPool.getTexture("assets/images/spritesheet.png"),
                        16, 16, 26, 0
                ));
    }

    @Override
    public void update(float dt) {
        for(GameObject go : this.gameObjects){
            go.update(dt);
        }
        this.renderer.render();
    }

    @Override
    public void imgui(){
    }
}
