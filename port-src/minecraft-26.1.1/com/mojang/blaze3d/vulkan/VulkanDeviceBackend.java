package com.mojang.blaze3d.vulkan;

import com.mojang.blaze3d.buffers.GpuBuffer;
import com.mojang.blaze3d.pipeline.CompiledRenderPipeline;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.shaders.ShaderSource;
import com.mojang.blaze3d.systems.CommandEncoderBackend;
import com.mojang.blaze3d.systems.GpuDeviceBackend;
import com.mojang.blaze3d.textures.AddressMode;
import com.mojang.blaze3d.textures.FilterMode;
import com.mojang.blaze3d.textures.GpuSampler;
import com.mojang.blaze3d.textures.GpuTexture;
import com.mojang.blaze3d.textures.GpuTextureView;
import com.mojang.blaze3d.textures.TextureFormat;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.OptionalDouble;
import java.util.function.Supplier;
import org.jspecify.annotations.Nullable;

public final class VulkanDeviceBackend implements GpuDeviceBackend {
    private final VulkanDeviceContext deviceContext;
    private final VulkanSwapchain swapchain;
    private final VulkanTuning tuning;
    private final VulkanQueueManager queueManager;
    private final VulkanDescriptorSystem descriptorSystem;
    private final VulkanMemoryBudgetManager memoryBudgetManager;
    private final VulkanPipelineLibrary pipelineLibrary;
    private final VulkanShaderLibrary shaderLibrary;
    private final VulkanCommandEncoderSystem commandEncoderSystem;
    private final VulkanTextureSystem textureSystem;
    private final VulkanBufferSystem bufferSystem;
    private final List<String> debugMessages = new ArrayList<String>();
    private boolean closed;

    public VulkanDeviceBackend(
        VulkanDeviceContext deviceContext,
        VulkanSwapchain swapchain,
        VulkanTuning tuning,
        VulkanQueueManager queueManager,
        VulkanDescriptorSystem descriptorSystem,
        VulkanMemoryBudgetManager memoryBudgetManager,
        VulkanPipelineLibrary pipelineLibrary,
        VulkanShaderLibrary shaderLibrary,
        VulkanCommandEncoderSystem commandEncoderSystem,
        VulkanTextureSystem textureSystem,
        VulkanBufferSystem bufferSystem
    ) {
        this.deviceContext = deviceContext;
        this.swapchain = swapchain;
        this.tuning = tuning;
        this.queueManager = queueManager;
        this.descriptorSystem = descriptorSystem;
        this.memoryBudgetManager = memoryBudgetManager;
        this.pipelineLibrary = pipelineLibrary;
        this.shaderLibrary = shaderLibrary;
        this.commandEncoderSystem = commandEncoderSystem;
        this.textureSystem = textureSystem;
        this.bufferSystem = bufferSystem;
    }

    @Override
    public CommandEncoderBackend createCommandEncoder() {
        return commandEncoderSystem.createEncoder();
    }

    @Override
    public GpuSampler createSampler(AddressMode addressModeU, AddressMode addressModeV, FilterMode minFilter, FilterMode magFilter, int maxAnisotropy, OptionalDouble maxLod) {
        return new VulkanSampler(addressModeU, addressModeV, minFilter, magFilter, maxAnisotropy, maxLod);
    }

    @Override
    public GpuTexture createTexture(@Nullable Supplier<String> label, int usage, TextureFormat format, int width, int height, int depthOrLayers, int mipLevels) {
        return createTexture(label == null ? null : label.get(), usage, format, width, height, depthOrLayers, mipLevels);
    }

    @Override
    public GpuTexture createTexture(@Nullable String label, int usage, TextureFormat format, int width, int height, int depthOrLayers, int mipLevels) {
        String resolvedLabel = label == null ? "vk_texture_" + System.nanoTime() : label;
        return textureSystem.createTexture(usage, resolvedLabel, format, width, height, depthOrLayers, mipLevels);
    }

    @Override
    public GpuTextureView createTextureView(GpuTexture texture) {
        return createTextureView(texture, 0, texture.getMipLevels());
    }

    @Override
    public GpuTextureView createTextureView(GpuTexture texture, int baseMipLevel, int mipLevels) {
        VulkanTexture vkTexture = requireTexture(texture);
        return textureSystem.createTextureView(vkTexture, baseMipLevel, mipLevels);
    }

