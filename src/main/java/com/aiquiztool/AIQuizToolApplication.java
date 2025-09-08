package com.aiquiztool;

import com.aiquiztool.ui.SystemTrayManager;
import com.formdev.flatlaf.FlatLightLaf;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;

/**
 * AI助手工具主程序
 * 详见README.md 反馈bug到jiant1827@gmail.com
 * @author Tang En Dong
 */
public class AIQuizToolApplication {
    private static final Logger logger = LoggerFactory.getLogger(AIQuizToolApplication.class);
    
    public static void main(String[] args) {
        // 设置中文环境
        System.setProperty("file.encoding", "UTF-8");
        System.setProperty("sun.jnu.encoding", "UTF-8");
        System.setProperty("user.language", "zh");
        System.setProperty("user.country", "CN");
        
        try {
            UIManager.setLookAndFeel(new FlatLightLaf());
        } catch (Exception e) {
            logger.warn("无法设置外观主题，使用默认主题", e);
        }
        
        System.setProperty("java.awt.headless", "false");
        
        SwingUtilities.invokeLater(() -> {
            try {
                logger.info("启动AI助手工具...");
                
                SystemTrayManager trayManager = new SystemTrayManager();
                trayManager.initialize();
                
                logger.info("AI助手工具启动成功");
                
            } catch (Exception e) {
                logger.error("启动应用程序时发生错误", e);
                JOptionPane.showMessageDialog(null, 
                    "启动应用程序时发生错误: " + e.getMessage(), 
                    "错误", 
                    JOptionPane.ERROR_MESSAGE);
                System.exit(1);
            }
        });
    }
}

