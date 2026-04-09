package com.mojang.blaze3d.vulkan;

import com.mojang.blaze3d.shaders.GpuDebugOptions;
import com.mojang.blaze3d.shaders.ShaderSource;

public final class VulkanRuntime {
    private VulkanWindowContext windowContext;
    private VulkanDeviceContext deviceContext;
    private VulkanSwapchain swapchain;
    private VulkanTuning tuning;
    private VulkanQueueManager queueManager;
    private VulkanDescriptorSystem descriptorSystem;
    private VulkanMemoryBudgetManager memoryBudgetManager;
    private VulkanStagingPool stagingPool;
    private VulkanPipelineLibrary pipelineLibrary;
    private VulkanShaderLibrary shaderLibrary;
    private VulkanCommandEncoderSystem commandEncoderSystem;
    private VulkanTextureSystem textureSystem;
    private VulkanBufferSystem bufferSystem;
    private VulkanDeviceBackend deviceBackend;

    public void bootstrap(long windowHandle, ShaderSource shaderSource, GpuDebugOptions debugOptions) {
        this.windowContext = VulkanWindowContext.create(windowHandle, debugOptions.useLabels(), debugOptions.synchronousLogs());
        this.deviceContext = VulkanDeviceContext.create(windowContext, String.valueOf(debugOptions.logLevel()));
        this.tuning = VulkanTuning.fromSystemProperties();
        this.swapchain = VulkanSwapchain.create(deviceContext);
        this.queueManager = VulkanQueueManager.create(deviceContext, swapchain, tuning);
        this.descriptorSystem = VulkanDescriptorSystem.create(deviceContext, tuning);
        this.memoryBudgetManager = VulkanMemoryBudgetManager.fromSystemProperties();
        this.stagingPool = new VulkanStagingPool(tuning.stagingPoolCapacityKiB());
        this.pipelineLibrary = VulkanPipelineLibrary.create(deviceContext, tuning);
        this.shaderLibrary = VulkanShaderLibrary.create(deviceContext, shaderSource);
        this.textureSystem = VulkanTextureSystem.create(deviceContext, memoryBudgetManager);
        this.bufferSystem = VulkanBufferSystem.create(deviceContext, memoryBudgetManager);
        this.commandEncoderSystem = VulkanCommandEncoderSystem.create(deviceContext, queueManager, descriptorSystem, textureSystem, bufferSystem, stagingPool);
        this.deviceBackend = new VulkanDeviceBackend(
            deviceContext,
            swapchain,
            tuning,
            queueManager,
            descriptorSystem,
            memoryBudgetManager,
            pipelineLibrary,
            shaderLibrary,
            commandEncoderSystem,
            textureSystem,
            bufferSystem
        );

        this.pipelineLibrary.prepareCommonPipelines(this.shaderLibrary);
    }

    public VulkanDeviceContext deviceContext() {
        return this.deviceContext;
    }

    public VulkanCommandEncoderSystem commandEncoderSystem() {
        return this.commandEncoderSystem;
    }

    public VulkanTextureSystem textureSystem() {
        return this.textureSystem;
    }

    public VulkanBufferSystem bufferSystem() {
        return this.bufferSystem;
    }

    public VulkanDeviceBackend deviceBackend() {
        return this.deviceBackend;
    }
}
