package com.aiquiztool.ui.theme;

import java.awt.*;

/**
 * 主题配置
 * @author Tang En Dong
 */
public class ModernTheme {
    
    public enum ThemeType {
        LIGHT
    }
    
    private static ThemeType currentTheme = ThemeType.LIGHT;
    
    public static class LightColors {
        public static final Color PRIMARY = new Color(64, 123, 255);
        public static final Color PRIMARY_HOVER = new Color(54, 113, 245);
        public static final Color SECONDARY = new Color(108, 117, 125);
        public static final Color SUCCESS = new Color(40, 167, 69);
        public static final Color WARNING = new Color(255, 193, 7);
        public static final Color DANGER = new Color(220, 53, 69);
        
        public static final Color BACKGROUND = new Color(248, 249, 250);
        public static final Color SURFACE = Color.WHITE;
        public static final Color SURFACE_HOVER = new Color(245, 245, 245);
        
        public static final Color TEXT_PRIMARY = new Color(33, 37, 41);
        public static final Color TEXT_SECONDARY = new Color(108, 117, 125);
        public static final Color TEXT_MUTED = new Color(173, 181, 189);
        
        public static final Color BORDER = new Color(222, 226, 230);
        public static final Color BORDER_FOCUS = new Color(128, 189, 255);
        
        public static final Color SHADOW = new Color(0, 0, 0, 20);
        public static final Color SHADOW_LIGHT = new Color(0, 0, 0, 10);
    }
    
    public static class Fonts {
        public static final String PRIMARY_FONT = "Microsoft YaHei UI";
        public static final String MONOSPACE_FONT = "Consolas";
        
        public static final Font TITLE = new Font(PRIMARY_FONT, Font.BOLD, 18);
        public static final Font SUBTITLE = new Font(PRIMARY_FONT, Font.BOLD, 14);
        public static final Font BODY = new Font(PRIMARY_FONT, Font.PLAIN, 12);
        public static final Font SMALL = new Font(PRIMARY_FONT, Font.PLAIN, 10);
        public static final Font BUTTON = new Font(PRIMARY_FONT, Font.PLAIN, 12);
        public static final Font CODE = new Font(MONOSPACE_FONT, Font.PLAIN, 11);
    }
    
    public static class Dimensions {
        public static final int BORDER_RADIUS = 8;
        public static final int BORDER_RADIUS_SMALL = 4;
        public static final int BORDER_RADIUS_LARGE = 12;
        
        public static final int PADDING_SMALL = 8;
        public static final int PADDING_MEDIUM = 16;
        public static final int PADDING_LARGE = 24;
        
        public static final int BUTTON_HEIGHT = 36;
        public static final int INPUT_HEIGHT = 36;
        
        public static final int SHADOW_BLUR = 8;
        public static final int SHADOW_SPREAD = 2;
    }
    
    public static Color getPrimary() {
        return LightColors.PRIMARY;
    }
    
    public static Color getPrimaryHover() {
        return LightColors.PRIMARY_HOVER;
    }
    
    public static Color getSecondary() {
        return LightColors.SECONDARY;
    }
    
    public static Color getBackground() {
        return LightColors.BACKGROUND;
    }
    
    public static Color getSurface() {
        return LightColors.SURFACE;
    }
    
    public static Color getSurfaceHover() {
        return LightColors.SURFACE_HOVER;
    }
    
    public static Color getTextPrimary() {
        return LightColors.TEXT_PRIMARY;
    }
    
    public static Color getTextSecondary() {
        return LightColors.TEXT_SECONDARY;
    }
    
    public static Color getTextMuted() {
        return LightColors.TEXT_MUTED;
    }
    
    public static Color getBorder() {
        return LightColors.BORDER;
    }
    
    public static Color getBorderFocus() {
        return LightColors.BORDER_FOCUS;
    }
    
    public static Color getShadow() {
        return LightColors.SHADOW;
    }
    
    public static Color getShadowLight() {
        return LightColors.SHADOW_LIGHT;
    }
    
    public static Color getSuccess() {
        return LightColors.SUCCESS;
    }
    
    public static Color getWarning() {
        return LightColors.WARNING;
    }
    
    public static Color getDanger() {
        return LightColors.DANGER;
    }
    
    public static void setTheme(ThemeType theme) {
        currentTheme = ThemeType.LIGHT;
    }
    
    public static ThemeType getCurrentTheme() {
        return ThemeType.LIGHT;
    }
    
    public static void toggleTheme() {
    }
    
    public static GradientPaint createGradient(int width, int height, Color startColor, Color endColor) {
        return new GradientPaint(0, 0, startColor, 0, height, endColor);
    }
    
    public static javax.swing.border.Border createRoundedBorder(Color color, int radius) {
        return new RoundedBorder(color, radius);
    }
    
    public static class RoundedBorder implements javax.swing.border.Border {
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
