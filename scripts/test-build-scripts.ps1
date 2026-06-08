$ErrorActionPreference = "Stop"
$root = Split-Path -Parent $PSScriptRoot
$fixtureBin = Join-Path $PSScriptRoot "fixtures\failing-bin"
$compileScript = Join-Path $PSScriptRoot "compile-desktop.ps1"
$originalPath = $env:PATH

try {
    $env:PATH = "$fixtureBin;$originalPath"
    $previousErrorActionPreference = $ErrorActionPreference
    $ErrorActionPreference = "Continue"
    $output = & powershell -NoProfile -ExecutionPolicy Bypass -File $compileScript 2>&1
    $exitCode = $LASTEXITCODE
    $ErrorActionPreference = $previousErrorActionPreference
}
finally {
    $ErrorActionPreference = "Stop"
    $env:PATH = $originalPath
}

if ($exitCode -eq 0) {
    throw "compile-desktop.ps1 deveria falhar quando javac retorna erro."
}

if ($output -match "Desktop compilado") {
    throw "compile-desktop.ps1 nao deve imprimir sucesso quando javac falha."
}

Write-Host "BuildScriptTest OK"
