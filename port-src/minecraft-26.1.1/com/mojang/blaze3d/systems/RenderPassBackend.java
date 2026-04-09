package com.mojang.blaze3d.systems;

import com.mojang.blaze3d.buffers.GpuBuffer;
import com.mojang.blaze3d.buffers.GpuBufferSlice;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.systems.RenderPass.Draw;
import com.mojang.blaze3d.textures.GpuSampler;
import com.mojang.blaze3d.textures.GpuTextureView;
import com.mojang.blaze3d.vertex.VertexFormat.IndexType;
import java.util.Collection;
import java.util.function.Supplier;
import org.jspecify.annotations.Nullable;

public interface RenderPassBackend extends AutoCloseable {
   void pushDebugGroup(final Supplier<String> label);

   void popDebugGroup();

   void setPipeline(final RenderPipeline pipeline);

   void bindTexture(final String name, @Nullable final GpuTextureView textureView, @Nullable final GpuSampler sampler);

   void setUniform(final String name, final GpuBuffer value);

   void setUniform(final String name, final GpuBufferSlice value);

   void enableScissor(final int x, final int y, final int width, final int height);

   void disableScissor();

   void setVertexBuffer(final int slot, final GpuBuffer vertexBuffer);

   void setIndexBuffer(final GpuBuffer indexBuffer, final IndexType indexType);

   void drawIndexed(final int baseVertex, final int firstIndex, final int indexCount, final int instanceCount);

   <T> void drawMultipleIndexed(
      final Collection<Draw<T>> draws,
      @Nullable final GpuBuffer defaultIndexBuffer,
      @Nullable final IndexType defaultIndexType,
      final Collection<String> dynamicUniforms,
      final T uniformArgument
   );

   void draw(final int firstVertex, final int vertexCount);

   @Override
   void close();

   boolean isClosed();
}
