package com.l0raxeo.test_imgui.scenes;

import com.l0raxeo.test_imgui.objects.GameObject;
import com.l0raxeo.test_imgui.renderer.Camera;
import com.l0raxeo.test_imgui.renderer.Renderer;
import com.l0raxeo.test_imgui.renderer.appWindow.AppWindow;
import imgui.ImGui;
import org.joml.Vector2f;
import org.joml.Vector4f;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public abstract class Scene
{

    protected Renderer renderer = new Renderer();
    public Camera camera;

    protected List<GameObject> gameObjects = new ArrayList<>();
    protected Vector4f backdrop = new Vector4f(0, 0, 0, 0);

    private boolean isRunning = false;
    protected String imguiSaveFilePath = "assets/saves/imgui.ini";

    public void init() {}

    public void start()
    {
        for (GameObject gameObject : gameObjects)
        {
            gameObject.start();
            this.renderer.add(gameObject);
        }

        isRunning = true;
    }

    public void addGameObject(GameObject gameObject)
    {
        if (!isRunning)
            gameObjects.add(gameObject);
        else
        {
            gameObjects.add(gameObject);
            gameObject.start();
            this.renderer.add(gameObject);
        }
    }

    public List<GameObject> getGameObjects(String name)
    {
        List<GameObject> gameObjects = new ArrayList<>();

        for (GameObject go : this.gameObjects)
            if (go.getName() != null && go.getName().equals(name))
                gameObjects.add(go);

        return gameObjects;
    }

    public GameObject getGameObject(int gameObjectId) {
        Optional<GameObject> result = this.gameObjects.stream()
                .filter(gameObject -> gameObject.getUid() == gameObjectId)
                .findFirst();
        return result.orElse(null);
    }

    public List<GameObject> getGameObjectsInCoordinates(Vector2f coordinates)
    {
        return getGameObjectsInCoordinates(coordinates.x, coordinates.y);
    }

    public List<GameObject> getGameObjectsInCoordinates(float x, float y)
    {
        List<GameObject> gos = new ArrayList<>();

        for (GameObject go : gameObjects)
        {
            Vector2f pos = go.transform.position;

            float ax = pos.x;
            float ay = pos.y;
            float bx = pos.x + go.transform.scale.x;
            float by = pos.y + go.transform.scale.y;

            if (x > ax && x < bx && y > ay && y < by)
                gos.add(go);
        }

        return gos;
    }

    public List<GameObject> getAllGameObjectsInScene()
    {
        return this.gameObjects;
    }

    public abstract void update(float dt);

    public abstract void render();

    public void loadProperties() {}

    public void loadResources() {}

    public void save()
    {
        ImGui.saveIniSettingsToDisk(imguiSaveFilePath);
    }

    protected void setBackdrop(Vector4f color)
    {
        setBackdrop(color.x, color.y, color.z, color.w);
    }

    protected void setBackdrop(float r, float g, float b, float a)
    {
        this.backdrop = new Vector4f(r, g, b, a);
        AppWindow.setBackdrop(this.backdrop);
    }

    protected void createCamera(Vector2f position)
    {
        this.camera = new Camera(position);
    }

}
