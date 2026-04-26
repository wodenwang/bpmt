package com.riversoft.platform.script.function;

import java.io.ByteArrayOutputStream;
import java.io.File;

import org.apache.commons.codec.binary.Base64;

import com.riversoft.core.script.annotation.ScriptSupport;

import net.glxn.qrgen.javase.QRCode;

/**
 * Created by exizhai on 1/30/2016.
 */
@ScriptSupport("qrcode")
public class QRCodeHelper {

	/**
	 * 默认的二维码
	 * 
	 * @param text
	 * @return
	 */
	public static File file(String text) {
		return QRCode.from(text).withCharset("UTF-8").file();
	}

	public static ByteArrayOutputStream stream(String text) {
		return QRCode.from(text).withCharset("UTF-8").stream();
	}

	public static String img(String text) {
		return "data:image/png;base64," + Base64.encodeBase64String(stream(text).toByteArray());
	}

	/**
	 * 可以设置长宽
	 * 
	 * @param text
	 * @param width
	 * @param height
	 * @return
	 */
	public static File file(String text, int width, int height) {
		return QRCode.from(text).withCharset("UTF-8").withSize(width, height).file();
	}

	public static ByteArrayOutputStream stream(String text, int width, int height) {
		return QRCode.from(text).withCharset("UTF-8").withSize(width, height).stream();
	}

	public static String img(String text, int width, int height) {
		return "data:image/png;base64," + Base64.encodeBase64String(stream(text, width, height).toByteArray());
	}

	/**
	 * 可以设置长宽和颜色
	 * 
	 * @param text
	 * @param width
	 * @param height
	 * @param onColor
	 * @param offColor
	 * @return
	 */
	public static File file(String text, int width, int height, int onColor, int offColor) {
		return QRCode.from(text).withCharset("UTF-8").withSize(width, height).withColor(onColor, offColor).file();
	}

	public static ByteArrayOutputStream stream(String text, int width, int height, int onColor, int offColor) {
		return QRCode.from(text).withCharset("UTF-8").withSize(width, height).withColor(onColor, offColor).stream();
	}

	public static String img(String text, int width, int height, int onColor, int offColor) {
		return "data:image/png;base64," + Base64.encodeBase64String(stream(text, width, height, onColor, offColor).toByteArray());
	}

	/**
	 * 也可以使用原生QRCode的API
	 * 
	 * @param text
	 * @return
	 */
	public static QRCode from(String text) {
		return QRCode.from(text).withCharset("UTF-8");
	}
}
