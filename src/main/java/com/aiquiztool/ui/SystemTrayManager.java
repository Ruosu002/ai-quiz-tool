package com.aiquiztool.ui;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;

/**
 * 托盘管理器
 * @author Tang En Dong
 */
public class SystemTrayManager {
    private static final Logger logger = LoggerFactory.getLogger(SystemTrayManager.class);
    
    private SystemTray systemTray;
    private TrayIcon trayIcon;
    private ModernChatWindow chatWindow;
    private ModernConfigWindow configWindow;
    
    public void initialize() throws Exception {
        if (!SystemTray.isSupported()) {
            throw new UnsupportedOperationException("系统不支持系统托盘功能");
        }
        
        systemTray = SystemTray.getSystemTray();
        
        createTrayIcon();
        chatWindow = new ModernChatWindow();
        configWindow = new ModernConfigWindow();
        
        logger.info("系统托盘管理器初始化完成");
    }
    
    private void createTrayIcon() throws Exception {
        Image icon = createTrayIconImage();
        
        trayIcon = new TrayIcon(icon, "AI助手工具", createPopupMenu());
        trayIcon.setImageAutoSize(true);
        
        trayIcon.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getButton() == MouseEvent.BUTTON1) {
                    toggleChatWindow();
                }
            }
        });
        systemTray.add(trayIcon);
        
        logger.info("系统托盘图标创建完成");
    }
    
    private Image createTrayIconImage() {
        BufferedImage image = new BufferedImage(16, 16, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = image.createGraphics();
        
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        g2d.setColor(new Color(0, 120, 215));
        g2d.fillOval(2, 2, 12, 12);
        
        g2d.setColor(Color.WHITE);
        g2d.setStroke(new BasicStroke(2));
        g2d.drawLine(6, 6, 10, 6);
        g2d.drawLine(6, 8, 10, 8);
        g2d.drawLine(6, 10, 8, 10);
        
        g2d.dispose();
        return image;
    }
    
    private PopupMenu createPopupMenu() {
        PopupMenu popup = new PopupMenu();
        
        // 开始对话菜单项
        MenuItem startChatItem = new MenuItem("Start Chat");
        startChatItem.addActionListener(e -> toggleChatWindow());
        popup.add(startChatItem);
        
        // 分隔线
        popup.addSeparator();
        
        // 设置菜单项
        MenuItem settingsItem = new MenuItem("Settings");
        settingsItem.addActionListener(e -> showConfigWindow());
        popup.add(settingsItem);
        
        // 分隔线
        popup.addSeparator();
        
        // 退出菜单项
        MenuItem exitItem = new MenuItem("Exit");
        exitItem.addActionListener(e -> exitApplication());
        popup.add(exitItem);
        
        return popup;
    }
    
    private void toggleChatWindow() {
        if (chatWindow.isVisible()) {
            chatWindow.setVisible(false);
        } else {
            chatWindow.setVisible(true);
            chatWindow.toFront();
            chatWindow.requestFocus();
        }
    }
    
    private void showConfigWindow() {
        configWindow.setVisible(true);
        configWindow.toFront();
        configWindow.requestFocus();
    }
    
    private void exitApplication() {
        int result = JOptionPane.showConfirmDialog(
            null,
            "Are you sure you want to exit AI Quiz Tool?",
            "Confirm Exit",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.QUESTION_MESSAGE
        );
        
        if (result == JOptionPane.YES_OPTION) {
            logger.info("用户选择退出应用程序");
            System.exit(0);
        }
    }
    
    /**
     * 显示系统通知
     */
    public void showNotification(String title, String message, TrayIcon.MessageType messageType) {
        if (trayIcon != null) {
            trayIcon.displayMessage(title, message, messageType);
        }
    }
    
    /**
     * 更新托盘图标提示文本
     */
    public void updateTooltip(String tooltip) {
        if (trayIcon != null) {
            trayIcon.setToolTip(tooltip);
        }
    }
}

