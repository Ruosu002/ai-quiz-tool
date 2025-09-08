package com.aiquiztool.ui.components;

import com.aiquiztool.ui.theme.ModernTheme;
import com.aiquiztool.ui.animation.UIAnimator;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.RoundRectangle2D;

/**
 * 按钮组件
 * @author Tang En Dong
 */
public class ModernButton extends JButton {
    
    public enum ButtonType {
        PRIMARY,
        SECONDARY,
        SUCCESS,
        WARNING,
        DANGER,
        OUTLINE
    }
    
    private ButtonType buttonType = ButtonType.PRIMARY;
    private boolean isHovered = false;
    private boolean isPressed = false;
    private Color currentBackgroundColor;
    private Color currentTextColor;
    
    public ModernButton(String text) {
        this(text, ButtonType.PRIMARY);
    }
    
    public ModernButton(String text, ButtonType type) {
        super(text);
        this.buttonType = type;
        initializeButton();
    }
    
    private void initializeButton() {
        setFont(ModernTheme.Fonts.BUTTON);
        setFocusPainted(false);
        setBorderPainted(false);
        setContentAreaFilled(false);
        setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        setPreferredSize(new Dimension(120, ModernTheme.Dimensions.BUTTON_HEIGHT));
        
        updateColors();
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                if (isEnabled()) {
                    isHovered = true;
                    animateToHoverState();
                }
            }
            
            @Override
            public void mouseExited(MouseEvent e) {
                if (isEnabled()) {
                    isHovered = false;
                    animateToNormalState();
                }
            }
            
            @Override
            public void mousePressed(MouseEvent e) {
                if (isEnabled()) {
                    isPressed = true;
                    animateToPressedState();
                }
            }
            
            @Override
            public void mouseReleased(MouseEvent e) {
                if (isEnabled()) {
                    isPressed = false;
                    if (isHovered) {
                        animateToHoverState();
                    } else {
                        animateToNormalState();
                    }
                }
            }
        });
    }
    
    private void updateColors() {
        switch (buttonType) {
            case PRIMARY:
                currentBackgroundColor = ModernTheme.getPrimary();
                currentTextColor = Color.WHITE;
                break;
            case SECONDARY:
                currentBackgroundColor = ModernTheme.getSecondary();
                currentTextColor = Color.WHITE;
                break;
            case SUCCESS:
                currentBackgroundColor = ModernTheme.getSuccess();
                currentTextColor = Color.WHITE;
                break;
            case WARNING:
                currentBackgroundColor = ModernTheme.getWarning();
                currentTextColor = ModernTheme.getTextPrimary();
                break;
            case DANGER:
                currentBackgroundColor = ModernTheme.getDanger();
                currentTextColor = Color.WHITE;
                break;
            case OUTLINE:
                currentBackgroundColor = new Color(0, 0, 0, 0);
                currentTextColor = ModernTheme.getPrimary();
                break;
        }
        setForeground(currentTextColor);
    }
    
    private void animateToHoverState() {
        Color targetColor;
        switch (buttonType) {
            case PRIMARY:
                targetColor = ModernTheme.getPrimaryHover();
                break;
            case OUTLINE:
                targetColor = new Color(ModernTheme.getPrimary().getRed(), 
                                       ModernTheme.getPrimary().getGreen(), 
                                       ModernTheme.getPrimary().getBlue(), 20);
                break;
            default:
                targetColor = darkenColor(currentBackgroundColor, 0.1f);
                break;
        }
        
        UIAnimator.animateColor(this, currentBackgroundColor, targetColor, 200, 
            color -> {
                currentBackgroundColor = color;
                repaint();
            });
    }
    
    private void animateToNormalState() {
        updateColors();
        Color targetColor = getCurrentBackgroundColor();
        
        UIAnimator.animateColor(this, currentBackgroundColor, targetColor, 200,
            color -> {
                currentBackgroundColor = color;
                repaint();
            });
    }
    
    private void animateToPressedState() {
        Color targetColor = darkenColor(currentBackgroundColor, 0.2f);
        UIAnimator.animateColor(this, currentBackgroundColor, targetColor, 100,
            color -> {
                currentBackgroundColor = color;
                repaint();
            });
    }
    
    private Color getCurrentBackgroundColor() {
        switch (buttonType) {
            case PRIMARY:
                return ModernTheme.getPrimary();
            case SECONDARY:
                return ModernTheme.getSecondary();
            case SUCCESS:
                return ModernTheme.getSuccess();
            case WARNING:
                return ModernTheme.getWarning();
            case DANGER:
                return ModernTheme.getDanger();
            case OUTLINE:
                return new Color(0, 0, 0, 0);
            default:
                return ModernTheme.getPrimary();
        }
    }
    
    private Color darkenColor(Color color, float factor) {
        float[] hsb = Color.RGBtoHSB(color.getRed(), color.getGreen(), color.getBlue(), null);
        return Color.getHSBColor(hsb[0], hsb[1], Math.max(0, hsb[2] - factor));
    }
    
    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        int width = getWidth();
        int height = getHeight();
        
        if (buttonType != ButtonType.OUTLINE) {
            g2.setColor(ModernTheme.getShadowLight());
            g2.fillRoundRect(2, 2, width - 2, height - 2, 
                           ModernTheme.Dimensions.BORDER_RADIUS, 
                           ModernTheme.Dimensions.BORDER_RADIUS);
        }
        if (buttonType == ButtonType.OUTLINE) {
            g2.setColor(currentBackgroundColor);
            g2.fillRoundRect(0, 0, width, height, 
                           ModernTheme.Dimensions.BORDER_RADIUS, 
                           ModernTheme.Dimensions.BORDER_RADIUS);
            
            g2.setColor(ModernTheme.getPrimary());
            g2.setStroke(new BasicStroke(2));
            g2.drawRoundRect(1, 1, width - 2, height - 2, 
                           ModernTheme.Dimensions.BORDER_RADIUS, 
                           ModernTheme.Dimensions.BORDER_RADIUS);
        } else {
            g2.setColor(currentBackgroundColor);
            g2.fillRoundRect(0, 0, width, height, 
                           ModernTheme.Dimensions.BORDER_RADIUS, 
                           ModernTheme.Dimensions.BORDER_RADIUS);
        }
        
        g2.dispose();
        
        super.paintComponent(g);
    }
    
    @Override
    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);
        if (!enabled) {
            currentBackgroundColor = ModernTheme.getTextMuted();
            currentTextColor = Color.WHITE;
        } else {
            updateColors();
        }
        repaint();
    }
    
    public void setButtonType(ButtonType type) {
        this.buttonType = type;
        updateColors();
        repaint();
    }
    
    public ButtonType getButtonType() {
        return buttonType;
    }
}
