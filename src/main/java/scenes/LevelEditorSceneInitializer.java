package scenes;

import Components.*;
import Nova.*;
import imgui.ImGui;
import imgui.ImVec2;
import org.joml.Vector2f;
import util.AssetPool;

public class LevelEditorSceneInitializer extends SceneInitializer {
    private SpriteSheet sprites;

    private GameObject levelEditorStuff;

    public LevelEditorSceneInitializer(){

    }

    @Override
    public void init(Scene scene) {
        this.sprites = AssetPool.getSpriteSheet("assets/images/s1/decAndblock.png");

        SpriteSheet gizmos = AssetPool.getSpriteSheet("assets/images/gizmos.png");

        levelEditorStuff = scene.createGameObject("LevelEditor");
        levelEditorStuff.setNoSerialize();
        levelEditorStuff.addComponent(new MouseControls());
        levelEditorStuff.addComponent(new GridLines());
        levelEditorStuff.addComponent(new EditorCamera(scene.getCamera()));
        levelEditorStuff.addComponent(new GizmoSystem(gizmos));

        scene.addGameObjectToScene(levelEditorStuff);
    }

    @Override
    public void loadResources(Scene scene){
        AssetPool.getShader("assets/shaders/default.glsl");
        AssetPool.addSpriteSheet("assets/images/s1/decAndblock.png",
                new SpriteSheet(AssetPool.getTexture("assets/images/s1/decAndblock.png"),
                        16, 16, 81, 0
                ));

        AssetPool.addSpriteSheet("assets/images/gizmos.png",
                new SpriteSheet(AssetPool.getTexture("assets/images/gizmos.png"),
                        24, 48, 3, 0)
                );

        for(GameObject g : scene.getGameObjects()){
            if (g.getComponent(SpriteRenderer.class) != null){
                SpriteRenderer spr = g.getComponent(SpriteRenderer.class);
                if (spr.getTexture() != null){
                    spr.setTexture(AssetPool.getTexture(spr.getTexture().getFilePath()));
                }
            }
        }
    }




    @Override
    public void imgui(){
        ImGui.begin("Level Editor stuffs");
        levelEditorStuff.imgui();
        ImGui.end();

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
