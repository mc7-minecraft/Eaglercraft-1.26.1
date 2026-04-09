package com.mojang.blaze3d.vulkan;

import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;

public final class VulkanBufferSystem {
    private final VulkanDeviceContext deviceContext;
    private final VulkanMemoryBudgetManager memoryBudgetManager;
    private final Map<String, VulkanBuffer> buffersByLabel = new HashMap<String, VulkanBuffer>();
    private long nextHandle = 1L;
    private int allocationsSinceReap;

    private VulkanBufferSystem(VulkanDeviceContext deviceContext, VulkanMemoryBudgetManager memoryBudgetManager) {
        this.deviceContext = deviceContext;
        this.memoryBudgetManager = memoryBudgetManager;
    }

    public static VulkanBufferSystem create(VulkanDeviceContext deviceContext, VulkanMemoryBudgetManager memoryBudgetManager) {
        return new VulkanBufferSystem(deviceContext, memoryBudgetManager);
    }

    public VulkanBuffer createBuffer(int usage, String label, long size) {
        maybeReapClosedBuffers();
        memoryBudgetManager.reserveBufferBytes(size, label);
        VulkanBuffer buffer = new VulkanBuffer(usage, size, nextHandle++, memoryBudgetManager);
        VulkanBuffer replaced = buffersByLabel.put(label, buffer);
        if (replaced != null && !replaced.isClosed()) {
            replaced.close();
        }
        return buffer;
    }

    public VulkanBuffer createBuffer(int usage, String label, ByteBuffer initialData) {
        VulkanBuffer buffer = createBuffer(usage, label, initialData.remaining());
        buffer.write(0L, initialData.slice());
        return buffer;
    }

    public VulkanBuffer lookup(String label) {
        return buffersByLabel.get(label);
    }

    public VulkanDeviceContext deviceContext() {
        return deviceContext;
    }

    public void clear() {
        for (VulkanBuffer buffer : buffersByLabel.values()) {
            if (buffer != null && !buffer.isClosed()) {
                buffer.close();
            }
        }
        buffersByLabel.clear();
    }

    private void maybeReapClosedBuffers() {
        allocationsSinceReap++;
        if (allocationsSinceReap < 128) {
            return;
        }
        allocationsSinceReap = 0;
        buffersByLabel.entrySet().removeIf(entry -> entry.getValue() == null || entry.getValue().isClosed());
    }
}
