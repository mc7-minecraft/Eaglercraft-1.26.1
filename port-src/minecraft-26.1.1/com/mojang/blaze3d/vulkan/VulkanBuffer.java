package com.mojang.blaze3d.vulkan;

import com.mojang.blaze3d.buffers.GpuBuffer;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public final class VulkanBuffer extends GpuBuffer {
    private final long nativeHandle;
    private final VulkanMemoryBudgetManager budgetManager;
    private final ByteBuffer storage;
    private boolean closed;

    public VulkanBuffer(int usage, long size, long nativeHandle, VulkanMemoryBudgetManager budgetManager) {
        super(usage, size);
        this.nativeHandle = nativeHandle;
        this.budgetManager = budgetManager;
        this.storage = ByteBuffer.allocateDirect((int)size).order(ByteOrder.nativeOrder());
    }

    public long nativeHandle() {
        return nativeHandle;
    }

    public synchronized void write(long offset, ByteBuffer src) {
        ByteBuffer copy = src.slice();
        if (offset + copy.remaining() > size()) {
            throw new IllegalArgumentException("Write exceeds buffer bounds");
        }
        ByteBuffer dst = storage.duplicate();
        dst.position((int)offset);
        dst.put(copy);
    }

    public synchronized ByteBuffer read(long offset, int length) {
        if (offset + length > size()) {
            throw new IllegalArgumentException("Read exceeds buffer bounds");
        }
        ByteBuffer dst = ByteBuffer.allocateDirect(length).order(ByteOrder.nativeOrder());
        readInto(offset, dst);
        dst.flip();
        return dst;
    }

    public synchronized void readInto(long offset, ByteBuffer dst) {
        if (offset + dst.remaining() > size()) {
            throw new IllegalArgumentException("Read exceeds buffer bounds");
        }
        ByteBuffer src = storage.duplicate();
        src.position((int)offset);
        src.limit((int)offset + dst.remaining());
        dst.put(src);
    }

    public synchronized ByteBuffer rawStorage() {
        return storage.duplicate().clear();
    }

    @Override
    public synchronized boolean isClosed() {
        return closed;
    }

    @Override
    public synchronized void close() {
        if (!closed) {
            closed = true;
            budgetManager.releaseBufferBytes(size());
        }
    }
}
