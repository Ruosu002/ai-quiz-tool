@echo off
chcp 65001 >nul
echo 启动AI助手工具...

REM 检查JAR文件
if not exist "target\ai-quiz-tool-1.0.0.jar" (
    echo 错误: 请先运行 build.bat 编译项目
    pause
    exit /b 1
)

REM JVM参数
set JAVA_OPTS=-Xmx512m -Djava.awt.headless=false -Dfile.encoding=UTF-8 -Duser.language=zh -Duser.country=CN

echo 程序将运行在系统托盘

REM 启动程序
java %JAVA_OPTS% -jar target\ai-quiz-tool-1.0.0.jar

echo 程序已退出
pause