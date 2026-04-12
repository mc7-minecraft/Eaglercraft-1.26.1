package com.mojang.blaze3d.platform;

import java.io.IOException;
import java.io.InputStream;
import java.util.Locale;
import net.minecraft.server.packs.resources.IoSupplier;

public class MacosUtil {
   public static final boolean IS_MACOS = System.getProperty("os.name").toLowerCase(Locale.ROOT).contains("mac");

   public static void exitNativeFullscreen(final com.mojang.blaze3d.platform.Window window) {
   }

   public static void clearResizableBit(final com.mojang.blaze3d.platform.Window window) {
   }

   public static void loadIcon(final IoSupplier<InputStream> icon) throws IOException {
      try (InputStream ignored = icon.get()) {
      }
   }
}
