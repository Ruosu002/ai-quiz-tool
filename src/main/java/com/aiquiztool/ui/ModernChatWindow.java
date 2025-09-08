package com.aiquiztool.ui;

import com.aiquiztool.service.AIService;
import com.aiquiztool.service.SpeechRecognitionService;
import com.aiquiztool.ui.theme.ModernTheme;
import com.aiquiztool.ui.animation.UIAnimator;
import com.aiquiztool.ui.components.ModernButton;
import com.aiquiztool.ui.components.ModernTextField;
import com.aiquiztool.ui.utils.IconManager;
import com.aiquiztool.config.ConfigManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

/**
 * 聊天窗口
 * @author Tang En Dong
 */
public class ModernChatWindow extends JFrame {
    private static final Logger logger = LoggerFactory.getLogger(ModernChatWindow.class);
    
    private JPanel chatPanel;
    private JScrollPane scrollPane;
    private ModernTextField inputField;
    private ModernButton sendButton;
    private ModernButton recordButton;
    private JLabel statusLabel;
    
    private AIService aiService;
    private SpeechRecognitionService speechService;
    private ConfigManager configManager;
    
    private boolean isRecording = false;
    
    public ModernChatWindow() {
        configManager = ConfigManager.getInstance();
        initializeComponents();
        setupLayout();
        setupEventHandlers();
        initializeServices();
        setupWindow();
    }
    
    private void initializeComponents() {
        chatPanel = new JPanel();
        chatPanel.setLayout(new BoxLayout(chatPanel, BoxLayout.Y_AXIS));
        chatPanel.setBackground(ModernTheme.getBackground());
        chatPanel.setBorder(BorderFactory.createEmptyBorder(
            ModernTheme.Dimensions.PADDING_MEDIUM,
            ModernTheme.Dimensions.PADDING_MEDIUM,
            ModernTheme.Dimensions.PADDING_MEDIUM,
            ModernTheme.Dimensions.PADDING_MEDIUM
        ));
        
        scrollPane = new JScrollPane(chatPanel);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.setBorder(null);
        scrollPane.getViewport().setBackground(ModernTheme.getBackground());
        
        customizeScrollBar(scrollPane);
        inputField = new ModernTextField("输入您的问题...", 30);
        sendButton = new ModernButton("发送", ModernButton.ButtonType.PRIMARY);
        sendButton.setIcon(IconManager.getSendIcon(16));
        
        recordButton = new ModernButton("", ModernButton.ButtonType.OUTLINE);
        recordButton.setIcon(IconManager.getMicIcon(18));
        recordButton.setPreferredSize(new Dimension(50, ModernTheme.Dimensions.BUTTON_HEIGHT));
        recordButton.setToolTipText("语音输入");
        
        statusLabel = new JLabel("就绪");
        statusLabel.setFont(ModernTheme.Fonts.SMALL);
        statusLabel.setForeground(ModernTheme.getTextMuted());
        
        addWelcomeMessage();
    }
    
    
    private void customizeScrollBar(JScrollPane scrollPane) {
        JScrollBar verticalScrollBar = scrollPane.getVerticalScrollBar();
        verticalScrollBar.setUI(new javax.swing.plaf.basic.BasicScrollBarUI() {
            @Override
            protected void configureScrollBarColors() {
                this.thumbColor = ModernTheme.getBorder();
                this.trackColor = ModernTheme.getBackground();
            }
            
            @Override
            protected JButton createDecreaseButton(int orientation) {
                return createZeroButton();
            }
            
            @Override
            protected JButton createIncreaseButton(int orientation) {
                return createZeroButton();
            }
            
            private JButton createZeroButton() {
                JButton button = new JButton();
                button.setPreferredSize(new Dimension(0, 0));
                button.setMinimumSize(new Dimension(0, 0));
                button.setMaximumSize(new Dimension(0, 0));
                return button;
            }
            
            @Override
            protected void paintThumb(Graphics g, JComponent c, Rectangle thumbBounds) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(thumbColor);
                g2.fillRoundRect(thumbBounds.x + 2, thumbBounds.y, 
                               thumbBounds.width - 4, thumbBounds.height, 
                               6, 6);
                g2.dispose();
            }
        });
        
