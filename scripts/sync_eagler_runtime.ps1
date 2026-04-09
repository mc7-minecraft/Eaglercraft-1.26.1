$ErrorActionPreference = "Stop"

$EaglercraftRoot = Split-Path -Parent $PSScriptRoot
$runtimeTarget = Join-Path $EaglercraftRoot "runtime\eagler-base"

New-Item -ItemType Directory -Force -Path $runtimeTarget | Out-Null

$required = @(
    "platformOpenGL.js",
    "platformAudio.js",
    "platformInput.js",
    "platformRuntime.js",
    "eagruntime_entrypoint.js"
)

foreach ($name in $required) {
    $path = Join-Path $runtimeTarget $name
    if (!(Test-Path $path)) {
        Write-Warning "Missing local runtime file: $path"
    }
}

Write-Host "Using local Eagler runtime base files at: $runtimeTarget"
