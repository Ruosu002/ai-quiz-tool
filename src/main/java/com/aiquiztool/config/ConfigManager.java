package com.aiquiztool.config;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import com.typesafe.config.ConfigValueFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * 配置
 * @author Tang En Dong
 */
public class ConfigManager {
    private static final Logger logger = LoggerFactory.getLogger(ConfigManager.class);
    private static final String CONFIG_FILE = "config.conf";
    private static final String CONFIG_DIR = System.getProperty("user.home") + File.separator + ".aiquiztool";
    
    private static ConfigManager instance;
    private Config config;
    private File configFile;
    
    private ConfigManager() {
        initializeConfig();
    }
    
    public static synchronized ConfigManager getInstance() {
        if (instance == null) {
            instance = new ConfigManager();
        }
        return instance;
    }
    
    private void initializeConfig() {
        try {
            // 创建配置目录
            File configDir = new File(CONFIG_DIR);
            if (!configDir.exists()) {
                configDir.mkdirs();
            }
            
            // 配置文件路径
            configFile = new File(configDir, CONFIG_FILE);
            
            // 加载配置
            if (configFile.exists()) {
                config = ConfigFactory.parseFile(configFile);
            } else {
                // 创建默认配置
                createDefaultConfig();
            }
            
            logger.info("配置管理器初始化完成，配置文件: {}", configFile.getAbsolutePath());
            
        } catch (Exception e) {
            logger.error("初始化配置管理器时发生错误", e);
            // 使用默认配置
            config = ConfigFactory.empty();
        }
    }
    
    private void createDefaultConfig() {
        try {
            // 创建默认配置文件
            StringBuilder defaultConfig = new StringBuilder();
            defaultConfig.append("# AI助手工具配置文件\n");
            defaultConfig.append("# 请根据您的需要修改以下配置\n\n");
            
            defaultConfig.append("# AI服务配置\n");
            defaultConfig.append("ai {\n");
            defaultConfig.append("  provider = \"openai\"\n");
            defaultConfig.append("  \n");
            defaultConfig.append("  # OpenAI配置\n");
            defaultConfig.append("  openai {\n");
            defaultConfig.append("    api-key = \"\"\n");
            defaultConfig.append("    api-url = \"https://api.openai.com/v1/chat/completions\"\n");
            defaultConfig.append("    model = \"gpt-3.5-turbo\"\n");
            defaultConfig.append("    max-tokens = 1000\n");
            defaultConfig.append("    temperature = 0.7\n");
            defaultConfig.append("  }\n");
            defaultConfig.append("  \n");
            defaultConfig.append("  # Claude配置（Anthropic）\n");
            defaultConfig.append("  claude {\n");
            defaultConfig.append("    api-key = \"\"\n");
            defaultConfig.append("    api-url = \"https://api.anthropic.com/v1/messages\"\n");
            defaultConfig.append("    model = \"claude-3-sonnet-20240229\"\n");
            defaultConfig.append("    max-tokens = 1000\n");
            defaultConfig.append("    temperature = 0.7\n");
            defaultConfig.append("  }\n");
            defaultConfig.append("  \n");
            defaultConfig.append("  # 通用配置（支持其他兼容OpenAI格式的API）\n");
            defaultConfig.append("  generic {\n");
            defaultConfig.append("    api-key = \"\"\n");
            defaultConfig.append("    api-url = \"\"\n");
            defaultConfig.append("    model = \"gpt-3.5-turbo\"\n");
            defaultConfig.append("    max-tokens = 1000\n");
            defaultConfig.append("    temperature = 0.7\n");
            defaultConfig.append("  }\n");
            defaultConfig.append("}\n\n");
            
            defaultConfig.append("# 语音识别配置\n");
            defaultConfig.append("speech {\n");
            defaultConfig.append("  enabled = false\n");
            defaultConfig.append("  provider = \"azure\"\n");
            defaultConfig.append("  language = \"zh-CN\"\n");
            defaultConfig.append("  sample-rate = 16000\n");
            defaultConfig.append("  \n");
            defaultConfig.append("  # Azure Speech Services\n");
            defaultConfig.append("  azure {\n");
            defaultConfig.append("    subscription-key = \"\"\n");
            defaultConfig.append("    region = \"eastasia\"\n");
            defaultConfig.append("    endpoint = \"https://eastasia.api.cognitive.microsoft.com/\"\n");
            defaultConfig.append("  }\n");
            defaultConfig.append("  \n");
            defaultConfig.append("  # Google Cloud Speech-to-Text\n");
            defaultConfig.append("  google {\n");
            defaultConfig.append("    api-key = \"\"\n");
            defaultConfig.append("    endpoint = \"https://speech.googleapis.com/v1/speech:recognize\"\n");
            defaultConfig.append("  }\n");
            defaultConfig.append("  \n");
            defaultConfig.append("  # 百度语音识别\n");
            defaultConfig.append("  baidu {\n");
            defaultConfig.append("    app-id = \"\"\n");
            defaultConfig.append("    api-key = \"\"\n");
            defaultConfig.append("    secret-key = \"\"\n");
            defaultConfig.append("  }\n");
            defaultConfig.append("}\n\n");
            
            defaultConfig.append("# 界面配置\n");
            defaultConfig.append("ui {\n");
            defaultConfig.append("  theme = \"light\"\n");
            defaultConfig.append("  font-size = 12\n");
            defaultConfig.append("  window-width = 500\n");
            defaultConfig.append("  window-height = 400\n");
            defaultConfig.append("}\n\n");
            
            defaultConfig.append("# 日志配置\n");
            defaultConfig.append("logging {\n");
            defaultConfig.append("  level = \"INFO\"\n");
            defaultConfig.append("  file = \"aiquiztool.log\"\n");
            defaultConfig.append("}\n");
            
            // 写入配置文件
            try (FileWriter writer = new FileWriter(configFile)) {
                writer.write(defaultConfig.toString());
            }
            
            // 加载配置
            config = ConfigFactory.parseFile(configFile);
            
            logger.info("创建默认配置文件: {}", configFile.getAbsolutePath());
            
        } catch (IOException e) {
            logger.error("创建默认配置文件时发生错误", e);
            config = ConfigFactory.empty();
        }
    }
    
