package com.mojang.blaze3d.vulkan;

import com.mojang.blaze3d.textures.GpuTextureView;

public final class VulkanTextureView extends GpuTextureView {
    private boolean closed;

    public VulkanTextureView(VulkanTexture texture, int baseMipLevel, int mipLevels) {
        super(texture, baseMipLevel, mipLevels);
    }

    @Override
    public synchronized void close() {
        closed = true;
    }

    @Override
    public synchronized boolean isClosed() {
        return closed;
    }
}
