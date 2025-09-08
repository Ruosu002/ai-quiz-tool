# 安装指南

## 环境准备

1. **Java 11+** - https://www.oracle.com/java/technologies/downloads/
2. **Maven 3.6+** - https://maven.apache.org/download.cgi

验证安装：
```bash
java -version
mvn -version
```

## 安装步骤

### Windows用户（推荐）
```bash
build.bat     # 编译
run.bat       # 运行
```

### 其他系统
```bash
mvn clean package
java -jar target/ai-quiz-tool-1.0.0.jar
```

### IDE运行
1. 导入Maven项目
2. 运行`AIQuizToolApplication`主类

## 首次配置

1. 程序启动后查看系统托盘
2. 右键托盘图标 → 设置
3. 配置AI服务：
   - OpenAI API Key
   - API URL（可用默认值）
   - 模型选择
4. 保存配置

## 常见问题

**托盘图标不显示？**
- 检查Java版本和系统托盘支持

**语音识别不工作？**
- 检查麦克风权限和设备

**AI无响应？**
- 检查API Key和网络连接

## 日志位置

Windows: `%USERPROFILE%\.aiquiztool\logs\aiquiztool.log`

## 作者

**Tang En Dong**

问题反馈: jiant1827@gmail.com