    public void saveConfig() throws Exception {
        try {
            // 构建新的配置内容
            StringBuilder configContent = new StringBuilder();
            configContent.append("# AI助手工具配置文件\n");
            configContent.append("# 请根据您的需要修改以下配置\n\n");
            
            configContent.append("# AI服务配置\n");
            configContent.append("ai {\n");
            configContent.append("  provider = \"").append(getAIProvider()).append("\"\n");
            configContent.append("  \n");
            configContent.append("  # OpenAI配置\n");
            configContent.append("  openai {\n");
            configContent.append("    api-key = \"").append(getOpenaiApiKey()).append("\"\n");
            configContent.append("    api-url = \"").append(getOpenaiApiUrl()).append("\"\n");
            configContent.append("    model = \"").append(getOpenaiModel()).append("\"\n");
            configContent.append("    max-tokens = ").append(getOpenaiMaxTokens()).append("\n");
            configContent.append("    temperature = ").append(getOpenaiTemperature()).append("\n");
            configContent.append("  }\n");
            configContent.append("  \n");
            configContent.append("  # Claude配置（Anthropic）\n");
            configContent.append("  claude {\n");
            configContent.append("    api-key = \"").append(getClaudeApiKey()).append("\"\n");
            configContent.append("    api-url = \"").append(getClaudeApiUrl()).append("\"\n");
            configContent.append("    model = \"").append(getClaudeModel()).append("\"\n");
            configContent.append("    max-tokens = ").append(getClaudeMaxTokens()).append("\n");
            configContent.append("    temperature = ").append(getClaudeTemperature()).append("\n");
            configContent.append("  }\n");
            configContent.append("  \n");
            configContent.append("  # 通用配置（支持其他兼容OpenAI格式的API）\n");
            configContent.append("  generic {\n");
            configContent.append("    api-key = \"").append(getGenericApiKey()).append("\"\n");
            configContent.append("    api-url = \"").append(getGenericApiUrl()).append("\"\n");
            configContent.append("    model = \"").append(getGenericModel()).append("\"\n");
            configContent.append("    max-tokens = ").append(getGenericMaxTokens()).append("\n");
            configContent.append("    temperature = ").append(getGenericTemperature()).append("\n");
            configContent.append("  }\n");
            configContent.append("}\n\n");
            
            configContent.append("# 语音识别配置\n");
            configContent.append("speech {\n");
            configContent.append("  enabled = ").append(isSpeechEnabled()).append("\n");
            configContent.append("  provider = \"").append(getSpeechProvider()).append("\"\n");
            configContent.append("  language = \"").append(getSpeechLanguage()).append("\"\n");
            configContent.append("  sample-rate = ").append(getSpeechSampleRate()).append("\n");
            configContent.append("  \n");
            configContent.append("  # Azure Speech Services\n");
            configContent.append("  azure {\n");
            configContent.append("    subscription-key = \"").append(getAzureSubscriptionKey()).append("\"\n");
            configContent.append("    region = \"").append(getAzureRegion()).append("\"\n");
            configContent.append("    endpoint = \"").append(getAzureEndpoint()).append("\"\n");
            configContent.append("  }\n");
            configContent.append("  \n");
            configContent.append("  # Google Cloud Speech-to-Text\n");
            configContent.append("  google {\n");
            configContent.append("    api-key = \"").append(getGoogleApiKey()).append("\"\n");
            configContent.append("    endpoint = \"").append(getGoogleEndpoint()).append("\"\n");
            configContent.append("  }\n");
            configContent.append("  \n");
            configContent.append("  # 百度语音识别\n");
            configContent.append("  baidu {\n");
            configContent.append("    app-id = \"").append(getBaiduAppId()).append("\"\n");
            configContent.append("    api-key = \"").append(getBaiduApiKey()).append("\"\n");
            configContent.append("    secret-key = \"").append(getBaiduSecretKey()).append("\"\n");
            configContent.append("  }\n");
            configContent.append("}\n\n");
            
            configContent.append("# 界面配置\n");
            configContent.append("ui {\n");
            configContent.append("  theme = \"").append(getUITheme()).append("\"\n");
            configContent.append("  font-size = ").append(getUIFontSize()).append("\n");
            configContent.append("  window-width = ").append(getUIWindowWidth()).append("\n");
            configContent.append("  window-height = ").append(getUIWindowHeight()).append("\n");
            configContent.append("}\n\n");
            
            configContent.append("# 日志配置\n");
            configContent.append("logging {\n");
            configContent.append("  level = \"").append(getLoggingLevel()).append("\"\n");
            configContent.append("  file = \"").append(getLoggingFile()).append("\"\n");
            configContent.append("}\n");
            
            // 写入配置文件
            try (FileWriter writer = new FileWriter(configFile)) {
                writer.write(configContent.toString());
            }
            
            // 重新加载配置
            config = ConfigFactory.parseFile(configFile);
            
            logger.info("配置保存成功");
            
        } catch (Exception e) {
            logger.error("保存配置时发生错误", e);
            throw new Exception("保存配置失败: " + e.getMessage());
        }
    }
    
