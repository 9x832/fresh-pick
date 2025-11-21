package com.agrismart.agrimallbackend.common.util;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.Random;

/**
 * 验证码生成工具类。
 *
 * 提供图形验证码的生成功能，支持普通验证码和旋转验证码两种模式。
 * 验证码字符从数字和字母（排除易混淆字符如 0、1、i、l、o、O）中随机选择。
 *
 * 功能特性：
 *
 * - 支持自定义验证码长度、字体大小、图片尺寸
 * - 支持普通验证码和旋转验证码两种模式
 * - 支持干扰线绘制，提高安全性
 * - 随机字体、颜色和旋转角度，增强验证码的随机性
 *
 * 使用示例：
 * <pre>
 * {@code
 * CpachaUtil cpachaUtil = new CpachaUtil(4, 21);
 * String vcode = cpachaUtil.generatorVCode();
 * BufferedImage image = cpachaUtil.generatorVCodeImage(vcode, true);
 * }
 * </pre>
 *
 * @author agrimall
 * @since 1.0
 */
public class CpachaUtil {

    /**
     * 验证码字符集。
     *
     * 包含数字 2-9 和大小写字母（排除易混淆字符：0、1、i、l、o、O）。
     *
     */
    private final char[] code = {
            '2', '3', '4', '5', '6', '7', '8', '9',
            'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j',
            'k', 'm', 'n', 'p', 'q', 'r', 's', 't', 'u', 'v',
            'w', 'x', 'y', 'z', 'A', 'B', 'C', 'D', 'E', 'F',
            'G', 'H', 'J', 'K', 'L', 'M', 'N', 'P', 'Q', 'R',
            'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z'
    };

    /**
     * 可用字体名称数组。
     * 用于随机选择字体，增加验证码的随机性。
     */
    private final String[] fontNames = new String[]{
            "黑体", "宋体", "Courier", "Arial",
            "Verdana", "Times", "Tahoma", "Georgia"};

    /**
     * 可用字体样式数组。
     * 包含粗体和粗斜体两种样式。
     */
    private final int[] fontStyles = new int[]{
            Font.BOLD, Font.ITALIC | Font.BOLD
    };

    /**
     * 验证码长度，默认 4 位。
     */
    private int vcodeLen = 4;

    /**
     * 字体大小，默认 21。
     */
    private int fontsize = 21;

    /**
     * 验证码图片宽度，根据字体大小和验证码长度自动计算。
     */
    private int width = (fontsize + 1) * vcodeLen + 10;

    /**
     * 验证码图片高度，根据字体大小自动计算。
     */
    private int height = fontsize + 12;

    /**
     * 干扰线数量，默认 3 条。
     */
    private int disturbline = 3;

    /**
     * 默认构造函数。
     * 使用默认参数：验证码长度 4，字体大小 21。
     */
    public CpachaUtil() {
    }

    /**
     * 构造函数，指定验证码长度。
     *
     * @param vcodeLen 验证码长度
     */
    public CpachaUtil(int vcodeLen) {
        this.vcodeLen = vcodeLen;
        this.width = (fontsize + 1) * vcodeLen + 10;
    }

    /**
     * 构造函数，指定验证码长度和字体大小。
     *
     * @param vcodeLen 验证码长度
     * @param fontSize 字体大小
     */
    public CpachaUtil(int vcodeLen, int fontSize) {
        this.vcodeLen = vcodeLen;
        this.fontsize = fontSize;
        this.width = (fontsize + 1) * vcodeLen + 10;
        this.height = fontsize + 12;
    }

    /**
     * 构造函数，指定所有参数。
     *
     * @param vcodeLen 验证码长度
     * @param fontSize 字体大小
     * @param width    图片宽度
     * @param height   图片高度
     */
    public CpachaUtil(int vcodeLen, int fontSize, int width, int height) {
        this.vcodeLen = vcodeLen;
        this.fontsize = fontSize;
        this.width = width;
        this.height = height;
    }

    /**
     * 生成普通验证码图片。
     *
     * 生成包含指定验证码字符串的图片，每个字符使用随机字体、颜色和位置。
     * 可选择是否绘制干扰线。
     *
     * @param vcode    验证码字符串
     * @param drawline 是否绘制干扰线，true 表示绘制
     * @return 生成的验证码图片 BufferedImage 对象
     */
    public BufferedImage generatorVCodeImage(String vcode, boolean drawline) {
        BufferedImage vcodeImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics g = vcodeImage.getGraphics();
        g.setColor(new Color(246, 240, 250));
        g.fillRect(0, 0, width, height);
        if (drawline) {
            drawDisturbLine(g);
        }
        Random ran = new Random();
        for (int i = 0; i < vcode.length(); i++) {
            g.setFont(new Font(fontNames[ran.nextInt(fontNames.length)], fontStyles[ran.nextInt(fontStyles.length)], fontsize));
            g.setColor(getRandomColor());
            g.drawString(vcode.charAt(i) + "", i * fontsize + 10, fontsize + 5);
        }
        g.dispose();
        return vcodeImage;
    }

