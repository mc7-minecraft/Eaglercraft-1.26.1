package com.mojang.blaze3d.vulkan;

import com.mojang.blaze3d.textures.TextureFormat;
import java.util.HashMap;
import java.util.Map;

public final class VulkanTextureSystem {
    private final VulkanDeviceContext deviceContext;
    private final VulkanMemoryBudgetManager memoryBudgetManager;
    private final Map<String, VulkanTexture> texturesByLabel = new HashMap<String, VulkanTexture>();
    private long nextHandle = 1L;
    private int allocationsSinceReap;

    private VulkanTextureSystem(VulkanDeviceContext deviceContext, VulkanMemoryBudgetManager memoryBudgetManager) {
        this.deviceContext = deviceContext;
        this.memoryBudgetManager = memoryBudgetManager;
    }

    public static VulkanTextureSystem create(VulkanDeviceContext deviceContext, VulkanMemoryBudgetManager memoryBudgetManager) {
        return new VulkanTextureSystem(deviceContext, memoryBudgetManager);
    }

    public VulkanTexture createTexture(int usage, String label, TextureFormat format, int width, int height, int depthOrLayers, int mipLevels) {
        maybeReapClosedTextures();
        long estimatedBytes = estimateBytes(format, width, height, depthOrLayers, mipLevels);
        memoryBudgetManager.reserveTextureBytes(estimatedBytes, label);
        VulkanTexture texture = new VulkanTexture(usage, label, format, width, height, depthOrLayers, mipLevels, nextHandle++, estimatedBytes, memoryBudgetManager);
        VulkanTexture replaced = texturesByLabel.put(label, texture);
        if (replaced != null && !replaced.isClosed()) {
            replaced.close();
        }
        return texture;
    }

    public VulkanTexture lookup(String label) {
        return texturesByLabel.get(label);
    }

    public VulkanTextureView createTextureView(VulkanTexture texture, int baseMipLevel, int mipLevels) {
        return new VulkanTextureView(texture, baseMipLevel, mipLevels);
    }

    private long estimateBytes(TextureFormat format, int width, int height, int depthOrLayers, int mipLevels) {
        long bytes = 0L;
        int pixelSize = Math.max(1, format.pixelSize());
        for (int i = 0; i < mipLevels; i++) {
            int mipWidth = Math.max(1, width >> i);
            int mipHeight = Math.max(1, height >> i);
            bytes += (long)mipWidth * (long)mipHeight * (long)depthOrLayers * (long)pixelSize;
        }
        return Math.max(1L, bytes);
    }

    public void clear() {
        for (VulkanTexture texture : texturesByLabel.values()) {
            if (texture != null && !texture.isClosed()) {
                texture.close();
            }
        }
        texturesByLabel.clear();
    }

    private void maybeReapClosedTextures() {
        allocationsSinceReap++;
        if (allocationsSinceReap < 64) {
            return;
        }
        allocationsSinceReap = 0;
        texturesByLabel.entrySet().removeIf(entry -> entry.getValue() == null || entry.getValue().isClosed());
    }

    public VulkanDeviceContext deviceContext() {
        return deviceContext;
    }
}
