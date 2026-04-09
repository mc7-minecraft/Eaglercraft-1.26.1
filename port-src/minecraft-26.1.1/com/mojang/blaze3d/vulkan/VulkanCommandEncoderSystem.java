package com.mojang.blaze3d.vulkan;

public final class VulkanCommandEncoderSystem {
    private final VulkanDeviceContext deviceContext;
    private final VulkanQueueManager queueManager;
    private final VulkanDescriptorSystem descriptorSystem;
    private final VulkanTextureSystem textureSystem;
    private final VulkanBufferSystem bufferSystem;
    private final VulkanStagingPool stagingPool;

    private VulkanCommandEncoderSystem(
        VulkanDeviceContext deviceContext,
        VulkanQueueManager queueManager,
        VulkanDescriptorSystem descriptorSystem,
        VulkanTextureSystem textureSystem,
        VulkanBufferSystem bufferSystem,
        VulkanStagingPool stagingPool
    ) {
        this.deviceContext = deviceContext;
        this.queueManager = queueManager;
        this.descriptorSystem = descriptorSystem;
        this.textureSystem = textureSystem;
        this.bufferSystem = bufferSystem;
        this.stagingPool = stagingPool;
    }

    public static VulkanCommandEncoderSystem create(
        VulkanDeviceContext deviceContext,
        VulkanQueueManager queueManager,
        VulkanDescriptorSystem descriptorSystem,
        VulkanTextureSystem textureSystem,
        VulkanBufferSystem bufferSystem,
        VulkanStagingPool stagingPool
    ) {
        return new VulkanCommandEncoderSystem(deviceContext, queueManager, descriptorSystem, textureSystem, bufferSystem, stagingPool);
    }

    public VulkanCommandEncoderBackend createEncoder() {
        return new VulkanCommandEncoderBackend(queueManager, descriptorSystem, textureSystem, bufferSystem, stagingPool);
    }

    public VulkanDeviceContext deviceContext() {
        return deviceContext;
    }
}
