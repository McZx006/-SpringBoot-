$ErrorActionPreference = "Stop"

$webDir = Split-Path -Parent $MyInvocation.MyCommand.Path
$npmCmd = "D:\node.js\npm.cmd"

if (-not (Test-Path -LiteralPath $npmCmd)) {
    $npmCmd = (Get-Command npm.cmd -ErrorAction Stop).Source
}

Set-Location $webDir
Write-Host "[student-web] 启动学员端 http://localhost:5173"
& $npmCmd "run" "dev"
