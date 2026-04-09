package com.mojang.blaze3d.buffers;

import com.mojang.blaze3d.buffers.GpuBuffer.Usage;

public abstract class GpuBuffer implements AutoCloseable {
   public static final int USAGE_MAP_READ = 1;
   public static final int USAGE_MAP_WRITE = 2;
   public static final int USAGE_HINT_CLIENT_STORAGE = 4;
   public static final int USAGE_COPY_DST = 8;
   public static final int USAGE_COPY_SRC = 16;
   public static final int USAGE_VERTEX = 32;
   public static final int USAGE_INDEX = 64;
   public static final int USAGE_UNIFORM = 128;
   public static final int USAGE_UNIFORM_TEXEL_BUFFER = 256;
   @Usage
   private final int usage;
   private final long size;

   public GpuBuffer(@Usage final int usage, final long size) {
      this.size = size;
      this.usage = usage;
   }

   public long size() {
      return this.size;
   }

   @Usage
   public int usage() {
      return this.usage;
   }

   public abstract boolean isClosed();

   @Override
   public abstract void close();

   public GpuBufferSlice slice(final long offset, final long length) {
      if (offset >= 0L && length >= 0L && offset + length <= this.size) {
         return new GpuBufferSlice(this, offset, length);
      } else {
         throw new IllegalArgumentException(
            "Offset of " + offset + " and length " + length + " would put new slice outside buffer's range (of 0," + length + ")"
         );
      }
   }

   public GpuBufferSlice slice() {
      return new GpuBufferSlice(this, 0L, this.size);
   }

   public @interface Usage {
   }

   public interface MappedView extends AutoCloseable {
      java.nio.ByteBuffer data();

      @Override
      void close();
   }
}