        verticalScrollBar.setPreferredSize(new Dimension(8, 0));
    }
    
    private void setupLayout() {
        setLayout(new BorderLayout());
        
        // 顶部工具栏
        JPanel topPanel = createTopPanel();
        
        // 底部输入面板
        JPanel bottomPanel = createBottomPanel();
        
        // 添加到主窗口
        add(topPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
        add(bottomPanel, BorderLayout.SOUTH);
        
        // 设置背景色
        getContentPane().setBackground(ModernTheme.getBackground());
    }
    
    private JPanel createTopPanel() {
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(ModernTheme.getSurface());
        topPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 0, 1, 0, ModernTheme.getBorder()),
            BorderFactory.createEmptyBorder(
                ModernTheme.Dimensions.PADDING_MEDIUM,
                ModernTheme.Dimensions.PADDING_MEDIUM,
                ModernTheme.Dimensions.PADDING_MEDIUM,
                ModernTheme.Dimensions.PADDING_MEDIUM
            )
        ));
        
        // 标题
        JLabel titleLabel = new JLabel("AI助手");
        titleLabel.setFont(ModernTheme.Fonts.TITLE);
        titleLabel.setForeground(ModernTheme.getTextPrimary());
        
        topPanel.add(titleLabel, BorderLayout.WEST);
        topPanel.add(statusLabel, BorderLayout.CENTER);
        
        return topPanel;
    }
    
    private JPanel createBottomPanel() {
        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.setBackground(ModernTheme.getSurface());
        bottomPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(1, 0, 0, 0, ModernTheme.getBorder()),
            BorderFactory.createEmptyBorder(
                ModernTheme.Dimensions.PADDING_MEDIUM,
                ModernTheme.Dimensions.PADDING_MEDIUM,
                ModernTheme.Dimensions.PADDING_MEDIUM,
                ModernTheme.Dimensions.PADDING_MEDIUM
            )
        ));
        
        // 输入面板
        JPanel inputPanel = new JPanel(new BorderLayout(ModernTheme.Dimensions.PADDING_SMALL, 0));
        inputPanel.setBackground(ModernTheme.getSurface());
        inputPanel.add(recordButton, BorderLayout.WEST);
        inputPanel.add(inputField, BorderLayout.CENTER);
        inputPanel.add(sendButton, BorderLayout.EAST);
        
        bottomPanel.add(inputPanel, BorderLayout.CENTER);
        
        return bottomPanel;
    }
    
    private void setupEventHandlers() {
        // 发送按钮事件
        sendButton.addActionListener(e -> sendMessage());
        
        // 输入框回车事件
        inputField.addActionListener(e -> sendMessage());
        
        // 录音按钮事件
        recordButton.addActionListener(e -> toggleRecording());
        
    }
    
    private void initializeServices() {
        try {
            aiService = new AIService();
            speechService = new SpeechRecognitionService();
        } catch (Exception e) {
            logger.error("初始化服务时发生错误", e);
            showNotification("初始化服务失败: " + e.getMessage(), NotificationType.ERROR);
        }
    }
    
    private void setupWindow() {
        setTitle("AI助手工具");
        setSize(configManager.getUIWindowWidth(), configManager.getUIWindowHeight());
        setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
        setLocationRelativeTo(null);
        
        // 添加窗口监听器
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                setVisible(false);
            }
        });
        
        // 设置窗口图标（如果有的话）
        try {
            // 这里可以设置窗口图标
        } catch (Exception e) {
            // 忽略图标设置错误
        }
    }
    
    private void addWelcomeMessage() {
        SwingUtilities.invokeLater(() -> {
            addMessage("AI助手", "您好！我是您的AI助手。有什么可以帮助您的吗？", MessageType.ASSISTANT, true);
        });
    }
    
    private void sendMessage() {
        String message = inputField.getText().trim();
        if (message.isEmpty()) {
            inputField.showError();
            return;
        }
        
        // 显示用户消息
        addMessage("您", message, MessageType.USER, false);
        inputField.setText("");
        
        // 更新状态
        updateStatus("正在思考...", StatusType.PROCESSING);
        
        // 异步发送到AI服务
        new Thread(() -> {
            try {
                String response = aiService.getResponse(message);
                SwingUtilities.invokeLater(() -> {
                    addMessage("AI助手", response, MessageType.ASSISTANT, true);
                    updateStatus("就绪", StatusType.READY);
                });
            } catch (Exception e) {
                logger.error("获取AI回复时发生错误", e);
                SwingUtilities.invokeLater(() -> {
                    showNotification("获取AI回复失败: " + e.getMessage(), NotificationType.ERROR);
                    updateStatus("就绪", StatusType.READY);
                });
            }
        }).start();
    }
    
    private void addMessage(String sender, String content, MessageType type, boolean animate) {
        SwingUtilities.invokeLater(() -> {
            JPanel messagePanel = createMessagePanel(sender, content, type);
            chatPanel.add(messagePanel);
            chatPanel.add(Box.createVerticalStrut(ModernTheme.Dimensions.PADDING_SMALL));
            
            if (animate) {
                // 添加淡入动画
                messagePanel.setOpaque(false);
                UIAnimator.fadeIn(messagePanel, 300);
            }
            
            // 滚动到底部
            SwingUtilities.invokeLater(() -> {
                chatPanel.revalidate();
                JScrollBar verticalScrollBar = scrollPane.getVerticalScrollBar();
                verticalScrollBar.setValue(verticalScrollBar.getMaximum());
            });
        });
    }
    
    private JPanel createMessagePanel(String sender, String content, MessageType type) {
        JPanel messagePanel = new JPanel(new BorderLayout());
        messagePanel.setBackground(ModernTheme.getBackground());
        messagePanel.setBorder(BorderFactory.createEmptyBorder(5, 0, 5, 0));
        
        // 头像
        JLabel avatarLabel = new JLabel();
        ImageIcon avatar = type == MessageType.USER ? 
            IconManager.getUserIcon(32) : 
            IconManager.getAIIcon(32);
        avatarLabel.setIcon(avatar);
        avatarLabel.setVerticalAlignment(SwingConstants.TOP);
        avatarLabel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        
        // 消息气泡容器
        JPanel bubbleContainer = new JPanel(new BorderLayout());
        bubbleContainer.setBackground(ModernTheme.getBackground());
        
        // 消息气泡
        JPanel bubblePanel = new JPanel(new BorderLayout());
        bubblePanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE));
        
        if (type == MessageType.USER) {
            bubblePanel.setBackground(ModernTheme.getPrimary());
            messagePanel.add(Box.createHorizontalGlue());
        } else {
            bubblePanel.setBackground(ModernTheme.getSurface());
        }
        
        bubblePanel.setBorder(new RoundedMessageBorder(
            bubblePanel.getBackground(), 
            ModernTheme.Dimensions.BORDER_RADIUS_LARGE
        ));
        
        // 发送者标签
        JLabel senderLabel = new JLabel(sender);
        senderLabel.setFont(ModernTheme.Fonts.SMALL.deriveFont(Font.BOLD));
        senderLabel.setForeground(type == MessageType.USER ? Color.WHITE : ModernTheme.getTextSecondary());
        senderLabel.setBorder(BorderFactory.createEmptyBorder(
            ModernTheme.Dimensions.PADDING_SMALL,
            ModernTheme.Dimensions.PADDING_MEDIUM,
            2,
            ModernTheme.Dimensions.PADDING_MEDIUM
        ));
        
        // 消息内容
        JTextArea contentArea = new JTextArea(content);
        contentArea.setFont(ModernTheme.Fonts.BODY);
        contentArea.setForeground(type == MessageType.USER ? Color.WHITE : ModernTheme.getTextPrimary());
        contentArea.setBackground(bubblePanel.getBackground());
        contentArea.setEditable(false);
        contentArea.setLineWrap(true);
        contentArea.setWrapStyleWord(true);
        contentArea.setBorder(BorderFactory.createEmptyBorder(
            0,
            ModernTheme.Dimensions.PADDING_MEDIUM,
            ModernTheme.Dimensions.PADDING_SMALL,
            ModernTheme.Dimensions.PADDING_MEDIUM
        ));
        
        // 时间戳
        String timestamp = LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm"));
        JLabel timeLabel = new JLabel(timestamp);
        timeLabel.setFont(ModernTheme.Fonts.SMALL);
        timeLabel.setForeground(type == MessageType.USER ? 
                               new Color(255, 255, 255, 180) : 
                               ModernTheme.getTextMuted());
        timeLabel.setBorder(BorderFactory.createEmptyBorder(
            0,
            ModernTheme.Dimensions.PADDING_MEDIUM,
            ModernTheme.Dimensions.PADDING_SMALL,
            ModernTheme.Dimensions.PADDING_MEDIUM
        ));
        
        bubblePanel.add(senderLabel, BorderLayout.NORTH);
        bubblePanel.add(contentArea, BorderLayout.CENTER);
        bubblePanel.add(timeLabel, BorderLayout.SOUTH);
        
        // 设置最大宽度
        int maxWidth = (int) (getWidth() * 0.6);
        bubblePanel.setMaximumSize(new Dimension(maxWidth, Integer.MAX_VALUE));
        
        bubbleContainer.add(bubblePanel, BorderLayout.CENTER);
        
        if (type == MessageType.USER) {
            messagePanel.add(bubbleContainer, BorderLayout.CENTER);
            messagePanel.add(avatarLabel, BorderLayout.EAST);
        } else {
            messagePanel.add(avatarLabel, BorderLayout.WEST);
            messagePanel.add(bubbleContainer, BorderLayout.CENTER);
        }
        
        return messagePanel;
    }
    
    private void toggleRecording() {
        if (!speechService.isAvailable()) {
            showNotification("请先在设置中配置语音识别服务", NotificationType.WARNING);
            return;
        }
        
        if (isRecording) {
            stopRecording();
        } else {
            startRecording();
        }
    }
    
    private void startRecording() {
        try {
            speechService.startRecording();
            isRecording = true;
            updateRecordingUI(true);
            updateStatus("正在录音...", StatusType.PROCESSING);
            logger.info("开始录音");
            
        } catch (Exception e) {
            logger.error("开始录音失败", e);
            showNotification("录音失败: " + e.getMessage(), NotificationType.ERROR);
            updateRecordingUI(false);
        }
    }
    
    private void stopRecording() {
        try {
            updateStatus("正在识别语音...", StatusType.PROCESSING);
            
            speechService.stopRecording().thenAccept(result -> {
                SwingUtilities.invokeLater(() -> {
                    isRecording = false;
                    updateRecordingUI(false);
                    
                    if (result != null && !result.trim().isEmpty()) {
                        inputField.setText(result);
                        updateStatus("语音识别完成", StatusType.SUCCESS);
                        
                        // 自动发送消息（可选）
                        Timer autoSendTimer = new Timer(1000, e -> {
                            if (!inputField.getText().trim().isEmpty()) {
                                sendMessage();
                            }
                        });
                        autoSendTimer.setRepeats(false);
                        autoSendTimer.start();
                        
                    } else {
                        updateStatus("未识别到语音内容", StatusType.ERROR);
                    }
                });
            }).exceptionally(throwable -> {
                SwingUtilities.invokeLater(() -> {
                    isRecording = false;
                    updateRecordingUI(false);
                    updateStatus("语音识别失败", StatusType.ERROR);
                    showNotification("语音识别出错: " + throwable.getMessage(), NotificationType.ERROR);
                });
                return null;
            });
            
        } catch (Exception e) {
            logger.error("停止录音失败", e);
            isRecording = false;
            updateRecordingUI(false);
            showNotification("停止录音失败: " + e.getMessage(), NotificationType.ERROR);
        }
    }
    
    private void updateRecordingUI(boolean recording) {
        if (recording) {
            recordButton.setButtonType(ModernButton.ButtonType.DANGER);
            recordButton.setText("停止");
            recordButton.setIcon(null);
            
            // 添加录音动画效果
            UIAnimator.pulse(recordButton, 0.9f, 1.1f, 1000);
            
            // 禁用发送按钮
            sendButton.setEnabled(false);
            inputField.setEnabled(false);
            
        } else {
            recordButton.setButtonType(ModernButton.ButtonType.OUTLINE);
            recordButton.setText("");
            recordButton.setIcon(IconManager.getMicIcon(18));
            
            // 启用发送按钮
            sendButton.setEnabled(true);
            inputField.setEnabled(true);
            
            updateStatus("就绪", StatusType.READY);
        }
    }
    
    
    
    private void updateTheme() {
        // 更新所有组件的颜色
        getContentPane().setBackground(ModernTheme.getBackground());
        chatPanel.setBackground(ModernTheme.getBackground());
        scrollPane.getViewport().setBackground(ModernTheme.getBackground());
        
        // 递归更新所有子组件
        updateComponentTheme(this);
        repaint();
    }
    
    private void updateComponentTheme(Container container) {
        for (Component component : container.getComponents()) {
            if (component instanceof Container) {
                updateComponentTheme((Container) component);
            }
            // 这里可以添加特定组件的主题更新逻辑
        }
    }
    
    private void updateStatus(String status, StatusType type) {
        statusLabel.setText(status);
        
        Color statusColor;
        switch (type) {
            case PROCESSING:
                statusColor = ModernTheme.getWarning();
                break;
            case ERROR:
                statusColor = ModernTheme.getDanger();
                break;
            case SUCCESS:
                statusColor = ModernTheme.getSuccess();
                break;
            default:
                statusColor = ModernTheme.getTextMuted();
                break;
        }
        
        UIAnimator.animateColor(statusLabel, statusLabel.getForeground(), statusColor, 200,
            color -> {
                statusLabel.setForeground(color);
                statusLabel.repaint();
            });
    }
    
    private void showNotification(String message, NotificationType type) {
        // 这里可以实现通知显示逻辑
        // 暂时使用状态标签显示
        StatusType statusType;
        switch (type) {
            case ERROR:
                statusType = StatusType.ERROR;
                break;
            case SUCCESS:
                statusType = StatusType.SUCCESS;
                break;
            case WARNING:
                statusType = StatusType.PROCESSING;
                break;
            default:
                statusType = StatusType.READY;
                break;
        }
        
        updateStatus(message, statusType);
        
        // 3秒后恢复正常状态
        Timer resetTimer = new Timer(3000, e -> updateStatus("就绪", StatusType.READY));
        resetTimer.setRepeats(false);
        resetTimer.start();
    }
    
    // 枚举定义
    private enum MessageType {
        USER, ASSISTANT
    }
    
    private enum StatusType {
        READY, PROCESSING, ERROR, SUCCESS
    }
    
    private enum NotificationType {
        INFO, SUCCESS, WARNING, ERROR
    }
    
    // 圆角消息边框
    private static class RoundedMessageBorder implements Border {
        private Color color;
        private int radius;
        
        public RoundedMessageBorder(Color color, int radius) {
            this.color = color;
            this.radius = radius;
        }
        
        @Override
        public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            
            // 绘制阴影
            g2.setColor(ModernTheme.getShadowLight());
            g2.fillRoundRect(x + 1, y + 1, width - 2, height - 2, radius, radius);
            
            // 绘制背景
            g2.setColor(color);
            g2.fillRoundRect(x, y, width - 1, height - 1, radius, radius);
            
            g2.dispose();
        }
        
        @Override
        public Insets getBorderInsets(Component c) {
            return new Insets(0, 0, 0, 0);
        }
        
        @Override
        public boolean isBorderOpaque() {
            return false;
        }
    }
}
