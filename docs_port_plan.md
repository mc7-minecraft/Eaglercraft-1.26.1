# Eaglercraft 26.1.1 Port Plan

## Goal

Port Minecraft 26.1.1 Optimised desktop runtime to Eaglercraft browser runtime using Eaglercraft-style platform shims.

## Priority mapping

1. GPU backend
- Source namespace: com.mojang.blaze3d.vulkan
- Web target: WebGPU backend, fallback to WebGL2 backend
- First bridge artifact: runtime/Eaglercraft_gpu_bridge.js

2. Window and input
- Source hot spots: com.mojang.blaze3d.platform and net.minecraft.client.MouseHandler/KeyboardHandler
- Web target: browser pointer lock, key events, wheel, touch and gamepad
- Runtime base reused: runtime/eagler-base/platformInput.js

3. Audio
- Source hot spots: net.minecraft.client.sounds and OpenAL references
- Web target: WebAudio graph and streaming decode path
- Runtime base reused: runtime/eagler-base/platformAudio.js

4. Build and packaging
- JS shell output: Eaglercraft_26-1-1_javascript.html
- WASM shell output: Eaglercraft_26-1-1_wasm.html
- Build scaffold entry: scripts/build_Eaglercraft.ps1

## Current status

- Local TeaVM project scaffold complete inside `26.1.1 - Eaglercraft`
- External donor repos removed from the workspace
- Legacy reference inputs have been consolidated under `reference/`
- Archive jars and zip inputs have been consolidated under `archives/`
- Java compile cleanup is in progress and paused before the next build
- Markdown/docs refreshed to match the current local-only setup

## Next implementation phase

1. Finish source cleanup from the latest compile diagnostics
2. Keep the build in-folder and local-only
3. Resume compile and TeaVM output generation in the next session