    public String getAIProvider() {
        return config.hasPath("ai.provider") ? config.getString("ai.provider") : "openai";
    }
    
    public void setAIProvider(String provider) {
        config = config.withValue("ai.provider", ConfigValueFactory.fromAnyRef(provider));
    }
    
    public String getOpenaiApiKey() {
        return config.hasPath("ai.openai.api-key") ? config.getString("ai.openai.api-key") : 
               (config.hasPath("openai.api-key") ? config.getString("openai.api-key") : "");
    }
    
    public void setOpenaiApiKey(String apiKey) {
        config = config.withValue("ai.openai.api-key", ConfigValueFactory.fromAnyRef(apiKey));
    }
    
    public String getOpenaiApiUrl() {
        return config.hasPath("ai.openai.api-url") ? config.getString("ai.openai.api-url") : 
               (config.hasPath("openai.api-url") ? config.getString("openai.api-url") : "https://api.openai.com/v1/chat/completions");
    }
    
    public void setOpenaiApiUrl(String apiUrl) {
        config = config.withValue("ai.openai.api-url", ConfigValueFactory.fromAnyRef(apiUrl));
    }
    
    public String getOpenaiModel() {
        return config.hasPath("ai.openai.model") ? config.getString("ai.openai.model") : 
               (config.hasPath("openai.model") ? config.getString("openai.model") : "gpt-3.5-turbo");
    }
    
    public void setOpenaiModel(String model) {
        config = config.withValue("ai.openai.model", ConfigValueFactory.fromAnyRef(model));
    }
    
    public int getOpenaiMaxTokens() {
        return config.hasPath("ai.openai.max-tokens") ? config.getInt("ai.openai.max-tokens") : 
               (config.hasPath("openai.max-tokens") ? config.getInt("openai.max-tokens") : 1000);
    }
    
    public void setOpenaiMaxTokens(int maxTokens) {
        config = config.withValue("ai.openai.max-tokens", ConfigValueFactory.fromAnyRef(maxTokens));
    }
    
    public double getOpenaiTemperature() {
        return config.hasPath("ai.openai.temperature") ? config.getDouble("ai.openai.temperature") : 
               (config.hasPath("openai.temperature") ? config.getDouble("openai.temperature") : 0.7);
    }
    
    public void setOpenaiTemperature(double temperature) {
        config = config.withValue("ai.openai.temperature", ConfigValueFactory.fromAnyRef(temperature));
    }
    
