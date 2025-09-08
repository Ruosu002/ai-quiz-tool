package com.aiquiztool.service;

import com.aiquiztool.config.ConfigManager;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

/**
 * AI外部服务
 * @author Tang En Dong
 */
public class AIService {
    private static final Logger logger = LoggerFactory.getLogger(AIService.class);
    
    private final OkHttpClient httpClient;
    private final ObjectMapper objectMapper;
    private final ConfigManager configManager;
    
    public AIService() {
        this.httpClient = new OkHttpClient.Builder()
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(60, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .build();
        
        this.objectMapper = new ObjectMapper();
        this.configManager = ConfigManager.getInstance();
    }
    
    public String getResponse(String userMessage) throws Exception {
        String provider = configManager.getAIProvider();
        
        switch (provider.toLowerCase()) {
            case "openai":
                return getOpenAIResponse(userMessage);
            case "claude":
                return getClaudeResponse(userMessage);
            case "generic":
                return getGenericResponse(userMessage);
            default:
                throw new Exception("不支持的AI服务提供商: " + provider);
        }
    }
    
    private String getOpenAIResponse(String userMessage) throws Exception {
        String apiKey = configManager.getOpenaiApiKey();
        String apiUrl = configManager.getOpenaiApiUrl();
        String model = configManager.getOpenaiModel();
        int maxTokens = configManager.getOpenaiMaxTokens();
        double temperature = configManager.getOpenaiTemperature();
        
        if (apiKey == null || apiKey.trim().isEmpty()) {
            throw new Exception("OpenAI API Key未配置，请在设置中配置");
        }
        
        if (apiUrl == null || apiUrl.trim().isEmpty()) {
            throw new Exception("OpenAI API URL未配置，请在设置中配置");
        }
        
        try {
            return callOpenAIAPI(userMessage, apiKey, apiUrl, model, maxTokens, temperature);
        } catch (Exception e) {
            logger.error("调用OpenAI API时发生错误", e);
            throw new Exception("获取OpenAI回复失败: " + e.getMessage());
        }
    }
    
    private String getClaudeResponse(String userMessage) throws Exception {
        String apiKey = configManager.getClaudeApiKey();
        String apiUrl = configManager.getClaudeApiUrl();
        String model = configManager.getClaudeModel();
        int maxTokens = configManager.getClaudeMaxTokens();
        double temperature = configManager.getClaudeTemperature();
        
        if (apiKey == null || apiKey.trim().isEmpty()) {
            throw new Exception("Claude API Key未配置，请在设置中配置");
        }
        
        if (apiUrl == null || apiUrl.trim().isEmpty()) {
            throw new Exception("Claude API URL未配置，请在设置中配置");
        }
        
        try {
            return callClaudeAPI(userMessage, apiKey, apiUrl, model, maxTokens, temperature);
        } catch (Exception e) {
            logger.error("调用Claude API时发生错误", e);
            throw new Exception("获取Claude回复失败: " + e.getMessage());
        }
    }
    
    private String getGenericResponse(String userMessage) throws Exception {
        String apiKey = configManager.getGenericApiKey();
        String apiUrl = configManager.getGenericApiUrl();
        String model = configManager.getGenericModel();
        int maxTokens = configManager.getGenericMaxTokens();
        double temperature = configManager.getGenericTemperature();
        
        if (apiKey == null || apiKey.trim().isEmpty()) {
            throw new Exception("通用API Key未配置，请在设置中配置");
        }
        
        if (apiUrl == null || apiUrl.trim().isEmpty()) {
            throw new Exception("通用API URL未配置，请在设置中配置");
        }
        
        try {
            return callOpenAIAPI(userMessage, apiKey, apiUrl, model, maxTokens, temperature);
        } catch (Exception e) {
            logger.error("调用通用API时发生错误", e);
            throw new Exception("获取通用API回复失败: " + e.getMessage());
        }
    }
    
    private String callOpenAIAPI(String userMessage, String apiKey, String apiUrl, 
                                String model, int maxTokens, double temperature) throws Exception {
        
        String requestBody = buildRequestBody(userMessage, model, maxTokens, temperature);
        RequestBody body = RequestBody.create(
            requestBody,
            MediaType.get("application/json; charset=utf-8")
        );
        
        Request request = new Request.Builder()
            .url(apiUrl)
            .addHeader("Authorization", "Bearer " + apiKey)
            .addHeader("Content-Type", "application/json")
            .post(body)
            .build();
        
        // 发送请求
        try (Response response = httpClient.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                String errorBody = response.body() != null ? response.body().string() : "未知错误";
                throw new Exception("API请求失败: " + response.code() + " - " + errorBody);
            }
            
            String responseBody = response.body().string();
            return parseResponse(responseBody);
            
        } catch (IOException e) {
            throw new Exception("网络请求失败: " + e.getMessage());
        }
    }
    
    private String buildRequestBody(String userMessage, String model, int maxTokens, double temperature) {
        try {
            StringBuilder requestBody = new StringBuilder();
            requestBody.append("{");
            requestBody.append("\"model\":\"").append(model).append("\",");
            requestBody.append("\"messages\":[");
            requestBody.append("{\"role\":\"system\",\"content\":\"你是一个有用的AI助手，请用中文回答问题。\"},");
            requestBody.append("{\"role\":\"user\",\"content\":\"").append(escapeJson(userMessage)).append("\"}");
            requestBody.append("],");
            requestBody.append("\"max_tokens\":").append(maxTokens).append(",");
            requestBody.append("\"temperature\":").append(temperature).append(",");
            requestBody.append("\"stream\":false");
            requestBody.append("}");
            
            return requestBody.toString();
        } catch (Exception e) {
            logger.error("构建请求体时发生错误", e);
            throw new RuntimeException("构建请求体失败", e);
        }
    }
    
