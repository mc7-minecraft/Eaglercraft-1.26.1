package com.mojang.blaze3d.vulkan;

public final class VulkanWindowContext {
    private final long windowHandle;
    private final boolean debugLabelsEnabled;
    private final boolean synchronousDebug;

    private VulkanWindowContext(long windowHandle, boolean debugLabelsEnabled, boolean synchronousDebug) {
        this.windowHandle = windowHandle;
        this.debugLabelsEnabled = debugLabelsEnabled;
        this.synchronousDebug = synchronousDebug;
    }

    public static VulkanWindowContext create(long windowHandle, boolean debugLabelsEnabled, boolean synchronousDebug) {
        return new VulkanWindowContext(windowHandle, debugLabelsEnabled, synchronousDebug);
    }

    public long windowHandle() {
        return windowHandle;
    }

    public boolean debugLabelsEnabled() {
        return debugLabelsEnabled;
    }

    public boolean synchronousDebug() {
        return synchronousDebug;
    }
}
