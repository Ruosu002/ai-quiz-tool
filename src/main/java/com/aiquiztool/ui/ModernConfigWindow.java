package com.aiquiztool.ui;

import com.aiquiztool.config.ConfigManager;
import com.aiquiztool.ui.theme.ModernTheme;
import com.aiquiztool.ui.animation.UIAnimator;
import com.aiquiztool.ui.components.ModernButton;
import com.aiquiztool.ui.components.ModernTextField;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * 配置窗口
 */
public class ModernConfigWindow extends JFrame {
    private static final Logger logger = LoggerFactory.getLogger(ModernConfigWindow.class);
    
    // API配置组件
    private ModernTextField apiKeyField;
    private ModernTextField apiUrlField;
    private ModernTextField modelField;
    private JSpinner maxTokensSpinner;
    private JSlider temperatureSlider;
    private JLabel temperatureValueLabel;
    
    // 界面配置组件
    private JSpinner fontSizeSpinner;
    private JCheckBox enableSpeechCheckBox;
    
    // 语音识别配置组件
    private JComboBox<String> speechProviderComboBox;
    private JComboBox<String> speechLanguageComboBox;
    private JSpinner speechSampleRateSpinner;
    
    // Azure配置
    private ModernTextField azureSubscriptionKeyField;
    private ModernTextField azureRegionField;
    private ModernTextField azureEndpointField;
    
    // Google配置
    private ModernTextField googleApiKeyField;
    private ModernTextField googleEndpointField;
    
    // 百度配置
    private ModernTextField baiduAppIdField;
    private ModernTextField baiduApiKeyField;
    private ModernTextField baiduSecretKeyField;
    
    // 语音配置面板
    private JPanel speechConfigPanel;
    
    // 按钮
    private ModernButton saveButton;
    private ModernButton cancelButton;
    private ModernButton testButton;
    private ModernButton resetButton;
    
    private ConfigManager configManager;
    
    public ModernConfigWindow() {
        configManager = ConfigManager.getInstance();
        initializeComponents();
        setupLayout();
        setupEventHandlers();
        loadConfig();
        setupWindow();
    }
    
    private void initializeComponents() {
        // API配置组件
        apiKeyField = new ModernTextField("请输入您的OpenAI API Key", 30);
        apiUrlField = new ModernTextField("API服务地址", 30);
        modelField = new ModernTextField("AI模型名称", 20);
        
        // Token数量调节器
        maxTokensSpinner = new JSpinner(new SpinnerNumberModel(1000, 100, 4000, 100));
        maxTokensSpinner.setFont(ModernTheme.Fonts.BODY);
        customizeSpinner(maxTokensSpinner);
        
        // 温度滑块
        temperatureSlider = new JSlider(0, 100, 70);
        temperatureSlider.setBackground(ModernTheme.getBackground());
        temperatureSlider.setForeground(ModernTheme.getPrimary());
        customizeSlider(temperatureSlider);
        
        temperatureValueLabel = new JLabel("0.70");
        temperatureValueLabel.setFont(ModernTheme.Fonts.BODY);
        temperatureValueLabel.setForeground(ModernTheme.getTextPrimary());
        
        // 界面配置组件
        fontSizeSpinner = new JSpinner(new SpinnerNumberModel(12, 8, 24, 1));
        fontSizeSpinner.setFont(ModernTheme.Fonts.BODY);
        customizeSpinner(fontSizeSpinner);
        
        enableSpeechCheckBox = new JCheckBox("启用语音识别功能");
        enableSpeechCheckBox.setFont(ModernTheme.Fonts.BODY);
        enableSpeechCheckBox.setBackground(ModernTheme.getBackground());
        enableSpeechCheckBox.setForeground(ModernTheme.getTextPrimary());
        
        // 语音识别配置组件
        speechProviderComboBox = new JComboBox<>(new String[]{"Azure", "Google", "百度"});
        speechProviderComboBox.setFont(ModernTheme.Fonts.BODY);
        customizeComboBox(speechProviderComboBox);
        
        speechLanguageComboBox = new JComboBox<>(new String[]{"zh-CN", "en-US", "ja-JP", "ko-KR"});
        speechLanguageComboBox.setFont(ModernTheme.Fonts.BODY);
        customizeComboBox(speechLanguageComboBox);
        
        speechSampleRateSpinner = new JSpinner(new SpinnerNumberModel(16000, 8000, 48000, 1000));
        speechSampleRateSpinner.setFont(ModernTheme.Fonts.BODY);
        customizeSpinner(speechSampleRateSpinner);
        
        // Azure配置
        azureSubscriptionKeyField = new ModernTextField("Azure订阅密钥", 30);
        azureRegionField = new ModernTextField("区域（如：eastasia）", 20);
        azureEndpointField = new ModernTextField("Azure端点URL", 40);
        
        // Google配置
        googleApiKeyField = new ModernTextField("Google Cloud API密钥", 30);
        googleEndpointField = new ModernTextField("Google端点URL", 40);
        
        // 百度配置
        baiduAppIdField = new ModernTextField("百度应用ID", 20);
        baiduApiKeyField = new ModernTextField("百度API Key", 30);
        baiduSecretKeyField = new ModernTextField("百度Secret Key", 30);
        
        // 按钮
        saveButton = new ModernButton("保存设置", ModernButton.ButtonType.PRIMARY);
        cancelButton = new ModernButton("取消", ModernButton.ButtonType.SECONDARY);
        testButton = new ModernButton("测试连接", ModernButton.ButtonType.OUTLINE);
        resetButton = new ModernButton("重置默认", ModernButton.ButtonType.WARNING);
        
        // 滑块值变化监听
        temperatureSlider.addChangeListener(e -> {
            double value = temperatureSlider.getValue() / 100.0;
            temperatureValueLabel.setText(String.format("%.2f", value));
        });
    }
    
