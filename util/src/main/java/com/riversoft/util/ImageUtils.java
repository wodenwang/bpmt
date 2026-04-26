package com.riversoft.util;

import org.apache.commons.codec.binary.Base64;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * @borball on 3/19/2016.
 */
public class ImageUtils {

    public static String text2Image(String text, String align, int y, int width, int height, Color fontColor, Font font, boolean reverse) throws IOException {
        BufferedImage img = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = img.createGraphics();
        img = g.getDeviceConfiguration().createCompatibleImage(width, height, Transparency.TRANSLUCENT);
        g = img.createGraphics();

        if(reverse) {
            g.setColor(fontColor); // 背景色
        } else {
            g.setColor(Color.WHITE); // 背景色
        }
        g.fillRect(0, 0, width, height); // 画一个矩形
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON); // 去除锯齿(当设置的字体过大的时候,会出现锯齿)

        if(reverse) {
            g.setColor(Color.WHITE); // 背景色
        } else {
            g.setColor(fontColor); // 字的颜色
        }
        g.setFont(font); // 字体字形字号

        int size = font.getSize();  //文字大小
        int x = 5;
        if (align.equals("left")) {
            x = 5;
        } else if (align.equals("right")) {
            x = width - size * text.length() - 5;
        } else if (align.equals("center")) {
            x = (width - size * text.length()) / 2;
        }
        g.drawString(text, x, y);
        g.dispose();

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        ImageIO.write(img, "png", outputStream);
        return "data:image/png;base64," + Base64.encodeBase64String(outputStream.toByteArray());
    }

    public static String text2Image(String text, Color fontColor) throws IOException {
        return text2Image(text, "center", 48, 64, 64, fontColor, new Font("微软雅黑", Font.PLAIN, 48), false);

    }
}
