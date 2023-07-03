package com.l0raxeo.test_imgui.scenes;

import org.joml.Vector4f;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

public class SceneManager
{

    private static final List<Scene> instantiatedScenes = new ArrayList<>();
    private static Scene activeScene = null;

    public static Vector4f backdrop;

    private SceneManager() {}

    public static <T extends Scene> Scene getScene(Class<T> sceneClass)
    {
        for (Scene s : instantiatedScenes)
            if (s.getClass().equals(sceneClass))
                return s;

        return null;
    }

    public static void changeScene(Class<?> sceneClass)
    {
        if (sceneClass.isInstance(Scene.class))
        {
            assert false : "Class '" + sceneClass + "' is not a subclass of Scene";
            return;
        }

        Scene targetScene = null;

        boolean sameScene = activeScene != null && activeScene.getClass().equals(sceneClass);

        if (sameScene) return;

        boolean sceneExists = false;

        for (Scene s : instantiatedScenes)
        {
            if (s.getClass().equals(sceneClass))
            {
                sceneExists = true;
                targetScene = s;
                break;
            }
        }

        if (!sceneExists)
        {
            try {
                targetScene = (Scene) sceneClass.getDeclaredConstructor().newInstance();
                instantiatedScenes.add(targetScene);
            } catch (InstantiationException |
                     IllegalAccessException |
                     InvocationTargetException |
                     NoSuchMethodException e) {
                throw new RuntimeException(e);
            }
        }

        activeScene = targetScene;
        activeScene.loadProperties();
        activeScene.loadResources();
        activeScene.init();
        activeScene.start();
    }

    public static Scene getActiveScene()
    {
        return activeScene;
    }

}
