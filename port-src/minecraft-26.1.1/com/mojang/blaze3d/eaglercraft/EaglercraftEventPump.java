package com.mojang.blaze3d.eaglercraft;

import org.lwjgl.glfw.GLFW;

public final class EaglercraftEventPump {
    private EaglercraftEventPump() {
    }

    public static void pollEvents() {
        if (EaglercraftRuntimeEnv.isBrowserRuntime()) {
            return;
        }
        GLFW.glfwPollEvents();
    }
}

