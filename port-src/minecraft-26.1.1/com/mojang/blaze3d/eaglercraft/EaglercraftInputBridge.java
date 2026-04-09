package com.mojang.blaze3d.eaglercraft;

public final class EaglercraftInputBridge {
    private static long browserEventTicks;

    private EaglercraftInputBridge() {
    }

    public static void pumpEvents() {
        if (EaglercraftRuntimeEnv.isBrowserRuntime()) {
            browserEventTicks++;
        }
    }

    public static long browserEventTicks() {
        return browserEventTicks;
    }
}

