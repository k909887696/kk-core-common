package com.kk.common.utils;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Random;

public class CaptchaUtil {

    // 定义验证码字符集（数字 + 大写字母 + 小写字母）
    private static final String CHARACTERS = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";

    // 验证码图片的宽度和高度
    private static final int IMAGE_WIDTH = 25;
    private static final int IMAGE_HEIGHT = 40;

    /**
     * 生成随机验证码
     *
     * @param length 验证码长度
     * @return 生成的验证码字符串
     */
    public static String generateCaptcha(int length) {
        if (length <= 0) {
            throw new IllegalArgumentException("验证码长度必须大于0");
        }

        Random random = new Random();
        StringBuilder captcha = new StringBuilder(length);

        for (int i = 0; i < length; i++) {
            // 从字符集中随机选择一个字符
            int index = random.nextInt(CHARACTERS.length());
            captcha.append(CHARACTERS.charAt(index));
        }

        return captcha.toString();
    }

    /**
     * 生成默认长度的验证码（默认长度为6）
     *
     * @return 生成的验证码字符串
     */
    public static String generateCaptcha() {
        return generateCaptcha(4);
    }

    /**
     * 生成验证码图片并返回字节流
     *
     * @param captcha 验证码字符串
     * @return 验证码图片的字节流
     */
    public static byte[] generateCaptchaImage(String captcha) throws IOException {
        // 创建 BufferedImage 对象
        BufferedImage image = new BufferedImage(IMAGE_WIDTH * captcha.length(), IMAGE_HEIGHT, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = image.createGraphics();

        // 设置背景颜色
        g.setColor(Color.WHITE);
        g.fillRect(0, 0, IMAGE_WIDTH * captcha.length(), IMAGE_HEIGHT);

        // 设置字体
        g.setFont(new Font("Arial", Font.BOLD, 24));

        // 绘制验证码字符
        Random random = new Random();
        for (int i = 0; i < captcha.length(); i++) {
            // 随机设置字符颜色
            g.setColor(new Color(random.nextInt(256), random.nextInt(256), random.nextInt(256)));
            // 绘制字符
            g.drawString(String.valueOf(captcha.charAt(i)), 20 + i * 20, 30);
        }

        // 添加干扰线
        for (int i = 0; i < captcha.length(); i++) {
            g.setColor(new Color(random.nextInt(256), random.nextInt(256), random.nextInt(256)));
            g.drawLine(random.nextInt(IMAGE_WIDTH * captcha.length()), random.nextInt(IMAGE_HEIGHT),
                    random.nextInt(IMAGE_WIDTH * captcha.length()), random.nextInt(IMAGE_HEIGHT));
        }

        // 释放资源
        g.dispose();

        // 将图片转换为字节流
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        ImageIO.write(image, "PNG", outputStream);
        return outputStream.toByteArray();
    }

    public static void main(String[] args) throws IOException {
        // 生成验证码
        String captcha = generateCaptcha(6);
        System.out.println("验证码: " + captcha);
       // File file = new File("captcha.png");
        // 生成验证码图片并保存为文件（测试用）
        byte[] imageBytes = generateCaptchaImage(captcha);
        //file.
        // 可以将 imageBytes 返回给前端，或者保存为文件
       Files.write(Paths.get("captcha.png"), imageBytes);
    }
}
