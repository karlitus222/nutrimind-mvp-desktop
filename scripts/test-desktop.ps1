$ErrorActionPreference = "Stop"
$root = Split-Path -Parent $PSScriptRoot
& (Join-Path $PSScriptRoot "compile-desktop.ps1")

$desktop = Join-Path $root "desktop"
$testSrc = Join-Path $desktop "src\test\java"
$testOut = Join-Path $desktop "out-test"
New-Item -ItemType Directory -Force -Path $testOut | Out-Null

$sources = Get-ChildItem -Path $testSrc -Recurse -Filter *.java | ForEach-Object { $_.FullName }
if ($sources.Count -gt 0) {
    $cp = @(
        (Join-Path $desktop "out"),
        (Join-Path $root "lib\*")
    ) -join ";"
    javac -encoding UTF-8 -cp $cp -d $testOut $sources
    Push-Location $root
    try {
        java "-Dnutrimind.root=$root" -cp "$cp;$testOut" br.com.nutrimind.SmokeTest
    }
    finally {
        Pop-Location
    }
}

Write-Host "Testes desktop concluidos."
