package renderer;

import Components.SpriteRenderer;
import styy.GameObject;

import java.util.ArrayList;
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
            if(batch.hasRoom()){
                batch.addSprite(spr);
                added = true;
                break;
            }
        }

        if(!added){
            RenderBatch rb = new RenderBatch(MAX_BATCH_SIZE);
            rb.start();
            batches.add(rb);
            rb.addSprite(spr);
        }
    }

    public void render(){
        for(RenderBatch batch : batches){
            batch.render();
        }
    }
}
