package com.mojang.text2speech;

public final class Narrator {
   private static final Narrator INSTANCE = new Narrator();

   private Narrator() {
   }

   public static Narrator getNarrator() {
      return INSTANCE;
   }

   public boolean active() {
      return false;
   }

   public void say(final String message, final boolean interrupt, final float volume) {
   }

   public void clear() {
   }

   public void destroy() {
   }
}