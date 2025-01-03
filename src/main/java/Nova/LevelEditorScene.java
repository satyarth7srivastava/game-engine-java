package Nova;

import Components.Sprite;
import Components.SpriteRenderer;
import Components.SpriteSheet;
import imgui.ImGui;
import org.joml.Vector2f;
import org.joml.Vector4f;
import util.AssetPool;

public class LevelEditorScene extends Scene{
    private GameObject obj1;
    private SpriteSheet sprites;

    public LevelEditorScene(){

    }

    @Override
    public void init() {
        loadResources();

        this.camera = new Camera(new Vector2f());

        this.sprites = AssetPool.getSpriteSheet("assets/images/spritesheet.png");
        this.obj1 = new GameObject("Ob1", new Transform(new Vector2f(100, 100), new Vector2f(256,256)), 1);
        this.obj1.addComponent(new SpriteRenderer(new Vector4f(1,0,0,0)));
        this.addGameObjectToScene(this.obj1);
        this.activeGameObject = obj1;

        GameObject obj2 = new GameObject("Ob2", new Transform(new Vector2f(400, 100), new Vector2f(256,256)), 1);
        obj2.addComponent(new SpriteRenderer(sprites.getSprite(15)));
        this.addGameObjectToScene(obj2);
    }


    private void loadResources(){
        AssetPool.getShader("assets/shaders/default.glsl");
        AssetPool.addSpriteSheet("assets/images/spritesheet.png",
                new SpriteSheet(AssetPool.getTexture("assets/images/spritesheet.png"),
                        16, 16, 26, 0
                ));
    }

//    private int spriteInd = 0;
//    private float spriteFlipTime = 0.0f;
//    private float spriteFlipTimeSpeed = 5f;
    @Override
    public void update(float dt) {
//        System.out.println("FPS: " + ((float) 1/dt));
//        spriteFlipTime += dt*spriteFlipTimeSpeed;
//        if(spriteFlipTime > 1){
//            spriteInd++;
//            spriteFlipTime = 0.0f;
//            if(spriteInd > 25){
//                spriteInd = 0;
//            }
//            this.obj1.getComponent(SpriteRenderer.class).setSprite(sprites.getSprite(spriteInd));
//        }


        for(GameObject go : this.gameObjects){
            go.update(dt);
        }
        this.renderer.render();
    }

    @Override
    public void imgui(){
        ImGui.begin("Test");
        ImGui.text("Random stuff");
        ImGui.end();
    }
}
