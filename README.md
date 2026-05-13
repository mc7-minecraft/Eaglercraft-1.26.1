# Eaglercraft 1.26.1

This workspace contains the active source port of Minecraft 26.1.1 into an Eaglercraft-branded browser build. The goal is to keep everything local to this repository, recover and repair the Java sources in place, and produce the JavaScript and WASM browser entrypoints from the same tree.

The main port source lives under `port-src/minecraft-26.1.1/`. Runtime shims and browser-facing platform layers live under `runtime/`, while donor inputs and recovered reference material are kept in `reference/` and `archives/`.

## What this repo is for

- Recovering and fixing the 26.1.1 Java sources so they compile cleanly in this workspace
- Keeping the browser port branded as Eaglercraft 26.1.1
- Building the Java-side output used by the TeaVM and browser runtime pipeline
- Syncing runtime bridge code between the base browser layer and the LWJGL3-oriented layer

## Repository layout

- `port-src/minecraft-26.1.1/` - active source tree for the port
- `runtime/eagler-base/` - shared browser runtime bridge files
- `runtime/eagler-lwjgl3/` - LWJGL3-oriented bridge files
- `scripts/` - build, sync, and recovery scripts
- `reference/` - donor runtime references and comparison material
- `archives/` - archived jars and other import inputs
- `build/` - generated build output
- `logs/` - build and recovery logs
- `bin/main/` - prebuilt assets and manifest files used by the current workflow

## Build and validation

From the repository root, the main compile check is:

- `.\gradlew.bat compileJava --console=plain --no-daemon`

Common workflow scripts:

- `powershell -ExecutionPolicy Bypass -File scripts/build_eaglercraft.ps1`
- `powershell -ExecutionPolicy Bypass -File scripts/compile_eaglercraft_client.ps1`
- `powershell -ExecutionPolicy Bypass -File scripts/recover_sources_from_jar.ps1`

## Current status

- Source recovery is already in place for the current port tree.
- Compile cleanup is still ongoing in `port-src/minecraft-26.1.1/`.
- Generated output is treated as build output, not as the place to fix source issues.
- The browser launch files are `eaglercraft_26-1-1_javascript.html` and `eaglercraft_26-1-1_wasm.html`.

## Browser targets

- `eaglercraft_26-1-1_javascript.html`
- `eaglercraft_26-1-1_wasm.html`

## Notes

- The workspace is intentionally local-only.
- The port keeps Eaglercraft branding throughout the produced files and runtime naming.
- If you are continuing the port, start with `compileJava`, fix the next source-level compiler front, and keep changes inside `port-src/minecraft-26.1.1/` unless a runtime bridge also needs adjustment.
