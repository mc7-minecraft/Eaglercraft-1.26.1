package com.mojang.blaze3d;

import com.mojang.blaze3d.GLFWErrorCapture.Error;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import org.jspecify.annotations.Nullable;
import org.lwjgl.glfw.GLFWErrorCallbackI;
import org.lwjgl.system.MemoryUtil;

public class GLFWErrorCapture implements GLFWErrorCallbackI, Iterable<Error> {
   public static record Error(int error, String description) {
   }

   @Nullable
   private List<Error> errors;

   public void invoke(final int error, final long description) {
      if (this.errors == null) {
         this.errors = new ArrayList<>();
      }

      this.errors.add(new Error(error, MemoryUtil.memUTF8(description)));
   }

   @Override
   public Iterator<Error> iterator() {
      return this.errors == null ? Collections.emptyIterator() : this.errors.iterator();
   }

   @Nullable
   public Error firstError() {
      return this.errors == null ? null : this.errors.getFirst();
   }
}
