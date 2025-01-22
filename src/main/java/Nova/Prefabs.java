package Nova;


import Components.Sprite;
import Components.SpriteRenderer;
import org.joml.Vector2f;

public class Prefabs {

    public static GameObject generateSpriteObject(Sprite sprite, float sizeX, float sizeY){
        return generateSpriteObject(sprite, sizeX, sizeY, 0);
    }

    public static GameObject generateSpriteObject(Sprite sprite, float sizeX, float sizeY, int zIndex){
        GameObject block = new GameObject("SpriteObjectGn",
                new Transform(new Vector2f(), new Vector2f(sizeX, sizeY)), zIndex);
        SpriteRenderer renderer = new SpriteRenderer();
        renderer.setSprite(sprite);
        block.addComponent(renderer);
        return block;
    }
}
