package com.riversoft.platform.script.function;

import java.awt.Color;
import java.awt.Font;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import javax.imageio.ImageIO;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.compress.utils.IOUtils;
import org.apache.commons.io.FileUtils;

import com.riversoft.core.Config;
import com.riversoft.core.IDGenerator;
import com.riversoft.core.exception.ExceptionType;
import com.riversoft.core.exception.SystemRuntimeException;
import com.riversoft.core.script.annotation.ScriptSupport;
import com.riversoft.platform.Platform;
import com.riversoft.platform.web.FileManager;
import com.riversoft.util.ImageUtils;

import net.coobird.thumbnailator.Thumbnails;
import net.coobird.thumbnailator.geometry.Coordinate;
import net.coobird.thumbnailator.geometry.Positions;

/**
 * @borball on 4/8/2016.
 */
@ScriptSupport("img")
public class ImageHelper {

	static Color color1 = new Color(0, 139, 230);
	static Color color2 = new Color(195, 90, 230);
	static Color color3 = new Color(230, 163, 96);
	static Color color4 = new Color(97, 230, 75);
	static Color color5 = new Color(58, 230, 187);
	static Color color6 = new Color(230, 49, 133);
	static Color color7 = new Color(230, 228, 18);
	static Color color8 = new Color(230, 32, 15);

	private static Color[] GOOD_COLORS = new Color[] { color1, color2, color3, color4, color5, color6, color7, color8 };

	static Coordinate coordinate1_0 = new Coordinate(0, 0);

	static Coordinate coordinate2_00 = new Coordinate(0, 148);
	static Coordinate coordinate2_01 = new Coordinate(325, 148);

	static Coordinate coordinate3_00 = new Coordinate(148, 0);
	static Coordinate coordinate3_01 = new Coordinate(0, 325);
	static Coordinate coordinate3_02 = new Coordinate(325, 325);

	static Coordinate coordinate4_00 = new Coordinate(0, 0);
	static Coordinate coordinate4_01 = new Coordinate(325, 0);
	static Coordinate coordinate4_02 = new Coordinate(0, 325);
	static Coordinate coordinate4_03 = new Coordinate(325, 325);

	static Coordinate coordinate5_00 = new Coordinate(108, 108);
	static Coordinate coordinate5_01 = new Coordinate(323, 108);
	static Coordinate coordinate5_02 = new Coordinate(0, 323);
	static Coordinate coordinate5_03 = new Coordinate(215, 323);
	static Coordinate coordinate5_04 = new Coordinate(430, 323);

	static Coordinate coordinate6_00 = new Coordinate(0, 108);
	static Coordinate coordinate6_01 = new Coordinate(215, 108);
	static Coordinate coordinate6_02 = new Coordinate(430, 108);
	static Coordinate coordinate6_03 = new Coordinate(0, 323);
	static Coordinate coordinate6_04 = new Coordinate(215, 323);
	static Coordinate coordinate6_05 = new Coordinate(430, 323);

	static Coordinate coordinate7_00 = new Coordinate(215, 0);
	static Coordinate coordinate7_01 = new Coordinate(0, 215);
	static Coordinate coordinate7_02 = new Coordinate(215, 215);
	static Coordinate coordinate7_03 = new Coordinate(430, 215);
	static Coordinate coordinate7_04 = new Coordinate(0, 430);
	static Coordinate coordinate7_05 = new Coordinate(215, 430);
	static Coordinate coordinate7_06 = new Coordinate(430, 430);

	static Coordinate coordinate8_00 = new Coordinate(108, 0);
	static Coordinate coordinate8_01 = new Coordinate(323, 0);
	static Coordinate coordinate8_02 = new Coordinate(0, 215);
	static Coordinate coordinate8_03 = new Coordinate(215, 215);
	static Coordinate coordinate8_04 = new Coordinate(430, 215);
	static Coordinate coordinate8_05 = new Coordinate(0, 430);
	static Coordinate coordinate8_06 = new Coordinate(215, 430);
	static Coordinate coordinate8_07 = new Coordinate(430, 430);

