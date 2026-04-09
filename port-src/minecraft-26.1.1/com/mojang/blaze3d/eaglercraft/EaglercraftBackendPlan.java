package com.mojang.blaze3d.eaglercraft;

import com.mojang.blaze3d.opengl.GlBackend;
import com.mojang.blaze3d.systems.GpuBackend;
import com.mojang.blaze3d.vulkan.VulkanBackend;

public final class EaglercraftBackendPlan {
    private EaglercraftBackendPlan() {
    }

    public static GpuBackend[] buildBackends(boolean enableExperimentalVulkan) {
        if (EaglercraftRuntimeEnv.isBrowserRuntime()) {
            return new GpuBackend[]{new VulkanBackend()};
        }
        return enableExperimentalVulkan ? new GpuBackend[]{new VulkanBackend(), new GlBackend()} : new GpuBackend[]{new GlBackend()};
    }
}

