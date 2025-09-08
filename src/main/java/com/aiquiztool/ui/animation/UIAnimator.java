package com.aiquiztool.ui.animation;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.function.Consumer;

/**
 * UI动画类
 */
public class UIAnimator {
    
    // 缓动函数
    public enum EasingType {
        LINEAR,
        EASE_IN,
        EASE_OUT,
        EASE_IN_OUT,
        BOUNCE
    }
    
    /**
     * 颜色过渡动画
     */
    public static void animateColor(Component component, Color fromColor, Color toColor, 
                                   int duration, Consumer<Color> colorSetter) {
        animateColor(component, fromColor, toColor, duration, EasingType.EASE_OUT, colorSetter);
    }
    
    public static void animateColor(Component component, Color fromColor, Color toColor, 
                                   int duration, EasingType easing, Consumer<Color> colorSetter) {
        Timer timer = new Timer(16, null); // 约60fps
        long startTime = System.currentTimeMillis();
        
        timer.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                long elapsed = System.currentTimeMillis() - startTime;
                double progress = Math.min(1.0, (double) elapsed / duration);
                
                // 应用缓动函数
                double easedProgress = applyEasing(progress, easing);
                
                // 计算当前颜色
                int r = (int) (fromColor.getRed() + (toColor.getRed() - fromColor.getRed()) * easedProgress);
                int g = (int) (fromColor.getGreen() + (toColor.getGreen() - fromColor.getGreen()) * easedProgress);
                int b = (int) (fromColor.getBlue() + (toColor.getBlue() - fromColor.getBlue()) * easedProgress);
                int a = (int) (fromColor.getAlpha() + (toColor.getAlpha() - fromColor.getAlpha()) * easedProgress);
                
                Color currentColor = new Color(r, g, b, a);
                colorSetter.accept(currentColor);
                component.repaint();
                
                if (progress >= 1.0) {
                    timer.stop();
                }
            }
        });
        
        timer.start();
    }
    
    /**
     * 透明度过渡动画
     */
    public static void animateOpacity(Component component, float fromOpacity, float toOpacity, 
                                     int duration) {
        animateOpacity(component, fromOpacity, toOpacity, duration, EasingType.EASE_OUT);
    }
    
    public static void animateOpacity(Component component, float fromOpacity, float toOpacity, 
                                     int duration, EasingType easing) {
        Timer timer = new Timer(16, null);
        long startTime = System.currentTimeMillis();
        
        timer.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                long elapsed = System.currentTimeMillis() - startTime;
                double progress = Math.min(1.0, (double) elapsed / duration);
                
                double easedProgress = applyEasing(progress, easing);
                float currentOpacity = (float) (fromOpacity + (toOpacity - fromOpacity) * easedProgress);
                
                if (component instanceof JComponent) {
                    ((JComponent) component).putClientProperty("opacity", currentOpacity);
                }
                component.repaint();
                
                if (progress >= 1.0) {
                    timer.stop();
                }
            }
        });
        
        timer.start();
    }
    
    /**
     * 尺寸过渡动画
     */
    public static void animateSize(Component component, Dimension fromSize, Dimension toSize, 
                                  int duration) {
        animateSize(component, fromSize, toSize, duration, EasingType.EASE_OUT);
    }
    
    public static void animateSize(Component component, Dimension fromSize, Dimension toSize, 
                                  int duration, EasingType easing) {
        Timer timer = new Timer(16, null);
        long startTime = System.currentTimeMillis();
        
        timer.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                long elapsed = System.currentTimeMillis() - startTime;
                double progress = Math.min(1.0, (double) elapsed / duration);
                
                double easedProgress = applyEasing(progress, easing);
                
                int currentWidth = (int) (fromSize.width + (toSize.width - fromSize.width) * easedProgress);
                int currentHeight = (int) (fromSize.height + (toSize.height - fromSize.height) * easedProgress);
                
                component.setSize(currentWidth, currentHeight);
                component.revalidate();
                
                if (progress >= 1.0) {
                    timer.stop();
                }
            }
        });
        
        timer.start();
    }
    
    /**
     * 位置过渡动画
     */
    public static void animatePosition(Component component, Point fromPos, Point toPos, 
                                      int duration) {
        animatePosition(component, fromPos, toPos, duration, EasingType.EASE_OUT);
    }
    
    public static void animatePosition(Component component, Point fromPos, Point toPos, 
                                      int duration, EasingType easing) {
        Timer timer = new Timer(16, null);
        long startTime = System.currentTimeMillis();
        
        timer.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                long elapsed = System.currentTimeMillis() - startTime;
                double progress = Math.min(1.0, (double) elapsed / duration);
                
                double easedProgress = applyEasing(progress, easing);
                
                int currentX = (int) (fromPos.x + (toPos.x - fromPos.x) * easedProgress);
                int currentY = (int) (fromPos.y + (toPos.y - fromPos.y) * easedProgress);
                
                component.setLocation(currentX, currentY);
                
                if (progress >= 1.0) {
                    timer.stop();
                }
            }
        });
        
        timer.start();
    }
    
    /**
     * 淡入效果
     */
    public static void fadeIn(Component component, int duration) {
        component.setVisible(true);
        animateOpacity(component, 0.0f, 1.0f, duration);
    }
    
    /**
     * 淡出效果
     */
    public static void fadeOut(Component component, int duration) {
        animateOpacity(component, 1.0f, 0.0f, duration);
        Timer hideTimer = new Timer(duration, e -> component.setVisible(false));
        hideTimer.setRepeats(false);
        hideTimer.start();
    }
    
    /**
     * 滑入效果
     */
    public static void slideIn(Component component, Direction direction, int distance, int duration) {
        Point originalPos = component.getLocation();
        Point startPos = new Point(originalPos);
        
        switch (direction) {
            case LEFT:
                startPos.x -= distance;
                break;
            case RIGHT:
                startPos.x += distance;
                break;
            case UP:
                startPos.y -= distance;
                break;
            case DOWN:
                startPos.y += distance;
                break;
        }
        
        component.setLocation(startPos);
        component.setVisible(true);
        animatePosition(component, startPos, originalPos, duration);
    }
    
    /**
     * 弹跳效果
     */
    public static void bounce(Component component, int intensity, int duration) {
        Point originalPos = component.getLocation();
        Point bouncePos = new Point(originalPos.x, originalPos.y - intensity);
        
        animatePosition(component, originalPos, bouncePos, duration / 2, EasingType.EASE_OUT);
        
        Timer returnTimer = new Timer(duration / 2, e -> 
            animatePosition(component, bouncePos, originalPos, duration / 2, EasingType.BOUNCE));
        returnTimer.setRepeats(false);
        returnTimer.start();
    }
    
    /**
     * 脉冲效果
     */
    public static void pulse(Component component, float minScale, float maxScale, int duration) {
        Dimension originalSize = component.getSize();
        Dimension maxSize = new Dimension(
            (int) (originalSize.width * maxScale),
            (int) (originalSize.height * maxScale)
        );
        
        animateSize(component, originalSize, maxSize, duration / 2);
        
        Timer returnTimer = new Timer(duration / 2, e -> 
            animateSize(component, maxSize, originalSize, duration / 2));
        returnTimer.setRepeats(false);
        returnTimer.start();
    }
    
    /**
     * 应用缓动函数
     */
    private static double applyEasing(double t, EasingType easing) {
        switch (easing) {
            case LINEAR:
                return t;
            case EASE_IN:
                return t * t;
            case EASE_OUT:
                return 1 - Math.pow(1 - t, 2);
            case EASE_IN_OUT:
                return t < 0.5 ? 2 * t * t : 1 - Math.pow(-2 * t + 2, 2) / 2;
            case BOUNCE:
                if (t < 1 / 2.75) {
                    return 7.5625 * t * t;
                } else if (t < 2 / 2.75) {
                    return 7.5625 * (t -= 1.5 / 2.75) * t + 0.75;
                } else if (t < 2.5 / 2.75) {
                    return 7.5625 * (t -= 2.25 / 2.75) * t + 0.9375;
                } else {
                    return 7.5625 * (t -= 2.625 / 2.75) * t + 0.984375;
                }
            default:
                return t;
        }
    }
    
    /**
     * 方向枚举
     */
    public enum Direction {
        LEFT, RIGHT, UP, DOWN
    }
}
