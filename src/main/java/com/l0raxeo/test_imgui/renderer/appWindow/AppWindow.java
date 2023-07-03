package com.l0raxeo.test_imgui.renderer.appWindow;

import com.l0raxeo.test_imgui.imgui.ImGuiLayer;
import com.l0raxeo.test_imgui.input.KeyListener;
import com.l0raxeo.test_imgui.input.MouseListener;
import com.l0raxeo.test_imgui.renderer.debugDraw.DebugDraw;
import com.l0raxeo.test_imgui.scenes.SampleScene;
import com.l0raxeo.test_imgui.scenes.Scene;
import com.l0raxeo.test_imgui.scenes.SceneManager;
import imgui.ImGui;
import imgui.gl3.ImGuiImplGl3;
import imgui.glfw.ImGuiImplGlfw;
import org.joml.Math;
import org.joml.Vector4f;
import org.lwjgl.Version;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.openal.AL;
import org.lwjgl.openal.ALC;
import org.lwjgl.openal.ALCCapabilities;
import org.lwjgl.openal.ALCapabilities;
import org.lwjgl.opengl.GL;

import java.awt.*;
import java.util.Objects;

import static org.lwjgl.glfw.Callbacks.glfwFreeCallbacks;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.openal.ALC10.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.system.MemoryUtil.NULL;

public class AppWindow
{

    private static AppWindow instance = null;

    public static String APP_TITLE;
    public static int WINDOW_WIDTH, WINDOW_HEIGHT;

    public static long glfwWindow;
    private String glslVersion = null;
    private ImGuiLayer imguiLayer;
    private final ImGuiImplGlfw imGuiGlfw = new ImGuiImplGlfw();
    private final ImGuiImplGl3 imGuiGl3 = new ImGuiImplGl3();

    private static Vector4f backdrop;

    private long audioContext;
    private long audioDevice;

    private AppWindow()
    {
        APP_TITLE = "Test ImGui";
        WINDOW_WIDTH = 1080;
        WINDOW_HEIGHT = 720;

        backdrop = new Vector4f(1, 1, 1, 1);
    }

    public static AppWindow getInstance()
    {
        if (AppWindow.instance == null)
            AppWindow.instance = new AppWindow();

        return AppWindow.instance;
    }

    public void run()
    {
        System.out.println("Hello LWJGL " + Version.getVersion() + "!");

        initWindow();
        imGuiGlfw.init(glfwWindow, true);
        imGuiGl3.init(glslVersion);

        loop();

        // Destroy audio context
        alcDestroyContext(audioContext);
        alcCloseDevice(audioDevice);

        // Free the memory
        glfwFreeCallbacks(glfwWindow);
        glfwDestroyWindow(glfwWindow);

        imGuiGlfw.dispose();
        imGuiGl3.dispose();
        ImGui.destroyContext();

        // Terminate GLFW and the free the error callback
        Objects.requireNonNull(glfwSetErrorCallback(null)).free();
        glfwTerminate();
    }

    public void initWindow()
    {

        // Setup an error callback. The default implementation
        // will print the error message in System.err.
        GLFWErrorCallback.createPrint(System.err).set();

        // Initialize GLFW. Most GLFW functions will not work before doing this.
        if (!glfwInit()) {
            throw new IllegalStateException("Unable to initialize GLFW.");
        }

        glslVersion = "#version 130";

        glfwDefaultWindowHints();
        glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR, 3);
        glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR, 3);
        glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE);
        glfwWindowHint(GLFW_RESIZABLE, GLFW_TRUE);

        glfwWindow = glfwCreateWindow(WINDOW_WIDTH, WINDOW_HEIGHT, APP_TITLE, NULL, NULL);
        if (glfwWindow == NULL) {
            throw new IllegalStateException("Failed to create the GLFW window.");
        }

        glfwSetCursorPosCallback(glfwWindow, MouseListener::mousePosCallback);
        glfwSetMouseButtonCallback(glfwWindow, MouseListener::mouseButtonCallback);
        glfwSetScrollCallback(glfwWindow, MouseListener::mouseScrollCallback);
        glfwSetKeyCallback(glfwWindow, KeyListener::keyCallback);
        glfwSetWindowSizeCallback(glfwWindow, (w, newWidth, newHeight) -> {
            WINDOW_WIDTH = newWidth;
            WINDOW_HEIGHT = newHeight;
        });

        // Make the OpenGL context current
        glfwMakeContextCurrent(glfwWindow);
        // Enable v-sync
        glfwSwapInterval(1);
        // show the window
        glfwShowWindow(glfwWindow);

        createAudioDevice();

        GL.createCapabilities();

        glEnable(GL_BLEND);
        glBlendFunc(GL_ONE, GL_ONE_MINUS_SRC_ALPHA);
        this.imguiLayer = new ImGuiLayer(glfwWindow);
        this.imguiLayer.initImGui();

        SceneManager.changeScene(SampleScene.class);
    }

    private void createAudioDevice()
    {
        // Initialize the audio device
        String defaultDeviceName = alcGetString(0, ALC_DEFAULT_DEVICE_SPECIFIER);
        audioDevice = alcOpenDevice(defaultDeviceName);

        // Audio Context
        int[] attributes = {0};
        audioContext = alcCreateContext(audioDevice, attributes);
        alcMakeContextCurrent(audioContext);

        ALCCapabilities alcCapabilities = ALC.createCapabilities(audioDevice);
        ALCapabilities alCapabilities = AL.createCapabilities(alcCapabilities);

        assert alCapabilities.OpenAL10 : "Audio Library not supported";
    }

    public void loop()
    {
        float beginTime = (float)glfwGetTime();
        float endTime;
        float dt = -1.0f;

        float timer = beginTime + 1;

        while (!glfwWindowShouldClose(glfwWindow)) {
            // Poll events
            glfwPollEvents();

            DebugDraw.beginFrame();

            glClearColor(backdrop.x, backdrop.y, backdrop.z, backdrop.w);
            glClear(GL_COLOR_BUFFER_BIT);

            if (dt >= 0) {
                Scene activeScene = SceneManager.getActiveScene();
                activeScene.update(dt);
                activeScene.render();
                DebugDraw.draw();

                if (beginTime >= timer)
                {
                    //Settings.FPS = Math.round(1 / dt);
                    timer = beginTime + 1;
                }
            }

            this.imguiLayer.update(dt);

            KeyListener.endFrame();
            MouseListener.endFrame();
            glfwSwapBuffers(glfwWindow);

            endTime = (float)glfwGetTime();
            dt = endTime - beginTime;
            beginTime = endTime;
        }

        SceneManager.getActiveScene().save();
    }

    public static void setBackdrop(Vector4f color)
    {
        backdrop = color;
    }

}
