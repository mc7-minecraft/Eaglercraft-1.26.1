package com.mojang.blaze3d.vulkan;

public final class VulkanMemoryBudgetManager {
    private static final long DEFAULT_TEXTURE_BUDGET_MIB = 640L;
    private static final long DEFAULT_BUFFER_BUDGET_MIB = 256L;
    private final long maxTextureBytes;
    private final long maxBufferBytes;
    private long usedTextureBytes;
    private long usedBufferBytes;

    private VulkanMemoryBudgetManager(long maxTextureBytes, long maxBufferBytes) {
        this.maxTextureBytes = maxTextureBytes;
        this.maxBufferBytes = maxBufferBytes;
    }

    public static VulkanMemoryBudgetManager fromSystemProperties() {
        long textureMiB = Long.getLong("Eaglercraft.vulkan.textureBudgetMiB", DEFAULT_TEXTURE_BUDGET_MIB);
        long bufferMiB = Long.getLong("Eaglercraft.vulkan.bufferBudgetMiB", DEFAULT_BUFFER_BUDGET_MIB);
        return new VulkanMemoryBudgetManager(textureMiB * 1024L * 1024L, bufferMiB * 1024L * 1024L);
    }

    public synchronized void reserveTextureBytes(long bytes, String label) {
        if (usedTextureBytes + bytes > maxTextureBytes) {
            throw new IllegalStateException("Texture budget exceeded while allocating " + label + ": " + bytes + " bytes");
        }
        usedTextureBytes += bytes;
    }

    public synchronized void releaseTextureBytes(long bytes) {
        usedTextureBytes = Math.max(0L, usedTextureBytes - Math.max(0L, bytes));
    }

    public synchronized void reserveBufferBytes(long bytes, String label) {
        if (usedBufferBytes + bytes > maxBufferBytes) {
            throw new IllegalStateException("Buffer budget exceeded while allocating " + label + ": " + bytes + " bytes");
        }
        usedBufferBytes += bytes;
    }

    public synchronized void releaseBufferBytes(long bytes) {
        usedBufferBytes = Math.max(0L, usedBufferBytes - Math.max(0L, bytes));
    }

    public synchronized long usedTextureBytes() {
        return usedTextureBytes;
    }

    public synchronized long usedBufferBytes() {
        return usedBufferBytes;
    }

    public long maxTextureBytes() {
        return maxTextureBytes;
    }

    public long maxBufferBytes() {
        return maxBufferBytes;
    }

    public synchronized double texturePressure() {
        if (maxTextureBytes <= 0L) {
            return 1.0;
        }
        return (double)usedTextureBytes / (double)maxTextureBytes;
    }

    public synchronized double bufferPressure() {
        if (maxBufferBytes <= 0L) {
            return 1.0;
        }
        return (double)usedBufferBytes / (double)maxBufferBytes;
    }

    public synchronized double combinedPressure() {
        return Math.max(texturePressure(), bufferPressure());
    }
}

