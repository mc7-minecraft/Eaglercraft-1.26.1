package com.mojang.blaze3d.vulkan;

import com.mojang.blaze3d.systems.GpuQuery;
import java.util.OptionalLong;

public final class VulkanQueryImpl implements GpuQuery {
    private OptionalLong value = OptionalLong.empty();

    public void setValue(long nanos) {
        this.value = OptionalLong.of(nanos);
    }

    @Override
    public OptionalLong getValue() {
        return value;
    }

    @Override
    public void close() {
        value = OptionalLong.empty();
    }
}
