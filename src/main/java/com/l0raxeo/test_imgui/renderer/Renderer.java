package com.l0raxeo.test_imgui.renderer;

import com.l0raxeo.test_imgui.components.SpriteRenderer;
import com.l0raxeo.test_imgui.objects.GameObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Renderer
{

    private final List<RenderBatch> batches;

    public Renderer()
    {
        this.batches = new ArrayList<>();
    }

    public void add(GameObject go)
    {
        SpriteRenderer spr = go.getComponent(SpriteRenderer.class);

        if (spr != null)
            add(spr);
    }

    private void add(SpriteRenderer sprite)
    {
        boolean added = false;

        for (RenderBatch batch : batches)
        {
            if (batch.hasRoom() && batch.zIndex() == sprite.gameObject.zIndex())
            {
                Texture tex = sprite.getTexture();
                if (tex == null || (batch.hasTexture(tex) || batch.hasTextureRoom()))
                {
                    batch.addSprite(sprite);
                    added = true;
                    break;
                }
            }
        }

        if (!added)
        {
            int MAX_BATCH_SIZE = 1000;
            RenderBatch newBatch = new RenderBatch(MAX_BATCH_SIZE, sprite.gameObject.zIndex());
            newBatch.start();
            batches.add(newBatch);
            newBatch.addSprite(sprite);
            Collections.sort(batches);
        }
    }

    public void destroyGameObject(GameObject go) {
        if (go.getComponent(SpriteRenderer.class) == null) return;
        for (RenderBatch batch : batches) {
            if (batch.destroyIfExists(go)) {
                return;
            }
        }
    }

    public void render()
    {
        for (RenderBatch batch : batches)
        {
            batch.render();
        }
    }

}
