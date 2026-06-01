$ErrorActionPreference = "Stop"
$root = Split-Path -Parent $PSScriptRoot
& (Join-Path $PSScriptRoot "download-deps.ps1")

$desktop = Join-Path $root "desktop"
$src = Join-Path $desktop "src\main\java"
$out = Join-Path $desktop "out"
$lib = Join-Path $root "lib"

if (Test-Path -LiteralPath $out) {
    $resolvedDesktop = [System.IO.Path]::GetFullPath($desktop)
    $resolvedOut = [System.IO.Path]::GetFullPath($out)
    if (-not $resolvedOut.StartsWith($resolvedDesktop + [System.IO.Path]::DirectorySeparatorChar, [System.StringComparison]::OrdinalIgnoreCase)) {
        throw "Diretorio de saida invalido: $resolvedOut"
    }
    Remove-Item -LiteralPath $resolvedOut -Recurse -Force
}
New-Item -ItemType Directory -Force -Path $out | Out-Null

$sources = Get-ChildItem -Path $src -Recurse -Filter *.java | ForEach-Object { $_.FullName }
if ($sources.Count -eq 0) {
    throw "Nenhum arquivo Java encontrado em $src"
}

$classpath = "$lib\*"
javac -encoding UTF-8 -cp $classpath -d $out $sources
if ($LASTEXITCODE -ne 0) {
    throw "Falha ao compilar desktop com javac. Codigo de saida: $LASTEXITCODE"
}
Write-Host "Desktop compilado em $out"
