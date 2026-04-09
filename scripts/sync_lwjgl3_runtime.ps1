$ErrorActionPreference = "Stop"

$EaglercraftRoot = Split-Path -Parent $PSScriptRoot

$required = @(
    "runtime/eagler-lwjgl3/platformOpenGL.js",
    "runtime/eagler-lwjgl3/platformAudio.js",
    "runtime/eagler-lwjgl3/platformInput.js",
    "runtime/eagler-lwjgl3/platformRuntime.js",
    "build/javascript/classes.js",
    "build/javascript/assets.epk",
    "build/wasm/bootstrap.js",
    "build/wasm/assets.epw"
)

foreach ($rel in $required) {
    $path = Join-Path $EaglercraftRoot $rel
    if (Test-Path $path) {
        Write-Host "Local asset OK: $rel"
    } else {
        Write-Warning "Missing local asset: $rel"
    }
}

