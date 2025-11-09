package com.riversoft.platform.script;

import com.riversoft.core.IDGenerator;
import com.riversoft.core.exception.ExceptionType;
import com.riversoft.core.exception.SystemRuntimeException;
import com.riversoft.platform.script.function.ImageHelper;
import net.coobird.thumbnailator.Thumbnails;
import net.coobird.thumbnailator.geometry.Coordinate;
import net.coobird.thumbnailator.geometry.Positions;
import org.apache.commons.io.FileUtils;
import org.junit.Assert;
import org.junit.Test;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * @borball on 4/8/2016.
 */
public class ImageHelperTest {

    String[] texts = new String[]{"王小文", "李嘉诚", "菜小姐", "张公子", "波波", "刘美", "哈哈哈", "广州", "中国", "张公子", "菜小", "波波翟", "刘刘", "2016届计算机3班", "Borball"};

    @Test
    public void testImg() {
        StringBuffer html = new StringBuffer();
        html.append("<html>\n" +
                "<body>\n" +
                "<h2>Text to Image Test</h2>\n" +
                "<table>\n" +
                "  <tr>\n" +
                "    <th>文字</th>\n" +
                "    <th>图片</th> \n" +
                "  </tr>\n");
        for (String text : texts) {
            html.append("<tr>\n" +
                    "    <td>").append(text).append("</td>\n");
            html.append("<td><img src=\"").append(ImageHelper.img(text)).append("\"></td>\n");
            html.append("</tr>\n");
        }
        html.append("</table>\n" +
                "</body>\n" +
                "</html>");
        System.out.println(html);
    }

    @Test
    public void testImgReverse() {
        Map<String, Object> map = new HashMap<>();
        map.put("bg", true);
        StringBuffer html = new StringBuffer();
        html.append("<html>\n" +
                "<body>\n" +
                "<h2>Text to Image Test</h2>\n" +
                "<table>\n" +
                "  <tr>\n" +
                "    <th>文字</th>\n" +
                "    <th>图片</th> \n" +
                "  </tr>\n");
        for (String text : texts) {
            html.append("<tr>\n" +
                    "    <td>").append(text).append("</td>\n");
            html.append("<td><img src=\"").append(ImageHelper.img(text, map)).append("\"></td>\n");
            html.append("</tr>\n");
        }
        html.append("</table>\n" +
                "</body>\n" +
                "</html>");
        System.out.println(html);
    }

    @Test
    public void testImgSetColor() {
        Map<String, Object> map = new HashMap<>();
        map.put("bg", true);
        map.put("color", "#ffff00");
        StringBuffer html = new StringBuffer();
        html.append("<html>\n" +
                "<body>\n" +
                "<h2>Text to Image Test</h2>\n" +
                "<table>\n" +
                "  <tr>\n" +
                "    <th>文字</th>\n" +
                "    <th>图片</th> \n" +
                "  </tr>\n");

        html.append("<tr>\n" +
                "    <td>").append("测试自定义颜色").append("</td>\n");
        html.append("<td><img src=\"").append(ImageHelper.img("测试自定义颜色", map)).append("\"></td>\n");
        html.append("</tr>\n");
        html.append("</table>\n" +
                "</body>\n" +
                "</html>");
        System.out.println(html);
    }

    @Test
    public void testImgSetSize() {
        Map<String, Object> map = new HashMap<>();
        map.put("bg", true);
        map.put("color", "#ffff00");
        map.put("width", 200);
        map.put("height", 200);
        StringBuffer html = new StringBuffer();
        html.append("<html>\n" +
                "<body>\n" +
                "<h2>Text to Image Test</h2>\n" +
                "<table>\n" +
                "  <tr>\n" +
                "    <th>文字</th>\n" +
                "    <th>图片</th> \n" +
                "  </tr>\n");

        html.append("<tr>\n" +
                "    <td>").append("测试自定义颜色").append("</td>\n");
        html.append("<td><img src=\"").append(ImageHelper.img("测试自定义颜色", map)).append("\"></td>\n");
        html.append("</tr>\n");
        html.append("</table>\n" +
                "</body>\n" +
                "</html>");
        System.out.println(html);
    }

    @Test
    public void testHead() {
        File file = new File(this.getClass().getClassLoader().getResource("images/borball.jpg").getFile());
        String base64 = "data:image/png;base64," + org.apache.commons.codec.binary.Base64.encodeBase64String(ImageHelper.head(file, 640));
        File saveAs = save(base64);
        Assert.assertNotNull(saveAs);
    }

    public File save(String base64) {
        String[] items = base64.split(",");
        byte[] binary = org.apache.commons.codec.binary.Base64.decodeBase64(items[1]);
        String type = items[0].replace("data:image/", "").replace(";base64", "");

        String fileName = IDGenerator.uuid().concat(".").concat(type);

        File defaultImagePath = new File(FileUtils.getTempDirectory(), "image");
        if (!defaultImagePath.exists()) {
            defaultImagePath.mkdirs();
        }

        File image = new File(defaultImagePath, fileName);
        try {
            FileUtils.copyInputStreamToFile(new ByteArrayInputStream(binary), image);
            return image;
        } catch (IOException e) {
            throw new SystemRuntimeException(ExceptionType.BUSINESS, "另存为文件失败", e);
        }
    }

