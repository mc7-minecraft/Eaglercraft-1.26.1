package com.mojang.blaze3d.vulkan;

import java.util.ArrayDeque;
import java.util.Queue;

public final class VulkanQueueManager {
    private final VulkanDeviceContext deviceContext;
    private final VulkanSwapchain swapchain;
    private final VulkanTuning tuning;
    private final Queue<String> submissions = new ArrayDeque<String>();
    private long droppedSubmissions;

    private VulkanQueueManager(VulkanDeviceContext deviceContext, VulkanSwapchain swapchain, VulkanTuning tuning) {
        this.deviceContext = deviceContext;
        this.swapchain = swapchain;
        this.tuning = tuning;
    }

    public static VulkanQueueManager create(VulkanDeviceContext deviceContext, VulkanSwapchain swapchain, VulkanTuning tuning) {
        return new VulkanQueueManager(deviceContext, swapchain, tuning);
    }

    public synchronized void submit(String commandBufferName) {
        while (submissions.size() >= tuning.maxQueuedSubmissions()) {
            submissions.poll();
            droppedSubmissions++;
        }
        submissions.add(commandBufferName);
    }

    public synchronized int flushAndPresent() {
        if (submissions.isEmpty() && !tuning.presentWhenIdle()) {
            return -1;
        }
        while (!submissions.isEmpty()) {
            submissions.poll();
        }
        int image = swapchain.acquireNextImage();
        swapchain.present(image);
        return image;
    }

    public synchronized long droppedSubmissions() {
        return droppedSubmissions;
    }

    public VulkanDeviceContext deviceContext() {
        return deviceContext;
    }
}
