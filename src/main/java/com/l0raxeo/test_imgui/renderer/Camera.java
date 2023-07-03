package com.l0raxeo.test_imgui.renderer;

import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector3f;

public class Camera
{

    private final Matrix4f projectionMatrix;
    private Matrix4f viewMatrix;
    private Matrix4f inverseProjection;
    private final Matrix4f inverseView;
    public Vector2f position;
    private final Vector2f projectionSize = new Vector2f(32.0f * 40.0f, 32.0f * 21.0f);
    private float zoom = 0.5f;

    private static Camera instance;

    public Camera(Vector2f position)
    {
        this.position = position;
        this.projectionMatrix = new Matrix4f();
        this.viewMatrix = new Matrix4f();
        this.inverseProjection = new Matrix4f();
        this.inverseView = new Matrix4f();
        adjustProjection();

        instance = this;
    }

    public static Camera get()
    {
        return instance;
    }

    public void adjustProjection()
    {
        projectionMatrix.identity();
        projectionMatrix.ortho(0.0f, projectionSize.x * zoom,
                0.0f, projectionSize.y * zoom, 0.0f, 100.0f);
        inverseProjection = new Matrix4f(projectionMatrix).invert();
    }

    /**
     * Forcibly sets camera to specific zoom
     */
    public void setZoom(float zoom)
    {
        this.zoom = zoom;
    }

    /**
     * Increases zoom value by parameter.
     * @param zoom factor in which zoom is increased/decreased
     */
    public void zoom(float zoom)
    {
        this.zoom += zoom;
    }

    public float getZoom()
    {
        return zoom;
    }

    public Matrix4f getViewMatrix()
    {
        Vector3f cameraFront = new Vector3f(0.0f, 0.0f, -1.0f);
        Vector3f cameraUp = new Vector3f(0.0f, 1.0f, 0.0f);
        this.viewMatrix.identity();
        this.viewMatrix = viewMatrix.lookAt(new Vector3f(position.x, position.y, 20.0f),
                cameraFront.add(position.x, position.y, 0.0f),
                cameraUp);
        this.viewMatrix.invert(inverseView);

        return this.viewMatrix;
    }

    public Matrix4f getProjectionMatrix()
    {
        return this.projectionMatrix;
    }

    public Matrix4f getInverseProjection()
    {
        return this.inverseProjection;
    }

    public Matrix4f getInverseView()
    {
        return this.inverseView;
    }

    public Vector2f getProjectionSize()
    {
        return this.projectionSize;
    }

}

