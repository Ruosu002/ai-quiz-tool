# 系统架构说明

## 整体架构

AI助手工具采用分层架构设计，主要包含以下几个层次：

```
┌─────────────────────────────────────────────────────────────┐
│                    用户界面层 (UI Layer)                      │
├─────────────────────────────────────────────────────────────┤
│  SystemTrayManager  │  ChatWindow  │  ConfigWindow          │
├─────────────────────────────────────────────────────────────┤
│                    业务逻辑层 (Service Layer)                 │
├─────────────────────────────────────────────────────────────┤
│  AIService  │  SpeechRecognitionService                     │
├─────────────────────────────────────────────────────────────┤
│                    配置管理层 (Config Layer)                  │
├─────────────────────────────────────────────────────────────┤
│                    ConfigManager                            │
├─────────────────────────────────────────────────────────────┤
│                    外部服务层 (External Services)             │
├─────────────────────────────────────────────────────────────┤
│  OpenAI API  │  Google Speech API  │  System Audio          │
└─────────────────────────────────────────────────────────────┘
```

## 核心组件

### 1. 主应用程序 (AIQuizToolApplication)

**职责**: 应用程序入口点，初始化系统外观和启动系统托盘管理器

**主要功能**:
- 设置系统外观主题
- 初始化配置管理器
- 启动系统托盘管理器
- 异常处理和错误提示

### 2. 系统托盘管理器 (SystemTrayManager)

**职责**: 管理系统托盘图标和用户交互

**主要功能**:
- 创建和管理系统托盘图标
- 处理鼠标点击事件
- 显示弹出菜单
- 管理窗口显示/隐藏
- 显示系统通知

**关键方法**:
- `initialize()`: 初始化系统托盘
- `createTrayIcon()`: 创建托盘图标
- `toggleChatWindow()`: 切换聊天窗口显示状态
- `showNotification()`: 显示系统通知

### 3. 聊天窗口 (ChatWindow)

**职责**: 提供用户与AI交互的界面

**主要功能**:
- 显示对话历史
- 提供文字输入功能
- 集成语音输入功能
- 显示AI回复结果
- 管理录音状态

**关键方法**:
- `startRecording()`: 开始语音录音
- `stopRecording()`: 停止语音录音
- `sendMessage()`: 发送消息到AI服务
- `appendMessage()`: 添加消息到聊天区域

### 4. 配置窗口 (ConfigWindow)

**职责**: 提供配置管理界面

**主要功能**:
- 显示和编辑配置项
- 保存配置到文件
- 测试API连接
- 验证配置有效性

**配置项**:
- OpenAI API配置
- AI参数设置
- 语音识别配置
- 界面设置

### 5. AI服务 (AIService)

**职责**: 处理与AI模型的交互

**主要功能**:
- 调用OpenAI API
- 构建请求参数
- 解析API响应
- 错误处理和重试

**关键方法**:
- `getResponse()`: 获取AI回复
- `callOpenAIAPI()`: 调用OpenAI API
- `buildRequestBody()`: 构建请求体
- `parseResponse()`: 解析API响应

### 6. 语音识别服务 (SpeechRecognitionService)

**职责**: 处理语音输入和识别

**主要功能**:
- 管理音频录制
- 调用语音识别API
- 处理音频格式转换
- 管理录音状态

**关键方法**:
- `startRecording()`: 开始录音
- `stopRecording()`: 停止录音
- `performSpeechRecognition()`: 执行语音识别
- `checkMicrophonePermission()`: 检查麦克风权限

### 7. 配置管理器 (ConfigManager)

**职责**: 管理应用程序配置

**主要功能**:
- 加载和保存配置
- 提供配置访问接口
- 管理配置文件
- 提供默认配置

**配置分类**:
- OpenAI API配置
- 语音识别配置
- 界面配置
- 日志配置

## 数据流

### 语音输入流程

```
用户点击录音按钮
    ↓
ChatWindow.startRecording()
    ↓
SpeechRecognitionService.startRecording()
    ↓
开始音频录制
    ↓
用户停止录音
    ↓
SpeechRecognitionService.stopRecording()
    ↓
调用语音识别API
    ↓
返回识别结果
    ↓
ChatWindow显示识别文本
```

### AI对话流程

```
用户输入消息
    ↓
ChatWindow.sendMessage()
    ↓
AIService.getResponse()
    ↓
构建API请求
    ↓
调用OpenAI API
    ↓
解析API响应
    ↓
返回AI回复
    ↓
ChatWindow显示回复
```

## 技术选型

### 核心技术

- **Java 11**: 主要开发语言
- **Swing**: 用户界面框架
- **FlatLaf**: 现代UI主题

### 第三方库

- **OkHttp**: HTTP客户端，用于API调用
- **Jackson**: JSON处理库
- **Logback**: 日志框架
- **Typesafe Config**: 配置管理
- **Google Cloud Speech**: 语音识别服务

### 设计模式

1. **单例模式**: ConfigManager使用单例模式确保配置一致性
2. **观察者模式**: 语音识别使用回调接口处理异步结果
3. **工厂模式**: 系统托盘图标创建
4. **策略模式**: 不同AI服务的调用策略

## 扩展性设计

### 添加新的AI服务

1. 在`AIService`中添加新的API调用方法
2. 在`ConfigManager`中添加相关配置项
3. 在`ConfigWindow`中添加配置界面

### 添加新的语音识别服务

1. 在`SpeechRecognitionService`中集成新的API
2. 更新配置管理以支持新的服务
3. 测试不同语言的识别效果

### 添加新的界面主题

1. 在`AIQuizToolApplication`中添加新的主题
2. 在`ConfigManager`中添加主题配置
3. 在`ConfigWindow`中添加主题选择

## 性能优化

### 异步处理

- 语音识别使用异步处理避免界面阻塞
- AI API调用使用后台线程
- 音频录制使用独立线程

### 资源管理

- 及时释放音频资源
- 管理HTTP连接池
- 清理临时文件

### 内存优化

- 限制聊天历史长度
- 及时清理音频缓冲区
- 优化图标资源使用

## 安全性考虑

### API密钥保护

- 配置文件加密存储
- 不在日志中输出敏感信息
- 提供密钥测试功能

### 网络安全

- 使用HTTPS连接
- 验证SSL证书
- 处理网络异常

### 用户隐私

- 本地处理音频数据
- 不存储用户对话内容
- 提供数据清理功能




