package com.aiquiztool.service;

import com.aiquiztool.config.ConfigManager;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sound.sampled.*;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.Base64;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

/**
 * 语音识别服务
 * @author Tang En Dong
 */
public class SpeechRecognitionService {
    private static final Logger logger = LoggerFactory.getLogger(SpeechRecognitionService.class);
    
    private final ConfigManager configManager;
    private final OkHttpClient httpClient;
    private final ObjectMapper objectMapper;
    
    private AudioFormat audioFormat;
    private TargetDataLine targetDataLine;
    private boolean isRecording = false;
    private ByteArrayOutputStream audioBuffer;
    
    public SpeechRecognitionService() {
        this.configManager = ConfigManager.getInstance();
        this.httpClient = new OkHttpClient.Builder()
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(60, TimeUnit.SECONDS)
            .writeTimeout(60, TimeUnit.SECONDS)
            .build();
        this.objectMapper = new ObjectMapper();
        
        initializeAudioFormat();
    }
    
    private void initializeAudioFormat() {
        float sampleRate = configManager.getSpeechSampleRate();
        int sampleSizeInBits = 16;
        int channels = 1;
        boolean signed = true;
        boolean bigEndian = false;
        
        audioFormat = new AudioFormat(sampleRate, sampleSizeInBits, channels, signed, bigEndian);
    }
    
    public void startRecording() throws Exception {
        if (isRecording) {
            throw new IllegalStateException("录音已在进行中");
        }
        
        try {
            DataLine.Info info = new DataLine.Info(TargetDataLine.class, audioFormat);
            
            if (!AudioSystem.isLineSupported(info)) {
                throw new UnsupportedOperationException("不支持的音频格式");
            }
            
            targetDataLine = (TargetDataLine) AudioSystem.getLine(info);
            targetDataLine.open(audioFormat);
            targetDataLine.start();
            
            audioBuffer = new ByteArrayOutputStream();
            isRecording = true;
            
            CompletableFuture.runAsync(this::recordAudio);
            
            logger.info("开始录音");
            
        } catch (LineUnavailableException e) {
            logger.error("无法打开音频设备", e);
            throw new Exception("无法访问麦克风，请检查设备权限", e);
        }
    }
    
    public CompletableFuture<String> stopRecording() {
        if (!isRecording) {
            return CompletableFuture.completedFuture("");
        }
        
        isRecording = false;
        
        if (targetDataLine != null) {
            targetDataLine.stop();
            targetDataLine.close();
        }
        
        logger.info("停止录音，音频长度: {} 字节", audioBuffer.size());
        
        return recognizeAudio(audioBuffer.toByteArray());
    }
    
    private void recordAudio() {
        byte[] buffer = new byte[4096];
        
        while (isRecording && targetDataLine != null) {
            int bytesRead = targetDataLine.read(buffer, 0, buffer.length);
            if (bytesRead > 0) {
                audioBuffer.write(buffer, 0, bytesRead);
            }
        }
    }
    
    private CompletableFuture<String> recognizeAudio(byte[] audioData) {
        String provider = configManager.getSpeechProvider();
        
        switch (provider.toLowerCase()) {
            case "azure":
                return recognizeWithAzure(audioData);
            case "google":
                return recognizeWithGoogle(audioData);
            case "baidu":
                return recognizeWithBaidu(audioData);
            default:
                logger.error("不支持的语音识别提供商: {}", provider);
                return CompletableFuture.completedFuture("不支持的语音识别提供商");
        }
    }
    
    /**
     * 使用Azure语音识别
     */
    private CompletableFuture<String> recognizeWithAzure(byte[] audioData) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                String subscriptionKey = configManager.getAzureSubscriptionKey();
                String region = configManager.getAzureRegion();
                
                if (subscriptionKey.isEmpty()) {
                    return "请在设置中配置Azure订阅密钥";
                }
                
                String url = String.format("https://%s.stt.speech.microsoft.com/speech/recognition/conversation/cognitiveservices/v1", region);
                
                RequestBody body = RequestBody.create(audioData, MediaType.parse("audio/wav"));
                
