# 故障排除指南

## 常见问题及解决方案

### 1. JNI错误和签名验证问题

**错误信息**：
```
Error: A JNI error has occurred, please check your installation and try again
Exception in thread "main" java.lang.SecurityException: Invalid signature file digest for Manifest main attributes
```

**原因**：
这是由于JAR文件中的签名验证问题导致的。当使用Maven shade插件打包时，某些依赖库的签名文件与合并后的JAR文件不匹配。

**解决方案**：

#### 方案1：使用修复后的启动脚本
```bash
run-simple.bat
```

#### 方案2：使用JVM参数绕过签名验证
```bash
java -Djava.awt.headless=false -Dfile.encoding=UTF-8 -jar target\ai-quiz-tool-1.0.0.jar
```

#### 方案3：重新构建项目
```bash
mvn clean package
```

### 2. Maven命令被中断

**问题**：Maven命令在运行时被意外中断

**解决方案**：
1. 确保有足够的磁盘空间
2. 关闭其他占用内存的程序
3. 使用更简单的命令：
   ```bash
   mvn clean compile
   mvn package
   ```

### 3. 系统托盘图标不显示

**原因**：
- 系统不支持系统托盘
- Java版本不兼容
- 权限问题

**解决方案**：
1. 检查系统是否支持系统托盘
2. 确保使用Java 11或更高版本
3. 以管理员身份运行

### 4. 语音识别不工作

**原因**：
- 麦克风权限未授予
- 音频设备问题
- 语音识别服务未配置

**解决方案**：
1. 检查麦克风权限
2. 确认音频设备正常工作
3. 在设置中检查语音识别配置

### 5. AI回复失败

**原因**：
- API Key未配置或错误
- 网络连接问题
- API服务不可用

**解决方案**：
1. 检查API Key配置
2. 确认网络连接正常
3. 检查API服务状态

## 启动脚本说明

### 可用的启动脚本

1. **`start.bat`** - 标准启动脚本
2. **`run-simple.bat`** - 简单模式启动（推荐用于解决签名问题）
3. **`start-safe.bat`** - 安全模式启动
4. **`run-fixed.bat`** - 修复版本启动

### 推荐使用顺序

1. 首先尝试 `run-simple.bat`
2. 如果仍有问题，尝试 `start-safe.bat`
3. 最后尝试重新构建项目

## 日志文件位置

程序运行日志保存在：
- Windows: `%USERPROFILE%\.aiquiztool\logs\aiquiztool.log`

## 获取帮助

如果问题仍然存在，请：
1. 查看日志文件
2. 检查系统要求
3. 提交Issue到项目仓库



