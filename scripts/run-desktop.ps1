$ErrorActionPreference = "Stop"
$root = Split-Path -Parent $PSScriptRoot
& (Join-Path $PSScriptRoot "compile-desktop.ps1")
$cp = @(
    (Join-Path $root "desktop\out"),
    (Join-Path $root "lib\*")
) -join ";"
Push-Location $root
try {
    java "-Dnutrimind.root=$root" -cp $cp br.com.nutrimind.App
    if ($LASTEXITCODE -ne 0) {
        throw "Falha ao executar o desktop. Codigo de saida: $LASTEXITCODE"
    }
}
finally {
    Pop-Location
}
