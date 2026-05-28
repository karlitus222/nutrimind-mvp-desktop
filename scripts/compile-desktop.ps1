$ErrorActionPreference = "Stop"
$root = Split-Path -Parent $PSScriptRoot
& (Join-Path $PSScriptRoot "download-deps.ps1")

$desktop = Join-Path $root "desktop"
$src = Join-Path $desktop "src\main\java"
$out = Join-Path $desktop "out"
$lib = Join-Path $root "lib"
New-Item -ItemType Directory -Force -Path $out | Out-Null

$sources = Get-ChildItem -Path $src -Recurse -Filter *.java | ForEach-Object { $_.FullName }
if ($sources.Count -eq 0) {
    throw "Nenhum arquivo Java encontrado em $src"
}

$classpath = "$lib\*"
javac -encoding UTF-8 -cp $classpath -d $out $sources
Write-Host "Desktop compilado em $out"

