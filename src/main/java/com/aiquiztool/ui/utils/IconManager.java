package com.aiquiztool.ui.utils;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

/**
 * 图标管理器
 * 提供统一的图标管理和生成功能
 */
public class IconManager {
    
    // 图标缓存
    private static final java.util.Map<String, ImageIcon> iconCache = new java.util.HashMap<>();
    
    /**
     * 获取应用程序图标
     */
    public static ImageIcon getAppIcon(int size) {
        String key = "app_" + size;
        return iconCache.computeIfAbsent(key, k -> createAppIcon(size));
    }
    
    /**
     * 获取发送图标
     */
    public static ImageIcon getSendIcon(int size) {
        String key = "send_" + size;
        return iconCache.computeIfAbsent(key, k -> createSendIcon(size));
    }
    
    /**
     * 获取麦克风图标
     */
    public static ImageIcon getMicIcon(int size) {
        String key = "mic_" + size;
        return iconCache.computeIfAbsent(key, k -> createMicIcon(size));
    }
    
    /**
     * 获取设置图标
     */
    public static ImageIcon getSettingsIcon(int size) {
        String key = "settings_" + size;
        return iconCache.computeIfAbsent(key, k -> createSettingsIcon(size));
    }
    
    /**
     * 获取主题切换图标
     */
    public static ImageIcon getThemeIcon(int size, boolean isDark) {
        String key = "theme_" + size + "_" + isDark;
        return iconCache.computeIfAbsent(key, k -> createThemeIcon(size, isDark));
    }
    
    /**
     * 获取用户头像图标
     */
    public static ImageIcon getUserIcon(int size) {
        String key = "user_" + size;
        return iconCache.computeIfAbsent(key, k -> createUserIcon(size));
    }
    
    /**
     * 获取AI助手图标
     */
    public static ImageIcon getAIIcon(int size) {
        String key = "ai_" + size;
        return iconCache.computeIfAbsent(key, k -> createAIIcon(size));
    }
    
    /**
     * 创建应用程序图标
     */
    private static ImageIcon createAppIcon(int size) {
        BufferedImage image = new BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = image.createGraphics();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        // 绘制渐变背景
        GradientPaint gradient = new GradientPaint(0, 0, new Color(64, 123, 255), 
                                                  size, size, new Color(13, 110, 253));
        g2.setPaint(gradient);
        g2.fillRoundRect(0, 0, size, size, size / 4, size / 4);
        
        // 绘制AI标识
        g2.setColor(Color.WHITE);
        g2.setFont(new Font("Arial", Font.BOLD, size / 2));
        FontMetrics fm = g2.getFontMetrics();
        String text = "AI";
        int x = (size - fm.stringWidth(text)) / 2;
        int y = (size + fm.getAscent() - fm.getDescent()) / 2;
        g2.drawString(text, x, y);
        
        g2.dispose();
        return new ImageIcon(image);
    }
    
    /**
     * 创建发送图标
     */
    private static ImageIcon createSendIcon(int size) {
        BufferedImage image = new BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = image.createGraphics();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        g2.setColor(new Color(64, 123, 255));
        g2.setStroke(new BasicStroke(2));
        
        // 绘制箭头
        int[] xPoints = {size / 6, size * 5 / 6, size / 2};
        int[] yPoints = {size / 2, size / 3, size / 2};
        g2.fillPolygon(xPoints, yPoints, 3);
        
        int[] xPoints2 = {size / 6, size * 5 / 6, size / 2};
        int[] yPoints2 = {size / 2, size * 2 / 3, size / 2};
        g2.fillPolygon(xPoints2, yPoints2, 3);
        
        g2.dispose();
        return new ImageIcon(image);
    }
    
    /**
     * 创建麦克风图标
     */
    private static ImageIcon createMicIcon(int size) {
        BufferedImage image = new BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = image.createGraphics();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        g2.setColor(new Color(108, 117, 125));
        g2.setStroke(new BasicStroke(2));
        
        // 绘制麦克风主体
        int micWidth = size / 3;
        int micHeight = size / 2;
        int x = (size - micWidth) / 2;
        int y = size / 6;
        g2.fillRoundRect(x, y, micWidth, micHeight, micWidth / 2, micWidth / 2);
        
        // 绘制支架
        g2.drawLine(size / 2, y + micHeight, size / 2, size * 5 / 6);
        g2.drawLine(size / 3, size * 5 / 6, size * 2 / 3, size * 5 / 6);
        
        g2.dispose();
        return new ImageIcon(image);
    }
    
