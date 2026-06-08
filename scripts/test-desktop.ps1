$ErrorActionPreference = "Stop"
$root = Split-Path -Parent $PSScriptRoot
& (Join-Path $PSScriptRoot "test-build-scripts.ps1")
& (Join-Path $PSScriptRoot "compile-desktop.ps1")

$desktop = Join-Path $root "desktop"
$testSrc = Join-Path $desktop "src\test\java"
$testOut = Join-Path $desktop "out-test"
$testRuntime = Join-Path $desktop "out-test-runtime"

if (Test-Path -LiteralPath $testOut) {
    $resolvedDesktop = [System.IO.Path]::GetFullPath($desktop)
    $resolvedTestOut = [System.IO.Path]::GetFullPath($testOut)
    if (-not $resolvedTestOut.StartsWith($resolvedDesktop + [System.IO.Path]::DirectorySeparatorChar, [System.StringComparison]::OrdinalIgnoreCase)) {
        throw "Diretorio de testes invalido: $resolvedTestOut"
    }
    Remove-Item -LiteralPath $resolvedTestOut -Recurse -Force
}
New-Item -ItemType Directory -Force -Path $testOut | Out-Null

if (Test-Path -LiteralPath $testRuntime) {
    $resolvedDesktop = [System.IO.Path]::GetFullPath($desktop)
    $resolvedTestRuntime = [System.IO.Path]::GetFullPath($testRuntime)
    if (-not $resolvedTestRuntime.StartsWith($resolvedDesktop + [System.IO.Path]::DirectorySeparatorChar, [System.StringComparison]::OrdinalIgnoreCase)) {
        throw "Diretorio temporario invalido: $resolvedTestRuntime"
    }
    Remove-Item -LiteralPath $resolvedTestRuntime -Recurse -Force
}
New-Item -ItemType Directory -Force -Path (Join-Path $testRuntime "desktop\sql") | Out-Null
Copy-Item -LiteralPath (Join-Path $desktop "sql\schema.sql") -Destination (Join-Path $testRuntime "desktop\sql\schema.sql")

$sources = Get-ChildItem -Path $testSrc -Recurse -Filter *.java | ForEach-Object { $_.FullName }
if ($sources.Count -gt 0) {
    $cp = @(
        (Join-Path $desktop "out"),
        (Join-Path $root "lib\*")
    ) -join ";"
    javac -encoding UTF-8 -cp $cp -d $testOut $sources
    if ($LASTEXITCODE -ne 0) {
        throw "Falha ao compilar testes com javac. Codigo de saida: $LASTEXITCODE"
    }
    Push-Location $testRuntime
    try {
        java "-Dnutrimind.root=$testRuntime" -cp "$cp;$testOut" br.com.nutrimind.SmokeTest
        if ($LASTEXITCODE -ne 0) {
            throw "Falha ao executar SmokeTest. Codigo de saida: $LASTEXITCODE"
        }
    }
    finally {
        Pop-Location
        if (Test-Path -LiteralPath $testRuntime) {
            Remove-Item -LiteralPath $testRuntime -Recurse -Force
        }
    }
}

Write-Host "Testes desktop concluidos."
