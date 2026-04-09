package com.mojang.blaze3d.vulkan;

import com.mojang.blaze3d.eaglercraft.EaglercraftRuntimeEnv;

public record VulkanTuning(
    int descriptorCacheEntries,
    int pipelineCacheEntries,
    int maxQueuedSubmissions,
    int stagingPoolCapacityKiB,
    int anisotropyPressureFloor,
    boolean presentWhenIdle
) {
    private static final int DEFAULT_DESCRIPTOR_CACHE_ENTRIES = 1024;
    private static final int DEFAULT_PIPELINE_CACHE_ENTRIES = 512;
    private static final int DEFAULT_MAX_QUEUED_SUBMISSIONS = 2048;
    private static final int DEFAULT_STAGING_POOL_CAPACITY_KIB = 512;
    private static final int DEFAULT_ANISOTROPY_PRESSURE_FLOOR = 4;

    public static VulkanTuning fromSystemProperties() {
        if (EaglercraftRuntimeEnv.isBrowserRuntime()) {
            int descriptorEntriesWeb = Integer.getInteger("Eaglercraft.vulkan.descriptorCacheEntries", 512);
            int pipelineEntriesWeb = Integer.getInteger("Eaglercraft.vulkan.pipelineCacheEntries", 256);
            int queueLimitWeb = Integer.getInteger("Eaglercraft.vulkan.maxQueuedSubmissions", 512);
            int stagingKiBWeb = Integer.getInteger("Eaglercraft.vulkan.stagingPoolCapacityKiB", 256);
            int anisotropyFloorWeb = Integer.getInteger("Eaglercraft.vulkan.anisotropyPressureFloor", 2);
            boolean presentWhenIdleWeb = Boolean.parseBoolean(System.getProperty("Eaglercraft.vulkan.presentWhenIdle", "true"));
            return new VulkanTuning(
                Math.max(64, descriptorEntriesWeb),
                Math.max(64, pipelineEntriesWeb),
                Math.max(128, queueLimitWeb),
                Math.max(64, stagingKiBWeb),
                Math.max(1, anisotropyFloorWeb),
                presentWhenIdleWeb
            );
        }

        int descriptorEntries = Integer.getInteger("Eaglercraft.vulkan.descriptorCacheEntries", DEFAULT_DESCRIPTOR_CACHE_ENTRIES);
        int pipelineEntries = Integer.getInteger("Eaglercraft.vulkan.pipelineCacheEntries", DEFAULT_PIPELINE_CACHE_ENTRIES);
        int queueLimit = Integer.getInteger("Eaglercraft.vulkan.maxQueuedSubmissions", DEFAULT_MAX_QUEUED_SUBMISSIONS);
        int stagingKiB = Integer.getInteger("Eaglercraft.vulkan.stagingPoolCapacityKiB", DEFAULT_STAGING_POOL_CAPACITY_KIB);
        int anisotropyFloor = Integer.getInteger("Eaglercraft.vulkan.anisotropyPressureFloor", DEFAULT_ANISOTROPY_PRESSURE_FLOOR);
        boolean presentWhenIdle = Boolean.getBoolean("Eaglercraft.vulkan.presentWhenIdle");
        return new VulkanTuning(
            Math.max(128, descriptorEntries),
            Math.max(128, pipelineEntries),
            Math.max(256, queueLimit),
            Math.max(64, stagingKiB),
            Math.max(1, anisotropyFloor),
            presentWhenIdle
        );
    }
}

