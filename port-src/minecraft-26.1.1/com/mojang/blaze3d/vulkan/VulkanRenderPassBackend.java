package com.mojang.blaze3d.vulkan;

import com.mojang.blaze3d.buffers.GpuBuffer;
import com.mojang.blaze3d.buffers.GpuBufferSlice;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.systems.RenderPass.Draw;
import com.mojang.blaze3d.systems.RenderPassBackend;
import com.mojang.blaze3d.textures.GpuSampler;
import com.mojang.blaze3d.textures.GpuTextureView;
import com.mojang.blaze3d.vertex.VertexFormat.IndexType;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;
import org.jspecify.annotations.Nullable;

public final class VulkanRenderPassBackend implements RenderPassBackend {
    private final String label;
    private final VulkanCommandEncoderBackend owner;
    private final VulkanDescriptorSystem descriptorSystem;
    private final Map<String, GpuBufferSlice> uniforms = new HashMap<String, GpuBufferSlice>();
    private boolean closed;

    public VulkanRenderPassBackend(String label, VulkanCommandEncoderBackend owner, VulkanDescriptorSystem descriptorSystem) {
        this.label = label;
        this.owner = owner;
        this.descriptorSystem = descriptorSystem;
    }

    @Override
    public void pushDebugGroup(Supplier<String> label) {
    }

    @Override
    public void popDebugGroup() {
    }

    @Override
    public void setPipeline(RenderPipeline pipeline) {
    }

    @Override
    public void bindTexture(String name, @Nullable GpuTextureView textureView, @Nullable GpuSampler sampler) {
        if (textureView != null && sampler != null) {
            descriptorSystem.bindTexture(name, textureView, sampler);
        }
    }

    @Override
    public void setUniform(String name, GpuBuffer value) {
        uniforms.put(name, value.slice());
    }

    @Override
    public void setUniform(String name, GpuBufferSlice value) {
        uniforms.put(name, value);
    }

    @Override
    public void enableScissor(int x, int y, int width, int height) {
    }

    @Override
    public void disableScissor() {
    }

    @Override
    public void setVertexBuffer(int slot, GpuBuffer vertexBuffer) {
    }

    @Override
    public void setIndexBuffer(GpuBuffer indexBuffer, IndexType indexType) {
    }

    @Override
    public void drawIndexed(int baseVertex, int firstIndex, int indexCount, int instanceCount) {
    }

    @Override
    public <T> void drawMultipleIndexed(Collection<Draw<T>> draws, @Nullable GpuBuffer defaultIndexBuffer, @Nullable IndexType defaultIndexType, Collection<String> dynamicUniforms, T uniformArgument) {
    }

    @Override
    public void draw(int firstVertex, int vertexCount) {
    }

    @Override
    public void close() {
        if (!closed) {
            closed = true;
            owner.notifyRenderPassClosed(label);
        }
    }

    @Override
    public boolean isClosed() {
        return closed;
    }
}
