package com.mojang.blaze3d.eaglercraft;

import com.mojang.blaze3d.shaders.GpuDebugOptions;
import com.mojang.blaze3d.shaders.ShaderSource;
import com.mojang.blaze3d.systems.GpuDevice;
import com.mojang.blaze3d.vulkan.VulkanRuntime;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWVulkan;

public final class EaglercraftVulkanBridge {
    private EaglercraftVulkanBridge() {
    }

    public static void applyWindowHints() {
        if (EaglercraftRuntimeEnv.isBrowserRuntime()) {
            return;
        }
        GLFW.glfwWindowHint(139265, 0);
    }

    public static GpuDevice createDevice(long window, ShaderSource defaultShaderSource, GpuDebugOptions debugOptions, VulkanRuntime runtime) {
        if (!EaglercraftRuntimeEnv.isBrowserRuntime() && !GLFWVulkan.glfwVulkanSupported()) {
            throw new IllegalStateException("GLFW reports Vulkan is not supported on this machine");
        }

        runtime.bootstrap(window, defaultShaderSource, debugOptions);
        return new GpuDevice(runtime.deviceBackend());
    }
}

