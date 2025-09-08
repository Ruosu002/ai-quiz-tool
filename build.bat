@echo off
echo 编译AI助手工具...

REM 检查Maven
mvn -version >nul 2>&1
if %errorlevel% neq 0 (
    echo 错误: 请安装Maven
    pause
    exit /b 1
)

REM 编译项目
mvn clean package

if %errorlevel% equ 0 (
    echo 编译完成！运行 run.bat 启动程序
) else (
    echo 编译失败
)

pause