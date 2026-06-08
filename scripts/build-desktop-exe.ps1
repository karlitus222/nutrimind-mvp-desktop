$ErrorActionPreference = "Stop"

$root = Split-Path -Parent $PSScriptRoot
$desktop = Join-Path $root "desktop"
$out = Join-Path $desktop "out"
$lib = Join-Path $root "lib"
$build = Join-Path $root "build\desktop-exe"
$input = Join-Path $build "input"
$dist = Join-Path $desktop "dist"
$appName = "Nutrimind"
$appImage = Join-Path $dist $appName

& (Join-Path $PSScriptRoot "compile-desktop.ps1")

foreach ($path in @($build, $dist)) {
    if (Test-Path -LiteralPath $path) {
        $resolvedRoot = [System.IO.Path]::GetFullPath($root)
        $resolvedPath = [System.IO.Path]::GetFullPath($path)
        if (-not $resolvedPath.StartsWith($resolvedRoot + [System.IO.Path]::DirectorySeparatorChar, [System.StringComparison]::OrdinalIgnoreCase)) {
            throw "Diretorio de build invalido: $resolvedPath"
        }
        Remove-Item -LiteralPath $resolvedPath -Recurse -Force
    }
}

New-Item -ItemType Directory -Force -Path $input | Out-Null

$jars = Get-ChildItem -Path $lib -Filter *.jar | Sort-Object Name
if ($jars.Count -eq 0) {
    throw "Nenhuma dependencia .jar encontrada em $lib"
}

$manifest = Join-Path $build "MANIFEST.MF"
$classPath = ($jars | ForEach-Object { $_.Name }) -join " "
@(
    "Manifest-Version: 1.0",
    "Main-Class: br.com.nutrimind.App",
    "Class-Path: $classPath",
    ""
) | Set-Content -Path $manifest -Encoding ascii

$appJar = Join-Path $input "nutrimind-desktop.jar"
jar cfm $appJar $manifest -C $out .
if ($LASTEXITCODE -ne 0) {
    throw "Falha ao gerar JAR do desktop."
}

foreach ($jar in $jars) {
    Copy-Item -LiteralPath $jar.FullName -Destination $input
}

jpackage `
    --type app-image `
    --name $appName `
    --dest $dist `
    --input $input `
    --main-jar "nutrimind-desktop.jar" `
    --main-class "br.com.nutrimind.App" `
    --java-options "-Dnutrimind.root=." `
    --java-options "-Dfile.encoding=UTF-8"

if ($LASTEXITCODE -ne 0) {
    throw "Falha ao gerar executavel com jpackage."
}

$schemaTarget = Join-Path $appImage "desktop\sql"
New-Item -ItemType Directory -Force -Path $schemaTarget | Out-Null
Copy-Item -LiteralPath (Join-Path $desktop "sql\schema.sql") -Destination (Join-Path $schemaTarget "schema.sql")

$runNotes = @"
Nutrimind Desktop

Como abrir:
1. Entre nesta pasta.
2. Dê dois cliques em Nutrimind.exe.

Credenciais:
- Nutricionista: nutri@nutrimind.com / 123456
- Administrador: admin@nutrimind.com / admin123

Observação:
O Java já está embutido neste pacote. Não é necessário usar PowerShell para abrir o sistema.
"@
$runNotes | Set-Content -Path (Join-Path $appImage "LEIA-ME.txt") -Encoding utf8

Write-Host "Executavel gerado em: $(Join-Path $appImage 'Nutrimind.exe')"
