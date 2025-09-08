# AI助手工具

基于Java的AI对话工具，支持多种AI服务和语音识别，运行在系统托盘。

## 核心功能

- **多AI服务**: 支持OpenAI、Claude、通用API
- **语音识别**: 支持Azure、Google、百度语音识别
- **系统托盘**: 最小化运行，随时可用
- **现代界面**: 简洁美观的聊天界面

## 系统要求

- Java 11+
- Windows 10/11
- 麦克风设备（语音功能）

## 快速开始

### 编译运行
```bash
# Windows
build.bat
run.bat

# 其他系统
mvn clean package
java -jar target/ai-quiz-tool-1.0.0.jar
```

### 首次配置
1. 右键系统托盘图标 → 设置
2. 配置API Key和服务地址
3. 保存配置

## 使用方法

- **打开对话**: 点击托盘图标
- **语音输入**: 点击麦克风按钮
- **文字输入**: 直接输入后回车

## 技术栈

- Java 11, Swing, FlatLaf
- OkHttp, Jackson, Logback

## 作者

**Tang En Dong**

反馈问题: jiant1827@gmail.com

## 许可证

MIT License