    private void customizeSpinner(JSpinner spinner) {
        JComponent editor = spinner.getEditor();
        if (editor instanceof JSpinner.DefaultEditor) {
            JTextField textField = ((JSpinner.DefaultEditor) editor).getTextField();
            textField.setBackground(ModernTheme.getSurface());
            textField.setForeground(ModernTheme.getTextPrimary());
            textField.setBorder(BorderFactory.createCompoundBorder(
                new RoundedBorder(ModernTheme.getBorder(), ModernTheme.Dimensions.BORDER_RADIUS),
                BorderFactory.createEmptyBorder(5, 10, 5, 10)
            ));
        }
        
        spinner.setBorder(null);
        spinner.setBackground(ModernTheme.getBackground());
    }
    
    private void customizeSlider(JSlider slider) {
        slider.setUI(new javax.swing.plaf.basic.BasicSliderUI(slider) {
            @Override
            public void paintTrack(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                Rectangle trackBounds = trackRect;
                g2.setColor(ModernTheme.getBorder());
                g2.fillRoundRect(trackBounds.x, trackBounds.y + trackBounds.height / 2 - 2,
                               trackBounds.width, 4, 4, 4);
                
                // 绘制进度部分
                int progressWidth = (int) ((double) (slider.getValue() - slider.getMinimum()) / 
                                          (slider.getMaximum() - slider.getMinimum()) * trackBounds.width);
                g2.setColor(ModernTheme.getPrimary());
                g2.fillRoundRect(trackBounds.x, trackBounds.y + trackBounds.height / 2 - 2,
                               progressWidth, 4, 4, 4);
                
                g2.dispose();
            }
            
            @Override
            public void paintThumb(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                Rectangle thumbBounds = thumbRect;
                g2.setColor(ModernTheme.getShadowLight());
                g2.fillOval(thumbBounds.x + 1, thumbBounds.y + 1, thumbBounds.width, thumbBounds.height);
                
                g2.setColor(ModernTheme.getPrimary());
                g2.fillOval(thumbBounds.x, thumbBounds.y, thumbBounds.width, thumbBounds.height);
                
                g2.setColor(Color.WHITE);
                g2.fillOval(thumbBounds.x + 3, thumbBounds.y + 3, 
                           thumbBounds.width - 6, thumbBounds.height - 6);
                
                g2.dispose();
            }
        });
    }
    
