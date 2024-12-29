package renderer;

import Components.SpriteRenderer;
import Nova.GameObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Renderer {
    private final int MAX_BATCH_SIZE = 1000;
    private List<RenderBatch> batches;

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
            if(batch.hasRoom() && batch.getzIndex() == spr.gameObject.getzIndex()){
                Texture tex = spr.getTexture();
                if(tex == null || batch.hasTexture(tex) || batch.hasTextureRoom()) {
                    batch.addSprite(spr);
                    added = true;
                    break;
                }
            }
        }

        if(!added){
            System.out.println("Adding " + batches.size() + " to the renderer list");
            RenderBatch rb = new RenderBatch(MAX_BATCH_SIZE, spr.gameObject.getzIndex());
            rb.start();
            batches.add(rb);
            rb.addSprite(spr);
            Collections.sort(batches);
        }
    }

    public void render(){
        int x = 1;
        for(RenderBatch batch : batches){
            System.out.println("We are rendering: " + x);
            x++;
            batch.render();
        }
    }
}
