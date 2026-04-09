package com.mojang.blaze3d.vulkan;

import com.mojang.blaze3d.shaders.ShaderSource;
import java.util.HashMap;
import java.util.Map;

public final class VulkanShaderLibrary {
    private final VulkanDeviceContext deviceContext;
    private final ShaderSource shaderSource;
    private final Map<String, String> shaderKeys = new HashMap<String, String>();

    private VulkanShaderLibrary(VulkanDeviceContext deviceContext, ShaderSource shaderSource) {
        this.deviceContext = deviceContext;
        this.shaderSource = shaderSource;
    }

    public static VulkanShaderLibrary create(VulkanDeviceContext deviceContext, ShaderSource shaderSource) {
        return new VulkanShaderLibrary(deviceContext, shaderSource);
    }

    public String shaderKey(String shaderId) {
        return shaderKeys.computeIfAbsent(shaderId, id -> id + "@vk." + Integer.toHexString(id.hashCode()));
    }

    public VulkanDeviceContext deviceContext() {
        return deviceContext;
    }

    public ShaderSource shaderSource() {
        return shaderSource;
    }
}