	static Coordinate coordinate9_00 = new Coordinate(0, 0);
	static Coordinate coordinate9_01 = new Coordinate(215, 0);
	static Coordinate coordinate9_02 = new Coordinate(430, 0);
	static Coordinate coordinate9_03 = new Coordinate(0, 215);
	static Coordinate coordinate9_04 = new Coordinate(215, 215);
	static Coordinate coordinate9_05 = new Coordinate(430, 215);
	static Coordinate coordinate9_06 = new Coordinate(0, 430);
	static Coordinate coordinate9_07 = new Coordinate(215, 430);
	static Coordinate coordinate9_08 = new Coordinate(430, 430);

	private static Coordinate[] XY1 = new Coordinate[] { coordinate1_0 };
	private static Coordinate[] XY2 = new Coordinate[] { coordinate2_00, coordinate2_01 };
	private static Coordinate[] XY3 = new Coordinate[] { coordinate3_00, coordinate3_01, coordinate3_02 };
	private static Coordinate[] XY4 = new Coordinate[] { coordinate4_00, coordinate4_01, coordinate4_02, coordinate4_03 };
	private static Coordinate[] XY5 = new Coordinate[] { coordinate5_00, coordinate5_01, coordinate5_02, coordinate5_03, coordinate5_04 };
	private static Coordinate[] XY6 = new Coordinate[] { coordinate6_00, coordinate6_01, coordinate6_02, coordinate6_03, coordinate6_04, coordinate6_05 };
	private static Coordinate[] XY7 = new Coordinate[] { coordinate7_00, coordinate7_01, coordinate7_02, coordinate7_03, coordinate7_04, coordinate7_05, coordinate7_06 };
	private static Coordinate[] XY8 = new Coordinate[] { coordinate8_00, coordinate8_01, coordinate8_02, coordinate8_03, coordinate8_04, coordinate8_05, coordinate8_06, coordinate8_07 };
	private static Coordinate[] XY9 = new Coordinate[] { coordinate9_00, coordinate9_01, coordinate9_02, coordinate9_03, coordinate9_04, coordinate9_05, coordinate9_06, coordinate9_07,
			coordinate9_08 };

	private static Coordinate[][] XY = new Coordinate[][] { XY1, XY2, XY3, XY4, XY5, XY6, XY7, XY8, XY9 };

	/**
	 * 根据文字生成彩色图片(base64字符流)
	 *
	 * @param text
	 * @return
	 */
	public static String img(String text) {
		return img(text, new HashMap<String, Object>());
	}

	/**
	 * 根据配置生成图片(base64字符流)
	 *
	 * @param text
	 * @param option
	 *            图片配置:<br>
	 *            width: 200px,默认值200<br>
	 *            height: 200px,默认值200<br>
	 *            color:#ff88ff,留空则根据文字内容算出固定颜色<br>
	 *            bg:true/false,默认true.false表示白色背景,color颜色的字体;true表示反转,
	 *            color背景白色字体
	 * @return
	 */
	public static String img(String text, Map<String, Object> option) {
		// int width = option.containsKey("width")? (Integer)option.get("width")
		// : 64;
		// int height = option.containsKey("height")?
		// (Integer)option.get("width") : 64;
		// 先写死64和48，计算和排版需要下点功夫调
		int width = 64;
		int height = 64;
		Color color = option.containsKey("color") ? Color.decode((String) option.get("color")) : calculate(text);
		boolean reverse = option.containsKey("bg") ? (Boolean) option.get("bg") : true;

		try {
			return ImageUtils.text2Image(text.substring(0, 1), "center", 48, width, height, color, new Font("微软雅黑", Font.PLAIN, 48), reverse);
		} catch (IOException e) {
			throw new SystemRuntimeException(ExceptionType.BUSINESS, "文字转图片失败", e);
		}
	}

	private static Color calculate(String text) {
		return GOOD_COLORS[Math.abs(text.hashCode() % GOOD_COLORS.length)];
	}

	/**
	 * 根据图片生成头像
	 *
	 * @param file
	 * @param size
	 * @return
	 */
	public static byte[] head(Object file, int size) {
		try {
			BufferedImage bufferedImage = ImageIO.read(readStream(file));
			int width = bufferedImage.getWidth();
			int height = bufferedImage.getHeight();
			int region = width > height ? height : width;
			ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
			Thumbnails.of(bufferedImage).sourceRegion(Positions.CENTER, region, region).size(size, size).keepAspectRatio(false).outputFormat("JPG").toOutputStream(outputStream);
			return outputStream.toByteArray();
		} catch (Exception e) {
			throw new SystemRuntimeException(ExceptionType.BUSINESS, "生成头像失败", e);
		}
	}

