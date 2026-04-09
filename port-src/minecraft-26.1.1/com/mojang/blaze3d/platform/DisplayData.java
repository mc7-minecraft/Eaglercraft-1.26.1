package com.mojang.blaze3d.platform;

import java.util.OptionalInt;

public record DisplayData(int width, int height, OptionalInt fullscreenWidth, OptionalInt fullscreenHeight, boolean isFullscreen) {
   public com.mojang.blaze3d.platform.DisplayData withSize(final int width, final int height) {
      return new com.mojang.blaze3d.platform.DisplayData(width, height, this.fullscreenWidth, this.fullscreenHeight, this.isFullscreen);
   }

   public com.mojang.blaze3d.platform.DisplayData withFullscreen(final boolean isFullscreen) {
      return new com.mojang.blaze3d.platform.DisplayData(this.width, this.height, this.fullscreenWidth, this.fullscreenHeight, isFullscreen);
   }
}
