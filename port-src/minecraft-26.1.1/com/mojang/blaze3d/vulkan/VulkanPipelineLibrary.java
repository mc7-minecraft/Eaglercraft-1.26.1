package com.mojang.blaze3d.vulkan;

import com.mojang.blaze3d.pipeline.CompiledRenderPipeline;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.shaders.ShaderSource;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public final class VulkanPipelineLibrary {
    private final VulkanDeviceContext deviceContext;
    private final int maxCacheEntries;
    private final Map<String, String> pipelineKeys = new HashMap<String, String>();
    private final Map<RenderPipeline, CompiledRenderPipeline> compiledPipelines;

    private VulkanPipelineLibrary(VulkanDeviceContext deviceContext, int maxCacheEntries) {
        this.deviceContext = deviceContext;
        this.maxCacheEntries = maxCacheEntries;
        this.compiledPipelines = new LinkedHashMap<RenderPipeline, CompiledRenderPipeline>(128, 0.75f, true) {
            @Override
            protected boolean removeEldestEntry(Map.Entry<RenderPipeline, CompiledRenderPipeline> eldest) {
                return size() > VulkanPipelineLibrary.this.maxCacheEntries;
            }
        };
    }

    public static VulkanPipelineLibrary create(VulkanDeviceContext deviceContext, VulkanTuning tuning) {
        return new VulkanPipelineLibrary(deviceContext, tuning.pipelineCacheEntries());
    }

    public void prepareCommonPipelines(VulkanShaderLibrary shaderLibrary) {
        pipelineKeys.put("terrain", shaderLibrary.shaderKey("terrain"));
        pipelineKeys.put("entities", shaderLibrary.shaderKey("entities"));
        pipelineKeys.put("ui", shaderLibrary.shaderKey("ui"));
    }

    public String pipelineKey(String name) {
        return pipelineKeys.get(name);
    }

    public CompiledRenderPipeline precompile(RenderPipeline pipeline, ShaderSource shaderSource) {
        String key = shaderSource == null ? "default" : shaderSource.toString();
        pipelineKeys.putIfAbsent(pipeline.toString(), key);
        CompiledRenderPipeline compiled = new VulkanCompiledRenderPipeline();
        compiledPipelines.put(pipeline, compiled);
        return compiled;
    }

    public void clear() {
        compiledPipelines.clear();
        pipelineKeys.clear();
    }

    public VulkanDeviceContext deviceContext() {
        return deviceContext;
    }

    private static final class VulkanCompiledRenderPipeline implements CompiledRenderPipeline {
        @Override
        public boolean isValid() {
            return true;
        }
    }
}
