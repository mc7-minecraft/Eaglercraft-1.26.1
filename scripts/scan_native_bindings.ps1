$ErrorActionPreference = "Stop"

$EaglercraftRoot = Split-Path -Parent $PSScriptRoot
$repoRoot = Split-Path -Parent $EaglercraftRoot
$optimized = Join-Path $repoRoot "26.1.1 - Optimised"
$outDir = Join-Path $EaglercraftRoot "build"
$outFile = Join-Path $outDir "native_bindings_report.txt"

if (!(Test-Path $optimized)) {
    throw "Source folder missing: $optimized"
}

New-Item -ItemType Directory -Force -Path $outDir | Out-Null

$patterns = @(
    "Vulkan",
    "org.lwjgl.vulkan",
    "GLFW",
    "org.lwjgl.glfw",
    "OpenAL",
    "AL10",
    "org.lwjgl.openal",
    "Keyboard",
    "Mouse",
    "InputConstants"
)

$javaFiles = Get-ChildItem -Path $optimized -Recurse -Filter *.java -File

$results = @()
foreach ($p in $patterns) {
    $matches = $javaFiles | Select-String -Pattern $p -SimpleMatch -ErrorAction SilentlyContinue
    foreach ($m in $matches) {
        $results += "[$p] $($m.Path):$($m.LineNumber): $($m.Line.Trim())"
    }
}

if ($results.Count -eq 0) {
    "No matching native binding markers were found." | Set-Content -Path $outFile -Encoding UTF8
} else {
    $results | Sort-Object | Set-Content -Path $outFile -Encoding UTF8
}

Write-Host "Wrote native binding report: $outFile"

