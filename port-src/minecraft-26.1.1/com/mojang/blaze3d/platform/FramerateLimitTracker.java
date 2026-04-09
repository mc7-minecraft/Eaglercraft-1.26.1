/*
 * Decompiled with CFR 0.152.
 */
package com.mojang.blaze3d.platform;

import net.minecraft.client.InactivityFpsLimit;
import net.minecraft.client.Minecraft;
import net.minecraft.client.Options;
import net.minecraft.util.Util;

public class FramerateLimitTracker {
    private static final int OUT_OF_LEVEL_MENU_LIMIT = 30;
    private static final int ICONIFIED_WINDOW_LIMIT = 3;
    private static final int AFK_LIMIT = 15;
    private static final int LONG_AFK_LIMIT = 3;
    private static final long AFK_THRESHOLD_MS = 15000L;
    private static final long LONG_AFK_THRESHOLD_MS = 120000L;
    private final Options options;
    private final Minecraft minecraft;
    private int framerateLimit;
    private long latestInputTime;

    public FramerateLimitTracker(Options options, Minecraft minecraft) {
        this.options = options;
        this.minecraft = minecraft;
        this.framerateLimit = options.framerateLimit().get();
    }

    public int getFramerateLimit() {
        return switch (this.getThrottleReason().ordinal()) {
            default -> throw new MatchException(null, null);
            case 0 -> this.framerateLimit;
            case 1 -> ICONIFIED_WINDOW_LIMIT;
            case 2 -> LONG_AFK_LIMIT;
            case 3 -> Math.min(this.framerateLimit, AFK_LIMIT);
            case 4 -> OUT_OF_LEVEL_MENU_LIMIT;
        };
    }

    public FramerateThrottleReason getThrottleReason() {
        InactivityFpsLimit inactivityFpsLimit = this.options.inactivityFpsLimit().get();
        if (this.minecraft.getWindow().isIconified()) {
            return FramerateThrottleReason.WINDOW_ICONIFIED;
        }
        if (inactivityFpsLimit == InactivityFpsLimit.AFK) {
            long afkTimeMillis = Util.getMillis() - this.latestInputTime;
            if (afkTimeMillis > LONG_AFK_THRESHOLD_MS) {
                return FramerateThrottleReason.LONG_AFK;
            }
            if (afkTimeMillis > AFK_THRESHOLD_MS) {
                return FramerateThrottleReason.SHORT_AFK;
            }
        }
        if (this.minecraft.level == null && (this.minecraft.screen != null || this.minecraft.getOverlay() != null)) {
            return FramerateThrottleReason.OUT_OF_LEVEL_MENU;
        }
        return FramerateThrottleReason.NONE;
    }

    public boolean isHeavilyThrottled() {
        FramerateThrottleReason reason = this.getThrottleReason();
        return reason == FramerateThrottleReason.WINDOW_ICONIFIED || reason == FramerateThrottleReason.LONG_AFK;
    }

    public void setFramerateLimit(int value) {
        this.framerateLimit = value;
    }

    public void onInputReceived() {
        this.latestInputTime = Util.getMillis();
    }

    public static enum FramerateThrottleReason {
        NONE,
        WINDOW_ICONIFIED,
        LONG_AFK,
        SHORT_AFK,
        OUT_OF_LEVEL_MENU;

    }
}
