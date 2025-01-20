package scenes;

import Components.*;
import Nova.Camera;
import Nova.GameObject;
import Nova.Prefabs;
import Nova.Transform;
import imgui.ImGui;
import imgui.ImVec2;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.joml.Vector4f;
import renderer.DebugDraw;
import util.AssetPool;

public class LevelEditorScene extends Scene {
    private GameObject obj1;
    private GameObject obj2;
    private SpriteSheet sprites;

    private GameObject levelEditorStuff = new GameObject("LevelEditor", new Transform(new Vector2f()), 0);

    public LevelEditorScene(){

    }

    @Override
    public void init() {
        this.camera = new Camera(new Vector2f());
        levelEditorStuff.addComponent(new MouseControls());
        levelEditorStuff.addComponent(new GridLines());
        levelEditorStuff.addComponent(new EditorCamera(this.camera));

        loadResources();
        this.sprites = AssetPool.getSpriteSheet("assets/images/s1/decAndblock.png");


        DebugDraw.addLine2D(new Vector2f(0,0), new Vector2f(800, 800), new Vector3f(1,0,0), 165);


//        this.obj1 = new GameObject("Ob1", new Transform(new Vector2f(100, 100), new Vector2f(256, 256)), 1);
//        SpriteRenderer obj1Sprite = new SpriteRenderer();
//        this.obj1.addComponent(obj1Sprite);
//        this.obj1.addComponent(new Rigidbody());
//        obj1Sprite.setColor(new Vector4f(1,1,0,1));
//        this.addGameObjectToScene(this.obj1);
//
//        this.obj2 = new GameObject("Ob2", new Transform(new Vector2f(400, 100), new Vector2f(256, 256)), 1);
//        SpriteRenderer obj2Sprite = new SpriteRenderer();
//        obj2.addComponent(obj2Sprite);
//        obj2Sprite.setSprite(sprites.getSprite(0));
//        this.addGameObjectToScene(obj2);
    }


    private void loadResources(){
        AssetPool.getShader("assets/shaders/default.glsl");
        AssetPool.addSpriteSheet("assets/images/s1/decAndblock.png",
                new SpriteSheet(AssetPool.getTexture("assets/images/s1/decAndblock.png"),
                        16, 16, 81, 0
                ));

        for(GameObject g : gameObjects){
            if (g.getComponent(SpriteRenderer.class) != null){
                SpriteRenderer spr = g.getComponent(SpriteRenderer.class);
                if (spr.getTexture() != null){
                    spr.setTexture(AssetPool.getTexture(spr.getTexture().getFilePath()));
                }
            }
        }
    }

    float angle = 0.0f;
    @Override
    public void update(float dt) {
        levelEditorStuff.update(dt);
        this.camera.adjustProjection();
//        DebugDraw.addCircle(new Vector2f(400, 150), 64, new Vector3f(0, 1, 1), 1);
//        DebugDraw.addBox2D(new Vector2f(200f, 200f), new Vector2f(64, 32), angle, new Vector3f(1,0,0), 1);
//        angle += 0.5f;

        for(GameObject go : this.gameObjects){
            go.update(dt);
        }
    }

    @Override
    public void render(){
        this.renderer.render();
    }

    @Override
    public void imgui(){
        ImGui.begin("Test Window");

        ImVec2 windowPos = new ImVec2();
        ImGui.getWindowPos(windowPos);
        ImVec2 windowSize = new ImVec2();
        ImGui.getWindowSize(windowSize);
        ImVec2 itemSpacing = new ImVec2();
        ImGui.getStyle().getItemSpacing(itemSpacing);

        float windowX2 = windowPos.x + windowSize.x;
        for (int i = 0; i < sprites.size(); i++){
            Sprite sprite = sprites.getSprite(i);
            float spriteWidth = sprite.getWidth() * 4;
            float spriteHeight = sprite.getHeight() * 4;
            int id = sprite.getTexId();
            Vector2f[] texCoords = sprite.getTexCoords();

            ImGui.pushID(i);
            if (ImGui.imageButton(id, spriteWidth, spriteHeight, texCoords[2].x, texCoords[0].y, texCoords[0].x, texCoords[2].y)){
                GameObject object = Prefabs.generateSpriteObject(sprite, 32, 32);
                levelEditorStuff.getComponent(MouseControls.class).pickUpObj(object);
            }
            ImGui.popID();

            ImVec2 lastButtonPos = new ImVec2();
            ImGui.getItemRectMax(lastButtonPos);
            float lastButtonX2 = lastButtonPos.x;
            float nextButtonX2 = lastButtonX2 + itemSpacing.x + spriteWidth;

            if(i+1 < sprites.size() && nextButtonX2<windowX2){
                ImGui.sameLine();
            }
        }
        ImGui.end();
    }
}
