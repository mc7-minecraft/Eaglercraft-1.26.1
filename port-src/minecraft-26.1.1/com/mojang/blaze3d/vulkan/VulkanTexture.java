package com.mojang.blaze3d.vulkan;

import com.mojang.blaze3d.textures.GpuTexture;
import com.mojang.blaze3d.textures.TextureFormat;

public final class VulkanTexture extends GpuTexture {
    private final long nativeHandle;
    private final long estimatedBytes;
    private final VulkanMemoryBudgetManager budgetManager;
    private boolean closed;

    public VulkanTexture(int usage, String label, TextureFormat format, int width, int height, int depthOrLayers, int mipLevels, long nativeHandle, long estimatedBytes, VulkanMemoryBudgetManager budgetManager) {
        super(usage, label, format, width, height, depthOrLayers, mipLevels);
        this.nativeHandle = nativeHandle;
        this.estimatedBytes = estimatedBytes;
        this.budgetManager = budgetManager;
    }

    public long nativeHandle() {
        return nativeHandle;
    }

    @Override
    public synchronized void close() {
        if (!closed) {
            closed = true;
            budgetManager.releaseTextureBytes(estimatedBytes);
        }
    }

    @Override
    public synchronized boolean isClosed() {
        return closed;
    }
}