    public String getModel() { return getOpenaiModel(); }
    public void setModel(String model) { setOpenaiModel(model); }
    public int getMaxTokens() { return getOpenaiMaxTokens(); }
    public void setMaxTokens(int maxTokens) { setOpenaiMaxTokens(maxTokens); }
    public double getTemperature() { return getOpenaiTemperature(); }
    public void setTemperature(double temperature) { setOpenaiTemperature(temperature); }
    
    public String getClaudeApiKey() {
        return config.hasPath("ai.claude.api-key") ? config.getString("ai.claude.api-key") : "";
    }
    
    public void setClaudeApiKey(String apiKey) {
        config = config.withValue("ai.claude.api-key", ConfigValueFactory.fromAnyRef(apiKey));
    }
    
    public String getClaudeApiUrl() {
        return config.hasPath("ai.claude.api-url") ? config.getString("ai.claude.api-url") : "https://api.anthropic.com/v1/messages";
    }
    
    public void setClaudeApiUrl(String apiUrl) {
        config = config.withValue("ai.claude.api-url", ConfigValueFactory.fromAnyRef(apiUrl));
    }
    
    public String getClaudeModel() {
        return config.hasPath("ai.claude.model") ? config.getString("ai.claude.model") : "claude-3-sonnet-20240229";
    }
    
    public void setClaudeModel(String model) {
        config = config.withValue("ai.claude.model", ConfigValueFactory.fromAnyRef(model));
    }
    
    public int getClaudeMaxTokens() {
        return config.hasPath("ai.claude.max-tokens") ? config.getInt("ai.claude.max-tokens") : 1000;
    }
    
    public void setClaudeMaxTokens(int maxTokens) {
        config = config.withValue("ai.claude.max-tokens", ConfigValueFactory.fromAnyRef(maxTokens));
    }
    
    public double getClaudeTemperature() {
        return config.hasPath("ai.claude.temperature") ? config.getDouble("ai.claude.temperature") : 0.7;
    }
    
    public void setClaudeTemperature(double temperature) {
        config = config.withValue("ai.claude.temperature", ConfigValueFactory.fromAnyRef(temperature));
    }
    
    public String getGenericApiKey() {
        return config.hasPath("ai.generic.api-key") ? config.getString("ai.generic.api-key") : "";
    }
    
    public void setGenericApiKey(String apiKey) {
        config = config.withValue("ai.generic.api-key", ConfigValueFactory.fromAnyRef(apiKey));
    }
    
    public String getGenericApiUrl() {
        return config.hasPath("ai.generic.api-url") ? config.getString("ai.generic.api-url") : "";
    }
    
    public void setGenericApiUrl(String apiUrl) {
        config = config.withValue("ai.generic.api-url", ConfigValueFactory.fromAnyRef(apiUrl));
    }
    
    public String getGenericModel() {
        return config.hasPath("ai.generic.model") ? config.getString("ai.generic.model") : "gpt-3.5-turbo";
    }
    
    public void setGenericModel(String model) {
        config = config.withValue("ai.generic.model", ConfigValueFactory.fromAnyRef(model));
    }
    
    public int getGenericMaxTokens() {
        return config.hasPath("ai.generic.max-tokens") ? config.getInt("ai.generic.max-tokens") : 1000;
    }
    
    public void setGenericMaxTokens(int maxTokens) {
        config = config.withValue("ai.generic.max-tokens", ConfigValueFactory.fromAnyRef(maxTokens));
    }
    
    public double getGenericTemperature() {
        return config.hasPath("ai.generic.temperature") ? config.getDouble("ai.generic.temperature") : 0.7;
    }
    
    public void setGenericTemperature(double temperature) {
        config = config.withValue("ai.generic.temperature", ConfigValueFactory.fromAnyRef(temperature));
    }
    
    public boolean isSpeechEnabled() {
        return config.getBoolean("speech.enabled");
    }
    
    public void setSpeechEnabled(boolean enabled) {
        config = config.withValue("speech.enabled", ConfigValueFactory.fromAnyRef(enabled));
    }
    
    public String getSpeechLanguage() {
        return config.getString("speech.language");
    }
    
    public void setSpeechLanguage(String language) {
        config = config.withValue("speech.language", ConfigValueFactory.fromAnyRef(language));
    }
    
    public int getSpeechSampleRate() {
        return config.getInt("speech.sample-rate");
    }
    
    public void setSpeechSampleRate(int sampleRate) {
        config = config.withValue("speech.sample-rate", ConfigValueFactory.fromAnyRef(sampleRate));
    }
    
