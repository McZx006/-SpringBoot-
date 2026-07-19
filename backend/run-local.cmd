@echo off
setlocal
chcp 65001 >nul

set "JAVA_BIN=java"
if exist "C:\Program Files\Microsoft\jdk-17.0.12.7-hotspot\bin\java.exe" (
  set "JAVA_BIN=C:\Program Files\Microsoft\jdk-17.0.12.7-hotspot\bin\java.exe"
)

cd /d "%~dp0"
if not exist "target\online-teaching-backend-1.0.0.jar" (
  echo [backend] 未发现打包产物，先执行 Maven 打包...
  call "D:\IDEA\IntelliJ IDEA 2024.3.5\plugins\maven\lib\maven3\bin\mvn.cmd" -Dmaven.repo.local=D:\桌面文件\实训\.m2repo -DskipTests package
  if errorlevel 1 exit /b 1
)

echo [backend] 启动后端服务 http://localhost:8080
"%JAVA_BIN%" -jar "target\online-teaching-backend-1.0.0.jar"
