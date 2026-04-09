param(
    [Parameter(Mandatory = $true)]
    [string]$JarPath,
    [Parameter(Mandatory = $true)]
    [string]$PackagePath,
    [Parameter(Mandatory = $true)]
    [string]$OutputRoot
)

$ErrorActionPreference = "Stop"

$repoRoot = Split-Path -Parent (Split-Path -Parent $PSScriptRoot)
$vineflower = Join-Path $repoRoot "26.1.1 - Optimised\vineflower.jar"

if (!(Test-Path $JarPath)) {
    throw "Jar not found: $JarPath"
}
if (!(Test-Path $vineflower)) {
    throw "vineflower.jar not found: $vineflower"
}

$tempExtract = Join-Path $env:TEMP ("Eaglercraft_extract_" + [guid]::NewGuid().ToString("N"))
$tempDecompile = Join-Path $env:TEMP ("Eaglercraft_decomp_" + [guid]::NewGuid().ToString("N"))
New-Item -ItemType Directory -Force -Path $tempExtract | Out-Null
New-Item -ItemType Directory -Force -Path $tempDecompile | Out-Null

try {
    Add-Type -AssemblyName System.IO.Compression.FileSystem
    [System.IO.Compression.ZipFile]::ExtractToDirectory($JarPath, $tempExtract)
    $pkgFolder = Join-Path $tempExtract $PackagePath
    if (!(Test-Path $pkgFolder)) {
        throw "Package path '$PackagePath' not found in jar"
    }

    & java -jar $vineflower $pkgFolder $tempDecompile | Out-Null

    $decompPkgFolder = Join-Path $tempDecompile (Split-Path $PackagePath -Leaf)
    if (!(Test-Path $decompPkgFolder)) {
        $decompPkgFolder = $tempDecompile
    }

    $copied = 0
    Get-ChildItem -Path $decompPkgFolder -Recurse -Filter *.java | ForEach-Object {
        $rel = $_.FullName.Substring($decompPkgFolder.Length).TrimStart([char]92, [char]47)
        $target = Join-Path $OutputRoot $rel
        $targetDir = Split-Path -Parent $target
        if (!(Test-Path $targetDir)) {
            New-Item -ItemType Directory -Force -Path $targetDir | Out-Null
        }
        if (!(Test-Path $target)) {
            Copy-Item -Path $_.FullName -Destination $target
            $copied++
        }
    }

    Write-Host "Recovered $copied Java files from $JarPath package $PackagePath"
}
finally {
    if (Test-Path $tempExtract) { Remove-Item -Recurse -Force $tempExtract }
    if (Test-Path $tempDecompile) { Remove-Item -Recurse -Force $tempDecompile }
}

