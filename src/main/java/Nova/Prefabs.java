package Nova;


import Components.Sprite;
import Components.SpriteRenderer;
import org.joml.Vector2f;

public class Prefabs {

    public static GameObject generateSpriteObject(Sprite sprite, float sizeX, float sizeY){
        return generateSpriteObject(sprite, sizeX, sizeY, 0);
    }

    public static GameObject generateSpriteObject(Sprite sprite, float sizeX, float sizeY, int zIndex){
        GameObject block = Window.getScene().createGameObject("Sprite_Object_Gen");
        block.transform.scale.x = sizeX;
        block.transform.scale.y = sizeY;
        SpriteRenderer renderer = new SpriteRenderer();
        renderer.setSprite(sprite);
        block.addComponent(renderer);
        return block;
    }
}
