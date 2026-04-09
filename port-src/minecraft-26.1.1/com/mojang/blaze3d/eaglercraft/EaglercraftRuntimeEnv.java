package com.mojang.blaze3d.eaglercraft;

import java.util.Locale;
import java.util.function.LongSupplier;

public final class EaglercraftRuntimeEnv {
    private static final String RUNTIME_MODE = System.getProperty("Eaglercraft.runtime", "desktop").toLowerCase(Locale.ROOT);

    private EaglercraftRuntimeEnv() {
    }

    public static boolean isBrowserRuntime() {
        return "browser".equals(RUNTIME_MODE) || "teavm".equals(RUNTIME_MODE) || "wasm".equals(RUNTIME_MODE);
    }

    public static boolean preferWebGpuBridge() {
        return Boolean.parseBoolean(System.getProperty("Eaglercraft.webgpu.prefer", "true"));
    }

    public static LongSupplier createNanoTimeSource() {
        return System::nanoTime;
    }

    public static String describeRuntime() {
        return isBrowserRuntime() ? "Eaglercraft Browser Runtime" : "Eaglercraft Desktop Runtime";
    }
}

