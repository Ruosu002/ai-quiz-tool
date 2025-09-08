package com.aiquiztool.ui.components;

import com.aiquiztool.ui.theme.ModernTheme;
import com.aiquiztool.ui.animation.UIAnimator;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * 文本输入框组件
 */
public class ModernTextField extends JTextField {
    
    private String placeholder = "";
    private boolean isFocused = false;
    private boolean isHovered = false;
    private Color currentBorderColor;
    private Color currentBackgroundColor;
    
    public ModernTextField() {
        this("");
    }
    
    public ModernTextField(String placeholder) {
        super();
        this.placeholder = placeholder;
        initializeTextField();
    }
    
    public ModernTextField(int columns) {
        super(columns);
        initializeTextField();
    }
    
    public ModernTextField(String placeholder, int columns) {
        super(columns);
        this.placeholder = placeholder;
        initializeTextField();
    }
    
    private void initializeTextField() {
        // 基础设置
        setFont(ModernTheme.Fonts.BODY);
        setPreferredSize(new Dimension(200, ModernTheme.Dimensions.INPUT_HEIGHT));
        setBackground(ModernTheme.getSurface());
        setForeground(ModernTheme.getTextPrimary());
        setCaretColor(ModernTheme.getPrimary());
        
        // 初始化颜色
        currentBorderColor = ModernTheme.getBorder();
        currentBackgroundColor = ModernTheme.getSurface();
        
        // 设置边框
        setBorder(createModernBorder());
        
        // 添加焦点监听器
        addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                isFocused = true;
                animateToFocusState();
            }
            
            @Override
            public void focusLost(FocusEvent e) {
                isFocused = false;
                animateToNormalState();
            }
        });
        
        // 添加鼠标监听器
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                if (!isFocused) {
                    isHovered = true;
                    animateToHoverState();
                }
            }
            
            @Override
            public void mouseExited(MouseEvent e) {
                if (!isFocused) {
                    isHovered = false;
                    animateToNormalState();
                }
            }
        });
    }
    
    private Border createModernBorder() {
        return new Border() {
            @Override
            public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // 绘制边框
                g2.setColor(currentBorderColor);
                g2.setStroke(new BasicStroke(isFocused ? 2 : 1));
                g2.drawRoundRect(x, y, width - 1, height - 1, 
                               ModernTheme.Dimensions.BORDER_RADIUS, 
                               ModernTheme.Dimensions.BORDER_RADIUS);
                
                g2.dispose();
            }
            
            @Override
            public Insets getBorderInsets(Component c) {
                return new Insets(ModernTheme.Dimensions.PADDING_SMALL, 
                                ModernTheme.Dimensions.PADDING_MEDIUM,
                                ModernTheme.Dimensions.PADDING_SMALL, 
                                ModernTheme.Dimensions.PADDING_MEDIUM);
            }
            
            @Override
            public boolean isBorderOpaque() {
                return false;
            }
        };
    }
    
    private void animateToFocusState() {
        UIAnimator.animateColor(this, currentBorderColor, ModernTheme.getBorderFocus(), 200,
            color -> {
                currentBorderColor = color;
                repaint();
            });
    }
    
    private void animateToHoverState() {
        Color targetColor = new Color(
            ModernTheme.getBorderFocus().getRed(),
            ModernTheme.getBorderFocus().getGreen(),
            ModernTheme.getBorderFocus().getBlue(),
            100
        );
        
        UIAnimator.animateColor(this, currentBorderColor, targetColor, 150,
            color -> {
                currentBorderColor = color;
                repaint();
            });
    }
    
    private void animateToNormalState() {
        UIAnimator.animateColor(this, currentBorderColor, ModernTheme.getBorder(), 200,
            color -> {
                currentBorderColor = color;
                repaint();
            });
    }
    
    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        
        // 绘制圆角背景
        g2.setColor(currentBackgroundColor);
        g2.fillRoundRect(0, 0, getWidth(), getHeight(), 
                       ModernTheme.Dimensions.BORDER_RADIUS, 
                       ModernTheme.Dimensions.BORDER_RADIUS);
        
        // 调用父类方法绘制文本
        super.paintComponent(g);
        
        // 绘制占位符
        if (getText().isEmpty() && !placeholder.isEmpty() && !isFocused) {
            g2.setColor(ModernTheme.getTextMuted());
            g2.setFont(getFont());
            
            FontMetrics fm = g2.getFontMetrics();
            int x = getInsets().left;
            int y = (getHeight() + fm.getAscent() - fm.getDescent()) / 2;
            
            g2.drawString(placeholder, x, y);
        }
        
        g2.dispose();
    }
    
    public void setPlaceholder(String placeholder) {
        this.placeholder = placeholder;
        repaint();
    }
    
    public String getPlaceholder() {
        return placeholder;
    }
    
    /**
     * 显示错误状态
     */
    public void showError() {
        UIAnimator.animateColor(this, currentBorderColor, ModernTheme.getDanger(), 200,
            color -> {
                currentBorderColor = color;
                repaint();
            });
        
        // 震动效果
        Point originalPos = getLocation();
        Timer shakeTimer = new Timer(50, null);
        final int[] shakeCount = {0};
        
        shakeTimer.addActionListener(e -> {
            if (shakeCount[0] < 6) {
                int offset = (shakeCount[0] % 2 == 0) ? 3 : -3;
                setLocation(originalPos.x + offset, originalPos.y);
                shakeCount[0]++;
            } else {
                setLocation(originalPos);
                shakeTimer.stop();
            }
        });
        
        shakeTimer.start();
    }
    
    /**
     * 显示成功状态
     */
    public void showSuccess() {
        UIAnimator.animateColor(this, currentBorderColor, ModernTheme.getSuccess(), 200,
            color -> {
                currentBorderColor = color;
                repaint();
            });
        
        // 2秒后恢复正常状态
        Timer resetTimer = new Timer(2000, e -> {
            if (!isFocused) {
                animateToNormalState();
            }
        });
        resetTimer.setRepeats(false);
        resetTimer.start();
    }
    
    /**
     * 重置到正常状态
     */
    public void resetState() {
        animateToNormalState();
    }
}