	/**
	 * 根据图片生成base64图片
	 *
	 * @param file
	 * @return
	 */
	public static String base64(Object file) {
		try {
			return "data:image/png;base64," + Base64.encodeBase64String(IOUtils.toByteArray(readStream(file)));
		} catch (Exception e) {
			throw new SystemRuntimeException(ExceptionType.BUSINESS, "生成base64图片失败", e);
		}
	}

	/**
	 * base64图片另存为文件
	 *
	 * @param base64
	 * @return
	 */
	public static File save(String base64) {
		String[] items = base64.split(",");
		byte[] binary = Base64.decodeBase64(items[1]);
		String type = items[0].replace("data:image/", "").replace(";base64", "");

		String fileName = IDGenerator.uuid().concat(".").concat(type);

		File defaultImagePath = new File(Platform.getDefaultDownloadPath(), "base64pic");
		File imageDownloadPath = new File(Config.get("file.base64.path", defaultImagePath.getAbsolutePath()));
		if (!imageDownloadPath.exists()) {
			imageDownloadPath.mkdirs();
		}

		File image = new File(imageDownloadPath, fileName);
		try {
			FileUtils.copyInputStreamToFile(new ByteArrayInputStream(binary), image);
			return image;
		} catch (IOException e) {
			throw new SystemRuntimeException(ExceptionType.BUSINESS, "生成文件失败", e);
		}
	}

	/**
	 * 字节数组转文件
	 * 
	 * @param bytes
	 * @param type
	 * @return
	 */
	public static File byte2file(byte[] bytes, String type) {
		String fileName = IDGenerator.uuid().concat(".").concat(type);
		File defaultImagePath = new File(Platform.getDefaultDownloadPath(), "base64pic");
		File imageDownloadPath = new File(Config.get("file.base64.path", defaultImagePath.getAbsolutePath()));
		if (!imageDownloadPath.exists()) {
			imageDownloadPath.mkdirs();
		}
		File image = new File(imageDownloadPath, fileName);
		try {
			FileUtils.writeByteArrayToFile(image, bytes);
			return image;
		} catch (IOException e) {
			throw new SystemRuntimeException(ExceptionType.BUSINESS, "生成文件失败", e);
		}
	}

	/**
	 * 字节数组转base64
	 * 
	 * @param bytes
	 * @param type
	 * @return
	 */
	public static String byte2base64(byte[] bytes, String type) {
		try {
			return "data:image/" + type + ";base64," + Base64.encodeBase64String(bytes);
		} catch (Exception e) {
			throw new SystemRuntimeException(ExceptionType.BUSINESS, "生成base64图片失败", e);
		}
	}

	/**
	 * 图片翻转
	 * 
	 * @param file
	 * @param rotation
	 * @return
	 */
	public static byte[] rotate(Object file, double rotation) {
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		try {
			Thumbnails.of(readStream(file)).rotate(rotation).outputFormat("JPG").toOutputStream(outputStream);
			return outputStream.toByteArray();
		} catch (IOException e) {
			throw new SystemRuntimeException(ExceptionType.BUSINESS, "图片翻转失败", e);
		}
	}

	/**
	 * 加水印， 默认0.5f透明程度
	 * 
	 * @param file
	 * @param waterMark
	 * @return
	 */
	public static byte[] mark(Object file, Object waterMark) {
		return mark(file, waterMark, 0.5f);
	}

	/**
	 * 加水印
	 * 
	 * @param file
	 * @param waterMark
	 * @param transparent
	 *            透明程度
	 * @return
	 */
	public static byte[] mark(Object file, Object waterMark, float transparent) {
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		try {
			BufferedImage waterMarkImage = ImageIO.read(readStream(waterMark));
			Thumbnails.of(readStream(file)).watermark(Positions.BOTTOM_RIGHT, waterMarkImage, transparent).outputFormat("JPG").toOutputStream(outputStream);
			return outputStream.toByteArray();
		} catch (IOException e) {
			throw new SystemRuntimeException(ExceptionType.BUSINESS, "图片翻转失败", e);
		}
	}

