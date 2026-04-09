$ErrorActionPreference = "Stop"

$EaglercraftRoot = Split-Path -Parent $PSScriptRoot
$repoRoot = Split-Path -Parent $EaglercraftRoot
$logPath = Join-Path $EaglercraftRoot "logs\build_$(Get-Date -Format 'yyyyMMdd_HHmmss').log"

Start-Transcript -Path $logPath | Out-Null

try {
    Write-Host "[1/5] Syncing runtime bridge from Eaglercraft..."
    & (Join-Path $PSScriptRoot "sync_eagler_runtime.ps1")

    Write-Host "[2/5] Preparing output folders..."
    New-Item -ItemType Directory -Force -Path (Join-Path $EaglercraftRoot "build\javascript") | Out-Null
    New-Item -ItemType Directory -Force -Path (Join-Path $EaglercraftRoot "build\wasm") | Out-Null

    Write-Host "[3/5] Checking optimized source tree..."
    $optimizedPath = Join-Path $repoRoot "26.1.1 - Optimised"
    if (!(Test-Path $optimizedPath)) {
        throw "Optimized source folder missing: $optimizedPath"
    }

    Write-Host "[4/5] Creating conversion TODO manifest for 26.1.1 runtime port..."
    $manifest = @{
        generatedAt = (Get-Date).ToString("o")
        source = "26.1.1 - Optimised"
        targets = @(
            "eaglercraft_26-1-1_javascript.html",
            "eaglercraft_26-1-1_wasm.html"
        )
        requiredRuntimePorts = @(
            "GPU bridge: Vulkan-style renderer abstraction -> WebGPU adapter with WebGL2 fallback",
            "Audio bridge: desktop mixer -> WebAudio graph",
            "Input bridge: GLFW/LWJGL controls -> browser keyboard/mouse/touch/gamepad",
            "Filesystem/storage bridge: local files -> IndexedDB"
        )
    } | ConvertTo-Json -Depth 5
    Set-Content -Path (Join-Path $EaglercraftRoot "build\conversion_manifest.json") -Value $manifest -Encoding UTF8

    Write-Host "[5/5] Build scaffold ready."
    Write-Host "Next: integrate 26.1.1 classes into TeaVM-compatible workspace and compile JS/Wasm payloads."
}
finally {
    Stop-Transcript | Out-Null
    Write-Host "Log written to: $logPath"
}
