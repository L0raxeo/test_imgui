package com.l0raxeo.test_imgui.scenes;

import com.l0raxeo.test_imgui.input.MouseListener;
import com.l0raxeo.test_imgui.renderer.debugDraw.DebugDraw;
import org.joml.Vector2f;
import org.joml.Vector3f;

public class SampleScene extends Scene
{

    @Override
    public void loadProperties()
    {
        imguiSaveFilePath = "assets/saves/start_save.txt";
        setBackdrop(.9f, 1f, .9f, 1);
    }

    @Override
    public void init()
    {
        createCamera(new Vector2f());
    }

    @Override
    public void update(float deltaTime)
    {
        updateCameraPan();
        updateCameraZoom();

        DebugDraw.addLine2D(new Vector2f(500, 50), new Vector2f(400, 200), new Vector3f(0.25f, 0.75f, 1f));
    }

    @Override
    public void render()
    {
        this.renderer.render();
    }

    private void updateCameraPan()
    {
        if (MouseListener.isDragging())
        {
            float dx = MouseListener.getDx();
            float dy = MouseListener.getDy();

            camera.position.x += (dx * camera.getZoom()) / 2;
            camera.position.y += (-dy * camera.getZoom()) / 2;
        }
    }

    private void updateCameraZoom()
    {
        if (MouseListener.isScrolling())
        {
            float wheel = MouseListener.getScrollY();
            float SCROLL_CONSTANT = 0.02f;
            camera.zoom(wheel * SCROLL_CONSTANT);
            camera.adjustProjection();
        }
    }

}
