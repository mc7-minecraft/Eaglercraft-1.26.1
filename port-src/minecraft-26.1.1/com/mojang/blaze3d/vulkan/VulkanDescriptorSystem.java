package com.mojang.blaze3d.vulkan;

import com.mojang.blaze3d.textures.GpuSampler;
import com.mojang.blaze3d.textures.GpuTextureView;
import java.util.LinkedHashMap;
import java.util.Map;

public final class VulkanDescriptorSystem {
    private final VulkanDeviceContext deviceContext;
    private final int maxEntries;
    private final Map<String, DescriptorBinding> bindings;

    private VulkanDescriptorSystem(VulkanDeviceContext deviceContext, int maxEntries) {
        this.deviceContext = deviceContext;
        this.maxEntries = maxEntries;
        this.bindings = new LinkedHashMap<String, DescriptorBinding>(64, 0.75f, true) {
            @Override
            protected boolean removeEldestEntry(Map.Entry<String, DescriptorBinding> eldest) {
                return size() > VulkanDescriptorSystem.this.maxEntries;
            }
        };
    }

    public static VulkanDescriptorSystem create(VulkanDeviceContext deviceContext, VulkanTuning tuning) {
        return new VulkanDescriptorSystem(deviceContext, tuning.descriptorCacheEntries());
    }

    public void bindTexture(String name, GpuTextureView view, GpuSampler sampler) {
        bindings.put(name, new DescriptorBinding(view, sampler));
    }

    public void clear() {
        bindings.clear();
    }

    public VulkanDeviceContext deviceContext() {
        return deviceContext;
    }

    private record DescriptorBinding(GpuTextureView textureView, GpuSampler sampler) {
    }
}