    private String parseResponse(String responseBody) throws Exception {
        try {
            JsonNode rootNode = objectMapper.readTree(responseBody);
            
            if (rootNode.has("error")) {
                String errorMessage = rootNode.get("error").get("message").asText();
                throw new Exception("API返回错误: " + errorMessage);
            }
            
            JsonNode choices = rootNode.get("choices");
            if (choices != null && choices.isArray() && choices.size() > 0) {
                JsonNode firstChoice = choices.get(0);
                JsonNode message = firstChoice.get("message");
                if (message != null && message.has("content")) {
                    return message.get("content").asText();
                }
            }
            
            throw new Exception("无法解析API响应");
            
        } catch (Exception e) {
            logger.error("解析API响应时发生错误", e);
            throw new Exception("解析API响应失败: " + e.getMessage());
        }
    }
    
    /**
     * 转义JSON字符串
     */
    private String escapeJson(String text) {
        if (text == null) {
            return "";
        }
        
        return text.replace("\\", "\\\\")
                  .replace("\"", "\\\"")
                  .replace("\b", "\\b")
                  .replace("\f", "\\f")
                  .replace("\n", "\\n")
                  .replace("\r", "\\r")
                  .replace("\t", "\\t");
    }
    
    /**
     * 调用Claude API
     */
    private String callClaudeAPI(String userMessage, String apiKey, String apiUrl, 
                               String model, int maxTokens, double temperature) throws Exception {
        
        // 构建Claude请求体
        String requestBody = buildClaudeRequestBody(userMessage, model, maxTokens, temperature);
        
        // 创建HTTP请求
        RequestBody body = RequestBody.create(
            requestBody,
            MediaType.get("application/json; charset=utf-8")
        );
        
        Request request = new Request.Builder()
            .url(apiUrl)
            .addHeader("x-api-key", apiKey)
            .addHeader("Content-Type", "application/json")
            .addHeader("anthropic-version", "2023-06-01")
            .post(body)
            .build();
        
        // 发送请求
        try (Response response = httpClient.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                String errorBody = response.body() != null ? response.body().string() : "未知错误";
                throw new Exception("Claude API请求失败: " + response.code() + " - " + errorBody);
            }
            
            String responseBody = response.body().string();
            return parseClaudeResponse(responseBody);
            
        } catch (IOException e) {
            throw new Exception("网络请求失败: " + e.getMessage());
        }
    }
    
    /**
     * 构建Claude请求体
     */
    private String buildClaudeRequestBody(String userMessage, String model, int maxTokens, double temperature) {
        try {
            StringBuilder requestBody = new StringBuilder();
            requestBody.append("{");
            requestBody.append("\"model\":\"").append(model).append("\",");
            requestBody.append("\"max_tokens\":").append(maxTokens).append(",");
            requestBody.append("\"temperature\":").append(temperature).append(",");
            requestBody.append("\"messages\":[");
            requestBody.append("{\"role\":\"user\",\"content\":\"").append(escapeJson(userMessage)).append("\"}");
            requestBody.append("]");
            requestBody.append("}");
            
            return requestBody.toString();
        } catch (Exception e) {
            logger.error("构建Claude请求体时发生错误", e);
            throw new RuntimeException("构建Claude请求体失败", e);
        }
    }
    
    /**
     * 解析Claude API响应
     */
    private String parseClaudeResponse(String responseBody) throws Exception {
        try {
            JsonNode rootNode = objectMapper.readTree(responseBody);
            
            // 检查是否有错误
            if (rootNode.has("error")) {
                String errorMessage = rootNode.get("error").get("message").asText();
                throw new Exception("Claude API返回错误: " + errorMessage);
            }
            
            // 提取回复内容
            JsonNode content = rootNode.get("content");
            if (content != null && content.isArray() && content.size() > 0) {
                JsonNode firstContent = content.get(0);
                if (firstContent.has("text")) {
                    return firstContent.get("text").asText();
                }
            }
            
            throw new Exception("无法解析Claude API响应");
            
        } catch (Exception e) {
            logger.error("解析Claude API响应时发生错误", e);
            throw new Exception("解析Claude API响应失败: " + e.getMessage());
        }
    }
    
    /**
     * 测试API连接
     */
    public boolean testConnection() throws Exception {
        try {
            String testMessage = "你好，请回复'连接成功'";
            String response = getResponse(testMessage);
            return response != null && !response.trim().isEmpty();
        } catch (Exception e) {
            logger.error("测试API连接时发生错误", e);
            throw e;
        }
    }
    
    /**
     * 获取API使用统计信息
     */
    public String getUsageStats() {
        // 这里可以添加使用统计功能
        return "使用统计功能待实现";
    }
    
    /**
     * 清理资源
     */
    public void cleanup() {
        if (httpClient != null) {
            httpClient.dispatcher().executorService().shutdown();
            httpClient.connectionPool().evictAll();
        }
    }
}


