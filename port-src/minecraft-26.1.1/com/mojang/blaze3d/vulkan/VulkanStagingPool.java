package com.mojang.blaze3d.vulkan;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayDeque;
import java.util.Deque;

public final class VulkanStagingPool {
    private final int maxPoolBytes;
    private final Deque<ByteBuffer> free = new ArrayDeque<ByteBuffer>();
    private int pooledBytes;

    public VulkanStagingPool(int capacityKiB) {
        this.maxPoolBytes = capacityKiB * 1024;
    }

    public synchronized ByteBuffer borrow(int minimumCapacity) {
        ByteBuffer best = null;
        for (ByteBuffer candidate : free) {
            if (candidate.capacity() >= minimumCapacity) {
                best = candidate;
                break;
            }
        }
        if (best != null) {
            free.remove(best);
            pooledBytes -= best.capacity();
            best.clear();
            return best;
        }
        return ByteBuffer.allocateDirect(minimumCapacity).order(ByteOrder.nativeOrder());
    }

    public synchronized void release(ByteBuffer buffer) {
        if (buffer == null) {
            return;
        }
        buffer.clear();
        if (buffer.capacity() > maxPoolBytes / 2) {
            return;
        }
        if (pooledBytes + buffer.capacity() > maxPoolBytes) {
            return;
        }
        pooledBytes += buffer.capacity();
        free.addFirst(buffer);
    }

    public synchronized int pooledBytes() {
        return pooledBytes;
    }
}
