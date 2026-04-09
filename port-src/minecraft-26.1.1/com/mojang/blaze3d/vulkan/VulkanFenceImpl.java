package com.mojang.blaze3d.vulkan;

import com.mojang.blaze3d.buffers.GpuFence;

public final class VulkanFenceImpl implements GpuFence {
    private volatile boolean signaled;

    public void signal() {
        this.signaled = true;
    }

    @Override
    public void close() {
        this.signaled = true;
    }

    @Override
    public boolean awaitCompletion(long timeoutMs) {
        return signaled;
    }
}
