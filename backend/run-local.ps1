$ErrorActionPreference = "Stop"

$backendDir = Split-Path -Parent $MyInvocation.MyCommand.Path
$jarPath = Join-Path $backendDir "target\online-teaching-backend-1.0.0.jar"
$mavenCmd = "D:\IDEA\IntelliJ IDEA 2024.3.5\plugins\maven\lib\maven3\bin\mvn.cmd"
$javaExe = "C:\Program Files\Microsoft\jdk-17.0.12.7-hotspot\bin\java.exe"

if (-not (Test-Path -LiteralPath $javaExe)) {
    $javaExe = (Get-Command java -ErrorAction Stop).Source
}

Set-Location $backendDir

if (-not (Test-Path -LiteralPath $jarPath)) {
    Write-Host "[backend] 未发现打包产物，先执行 Maven 打包..."
    & $mavenCmd "-Dmaven.repo.local=D:\桌面文件\实训\.m2repo" "-DskipTests" "package"
    if ($LASTEXITCODE -ne 0) {
        throw "Maven 打包失败"
    }
}

Write-Host "[backend] 启动后端服务 http://localhost:8080"
& $javaExe "-jar" $jarPath
