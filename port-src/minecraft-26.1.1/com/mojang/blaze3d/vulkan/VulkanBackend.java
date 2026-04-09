package com.mojang.blaze3d.vulkan;

import com.mojang.blaze3d.GLFWErrorCapture;
import com.mojang.blaze3d.shaders.GpuDebugOptions;
import com.mojang.blaze3d.shaders.ShaderSource;
import com.mojang.blaze3d.systems.BackendCreationException;
import com.mojang.blaze3d.systems.GpuBackend;
import com.mojang.blaze3d.systems.GpuDevice;
import com.mojang.blaze3d.eaglercraft.EaglercraftRuntimeEnv;
import com.mojang.blaze3d.eaglercraft.EaglercraftVulkanBridge;
import com.mojang.logging.LogUtils;
import org.slf4j.Logger;
import org.jspecify.annotations.Nullable;

public class VulkanBackend implements GpuBackend {
    private static final Logger LOGGER = LogUtils.getLogger();
    private final VulkanRuntime runtime = new VulkanRuntime();

    @Override
    public String getName() {
        if (EaglercraftRuntimeEnv.isBrowserRuntime()) {
            return "Eaglercraft Vulkan/WebGPU";
        }
        return "Vulkan (Experimental)";
    }

    @Override
    public void setWindowHints() {
        EaglercraftVulkanBridge.applyWindowHints();
    }

    @Override
    public void handleWindowCreationErrors(@Nullable GLFWErrorCapture.Error error) throws BackendCreationException {
        if (error != null) {
            throw new BackendCreationException("Vulkan window creation failed: " + error);
        }

        throw new BackendCreationException("Failed to create Vulkan-capable window");
    }

    @Override
    public GpuDevice createDevice(long window, ShaderSource defaultShaderSource, GpuDebugOptions debugOptions) {
        GpuDevice device = EaglercraftVulkanBridge.createDevice(window, defaultShaderSource, debugOptions, runtime);
        LOGGER.info("Initialized Vulkan runtime context ({})", (Object)(EaglercraftRuntimeEnv.isBrowserRuntime() ? "Eaglercraft-bridge" : "experimental"));
        return device;
    }
}

