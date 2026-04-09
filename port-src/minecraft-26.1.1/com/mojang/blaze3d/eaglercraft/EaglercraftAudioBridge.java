package com.mojang.blaze3d.eaglercraft;

public final class EaglercraftAudioBridge {
    private static boolean initialized;

    private EaglercraftAudioBridge() {
    }

    public static void initialize() {
        initialized = true;
    }

    public static boolean isInitialized() {
        return initialized;
    }
}

