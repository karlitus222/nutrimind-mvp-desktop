param(
    [string]$LibDir = "lib"
)

$ErrorActionPreference = "Stop"
$root = Split-Path -Parent $PSScriptRoot
$target = Join-Path $root $LibDir
New-Item -ItemType Directory -Force -Path $target | Out-Null

$deps = @(
    @{
        Name = "sqlite-jdbc-3.53.0.0.jar"
        Url = "https://repo.maven.apache.org/maven2/org/xerial/sqlite-jdbc/3.53.0.0/sqlite-jdbc-3.53.0.0.jar"
    },
    @{
        Name = "slf4j-api-2.0.17.jar"
        Url = "https://repo.maven.apache.org/maven2/org/slf4j/slf4j-api/2.0.17/slf4j-api-2.0.17.jar"
    },
    @{
        Name = "slf4j-simple-2.0.17.jar"
        Url = "https://repo.maven.apache.org/maven2/org/slf4j/slf4j-simple/2.0.17/slf4j-simple-2.0.17.jar"
    }
)

foreach ($dep in $deps) {
    $file = Join-Path $target $dep.Name
    if (-not (Test-Path $file)) {
        Write-Host "Baixando $($dep.Name)..."
        Invoke-WebRequest -Uri $dep.Url -OutFile $file
    }
}

Write-Host "Dependencias prontas em $target"

