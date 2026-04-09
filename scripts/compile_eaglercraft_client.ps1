$ErrorActionPreference = "Stop"

$eaglercraftRoot = Split-Path -Parent $PSScriptRoot

Write-Host "[1/3] Preparing Eaglercraft build scaffold..."
& (Join-Path $PSScriptRoot "build_eaglercraft.ps1")

Write-Host "[2/3] Using local 26.1.1 Eaglercraft workspace assets..."
if (!(Test-Path (Join-Path $eaglercraftRoot "build/javascript/classes.js"))) {
    throw "Missing local JS payload: build/javascript/classes.js"
}
if (!(Test-Path (Join-Path $eaglercraftRoot "build/wasm/bootstrap.js"))) {
    throw "Missing local WASM payload: build/wasm/bootstrap.js"
}

Write-Host "[3/3] Verifying local runtime sync..."
& (Join-Path $PSScriptRoot "sync_lwjgl3_runtime.ps1")

Write-Host "Compile and sync finished."
Write-Host "Launch entries:"
Write-Host " - eaglercraft_26-1-1_javascript.html"
Write-Host " - eaglercraft_26-1-1_wasm.html"