                Request request = new Request.Builder()
                    .url(url + "?language=" + configManager.getSpeechLanguage())
                    .post(body)
                    .addHeader("Ocp-Apim-Subscription-Key", subscriptionKey)
                    .addHeader("Content-Type", "audio/wav")
                    .build();
                
                try (Response response = httpClient.newCall(request).execute()) {
                    if (!response.isSuccessful()) {
                        logger.error("Azure语音识别请求失败: {}", response.code());
                        return "语音识别失败: " + response.message();
                    }
                    
                    String responseBody = response.body().string();
                    JsonNode json = objectMapper.readTree(responseBody);
                    
                    if (json.has("DisplayText")) {
                        String text = json.get("DisplayText").asText();
                        logger.info("Azure语音识别结果: {}", text);
                        return text;
                    } else {
                        logger.warn("Azure语音识别无结果: {}", responseBody);
                        return "未识别到语音内容";
                    }
                }
                
            } catch (Exception e) {
                logger.error("Azure语音识别异常", e);
                return "语音识别出错: " + e.getMessage();
            }
        });
    }
    
    /**
     * 使用Google语音识别
     */
    private CompletableFuture<String> recognizeWithGoogle(byte[] audioData) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                String apiKey = configManager.getGoogleApiKey();
                String endpoint = configManager.getGoogleEndpoint();
                
                if (apiKey.isEmpty()) {
                    return "请在设置中配置Google API密钥";
                }
                
                // 构建请求JSON
                String audioBase64 = Base64.getEncoder().encodeToString(audioData);
                String requestJson = String.format(
                    "{\"config\":{\"encoding\":\"LINEAR16\",\"sampleRateHertz\":%d,\"languageCode\":\"%s\"},\"audio\":{\"content\":\"%s\"}}",
                    configManager.getSpeechSampleRate(),
                    configManager.getSpeechLanguage(),
                    audioBase64
                );
                
                RequestBody body = RequestBody.create(requestJson, MediaType.parse("application/json"));
                
                Request request = new Request.Builder()
                    .url(endpoint + "?key=" + apiKey)
                    .post(body)
                    .addHeader("Content-Type", "application/json")
                    .build();
                
                try (Response response = httpClient.newCall(request).execute()) {
                    if (!response.isSuccessful()) {
                        logger.error("Google语音识别请求失败: {}", response.code());
                        return "语音识别失败: " + response.message();
                    }
                    
                    String responseBody = response.body().string();
                    JsonNode json = objectMapper.readTree(responseBody);
                    
                    if (json.has("results") && json.get("results").size() > 0) {
                        JsonNode firstResult = json.get("results").get(0);
                        if (firstResult.has("alternatives") && firstResult.get("alternatives").size() > 0) {
                            String text = firstResult.get("alternatives").get(0).get("transcript").asText();
                            logger.info("Google语音识别结果: {}", text);
                            return text;
                        }
                    }
                    
                    logger.warn("Google语音识别无结果: {}", responseBody);
                    return "未识别到语音内容";
                }
                
            } catch (Exception e) {
                logger.error("Google语音识别异常", e);
                return "语音识别出错: " + e.getMessage();
            }
        });
    }
    
    /**
     * 使用百度语音识别
     */
    private CompletableFuture<String> recognizeWithBaidu(byte[] audioData) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                String apiKey = configManager.getBaiduApiKey();
                String secretKey = configManager.getBaiduSecretKey();
                
                if (apiKey.isEmpty() || secretKey.isEmpty()) {
                    return "请在设置中配置百度API密钥";
                }
                
                // 获取访问令牌
                String accessToken = getBaiduAccessToken(apiKey, secretKey);
                if (accessToken == null) {
                    return "获取百度访问令牌失败";
                }
                
                // 构建识别请求
                String audioBase64 = Base64.getEncoder().encodeToString(audioData);
                String requestJson = String.format(
                    "{\"format\":\"wav\",\"rate\":%d,\"channel\":1,\"cuid\":\"aiquiztool\",\"token\":\"%s\",\"speech\":\"%s\",\"len\":%d}",
                    configManager.getSpeechSampleRate(),
                    accessToken,
                    audioBase64,
                    audioData.length
                );
                
                RequestBody body = RequestBody.create(requestJson, MediaType.parse("application/json"));
                
                Request request = new Request.Builder()
                    .url("https://vop.baidu.com/server_api")
                    .post(body)
                    .addHeader("Content-Type", "application/json")
                    .build();
                
                try (Response response = httpClient.newCall(request).execute()) {
                    if (!response.isSuccessful()) {
                        logger.error("百度语音识别请求失败: {}", response.code());
                        return "语音识别失败: " + response.message();
                    }
                    
                    String responseBody = response.body().string();
                    JsonNode json = objectMapper.readTree(responseBody);
                    
                    if (json.has("err_no") && json.get("err_no").asInt() == 0) {
                        if (json.has("result") && json.get("result").size() > 0) {
                            String text = json.get("result").get(0).asText();
                            logger.info("百度语音识别结果: {}", text);
                            return text;
                        }
                    }
                    
                    logger.warn("百度语音识别失败: {}", responseBody);
                    return "语音识别失败: " + json.get("err_msg").asText();
                }
                
            } catch (Exception e) {
                logger.error("百度语音识别异常", e);
                return "语音识别出错: " + e.getMessage();
            }
        });
    }
    
    /**
     * 获取百度访问令牌
     */
    private String getBaiduAccessToken(String apiKey, String secretKey) {
        try {
            String url = "https://aip.baidubce.com/oauth/2.0/token";
            RequestBody body = new FormBody.Builder()
                .add("grant_type", "client_credentials")
                .add("client_id", apiKey)
                .add("client_secret", secretKey)
                .build();
            
            Request request = new Request.Builder()
                .url(url)
                .post(body)
                .build();
            
            try (Response response = httpClient.newCall(request).execute()) {
                if (response.isSuccessful()) {
                    String responseBody = response.body().string();
                    JsonNode json = objectMapper.readTree(responseBody);
                    return json.get("access_token").asText();
                }
            }
        } catch (Exception e) {
            logger.error("获取百度访问令牌失败", e);
        }
        return null;
    }
    
    /**
     * 保存音频到文件（调试用）
     */
    private void saveAudioToFile(byte[] audioData, String filename) {
        try {
            File audioFile = new File(filename);
            AudioInputStream audioInputStream = new AudioInputStream(
                new java.io.ByteArrayInputStream(audioData),
                audioFormat,
                audioData.length / audioFormat.getFrameSize()
            );
            AudioSystem.write(audioInputStream, AudioFileFormat.Type.WAVE, audioFile);
            logger.info("音频已保存到: {}", audioFile.getAbsolutePath());
        } catch (IOException e) {
            logger.error("保存音频文件失败", e);
        }
    }
    
    /**
     * 检查是否正在录音
     */
    public boolean isRecording() {
        return isRecording;
    }
    
    /**
     * 检查语音识别是否可用
     */
    public boolean isAvailable() {
        if (!configManager.isSpeechEnabled()) {
            return false;
        }
        
        String provider = configManager.getSpeechProvider();
        switch (provider.toLowerCase()) {
            case "azure":
                return !configManager.getAzureSubscriptionKey().isEmpty();
            case "google":
                return !configManager.getGoogleApiKey().isEmpty();
            case "baidu":
                return !configManager.getBaiduApiKey().isEmpty() && 
                       !configManager.getBaiduSecretKey().isEmpty();
            default:
                return false;
        }
    }
    
    /**
     * 获取可用的音频设备列表
     */
    public String[] getAvailableAudioDevices() {
        Mixer.Info[] mixerInfos = AudioSystem.getMixerInfo();
        java.util.List<String> deviceNames = new java.util.ArrayList<>();
        
        for (Mixer.Info mixerInfo : mixerInfos) {
            Mixer mixer = AudioSystem.getMixer(mixerInfo);
            Line.Info[] lineInfos = mixer.getTargetLineInfo();
            
            for (Line.Info lineInfo : lineInfos) {
                if (lineInfo instanceof DataLine.Info) {
                    DataLine.Info dataLineInfo = (DataLine.Info) lineInfo;
                    if (dataLineInfo.isFormatSupported(audioFormat)) {
                        deviceNames.add(mixerInfo.getName());
                        break;
                    }
                }
            }
        }
        
        return deviceNames.toArray(new String[0]);
    }
}