    /**
     * 生成旋转验证码图片。
     *
     * 生成包含指定验证码字符串的图片，每个字符会随机旋转一定角度。
     * 可选择是否绘制干扰线。
     *
     * 相比普通验证码，旋转验证码更难被 OCR 识别，安全性更高。
     *
     * @param vcode    验证码字符串
     * @param drawline 是否绘制干扰线，true 表示绘制
     * @return 生成的旋转验证码图片 BufferedImage 对象
     */
    public BufferedImage generatorRotateVCodeImage(String vcode, boolean drawline) {
        BufferedImage rotateVcodeImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2d = rotateVcodeImage.createGraphics();
        g2d.setColor(new Color(246, 240, 250));
        g2d.fillRect(0, 0, width, height);
        if (drawline) {
            drawDisturbLine(g2d);
        }
        for (int i = 0; i < vcode.length(); i++) {
            BufferedImage rotateImage = getRotateImage(vcode.charAt(i));
            g2d.drawImage(rotateImage, null, (int) (this.height * 0.7) * i, 0);
        }
        g2d.dispose();
        return rotateVcodeImage;
    }

    /**
     * 生成验证码字符串。
     *
     * 从字符集中随机选择字符，生成指定长度的验证码字符串。
     *
     * @return 生成的验证码字符串
     */
    public String generatorVCode() {
        int len = code.length;
        Random ran = new Random();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < vcodeLen; i++) {
            int index = ran.nextInt(len);
            sb.append(code[index]);
        }
        return sb.toString();
    }

    /**
     * 绘制干扰线。
     *
     * 在图片上随机绘制指定数量的干扰线，用于提高验证码的安全性。
     * 干扰线的起点和终点都是随机生成的，颜色也是随机的。
     *
     * @param g 图形上下文对象
     */
    private void drawDisturbLine(Graphics g) {
        Random ran = new Random();
        for (int i = 0; i < disturbline; i++) {
            int x1 = ran.nextInt(width);
            int y1 = ran.nextInt(height);
            int x2 = ran.nextInt(width);
            int y2 = ran.nextInt(height);
            g.setColor(getRandomColor());
            g.drawLine(x1, y1, x2, y2);
        }
    }

    /**
     * 获取旋转后的字符图片。
     *
     * 为单个字符生成一个旋转后的图片，旋转角度是随机的。
     *
     * @param c 要旋转的字符
     * @return 旋转后的字符图片 BufferedImage 对象
     */
    private BufferedImage getRotateImage(char c) {
        BufferedImage rotateImage = new BufferedImage(height, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = rotateImage.createGraphics();
        g2d.setColor(new Color(255, 255, 255, 0));
        g2d.fillRect(0, 0, height, height);
        Random ran = new Random();
        g2d.setFont(new Font(fontNames[ran.nextInt(fontNames.length)], fontStyles[ran.nextInt(fontStyles.length)], fontsize));
        g2d.setColor(getRandomColor());
        double theta = getTheta();
        g2d.rotate(theta, height / 2.0, height / 2.0);
        g2d.drawString(Character.toString(c), (height - fontsize) / 2.0f, fontsize + 5);
        g2d.dispose();
        return rotateImage;
    }

    /**
     * 获取随机颜色。
     *
     * 生成一个 RGB 值在 0-220 范围内的随机颜色，避免颜色过亮。
     *
     * @return 随机颜色对象
     */
    private Color getRandomColor() {
        Random ran = new Random();
        return new Color(ran.nextInt(220), ran.nextInt(220), ran.nextInt(220));
    }

    /**
     * 获取随机旋转角度。
     *
     * 生成一个 -1 到 1 之间的随机角度（弧度），用于字符旋转。
     *
     * @return 随机旋转角度（弧度）
     */
    private double getTheta() {
        return ((int) (Math.random() * 1000) % 2 == 0 ? -1 : 1) * Math.random();
    }

    /**
     * 获取验证码长度。
     *
     * @return 验证码长度
     */
    public int getVcodeLen() {
        return vcodeLen;
    }

    /**
     * 设置验证码长度。
     * 设置后会自动重新计算图片宽度。
     *
     * @param vcodeLen 验证码长度
     */
    public void setVcodeLen(int vcodeLen) {
        this.width = (fontsize + 3) * vcodeLen + 10;
        this.vcodeLen = vcodeLen;
    }

    /**
     * 获取字体大小。
     *
     * @return 字体大小
     */
    public int getFontsize() {
        return fontsize;
    }

    /**
     * 设置字体大小。
     * 设置后会自动重新计算图片宽度和高度。
     *
     * @param fontsize 字体大小
     */
    public void setFontsize(int fontsize) {
        this.width = (fontsize + 3) * vcodeLen + 10;
        this.height = fontsize + 15;
        this.fontsize = fontsize;
    }

    /**
     * 获取图片宽度。
     *
     * @return 图片宽度
     */
    public int getWidth() {
        return width;
    }

    /**
     * 设置图片宽度。
     *
     * @param width 图片宽度
     */
    public void setWidth(int width) {
        this.width = width;
    }

    /**
     * 获取图片高度。
     *
     * @return 图片高度
     */
    public int getHeight() {
        return height;
    }

    /**
     * 设置图片高度。
     *
     * @param height 图片高度
     */
    public void setHeight(int height) {
        this.height = height;
    }

    /**
     * 获取干扰线数量。
     *
     * @return 干扰线数量
     */
    public int getDisturbline() {
        return disturbline;
    }

    /**
     * 设置干扰线数量。
     *
     * @param disturbline 干扰线数量
     */
    public void setDisturbline(int disturbline) {
        this.disturbline = disturbline;
    }
}