    @Test
    public void testMerge1() {
        try {
            File white = new File(this.getClass().getClassLoader().getResource("images/white.png").getFile());
            File watermark = new File(this.getClass().getClassLoader().getResource("images/watermark.jpg").getFile());
            byte[] head = head(watermark, 640);
            BufferedImage bufferedImage = ImageIO.read(new ByteArrayInputStream(head));
            Thumbnails.of(white).size(640, 640).
                    watermark(new Coordinate(0, 0), bufferedImage, 1f).
                    outputFormat("JPG").toFile(new File(FileUtils.getTempDirectory(), "test1.JPG"));

        } catch (IOException e) {
        }
    }

    @Test
    public void testMerge2() {
        try {
            File white = new File(this.getClass().getClassLoader().getResource("images/white.png").getFile());
            File watermark = new File(this.getClass().getClassLoader().getResource("images/watermark.jpg").getFile());
            byte[] head = head(watermark, 315);
            BufferedImage bufferedImage = ImageIO.read(new ByteArrayInputStream(head));
            Thumbnails.of(white).size(640, 640).
                    watermark(new Coordinate(0, 148), bufferedImage, 1f).
                    watermark(new Coordinate(325, 148), bufferedImage, 1f).
                    outputFormat("JPG").toFile(new File(FileUtils.getTempDirectory(), "test2.JPG"));

        } catch (IOException e) {
        }
    }

    @Test
    public void testMerge3() {
        try {
            File white = new File(this.getClass().getClassLoader().getResource("images/white.png").getFile());
            File watermark = new File(this.getClass().getClassLoader().getResource("images/watermark.jpg").getFile());
            byte[] head = head(watermark, 315);
            BufferedImage bufferedImage = ImageIO.read(new ByteArrayInputStream(head));
            Thumbnails.of(white).size(640, 640).
                    watermark(new Coordinate(148, 0), bufferedImage, 1f).
                    watermark(new Coordinate(0, 325), bufferedImage, 1f).
                    watermark(new Coordinate(325, 325), bufferedImage, 1f).
                    outputFormat("JPG").toFile(new File(FileUtils.getTempDirectory(), "test3.JPG"));

        } catch (IOException e) {
        }
    }

    @Test
    public void testMerge4() {
        try {
            File white = new File(this.getClass().getClassLoader().getResource("images/white.png").getFile());
            File watermark = new File(this.getClass().getClassLoader().getResource("images/watermark.jpg").getFile());
            byte[] head = head(watermark, 315);
            BufferedImage bufferedImage = ImageIO.read(new ByteArrayInputStream(head));
            Thumbnails.of(white).size(640, 640).
                    watermark(new Coordinate(0, 0), bufferedImage, 1f).
                    watermark(new Coordinate(325, 0), bufferedImage, 1f).
                    watermark(new Coordinate(0, 325), bufferedImage, 1f).
                    watermark(new Coordinate(325, 325), bufferedImage, 1f).
                    outputFormat("JPG").toFile(new File(FileUtils.getTempDirectory(), "test4.JPG"));

        } catch (IOException e) {
        }
    }

    @Test
    public void testMerge5() {
        try {
            File white = new File(this.getClass().getClassLoader().getResource("images/white.png").getFile());
            File watermark = new File(this.getClass().getClassLoader().getResource("images/watermark.jpg").getFile());
            byte[] head = head(watermark, 210);
            BufferedImage bufferedImage = ImageIO.read(new ByteArrayInputStream(head));
            Thumbnails.of(white).size(640, 640).
                    watermark(new Coordinate(108, 108), bufferedImage, 1f).
                    watermark(new Coordinate(323, 108), bufferedImage, 1f).
                    watermark(new Coordinate(0, 323), bufferedImage, 1f).
                    watermark(new Coordinate(215, 323), bufferedImage, 1f).
                    watermark(new Coordinate(430, 323), bufferedImage, 1f).
                    outputFormat("JPG").toFile(new File(FileUtils.getTempDirectory(), "test5.JPG"));
        } catch (IOException e) {
        }
    }

    @Test
    public void testMerge6() {
        try {
            File white = new File(this.getClass().getClassLoader().getResource("images/white.png").getFile());
            File watermark = new File(this.getClass().getClassLoader().getResource("images/watermark.jpg").getFile());
            byte[] head = head(watermark, 210);
            BufferedImage bufferedImage = ImageIO.read(new ByteArrayInputStream(head));
            Thumbnails.of(white).size(640, 640).
                    watermark(new Coordinate(0, 108), bufferedImage, 1f).
                    watermark(new Coordinate(215, 108), bufferedImage, 1f).
                    watermark(new Coordinate(430, 108), bufferedImage, 1f).
                    watermark(new Coordinate(0, 323), bufferedImage, 1f).
                    watermark(new Coordinate(215, 323), bufferedImage, 1f).
                    watermark(new Coordinate(430, 323), bufferedImage, 1f).
                    outputFormat("JPG").toFile(new File(FileUtils.getTempDirectory(), "test6.JPG"));
        } catch (IOException e) {
        }
    }

