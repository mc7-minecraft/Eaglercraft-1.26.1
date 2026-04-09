package com.mojang.blaze3d.vulkan;

import java.util.concurrent.atomic.AtomicInteger;

public final class VulkanSwapchain {
    private final VulkanDeviceContext deviceContext;
    private final AtomicInteger frameIndex = new AtomicInteger();
    private volatile boolean vsyncEnabled = true;

    private VulkanSwapchain(VulkanDeviceContext deviceContext) {
        this.deviceContext = deviceContext;
    }

    public static VulkanSwapchain create(VulkanDeviceContext deviceContext) {
        return new VulkanSwapchain(deviceContext);
    }

    public int acquireNextImage() {
        return frameIndex.getAndIncrement();
    }

    public void present(int imageIndex) {
        frameIndex.compareAndSet(imageIndex + 1, imageIndex + 1);
    }

    public void setVsyncEnabled(boolean enabled) {
        this.vsyncEnabled = enabled;
    }

    public boolean isVsyncEnabled() {
        return vsyncEnabled;
    }

    public VulkanDeviceContext deviceContext() {
        return deviceContext;
    }
}