    private void customizeComboBox(JComboBox<?> comboBox) {
        comboBox.setBackground(ModernTheme.getSurface());
        comboBox.setForeground(ModernTheme.getTextPrimary());
        comboBox.setBorder(new RoundedBorder(ModernTheme.getBorder(), ModernTheme.Dimensions.BORDER_RADIUS));
        
        // 自定义渲染器
        comboBox.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value,
                                                        int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                
                setFont(ModernTheme.Fonts.BODY);
                setBorder(BorderFactory.createEmptyBorder(8, 12, 8, 12));
                
                if (isSelected) {
                    setBackground(ModernTheme.getPrimary());
                    setForeground(Color.WHITE);
                } else {
                    setBackground(ModernTheme.getSurface());
                    setForeground(ModernTheme.getTextPrimary());
                }
                
                return this;
            }
        });
    }
    
    private void setupLayout() {
        setLayout(new BorderLayout());
        
        // 主面板
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBackground(ModernTheme.getBackground());
        mainPanel.setBorder(BorderFactory.createEmptyBorder(
            ModernTheme.Dimensions.PADDING_LARGE,
            ModernTheme.Dimensions.PADDING_LARGE,
            ModernTheme.Dimensions.PADDING_LARGE,
            ModernTheme.Dimensions.PADDING_LARGE
        ));
        
        // 标题
        JLabel titleLabel = new JLabel("设置");
        titleLabel.setFont(ModernTheme.Fonts.TITLE);
        titleLabel.setForeground(ModernTheme.getTextPrimary());
        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        mainPanel.add(titleLabel);
        mainPanel.add(Box.createVerticalStrut(ModernTheme.Dimensions.PADDING_LARGE));
        
        // API配置组
        JPanel apiGroup = createGroupPanel("API配置", createApiConfigPanel());
        mainPanel.add(apiGroup);
        mainPanel.add(Box.createVerticalStrut(ModernTheme.Dimensions.PADDING_MEDIUM));
        
        // AI参数组
        JPanel aiGroup = createGroupPanel("AI参数", createAIConfigPanel());
        mainPanel.add(aiGroup);
        mainPanel.add(Box.createVerticalStrut(ModernTheme.Dimensions.PADDING_MEDIUM));
        
        // 界面配置组
        JPanel uiGroup = createGroupPanel("界面配置", createUIConfigPanel());
        mainPanel.add(uiGroup);
        mainPanel.add(Box.createVerticalStrut(ModernTheme.Dimensions.PADDING_MEDIUM));
        
        // 语音识别配置组
        JPanel speechGroup = createGroupPanel("语音识别配置", createSpeechConfigPanel());
        mainPanel.add(speechGroup);
        mainPanel.add(Box.createVerticalStrut(ModernTheme.Dimensions.PADDING_LARGE));
        
        // 按钮面板
        JPanel buttonPanel = createButtonPanel();
        mainPanel.add(buttonPanel);
        
        // 开发者信息面板
        JPanel developerPanel = createDeveloperInfoPanel();
        mainPanel.add(Box.createVerticalStrut(ModernTheme.Dimensions.PADDING_MEDIUM));
        mainPanel.add(developerPanel);
        
        // 滚动面板
        JScrollPane scrollPane = new JScrollPane(mainPanel);
        scrollPane.setBorder(null);
        scrollPane.getViewport().setBackground(ModernTheme.getBackground());
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        
        add(scrollPane, BorderLayout.CENTER);
    }
    
    private JPanel createGroupPanel(String title, JPanel content) {
        JPanel groupPanel = new JPanel(new BorderLayout());
        groupPanel.setBackground(ModernTheme.getSurface());
        groupPanel.setBorder(new RoundedBorder(ModernTheme.getBorder(), ModernTheme.Dimensions.BORDER_RADIUS));
        
        // 标题面板
        JPanel titlePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        titlePanel.setBackground(ModernTheme.getSurface());
        titlePanel.setBorder(BorderFactory.createEmptyBorder(
            ModernTheme.Dimensions.PADDING_MEDIUM,
            ModernTheme.Dimensions.PADDING_MEDIUM,
            0,
            ModernTheme.Dimensions.PADDING_MEDIUM
        ));
        
        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(ModernTheme.Fonts.SUBTITLE);
        titleLabel.setForeground(ModernTheme.getTextPrimary());
        titlePanel.add(titleLabel);
        
        // 内容面板
        content.setBackground(ModernTheme.getSurface());
        content.setBorder(BorderFactory.createEmptyBorder(
            ModernTheme.Dimensions.PADDING_SMALL,
            ModernTheme.Dimensions.PADDING_MEDIUM,
            ModernTheme.Dimensions.PADDING_MEDIUM,
            ModernTheme.Dimensions.PADDING_MEDIUM
        ));
        
        groupPanel.add(titlePanel, BorderLayout.NORTH);
        groupPanel.add(content, BorderLayout.CENTER);
        
        return groupPanel;
    }
    
    private JPanel createApiConfigPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 0, 5, 10);
        gbc.anchor = GridBagConstraints.WEST;
        
        // API Key
        gbc.gridx = 0; gbc.gridy = 0;
        panel.add(createLabel("API Key:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
        panel.add(apiKeyField, gbc);
        
        // API URL
        gbc.gridx = 0; gbc.gridy = 1; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0;
        panel.add(createLabel("API地址:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
        panel.add(apiUrlField, gbc);
        
        // 模型
        gbc.gridx = 0; gbc.gridy = 2; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0;
        panel.add(createLabel("模型:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
        panel.add(modelField, gbc);
        
        return panel;
    }
    
    private JPanel createAIConfigPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 0, 5, 10);
        gbc.anchor = GridBagConstraints.WEST;
        
        // Max Tokens
        gbc.gridx = 0; gbc.gridy = 0;
        panel.add(createLabel("最大Token数:"), gbc);
        gbc.gridx = 1;
        panel.add(maxTokensSpinner, gbc);
        
        // Temperature
        gbc.gridx = 0; gbc.gridy = 1;
        panel.add(createLabel("创造性:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
        
        JPanel tempPanel = new JPanel(new BorderLayout(5, 0));
        tempPanel.setBackground(ModernTheme.getSurface());
        tempPanel.add(temperatureSlider, BorderLayout.CENTER);
        tempPanel.add(temperatureValueLabel, BorderLayout.EAST);
        
        panel.add(tempPanel, gbc);
        
        return panel;
    }
    
    private JPanel createUIConfigPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 0, 5, 10);
        gbc.anchor = GridBagConstraints.WEST;
        
        // 字体大小
        gbc.gridx = 0; gbc.gridy = 0;
        panel.add(createLabel("字体大小:"), gbc);
        gbc.gridx = 1;
        panel.add(fontSizeSpinner, gbc);
        
        return panel;
    }
    
    private JPanel createSpeechConfigPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 0, 5, 10);
        gbc.anchor = GridBagConstraints.WEST;
        
        // 启用语音识别
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        panel.add(enableSpeechCheckBox, gbc);
        
        // 语音服务提供商
        gbc.gridx = 0; gbc.gridy = 1; gbc.gridwidth = 1;
        panel.add(createLabel("服务提供商:"), gbc);
        gbc.gridx = 1;
        panel.add(speechProviderComboBox, gbc);
        
        // 语言
        gbc.gridx = 0; gbc.gridy = 2;
        panel.add(createLabel("识别语言:"), gbc);
        gbc.gridx = 1;
        panel.add(speechLanguageComboBox, gbc);
        
        // 采样率
        gbc.gridx = 0; gbc.gridy = 3;
        panel.add(createLabel("采样率:"), gbc);
        gbc.gridx = 1;
        panel.add(speechSampleRateSpinner, gbc);
        
        // 创建动态配置面板
        speechConfigPanel = new JPanel(new CardLayout());
        speechConfigPanel.setBackground(ModernTheme.getSurface());
        
        // Azure配置面板
        JPanel azurePanel = createAzureConfigPanel();
        speechConfigPanel.add(azurePanel, "Azure");
        
        // Google配置面板
        JPanel googlePanel = createGoogleConfigPanel();
        speechConfigPanel.add(googlePanel, "Google");
        
        // 百度配置面板
        JPanel baiduPanel = createBaiduConfigPanel();
        speechConfigPanel.add(baiduPanel, "百度");
        
        gbc.gridx = 0; gbc.gridy = 4; gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
        panel.add(speechConfigPanel, gbc);
        
        // 添加提供商切换监听器
        speechProviderComboBox.addActionListener(e -> {
            String provider = (String) speechProviderComboBox.getSelectedItem();
            CardLayout layout = (CardLayout) speechConfigPanel.getLayout();
            layout.show(speechConfigPanel, provider);
        });
        
        // 添加启用/禁用监听器
        enableSpeechCheckBox.addActionListener(e -> {
            boolean enabled = enableSpeechCheckBox.isSelected();
            speechProviderComboBox.setEnabled(enabled);
            speechLanguageComboBox.setEnabled(enabled);
            speechSampleRateSpinner.setEnabled(enabled);
            enableSpeechConfigComponents(enabled);
        });
        
        return panel;
    }
    
    private JPanel createAzureConfigPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(ModernTheme.getSurface());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 0, 5, 10);
        gbc.anchor = GridBagConstraints.WEST;
        
        // 订阅密钥
        gbc.gridx = 0; gbc.gridy = 0;
        panel.add(createLabel("订阅密钥:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
        panel.add(azureSubscriptionKeyField, gbc);
        
        // 区域
        gbc.gridx = 0; gbc.gridy = 1; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0;
        panel.add(createLabel("区域:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
        panel.add(azureRegionField, gbc);
        
        // 端点
        gbc.gridx = 0; gbc.gridy = 2; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0;
        panel.add(createLabel("端点:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
        panel.add(azureEndpointField, gbc);
        
        return panel;
    }
    
    private JPanel createGoogleConfigPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(ModernTheme.getSurface());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 0, 5, 10);
        gbc.anchor = GridBagConstraints.WEST;
        
        // API密钥
        gbc.gridx = 0; gbc.gridy = 0;
        panel.add(createLabel("API密钥:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
        panel.add(googleApiKeyField, gbc);
        
        // 端点
        gbc.gridx = 0; gbc.gridy = 1; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0;
        panel.add(createLabel("端点:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
        panel.add(googleEndpointField, gbc);
        
        return panel;
    }
    
    private JPanel createBaiduConfigPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(ModernTheme.getSurface());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 0, 5, 10);
        gbc.anchor = GridBagConstraints.WEST;
        
        // 应用ID
        gbc.gridx = 0; gbc.gridy = 0;
        panel.add(createLabel("应用ID:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
        panel.add(baiduAppIdField, gbc);
        
        // API Key
        gbc.gridx = 0; gbc.gridy = 1; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0;
        panel.add(createLabel("API Key:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
        panel.add(baiduApiKeyField, gbc);
        
        // Secret Key
        gbc.gridx = 0; gbc.gridy = 2; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0;
        panel.add(createLabel("Secret Key:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
        panel.add(baiduSecretKeyField, gbc);
        
        return panel;
    }
    
    private void enableSpeechConfigComponents(boolean enabled) {
        // Azure组件
        azureSubscriptionKeyField.setEnabled(enabled);
        azureRegionField.setEnabled(enabled);
        azureEndpointField.setEnabled(enabled);
        
        // Google组件
        googleApiKeyField.setEnabled(enabled);
        googleEndpointField.setEnabled(enabled);
        
        // 百度组件
        baiduAppIdField.setEnabled(enabled);
        baiduApiKeyField.setEnabled(enabled);
        baiduSecretKeyField.setEnabled(enabled);
    }
    
    private JPanel createButtonPanel() {
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        buttonPanel.setBackground(ModernTheme.getBackground());
        
        buttonPanel.add(resetButton);
        buttonPanel.add(testButton);
        buttonPanel.add(cancelButton);
        buttonPanel.add(saveButton);
        
        return buttonPanel;
    }
    
    private JPanel createDeveloperInfoPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(ModernTheme.getSurface());
        panel.setBorder(new RoundedBorder(ModernTheme.getBorder(), ModernTheme.Dimensions.BORDER_RADIUS));
        
        // 创建信息面板
        JPanel infoPanel = new JPanel(new GridBagLayout());
        infoPanel.setBackground(ModernTheme.getSurface());
        infoPanel.setBorder(BorderFactory.createEmptyBorder(
            ModernTheme.Dimensions.PADDING_MEDIUM,
            ModernTheme.Dimensions.PADDING_MEDIUM,
            ModernTheme.Dimensions.PADDING_MEDIUM,
            ModernTheme.Dimensions.PADDING_MEDIUM
        ));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(2, 0, 2, 0);
        gbc.anchor = GridBagConstraints.CENTER;
        
        // 标题
        gbc.gridx = 0; gbc.gridy = 0;
        JLabel titleLabel = new JLabel("关于软件");
        titleLabel.setFont(ModernTheme.Fonts.SUBTITLE);
        titleLabel.setForeground(ModernTheme.getTextPrimary());
        infoPanel.add(titleLabel, gbc);
        
        // 分隔线
        gbc.gridy = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 0, 10, 0);
        JSeparator separator = new JSeparator();
        separator.setForeground(ModernTheme.getBorder());
        infoPanel.add(separator, gbc);
        
        // 软件名称
        gbc.gridy = 2;
        gbc.fill = GridBagConstraints.NONE;
        gbc.insets = new Insets(2, 0, 2, 0);
        JLabel softwareLabel = new JLabel("AI助手工具 v1.0.0");
        softwareLabel.setFont(ModernTheme.Fonts.BODY.deriveFont(Font.BOLD));
        softwareLabel.setForeground(ModernTheme.getTextPrimary());
        infoPanel.add(softwareLabel, gbc);
        
        // 开发者信息
        gbc.gridy = 3;
        JLabel developerLabel = new JLabel("开发者：Tang EnDong");
        developerLabel.setFont(ModernTheme.Fonts.BODY);
        developerLabel.setForeground(ModernTheme.getTextSecondary());
        infoPanel.add(developerLabel, gbc);
        
        // 版权信息
        gbc.gridy = 4;
        JLabel copyrightLabel = new JLabel("© 2025 Tang EnDong. All rights reserved.");
        copyrightLabel.setFont(ModernTheme.Fonts.SMALL);
        copyrightLabel.setForeground(ModernTheme.getTextMuted());
        infoPanel.add(copyrightLabel, gbc);
        
        // 描述信息
        gbc.gridy = 5;
        gbc.insets = new Insets(8, 0, 2, 0);
        JLabel descriptionLabel = new JLabel("智能语音对话助手，支持多种语音识别服务");
        descriptionLabel.setFont(ModernTheme.Fonts.SMALL);
        descriptionLabel.setForeground(ModernTheme.getTextMuted());
        infoPanel.add(descriptionLabel, gbc);
        
        panel.add(infoPanel, BorderLayout.CENTER);
        
        return panel;
    }
    
    private JLabel createLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(ModernTheme.Fonts.BODY);
        label.setForeground(ModernTheme.getTextPrimary());
        return label;
    }
    
    private void setupEventHandlers() {
        saveButton.addActionListener(e -> saveConfig());
        cancelButton.addActionListener(e -> dispose());
        testButton.addActionListener(e -> testConnection());
        resetButton.addActionListener(e -> resetToDefaults());
    }
    
    private void setupWindow() {
        setTitle("AI助手工具 - 设置");
        setSize(600, 700);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(true);
        
        getContentPane().setBackground(ModernTheme.getBackground());
    }
    
    private void loadConfig() {
        try {
            apiKeyField.setText(configManager.getOpenaiApiKey());
            apiUrlField.setText(configManager.getOpenaiApiUrl());
            modelField.setText(configManager.getModel());
            maxTokensSpinner.setValue(configManager.getMaxTokens());
            temperatureSlider.setValue((int) (configManager.getTemperature() * 100));
            
            fontSizeSpinner.setValue(configManager.getUIFontSize());
            enableSpeechCheckBox.setSelected(configManager.isSpeechEnabled());
            
            // 加载语音识别配置
            String provider = configManager.getSpeechProvider();
            speechProviderComboBox.setSelectedItem(provider.substring(0, 1).toUpperCase() + provider.substring(1));
            
            String language = configManager.getSpeechLanguage();
            speechLanguageComboBox.setSelectedItem(language);
            
            speechSampleRateSpinner.setValue(configManager.getSpeechSampleRate());
            
            // Azure配置
            azureSubscriptionKeyField.setText(configManager.getAzureSubscriptionKey());
            azureRegionField.setText(configManager.getAzureRegion());
            azureEndpointField.setText(configManager.getAzureEndpoint());
            
            // Google配置
            googleApiKeyField.setText(configManager.getGoogleApiKey());
            googleEndpointField.setText(configManager.getGoogleEndpoint());
            
            // 百度配置
            baiduAppIdField.setText(configManager.getBaiduAppId());
            baiduApiKeyField.setText(configManager.getBaiduApiKey());
            baiduSecretKeyField.setText(configManager.getBaiduSecretKey());
            
            // 更新界面状态
            boolean speechEnabled = configManager.isSpeechEnabled();
            enableSpeechConfigComponents(speechEnabled);
            speechProviderComboBox.setEnabled(speechEnabled);
            speechLanguageComboBox.setEnabled(speechEnabled);
            speechSampleRateSpinner.setEnabled(speechEnabled);
            
            // 显示对应的配置面板
            CardLayout layout = (CardLayout) speechConfigPanel.getLayout();
            layout.show(speechConfigPanel, provider.substring(0, 1).toUpperCase() + provider.substring(1));
            
        } catch (Exception e) {
            logger.error("加载配置时发生错误", e);
            showMessage("加载配置失败: " + e.getMessage(), MessageType.ERROR);
        }
    }
    
    private void saveConfig() {
        try {
            // 验证必填字段
            if (apiKeyField.getText().trim().isEmpty()) {
                apiKeyField.showError();
                showMessage("请输入API Key", MessageType.ERROR);
                return;
            }
            
            if (apiUrlField.getText().trim().isEmpty()) {
                apiUrlField.showError();
                showMessage("请输入API地址", MessageType.ERROR);
                return;
            }
            
            // 保存配置
            configManager.setOpenaiApiKey(apiKeyField.getText().trim());
            configManager.setOpenaiApiUrl(apiUrlField.getText().trim());
            configManager.setModel(modelField.getText().trim());
            configManager.setMaxTokens((Integer) maxTokensSpinner.getValue());
            configManager.setTemperature(temperatureSlider.getValue() / 100.0);
            
            // 保存界面配置
            configManager.setUITheme("light"); // 固定为亮色主题
            configManager.setUIFontSize((Integer) fontSizeSpinner.getValue());
            configManager.setSpeechEnabled(enableSpeechCheckBox.isSelected());
            
            // 保存语音识别配置
            String provider = ((String) speechProviderComboBox.getSelectedItem()).toLowerCase();
            configManager.setSpeechProvider(provider);
            configManager.setSpeechLanguage((String) speechLanguageComboBox.getSelectedItem());
            configManager.setSpeechSampleRate((Integer) speechSampleRateSpinner.getValue());
            
            // Azure配置
            configManager.setAzureSubscriptionKey(azureSubscriptionKeyField.getText().trim());
            configManager.setAzureRegion(azureRegionField.getText().trim());
            configManager.setAzureEndpoint(azureEndpointField.getText().trim());
            
            // Google配置
            configManager.setGoogleApiKey(googleApiKeyField.getText().trim());
            configManager.setGoogleEndpoint(googleEndpointField.getText().trim());
            
            // 百度配置
            configManager.setBaiduAppId(baiduAppIdField.getText().trim());
            configManager.setBaiduApiKey(baiduApiKeyField.getText().trim());
            configManager.setBaiduSecretKey(baiduSecretKeyField.getText().trim());
            
            configManager.saveConfig();
            
            // 显示成功动画
            saveButton.setEnabled(false);
            UIAnimator.pulse(saveButton, 0.9f, 1.1f, 300);
            
            Timer enableTimer = new Timer(500, e -> {
                saveButton.setEnabled(true);
                showMessage("配置保存成功！", MessageType.SUCCESS);
                
                Timer closeTimer = new Timer(1500, evt -> dispose());
                closeTimer.setRepeats(false);
                closeTimer.start();
            });
            enableTimer.setRepeats(false);
            enableTimer.start();
            
        } catch (Exception e) {
            logger.error("保存配置时发生错误", e);
            showMessage("保存配置失败: " + e.getMessage(), MessageType.ERROR);
        }
    }
    
    private void testConnection() {
        String apiKey = apiKeyField.getText().trim();
        String apiUrl = apiUrlField.getText().trim();
        
        if (apiKey.isEmpty()) {
            apiKeyField.showError();
            showMessage("请输入API Key", MessageType.ERROR);
            return;
        }
        
        if (apiUrl.isEmpty()) {
            apiUrlField.showError();
            showMessage("请输入API地址", MessageType.ERROR);
            return;
        }
        
        // 禁用按钮并显示加载状态
        testButton.setEnabled(false);
        testButton.setText("测试中...");
        
        // 在后台线程中进行实际的连接测试
        new Thread(() -> {
            try {
                // 临时保存当前配置
                String originalApiKey = configManager.getOpenaiApiKey();
                String originalApiUrl = configManager.getOpenaiApiUrl();
                
                // 设置临时配置进行测试
                configManager.setOpenaiApiKey(apiKey);
                configManager.setOpenaiApiUrl(apiUrl);
                
                // 创建AI服务实例进行测试
                com.aiquiztool.service.AIService testService = new com.aiquiztool.service.AIService();
                boolean success = testService.testConnection();
                
                // 恢复原始配置
                configManager.setOpenaiApiKey(originalApiKey);
                configManager.setOpenaiApiUrl(originalApiUrl);
                
                // 在UI线程中更新界面
                SwingUtilities.invokeLater(() -> {
                    testButton.setEnabled(true);
                    testButton.setText("测试连接");
                    
                    if (success) {
                        showMessage("连接测试成功！", MessageType.SUCCESS);
                    } else {
                        showMessage("连接测试失败", MessageType.ERROR);
                    }
                });
                
            } catch (Exception e) {
                // 在UI线程中显示错误
                SwingUtilities.invokeLater(() -> {
                    testButton.setEnabled(true);
                    testButton.setText("测试连接");
                    showMessage("连接测试失败: " + e.getMessage(), MessageType.ERROR);
                });
            }
        }).start();
    }
    
    private void resetToDefaults() {
        int result = JOptionPane.showConfirmDialog(
            this,
            "确定要重置所有设置为默认值吗？",
            "重置确认",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.QUESTION_MESSAGE
        );
        
        if (result == JOptionPane.YES_OPTION) {
            try {
                configManager.resetToDefault();
                loadConfig();
                showMessage("设置已重置为默认值", MessageType.SUCCESS);
            } catch (Exception e) {
                logger.error("重置配置时发生错误", e);
                showMessage("重置配置失败: " + e.getMessage(), MessageType.ERROR);
            }
        }
    }
    
    private void showMessage(String message, MessageType type) {
        // 创建临时消息标签
        JLabel messageLabel = new JLabel(message);
        messageLabel.setFont(ModernTheme.Fonts.SMALL);
        messageLabel.setOpaque(true);
        messageLabel.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        
        Color bgColor, textColor;
        switch (type) {
            case SUCCESS:
                bgColor = ModernTheme.getSuccess();
                textColor = Color.WHITE;
                break;
            case ERROR:
                bgColor = ModernTheme.getDanger();
                textColor = Color.WHITE;
                break;
            case WARNING:
                bgColor = ModernTheme.getWarning();
                textColor = ModernTheme.getTextPrimary();
                break;
            default:
                bgColor = ModernTheme.getPrimary();
                textColor = Color.WHITE;
                break;
        }
        
        messageLabel.setBackground(bgColor);
        messageLabel.setForeground(textColor);
        
        // 可以在这里实现更复杂的消息显示逻辑
        // 暂时使用系统通知
        if (SystemTray.isSupported()) {
            try {
                SystemTray tray = SystemTray.getSystemTray();
                TrayIcon.MessageType msgType = type == MessageType.ERROR ? 
                    TrayIcon.MessageType.ERROR : TrayIcon.MessageType.INFO;
                
                // 这里需要一个TrayIcon实例，暂时跳过
            } catch (Exception e) {
                // 忽略通知错误
            }
        }
    }
    
    private enum MessageType {
        INFO, SUCCESS, WARNING, ERROR
    }
    
    // 圆角边框类
    private static class RoundedBorder implements Border {
        private Color color;
        private int radius;
        
        public RoundedBorder(Color color, int radius) {
            this.color = color;
            this.radius = radius;
        }
        
        @Override
        public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(color);
            g2.drawRoundRect(x, y, width - 1, height - 1, radius, radius);
            g2.dispose();
        }
        
        @Override
        public Insets getBorderInsets(Component c) {
            return new Insets(1, 1, 1, 1);
        }
        
        @Override
        public boolean isBorderOpaque() {
            return false;
        }
    }
}