    @Test
    public void testMerge7() {
        try {
            File white = new File(this.getClass().getClassLoader().getResource("images/white.png").getFile());
            File watermark = new File(this.getClass().getClassLoader().getResource("images/watermark.jpg").getFile());
            byte[] head = head(watermark, 210);
            BufferedImage bufferedImage = ImageIO.read(new ByteArrayInputStream(head));
            Thumbnails.of(white).size(640, 640).
                    watermark(Positions.TOP_CENTER, bufferedImage, 1f).
                    watermark(Positions.CENTER_LEFT, bufferedImage, 1f).
                    watermark(Positions.CENTER, bufferedImage, 1f).
                    watermark(Positions.CENTER_RIGHT, bufferedImage, 1f).
                    watermark(Positions.BOTTOM_LEFT, bufferedImage, 1f).
                    watermark(Positions.BOTTOM_CENTER, bufferedImage, 1f).
                    watermark(Positions.BOTTOM_RIGHT, bufferedImage, 1f).
                    outputFormat("JPG").toFile(new File(FileUtils.getTempDirectory(), "test7.JPG"));

        } catch (IOException e) {
        }
    }

    @Test
    public void testMerge8() {
        try {
            File white = new File(this.getClass().getClassLoader().getResource("images/white.png").getFile());
            File watermark = new File(this.getClass().getClassLoader().getResource("images/watermark.jpg").getFile());
            byte[] head = head(watermark, 210);
            BufferedImage bufferedImage = ImageIO.read(new ByteArrayInputStream(head));
            Thumbnails.of(white).size(640, 640).
                    watermark(new Coordinate(108, 0), bufferedImage, 1f).
                    watermark(new Coordinate(323, 0), bufferedImage, 1f).
                    watermark(Positions.CENTER_LEFT, bufferedImage, 1f).
                    watermark(Positions.CENTER, bufferedImage, 1f).
                    watermark(Positions.CENTER_RIGHT, bufferedImage, 1f).
                    watermark(Positions.BOTTOM_LEFT, bufferedImage, 1f).
                    watermark(Positions.BOTTOM_CENTER, bufferedImage, 1f).
                    watermark(Positions.BOTTOM_RIGHT, bufferedImage, 1f).
                    outputFormat("JPG").toFile(new File(FileUtils.getTempDirectory(), "test8.JPG"));
        } catch (IOException e) {
        }
    }

    @Test
    public void testMerge9() {
        try {
            File white = new File(this.getClass().getClassLoader().getResource("images/white.png").getFile());
            File watermark = new File(this.getClass().getClassLoader().getResource("images/watermark.jpg").getFile());
            byte[] head = head(watermark, 210);
            BufferedImage bufferedImage = ImageIO.read(new ByteArrayInputStream(head));
            Thumbnails.of(white).size(640, 640).
                    watermark(Positions.TOP_LEFT, bufferedImage, 1f).
                    watermark(Positions.TOP_CENTER, bufferedImage, 1f).
                    watermark(Positions.TOP_RIGHT, bufferedImage, 1f).
                    watermark(Positions.CENTER_LEFT, bufferedImage, 1f).
                    watermark(Positions.CENTER, bufferedImage, 1f).
                    watermark(Positions.CENTER_RIGHT, bufferedImage, 1f).
                    watermark(Positions.BOTTOM_LEFT, bufferedImage, 1f).
                    watermark(Positions.BOTTOM_CENTER, bufferedImage, 1f).
                    watermark(Positions.BOTTOM_RIGHT, bufferedImage, 1f).
                    outputFormat("JPG").toFile(new File(FileUtils.getTempDirectory(), "test9.JPG"));

        } catch (IOException e) {
        }
    }

    public byte[] head(File file, int size) {
        try {
            BufferedImage bufferedImage = ImageIO.read(file);
            int width = bufferedImage.getWidth();
            int height = bufferedImage.getHeight();
            int region = width > height ? height : width;
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            Thumbnails.of(bufferedImage).sourceRegion(Positions.CENTER, region, region).size(size, size).
                    keepAspectRatio(false).outputFormat("JPG").toOutputStream(outputStream);
            return outputStream.toByteArray();
        } catch (Exception e) {
            throw new SystemRuntimeException(ExceptionType.BUSINESS, "生成头像失败", e);
        }
    }

    @Test
    public void testMerge(){
        File watermark = new File(this.getClass().getClassLoader().getResource("images/watermark.jpg").getFile());
        Object[] files = new Object[]{watermark};
        byte[] bytes = ImageHelper.merge(files);
        try {
            FileUtils.copyInputStreamToFile(new ByteArrayInputStream(bytes), new File(FileUtils.getTempDirectory(), "test.JPG"));
        } catch (IOException e) {
            Assert.fail(e.getMessage());
        }
    }
}