    @Override
    public GpuBuffer createBuffer(@Nullable Supplier<String> label, int usage, long size) {
        String resolvedLabel = label == null ? "vk_buffer_" + System.nanoTime() : label.get();
        return bufferSystem.createBuffer(usage, resolvedLabel, size);
    }

    @Override
    public GpuBuffer createBuffer(@Nullable Supplier<String> label, int usage, ByteBuffer data) {
        String resolvedLabel = label == null ? "vk_buffer_" + System.nanoTime() : label.get();
        return bufferSystem.createBuffer(usage, resolvedLabel, data);
    }

    @Override
    public String getImplementationInformation() {
        return getRenderer() + " Vulkan " + getVersion() + " (vendor: " + getVendor() + ")";
    }

    @Override
    public List<String> getLastDebugMessages() {
        List<String> out = new ArrayList<String>(debugMessages);
        out.add("vk.queue.dropped=" + queueManager.droppedSubmissions());
        out.add("vk.memory.textureBytes=" + memoryBudgetManager.usedTextureBytes());
        out.add("vk.memory.bufferBytes=" + memoryBudgetManager.usedBufferBytes());
        out.add("vk.memory.pressure=" + memoryBudgetManager.combinedPressure());
        out.add("vk.tuning.maxQueuedSubmissions=" + tuning.maxQueuedSubmissions());
        out.add("vk.pipeline.cacheLimit=" + tuning.pipelineCacheEntries());
        out.add("vk.anisotropy.dynamicCap=" + getMaxSupportedAnisotropy());
        return List.copyOf(out);
    }

    @Override
    public boolean isDebuggingEnabled() {
        return deviceContext.windowContext().debugLabelsEnabled();
    }

    @Override
    public String getVendor() {
        return deviceContext.vendor();
    }

    @Override
    public String getBackendName() {
        return "Vulkan";
    }

    @Override
    public String getVersion() {
        return deviceContext.version();
    }

    @Override
    public String getRenderer() {
        return deviceContext.renderer();
    }

    @Override
    public int getMaxTextureSize() {
        return 16384;
    }

    @Override
    public int getUniformOffsetAlignment() {
        return 256;
    }

    @Override
    public CompiledRenderPipeline precompilePipeline(RenderPipeline pipeline, @Nullable ShaderSource shaderSource) {
        return pipelineLibrary.precompile(pipeline, shaderSource == null ? shaderLibrary.shaderSource() : shaderSource);
    }

    @Override
    public void clearPipelineCache() {
        pipelineLibrary.clear();
    }

    @Override
    public List<String> getEnabledExtensions() {
        return deviceContext.enabledExtensions();
    }

    @Override
    public int getMaxSupportedAnisotropy() {
        double pressure = memoryBudgetManager.combinedPressure();
        if (pressure >= 0.95) {
            return Math.min(2, tuning.anisotropyPressureFloor());
        }
        if (pressure >= 0.85) {
            return Math.min(4, Math.max(2, tuning.anisotropyPressureFloor()));
        }
        if (pressure >= 0.75) {
            return Math.min(8, Math.max(4, tuning.anisotropyPressureFloor()));
        }
        return 16;
    }

    @Override
    public void close() {
        if (!closed) {
            descriptorSystem.clear();
            clearPipelineCache();
            textureSystem.clear();
            bufferSystem.clear();
            closed = true;
        }
    }

    @Override
    public void setVsync(boolean enabled) {
        swapchain.setVsyncEnabled(enabled);
    }

    @Override
    public void presentFrame() {
        queueManager.flushAndPresent();
    }

    @Override
    public boolean isZZeroToOne() {
        return true;
    }

    VulkanQueueManager queueManager() {
        return queueManager;
    }

    VulkanDescriptorSystem descriptorSystem() {
        return descriptorSystem;
    }

    VulkanMemoryBudgetManager memoryBudgetManager() {
        return memoryBudgetManager;
    }

    private VulkanTexture requireTexture(GpuTexture texture) {
        if (texture instanceof VulkanTexture vkTexture) {
            return vkTexture;
        }
        throw new IllegalArgumentException("Texture was not created by Vulkan backend");
    }
}
