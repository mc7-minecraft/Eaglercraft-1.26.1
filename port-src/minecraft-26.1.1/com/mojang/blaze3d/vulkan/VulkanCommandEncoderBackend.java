package com.mojang.blaze3d.vulkan;

import com.mojang.blaze3d.buffers.GpuBuffer;
import com.mojang.blaze3d.buffers.GpuBufferSlice;
import com.mojang.blaze3d.buffers.GpuFence;
import com.mojang.blaze3d.buffers.GpuBuffer.MappedView;
import com.mojang.blaze3d.platform.NativeImage;
import com.mojang.blaze3d.platform.NativeImage.Format;
import com.mojang.blaze3d.systems.CommandEncoderBackend;
import com.mojang.blaze3d.systems.GpuQuery;
import com.mojang.blaze3d.systems.RenderPassBackend;
import com.mojang.blaze3d.textures.GpuTexture;
import com.mojang.blaze3d.textures.GpuTextureView;
import java.nio.ByteBuffer;
import java.util.OptionalDouble;
import java.util.OptionalInt;
import java.util.function.Supplier;
import org.jspecify.annotations.Nullable;

public final class VulkanCommandEncoderBackend implements CommandEncoderBackend {
    private final VulkanQueueManager queueManager;
    private final VulkanDescriptorSystem descriptorSystem;
    private final VulkanTextureSystem textureSystem;
    private final VulkanBufferSystem bufferSystem;
    private final VulkanStagingPool stagingPool;
    private boolean inRenderPass;

    public VulkanCommandEncoderBackend(
        VulkanQueueManager queueManager,
        VulkanDescriptorSystem descriptorSystem,
        VulkanTextureSystem textureSystem,
        VulkanBufferSystem bufferSystem,
        VulkanStagingPool stagingPool
    ) {
        this.queueManager = queueManager;
        this.descriptorSystem = descriptorSystem;
        this.textureSystem = textureSystem;
        this.bufferSystem = bufferSystem;
        this.stagingPool = stagingPool;
    }

    @Override
    public RenderPassBackend createRenderPass(Supplier<String> label, GpuTextureView colorTexture, OptionalInt clearColor) {
        return createRenderPass(label, colorTexture, clearColor, null, OptionalDouble.empty());
    }

    @Override
    public RenderPassBackend createRenderPass(Supplier<String> label, GpuTextureView colorTexture, OptionalInt clearColor, @Nullable GpuTextureView depthTexture, OptionalDouble clearDepth) {
        if (inRenderPass) {
            throw new IllegalStateException("Render pass already active");
        }
        inRenderPass = true;
        return new VulkanRenderPassBackend(label.get(), this, descriptorSystem);
    }

    @Override
    public boolean isInRenderPass() {
        return inRenderPass;
    }

    void notifyRenderPassClosed(String label) {
        inRenderPass = false;
        queueManager.submit("pass:" + label);
    }

    @Override
    public void clearColorTexture(GpuTexture colorTexture, int clearColor) {
    }

    @Override
    public void clearColorAndDepthTextures(GpuTexture colorTexture, int clearColor, GpuTexture depthTexture, double clearDepth) {
    }

    @Override
    public void clearColorAndDepthTextures(GpuTexture colorTexture, int clearColor, GpuTexture depthTexture, double clearDepth, int regionX, int regionY, int regionWidth, int regionHeight) {
    }

    @Override
    public void clearDepthTexture(GpuTexture depthTexture, double clearDepth) {
    }

    @Override
    public void writeToBuffer(GpuBufferSlice destination, ByteBuffer data) {
        requireBuffer(destination.buffer()).write(destination.offset(), data.slice());
    }

    @Override
    public MappedView mapBuffer(GpuBufferSlice buffer, boolean read, boolean write) {
        VulkanBuffer vkBuffer = requireBuffer(buffer.buffer());
        ByteBuffer window = stagingPool.borrow((int)buffer.length());
        window.limit((int)buffer.length());
        vkBuffer.readInto(buffer.offset(), window);
        window.flip();
        return new VulkanMappedView(vkBuffer, buffer.offset(), window, write, stagingPool);
    }

    @Override
    public void copyToBuffer(GpuBufferSlice source, GpuBufferSlice target) {
        VulkanBuffer sourceBuffer = requireBuffer(source.buffer());
        VulkanBuffer targetBuffer = requireBuffer(target.buffer());
        ByteBuffer data = stagingPool.borrow((int)source.length());
        data.limit((int)source.length());
        sourceBuffer.readInto(source.offset(), data);
        data.flip();
        targetBuffer.write(target.offset(), data);
        stagingPool.release(data);
    }

    @Override
    public void writeToTexture(GpuTexture destination, NativeImage source, int mipLevel, int depthOrLayer, int destX, int destY, int width, int height, int sourceX, int sourceY) {
        // The production path should stage and copy through a transfer queue.
    }

    @Override
    public void writeToTexture(GpuTexture destination, ByteBuffer source, Format format, int mipLevel, int depthOrLayer, int destX, int destY, int width, int height) {
        // The production path should stage and copy through a transfer queue.
    }

    @Override
    public void copyTextureToBuffer(GpuTexture source, GpuBuffer destination, long offset, Runnable callback, int mipLevel) {
        callback.run();
    }

    @Override
    public void copyTextureToBuffer(GpuTexture source, GpuBuffer destination, long offset, Runnable callback, int mipLevel, int x, int y, int width, int height) {
        callback.run();
    }

    @Override
    public void copyTextureToTexture(GpuTexture source, GpuTexture destination, int mipLevel, int destX, int destY, int sourceX, int sourceY, int width, int height) {
    }

    @Override
    public void presentTexture(GpuTextureView texture) {
        queueManager.submit("present_texture");
    }

    @Override
    public GpuFence createFence() {
        VulkanFenceImpl fence = new VulkanFenceImpl();
        fence.signal();
        return fence;
    }

    @Override
    public GpuQuery timerQueryBegin() {
        VulkanQueryImpl query = new VulkanQueryImpl();
        query.setValue(System.nanoTime());
        return query;
    }

    @Override
    public void timerQueryEnd(GpuQuery query) {
        if (query instanceof VulkanQueryImpl vkQuery) {
            vkQuery.setValue(System.nanoTime());
        }
    }

    private VulkanBuffer requireBuffer(GpuBuffer buffer) {
        if (buffer instanceof VulkanBuffer vkBuffer) {
            return vkBuffer;
        }
        throw new IllegalArgumentException("Buffer was not created by Vulkan backend");
    }

    private static final class VulkanMappedView implements MappedView {
        private final VulkanBuffer buffer;
        private final long offset;
        private final ByteBuffer view;
        private final boolean writable;
        private final VulkanStagingPool stagingPool;

        private VulkanMappedView(VulkanBuffer buffer, long offset, ByteBuffer view, boolean writable, VulkanStagingPool stagingPool) {
            this.buffer = buffer;
            this.offset = offset;
            this.view = view;
            this.writable = writable;
            this.stagingPool = stagingPool;
        }

        @Override
        public ByteBuffer data() {
            return view;
        }

        @Override
        public void close() {
            if (writable) {
                ByteBuffer duplicate = view.duplicate();
                duplicate.position(0);
                duplicate.limit(view.limit());
                buffer.write(offset, duplicate);
            }
            stagingPool.release(view);
        }
    }
}
