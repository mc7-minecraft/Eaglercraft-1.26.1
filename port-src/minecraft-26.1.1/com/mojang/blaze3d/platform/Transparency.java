package com.mojang.blaze3d.platform;

public record Transparency(boolean hasTransparent, boolean hasTranslucent) {
   public static final com.mojang.blaze3d.platform.Transparency NONE = new com.mojang.blaze3d.platform.Transparency(false, false);
   public static final com.mojang.blaze3d.platform.Transparency TRANSPARENT = new com.mojang.blaze3d.platform.Transparency(true, false);
   public static final com.mojang.blaze3d.platform.Transparency TRANSLUCENT = new com.mojang.blaze3d.platform.Transparency(false, true);
   public static final com.mojang.blaze3d.platform.Transparency TRANSPARENT_AND_TRANSLUCENT = new com.mojang.blaze3d.platform.Transparency(true, true);

   public static com.mojang.blaze3d.platform.Transparency of(final boolean hasTransparent, final boolean hasTranslucent) {
      if (hasTransparent && hasTranslucent) {
         return TRANSPARENT_AND_TRANSLUCENT;
      } else if (hasTransparent) {
         return TRANSPARENT;
      } else {
         return hasTranslucent ? TRANSLUCENT : NONE;
      }
   }

   public com.mojang.blaze3d.platform.Transparency or(final com.mojang.blaze3d.platform.Transparency other) {
      return of(this.hasTransparent || other.hasTransparent, this.hasTranslucent || other.hasTranslucent);
   }

   public boolean isOpaque() {
      return !this.hasTransparent && !this.hasTranslucent;
   }
}
