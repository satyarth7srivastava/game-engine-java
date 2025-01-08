package Components;

import org.joml.Vector2f;
import renderer.Texture;

import java.util.ArrayList;
import java.util.List;

public class SpriteSheet {
    private Texture texture;
    private List<Sprite> sprites;

    public SpriteSheet(Texture texture, int spriteWidth, int spriteHeight, int numSprites, int spacing){
        this.sprites = new ArrayList<>();

        this.texture = texture;
        float currentX = 0;
        float currentY = texture.getHeight() - spriteHeight;
        for(int i = 0; i < numSprites; i++){
            float topY = (currentY + spriteHeight) / (float)texture.getHeight();
            float rightX = (currentX + spriteWidth) / (float) texture.getWidth();
            float bottomY = currentY / (float) texture.getHeight();
            float leftX = currentX / (float) texture.getWidth();

            Vector2f[] texCoords = {
                    new Vector2f(rightX,topY),
                    new Vector2f(rightX,bottomY),
                    new Vector2f(leftX,bottomY),
                    new Vector2f(leftX,topY),
            };

            Sprite sprite = new Sprite();
            sprite.setTexture(this.texture);
            sprite.setTexCoords(texCoords);
            this.sprites.add(sprite);
            sprite.setWidth(spriteWidth);
            sprite.setHeight(spriteHeight);

            currentX += spriteWidth + spacing;
            if(currentX >= texture.getWidth()){
                currentX = 0;
                currentY -= (spriteHeight + spacing);
            }
        }
    }

    public int size(){return this.sprites.size();}
    public Sprite getSprite(int i){
        return this.sprites.get(i);
    }
}
