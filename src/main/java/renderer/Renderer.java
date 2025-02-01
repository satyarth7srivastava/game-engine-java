package renderer;

import Components.SpriteRenderer;
import Nova.GameObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Renderer {
    private final int MAX_BATCH_SIZE = 1000;
    private List<RenderBatch> batches;
    private static Shader currentShader;

    public Renderer(){
        this.batches = new ArrayList<>();
    }

    public void add(GameObject go){
        SpriteRenderer spr = go.getComponent(SpriteRenderer.class);
        if(spr != null){
            add(spr);
        }
    }

    public void add(SpriteRenderer spr){
        boolean added = false;
        for(RenderBatch batch : batches){
            if(batch.hasRoom() && batch.getzIndex() == spr.gameObject.transform.zIndex){
                Texture tex = spr.getTexture();
                if(tex == null || batch.hasTexture(tex) || batch.hasTextureRoom()) {
                    batch.addSprite(spr);
                    added = true;
                    break;
                }
            }
        }

        if(!added){
            RenderBatch rb = new RenderBatch(MAX_BATCH_SIZE, spr.gameObject.transform.zIndex);
            rb.start();
            batches.add(rb);
            rb.addSprite(spr);
            Collections.sort(batches);
        }
    }

    public void destroyGameObject(GameObject go){
        if (go.getComponent(SpriteRenderer.class) == null) return;

        for (RenderBatch batch : batches){
            if (batch.destroyIfExist(go)){
                return;
            }
        }
    }

    public static void bindShader(Shader shader){
        currentShader = shader;
    }
    public static Shader getBoundShader(){
        return currentShader;
    }

    public void render(){
        currentShader.use();
        int x = 1;
        for(RenderBatch batch : batches){
            x++;
            batch.render();
        }
    }
}