    public String getSpeechProvider() {
        return config.hasPath("speech.provider") ? config.getString("speech.provider") : "azure";
    }
    
    public void setSpeechProvider(String provider) {
        config = config.withValue("speech.provider", ConfigValueFactory.fromAnyRef(provider));
    }
    
    public String getAzureSubscriptionKey() {
        return config.hasPath("speech.azure.subscription-key") ? config.getString("speech.azure.subscription-key") : "";
    }
    
    public void setAzureSubscriptionKey(String key) {
        config = config.withValue("speech.azure.subscription-key", ConfigValueFactory.fromAnyRef(key));
    }
    
    public String getAzureRegion() {
        return config.hasPath("speech.azure.region") ? config.getString("speech.azure.region") : "eastasia";
    }
    
    public void setAzureRegion(String region) {
        config = config.withValue("speech.azure.region", ConfigValueFactory.fromAnyRef(region));
    }
    
    public String getAzureEndpoint() {
        return config.hasPath("speech.azure.endpoint") ? config.getString("speech.azure.endpoint") : "https://eastasia.api.cognitive.microsoft.com/";
    }
    
    public void setAzureEndpoint(String endpoint) {
        config = config.withValue("speech.azure.endpoint", ConfigValueFactory.fromAnyRef(endpoint));
    }
    
    public String getGoogleApiKey() {
        return config.hasPath("speech.google.api-key") ? config.getString("speech.google.api-key") : "";
    }
    
    public void setGoogleApiKey(String key) {
        config = config.withValue("speech.google.api-key", ConfigValueFactory.fromAnyRef(key));
    }
    
    public String getGoogleEndpoint() {
        return config.hasPath("speech.google.endpoint") ? config.getString("speech.google.endpoint") : "https://speech.googleapis.com/v1/speech:recognize";
    }
    
    public void setGoogleEndpoint(String endpoint) {
        config = config.withValue("speech.google.endpoint", ConfigValueFactory.fromAnyRef(endpoint));
    }
    
    public String getBaiduAppId() {
        return config.hasPath("speech.baidu.app-id") ? config.getString("speech.baidu.app-id") : "";
    }
    
    public void setBaiduAppId(String appId) {
        config = config.withValue("speech.baidu.app-id", ConfigValueFactory.fromAnyRef(appId));
    }
    
    public String getBaiduApiKey() {
        return config.hasPath("speech.baidu.api-key") ? config.getString("speech.baidu.api-key") : "";
    }
    
    public void setBaiduApiKey(String key) {
        config = config.withValue("speech.baidu.api-key", ConfigValueFactory.fromAnyRef(key));
    }
    
    public String getBaiduSecretKey() {
        return config.hasPath("speech.baidu.secret-key") ? config.getString("speech.baidu.secret-key") : "";
    }
    
    public void setBaiduSecretKey(String key) {
        config = config.withValue("speech.baidu.secret-key", ConfigValueFactory.fromAnyRef(key));
    }
    
    public String getUITheme() {
        return config.getString("ui.theme");
    }
    
    public void setUITheme(String theme) {
        config = config.withValue("ui.theme", ConfigValueFactory.fromAnyRef(theme));
    }
    
    public int getUIFontSize() {
        return config.getInt("ui.font-size");
    }
    
    public void setUIFontSize(int fontSize) {
        config = config.withValue("ui.font-size", ConfigValueFactory.fromAnyRef(fontSize));
    }
    
    public int getUIWindowWidth() {
        return config.getInt("ui.window-width");
    }
    
    public void setUIWindowWidth(int width) {
        config = config.withValue("ui.window-width", ConfigValueFactory.fromAnyRef(width));
    }
    
    public int getUIWindowHeight() {
        return config.getInt("ui.window-height");
    }
    
    public void setUIWindowHeight(int height) {
        config = config.withValue("ui.window-height", ConfigValueFactory.fromAnyRef(height));
    }
    
    public String getLoggingLevel() {
        return config.getString("logging.level");
    }
    
    public void setLoggingLevel(String level) {
        config = config.withValue("logging.level", ConfigValueFactory.fromAnyRef(level));
    }
    
    public String getLoggingFile() {
        return config.getString("logging.file");
    }
    
    public void setLoggingFile(String file) {
        config = config.withValue("logging.file", ConfigValueFactory.fromAnyRef(file));
    }
    
    public String getConfigFilePath() {
        return configFile != null ? configFile.getAbsolutePath() : "";
    }
    
    public void resetToDefault() throws Exception {
        if (configFile != null && configFile.exists()) {
            configFile.delete();
        }
        createDefaultConfig();
        logger.info("配置已重置为默认值");
    }
}

