@echo off
setlocal
chcp 65001 >nul
cd /d "%~dp0"

set "NPM_BIN=npm.cmd"
if exist "D:\node.js\npm.cmd" (
  set "NPM_BIN=D:\node.js\npm.cmd"
)

echo [admin-web] 启动管理端 http://localhost:5174/admin/login
call "%NPM_BIN%" run serve
