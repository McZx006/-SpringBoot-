@echo off
setlocal
chcp 65001 >nul
cd /d "%~dp0"

set "NPM_BIN=npm.cmd"
if exist "D:\node.js\npm.cmd" (
  set "NPM_BIN=D:\node.js\npm.cmd"
)

echo [student-web] 启动学员端 http://localhost:5173
call "%NPM_BIN%" run dev
