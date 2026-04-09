$ErrorActionPreference = "Stop"

$EaglercraftRoot = Split-Path -Parent $PSScriptRoot
$repoRoot = Split-Path -Parent $EaglercraftRoot
$optimized = Join-Path $repoRoot "26.1.1 - Optimised"
$portSrc = Join-Path $EaglercraftRoot "port-src\minecraft-26.1.1"

if (!(Test-Path $optimized)) {
    throw "Missing source tree: $optimized"
}

New-Item -ItemType Directory -Force -Path $portSrc | Out-Null

$null = robocopy $optimized $portSrc /E /NFL /NDL /NJH /NJS /NP /XF *.class
if ($LASTEXITCODE -gt 7) {
    throw "robocopy failed with exit code $LASTEXITCODE"
}

Write-Host "Port workspace seeded at: $portSrc"