	/**
	 * 保持比例缩放 resize(file, 200, 300) 若图片横比200小，高比300小，不变
	 * 若图片横比200小，高比300大，高缩小到300，图片比例不变 若图片横比200大，高比300小，横缩小到200，图片比例不变
	 * 若图片横比200大，高比300大，图片按比例缩小，横为200或高为300
	 *
	 * @param file
	 * @param width
	 *            宽
	 * @param height
	 *            高
	 * @return
	 */
	public static byte[] resize(Object file, int width, int height) {
		return resize(file, width, height, true);
	}

	/**
	 * 按比例缩放
	 * 
	 * @param file
	 * @param scale
	 *            比例， 如0.5d
	 * @return
	 */
	public static byte[] resize(Object file, double scale) {
		try {
			ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
			Thumbnails.of(readStream(file)).scale(scale).toOutputStream(outputStream);
			return outputStream.toByteArray();
		} catch (Exception e) {
			throw new SystemRuntimeException(ExceptionType.BUSINESS, "图片缩放失败", e);
		}
	}

	/**
	 * 强制按指定尺寸缩放
	 * 
	 * @param file
	 * @param width
	 * @param height
	 * @return
	 */
	public static byte[] forceResize(Object file, int width, int height) {
		return resize(file, width, height, false);
	}

	/**
	 * 合并多个图片, 生成一个640*640的图片
	 * 
	 * @param files
	 * @return
	 */
	public static byte[] merge(Object... files) {
		int length = files.length;
		int size = 0;
		if (length == 1) {
			return head(files[0], 320);// 一张图片直接缩小返回
		} else if (length >= 2 && length < 5) {
			size = 315;
		} else if (length >= 5 && length <= 9) {
			size = 210;
		} else {
			throw new IllegalArgumentException("文件数不能少于1或者多于9");
		}

		try {
			File white = new File(ImageHelper.class.getClassLoader().getResource("images/white.png").getFile());
			ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

			Thumbnails.Builder builder = Thumbnails.of(white).size(640, 640);

			Coordinate[] xys = XY[length - 1];
			for (int i = 0; i < length; i++) {
				byte[] head = head(readStream(files[i]), size);
				BufferedImage bufferedImage = ImageIO.read(new ByteArrayInputStream(head));
				builder.watermark(xys[i], bufferedImage, 1f);
			}

			builder.outputFormat("JPG").toOutputStream(outputStream);
			return outputStream.toByteArray();
		} catch (IOException e) {
			throw new SystemRuntimeException(ExceptionType.BUSINESS, "合并图片失败", e);
		}
	}

	/**
	 * 合并多个图片, 生成一个640*640的图片
	 * 
	 * @param files,
	 *            base64图片
	 * @return
	 */
	public static byte[] merge(String... files) {
		ByteArrayInputStream[] streams = new ByteArrayInputStream[files.length];
		for (int i = 0; i < files.length; i++) {
			String[] items = files[i].split(",");
			byte[] binary = Base64.decodeBase64(items[1]);
			streams[i] = new ByteArrayInputStream(binary);
		}
		return merge(streams);
	}

	private static byte[] resize(Object file, int width, int height, boolean keepRatio) {
		try {
			ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
			Thumbnails.of(readStream(file)).size(width, height).keepAspectRatio(keepRatio).toOutputStream(outputStream);
			return outputStream.toByteArray();
		} catch (Exception e) {
			throw new SystemRuntimeException(ExceptionType.BUSINESS, "图片缩放失败", e);
		}
	}

	private static InputStream readStream(Object file) {
		try {
			InputStream fileStream = null;
			if (file instanceof byte[]) {
				java.util.List<FileManager.UploadFile> fileList = FileManager.toFiles((byte[]) file);
				if (fileList == null || fileList.size() < 1) {
					throw new SystemRuntimeException(ExceptionType.SCRIPT, "file不存在.");
				}
				fileStream = fileList.get(0).getInputStream();
			} else if (file instanceof InputStream) {
				fileStream = (InputStream) file;
			} else if (file instanceof File) {
				fileStream = new FileInputStream((File) file);
			}

			return fileStream;
		} catch (Exception e) {
			throw new SystemRuntimeException(ExceptionType.BUSINESS, "获取文件流失败", e);
		}
	}
}
