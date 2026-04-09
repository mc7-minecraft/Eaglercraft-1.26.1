# Eaglercraft 26.1.1

This folder hosts the active in-repo conversion pipeline for turning Minecraft 26.1.1 (Optimised) into a browser build using a local TeaVM setup and the Eaglercraft runtime technique.

## Current Outputs

- eaglercraft_26-1-1_javascript.html
- eaglercraft_26-1-1_wasm.html

## What is already wired

- Local runtime bridge files copied into this workspace
- Browser bootstrap with dynamic canvas and resize handling
- GPU adapter choosing WebGPU first, with WebGL2 fallback
- Build manifest generation for JS and WASM targets
- Java-side Eaglercraft runtime environment abstraction for browser mode
- Vulkan backend routing through Eaglercraft bridge hooks
- Browser-aware GLX and event pump path
- Browser input/audio bridge stubs connected into runtime paths
- Missing `com.mojang.blaze3d` Java sources recovered from `26.1.1 - Original` class files
- Full non-inner class source recovery from `26.1.1 - Original` into `port-src/minecraft-26.1.1/`
- Direct Eagler-style launch flow in both HTML entrypoints
- WSS relay/server wiring aligned to Eaglercraft patterns

## Run the process

From this folder:

- powershell -ExecutionPolicy Bypass -File scripts/build_eaglercraft.ps1

This starts the conversion scaffold process and writes logs into logs/.

To compile donor JS and WASM payloads and sync them into this workspace in one step:

- powershell -ExecutionPolicy Bypass -File scripts/compile_eaglercraft_client.ps1

## Remaining heavy lift

The final fully working port requires a full Java-side compatibility layer for 26.1.1 internals and rendering calls so TeaVM can emit valid browser payloads.

Key workstreams:

- Replace desktop-native GPU path calls with Eaglercraft platform abstractions
- Bind audio engine to WebAudio nodes for streaming and positional audio
- Bind keyboard/mouse/touch/gamepad controls to browser input APIs
- Build and link JavaScript and WASM targets from converted Java sources

## Current development state

- The local Gradle and TeaVM project lives entirely inside this folder.
- External `eaglercraftx-*` donor folders were removed from the workspace.
- Java compile cleanup is in progress and paused for the day.
- No new build should be started until the next session.

## Source Recovery Notes

- Recovered decompiled source cache: `build/decompiled_blaze3d_all/`
- Full recovered source cache: `build/decompiled_original_all/`
- Imported missing classes into: `port-src/minecraft-26.1.1/com/mojang/blaze3d/`
- Imported all missing classes into: `port-src/minecraft-26.1.1/`
- Verification status: all non-inner classes from `26.1.1 - Original` now have matching `.java` files in the port source tree
- Extra fallback script (from `.minecraft` jars): `scripts/recover_sources_from_jar.ps1`
- Organized reference inputs live under `reference/`
- Archive jars and zip inputs live under `archives/`

## Naming

All produced files and runtime naming use Eaglercraft branding.