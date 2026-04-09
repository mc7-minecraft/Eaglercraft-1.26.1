package com.mojang.blaze3d.vulkan;

import com.mojang.blaze3d.eaglercraft.EaglercraftRuntimeEnv;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class VulkanDeviceContext {
    private final VulkanWindowContext windowContext;
    private final String debugLevel;
    private final String vendor;
    private final String renderer;
    private final String version;
    private final List<String> enabledExtensions;

    private VulkanDeviceContext(VulkanWindowContext windowContext, String debugLevel, String vendor, String renderer, String version, List<String> enabledExtensions) {
        this.windowContext = windowContext;
        this.debugLevel = debugLevel;
        this.vendor = vendor;
        this.renderer = renderer;
        this.version = version;
        this.enabledExtensions = Collections.unmodifiableList(new ArrayList<String>(enabledExtensions));
    }

    public static VulkanDeviceContext create(VulkanWindowContext windowContext, String debugLevel) {
        List<String> extensions = new ArrayList<String>();
        extensions.add("VK_KHR_swapchain");
        extensions.add("VK_EXT_descriptor_indexing");
        if (EaglercraftRuntimeEnv.isBrowserRuntime()) {
            if (EaglercraftRuntimeEnv.preferWebGpuBridge()) {
                extensions.add("Eaglercraft_WEBGPU");
                extensions.add("Eaglercraft_WEBGL2_FALLBACK");
            } else {
                extensions.add("Eaglercraft_WEBGL2");
            }
            return new VulkanDeviceContext(windowContext, debugLevel, "Eaglercraft", "Eaglercraft WebGPU Bridge", "1.3-web", extensions);
        }
        return new VulkanDeviceContext(windowContext, debugLevel, "Eaglercraft", "Vulkan Runtime", "1.3", extensions);
    }

    public VulkanWindowContext windowContext() {
        return windowContext;
    }

    public String debugLevel() {
        return debugLevel;
    }

    public String vendor() {
        return vendor;
    }

    public String renderer() {
        return renderer;
    }

    public String version() {
        return version;
    }

    public List<String> enabledExtensions() {
        return enabledExtensions;
    }
}

