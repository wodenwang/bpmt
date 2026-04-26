package com.riversoft.util;

import org.junit.Test;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

/**
 * @borball on 3/19/2016.
 */
public class ImageUtilsTest {

    public static BufferedImage drawTranslucentStringPic(int width, int height, Integer fontHeight, String drawStr) {
        try {
            BufferedImage buffImg = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
            Graphics2D gd = buffImg.createGraphics();
            //设置透明  start
            buffImg = gd.getDeviceConfiguration().createCompatibleImage(width, height, Transparency.TRANSLUCENT);
            gd.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON); // 去除锯齿(当设置的字体过大的时候,会出现锯齿)

            gd = buffImg.createGraphics();
            //设置透明  end
            gd.setFont(new Font("微软雅黑", Font.BOLD, fontHeight)); //设置字体
            gd.setColor(Color.WHITE); //设置颜色
            gd.drawRect(0, 0, width, height); //画边框

            gd.setColor(Color.BLUE);
            gd.drawString(drawStr, width / 2 - fontHeight * drawStr.length() / 2, fontHeight); //输出文字（中文横向居中）
            return buffImg;
        } catch (Exception e) {
            return null;
        }
    }

    public static void main(String[] args) {
        BufferedImage imgMap = drawTranslucentStringPic(64, 64, 48, "我");
        File imgFile = new File("test.png");
        try {
            ImageIO.write(imgMap, "PNG", imgFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("生成完成");
    }

    @Test
    public void testImage() throws IOException {
        String image = ImageUtils.text2Image("我", new Color(0,139,230));
        System.out.println(image);
    }

}