    /**
     * 创建设置图标
     */
    private static ImageIcon createSettingsIcon(int size) {
        BufferedImage image = new BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = image.createGraphics();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        g2.setColor(new Color(108, 117, 125));
        g2.setStroke(new BasicStroke(2));
        
        // 绘制齿轮
        int centerX = size / 2;
        int centerY = size / 2;
        int radius = size / 3;
        
        // 外圈齿轮
        for (int i = 0; i < 8; i++) {
            double angle = i * Math.PI / 4;
            int x1 = (int) (centerX + Math.cos(angle) * radius);
            int y1 = (int) (centerY + Math.sin(angle) * radius);
            int x2 = (int) (centerX + Math.cos(angle) * (radius + 4));
            int y2 = (int) (centerY + Math.sin(angle) * (radius + 4));
            g2.drawLine(x1, y1, x2, y2);
        }
        
        // 内圈
        g2.drawOval(centerX - radius / 2, centerY - radius / 2, radius, radius);
        g2.fillOval(centerX - radius / 4, centerY - radius / 4, radius / 2, radius / 2);
        
        g2.dispose();
        return new ImageIcon(image);
    }
    
    /**
     * 创建主题切换图标
     */
    private static ImageIcon createThemeIcon(int size, boolean isDark) {
        BufferedImage image = new BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = image.createGraphics();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        if (isDark) {
            // 绘制太阳图标
            g2.setColor(new Color(255, 193, 7));
            g2.fillOval(size / 4, size / 4, size / 2, size / 2);
            
            // 绘制光线
            g2.setStroke(new BasicStroke(2));
            for (int i = 0; i < 8; i++) {
                double angle = i * Math.PI / 4;
                int x1 = (int) (size / 2 + Math.cos(angle) * size / 3);
                int y1 = (int) (size / 2 + Math.sin(angle) * size / 3);
                int x2 = (int) (size / 2 + Math.cos(angle) * size / 2.2);
                int y2 = (int) (size / 2 + Math.sin(angle) * size / 2.2);
                g2.drawLine(x1, y1, x2, y2);
            }
        } else {
            // 绘制月亮图标
            g2.setColor(new Color(108, 117, 125));
            g2.fillOval(size / 6, size / 6, size * 2 / 3, size * 2 / 3);
            g2.setColor(new Color(248, 249, 250));
            g2.fillOval(size / 4, size / 8, size * 2 / 3, size * 2 / 3);
        }
        
        g2.dispose();
        return new ImageIcon(image);
    }
    
    /**
     * 创建用户头像图标
     */
    private static ImageIcon createUserIcon(int size) {
        BufferedImage image = new BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = image.createGraphics();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        g2.setColor(new Color(64, 123, 255));
        
        // 绘制头部
        g2.fillOval(size / 3, size / 6, size / 3, size / 3);
        
        // 绘制身体
        g2.fillOval(size / 6, size / 2, size * 2 / 3, size / 2);
        
        g2.dispose();
        return new ImageIcon(image);
    }
    
    /**
     * 创建AI助手图标
     */
    private static ImageIcon createAIIcon(int size) {
        BufferedImage image = new BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = image.createGraphics();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        // 绘制机器人头部
        g2.setColor(new Color(13, 110, 253));
        g2.fillRoundRect(size / 6, size / 4, size * 2 / 3, size / 2, size / 8, size / 8);
        
        // 绘制眼睛
        g2.setColor(Color.WHITE);
        g2.fillOval(size / 3, size * 2 / 5, size / 8, size / 8);
        g2.fillOval(size / 2, size * 2 / 5, size / 8, size / 8);
        
        // 绘制天线
        g2.setColor(new Color(13, 110, 253));
        g2.setStroke(new BasicStroke(2));
        g2.drawLine(size / 2, size / 4, size / 2, size / 8);
        g2.fillOval(size / 2 - 2, size / 8 - 2, 4, 4);
        
        g2.dispose();
        return new ImageIcon(image);
    }
    
    /**
     * 创建圆形图标
     */
    public static ImageIcon createCircleIcon(int size, Color color) {
        BufferedImage image = new BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = image.createGraphics();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        g2.setColor(color);
        g2.fillOval(0, 0, size, size);
        
        g2.dispose();
        return new ImageIcon(image);
    }
    
    /**
     * 创建带文字的图标
     */
    public static ImageIcon createTextIcon(String text, int size, Color bgColor, Color textColor) {
        BufferedImage image = new BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = image.createGraphics();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        
        // 绘制背景
        g2.setColor(bgColor);
        g2.fillRoundRect(0, 0, size, size, size / 4, size / 4);
        
        // 绘制文字
        g2.setColor(textColor);
        g2.setFont(new Font("Arial", Font.BOLD, size / 2));
        FontMetrics fm = g2.getFontMetrics();
        int x = (size - fm.stringWidth(text)) / 2;
        int y = (size + fm.getAscent() - fm.getDescent()) / 2;
        g2.drawString(text, x, y);
        
        g2.dispose();
        return new ImageIcon(image);
    }
    
    /**
     * 调整图标大小
     */
    public static ImageIcon resizeIcon(ImageIcon icon, int newSize) {
        Image img = icon.getImage();
        Image scaledImg = img.getScaledInstance(newSize, newSize, Image.SCALE_SMOOTH);
        return new ImageIcon(scaledImg);
    }
    
    /**
     * 清除图标缓存
     */
    public static void clearCache() {
        iconCache.clear();
    }
}








