# 安装指南

## 环境要求

### 必需软件

1. **Java 11 或更高版本**
   - 下载地址: https://www.oracle.com/java/technologies/downloads/
   - 或者使用OpenJDK: https://adoptium.net/
   - 安装后需要配置JAVA_HOME环境变量

2. **Apache Maven 3.6+**
   - 下载地址: https://maven.apache.org/download.cgi
   - 安装后需要配置MAVEN_HOME环境变量
   - 将Maven的bin目录添加到PATH环境变量

### 验证安装

打开命令提示符，运行以下命令验证安装：

```bash
java -version
mvn -version
```

## 编译和运行

### 方法一：使用批处理文件（推荐）

1. **编译项目**
   ```bash
   build.bat
   ```

2. **运行程序**
   ```bash
   run.bat
   ```

### 方法二：使用Maven命令

1. **编译项目**
   ```bash
   mvn clean package
   ```

2. **运行程序**
   ```bash
   java -jar target/ai-quiz-tool-1.0.0.jar
   ```

### 方法三：使用IDE

1. 使用IntelliJ IDEA或Eclipse导入项目
2. 等待Maven依赖下载完成
3. 运行`AIQuizToolApplication`类的main方法

## 首次配置

1. 程序启动后，会在系统托盘显示AI助手图标
2. 右键点击托盘图标，选择"设置"
3. 在设置窗口中配置：
   - OpenAI API Key
   - API URL（默认已填写）
   - 其他参数
4. 点击"保存"按钮

## 使用说明

### 基本操作

1. **打开对话窗口**
   - 左键点击系统托盘图标
   - 或右键点击选择"开始对话"

2. **语音输入**
   - 点击"🎤 语音输入"按钮
   - 开始说话
   - 再次点击停止录音

3. **文字输入**
   - 在输入框中直接输入文字
   - 按回车键或点击"发送"按钮

### 系统托盘菜单

- **开始对话**: 打开对话窗口
- **设置**: 打开配置窗口
- **退出**: 退出程序

## 故障排除

### 编译问题

1. **Maven命令不识别**
   - 检查Maven是否正确安装
   - 检查PATH环境变量是否包含Maven的bin目录

2. **Java版本不兼容**
   - 确保使用Java 11或更高版本
   - 检查JAVA_HOME环境变量

3. **依赖下载失败**
   - 检查网络连接
   - 尝试使用国内Maven镜像

### 运行问题

1. **系统托盘图标不显示**
   - 检查系统是否支持系统托盘
   - 确认Java版本兼容性

2. **语音识别不工作**
   - 检查麦克风权限
   - 确认音频设备正常工作

3. **AI回复失败**
   - 检查API Key是否正确
   - 确认网络连接正常

## 开发环境设置

### IntelliJ IDEA

1. 打开IntelliJ IDEA
2. 选择"Open"打开项目目录
3. 等待Maven依赖下载完成
4. 配置运行配置：
   - Main class: `com.aiquiztool.AIQuizToolApplication`
   - VM options: `-Djava.awt.headless=false`

### Eclipse

1. 打开Eclipse
2. 选择"Import" -> "Existing Maven Projects"
3. 选择项目目录
4. 等待Maven依赖下载完成
5. 右键项目 -> "Run As" -> "Java Application"

## 常见问题

### Q: 程序启动后没有反应？
A: 检查系统托盘区域，程序会最小化到系统托盘。

### Q: 语音识别功能不工作？
A: 确保麦克风权限已授予，并且音频设备正常工作。

### Q: AI回复很慢？
A: 检查网络连接，或者尝试更换API服务地址。

### Q: 如何更换AI模型？
A: 在设置窗口中修改"模型"字段，支持gpt-3.5-turbo、gpt-4等。

## 技术支持

如果遇到问题，请：

1. 查看日志文件：`%USERPROFILE%\.aiquiztool\logs\aiquiztool.log`
2. 检查系统要求是否满足
3. 提交Issue到项目仓库




