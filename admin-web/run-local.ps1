$ErrorActionPreference = "Stop"

$webDir = Split-Path -Parent $MyInvocation.MyCommand.Path
$npmCmd = "D:\node.js\npm.cmd"

if (-not (Test-Path -LiteralPath $npmCmd)) {
    $npmCmd = (Get-Command npm.cmd -ErrorAction Stop).Source
}

Set-Location $webDir
Write-Host "[admin-web] 启动管理端 http://localhost:5174/admin/login"
& $npmCmd "run" "serve"
