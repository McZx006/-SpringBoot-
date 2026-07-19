@echo off
setlocal
chcp 65001 >nul
cd /d "%~dp0"

start "online-teaching-backend" cmd /k call "%~dp0backend\run-local.cmd"
start "online-teaching-student" cmd /k call "%~dp0student-web\run-local.cmd"
start "online-teaching-admin" cmd /k call "%~dp0admin-web\run-local.cmd"

echo 已分别打开后端、学员端、管理端启动窗口。
