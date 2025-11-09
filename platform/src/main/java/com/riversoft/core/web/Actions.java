/*
 * File Name  :Directs.java
 * Create Date:2012-11-6 上午12:22:55
 * Author     :woden
 */

package com.riversoft.core.web;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.imageio.ImageIO;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.riversoft.core.Config;
import com.riversoft.core.exception.ExceptionType;
import com.riversoft.core.exception.SystemRuntimeException;
import com.riversoft.core.web.annotation.ActionMode;
import com.riversoft.core.web.annotation.ActionMode.Mode;
import com.riversoft.util.PoiUtils;
import com.riversoft.util.jackson.JsonMapper;

/**
 * Action 框架下转发工具类，每一个action实例最终都需要依赖Directs中的静态方法来决定下一步。
 * 
 */
public final class Actions {
	/**
	 * Logger for this class
	 */
	private static final Logger logger = LoggerFactory.getLogger(Actions.class);

	private static final String MOBILE_UA_REG = "(?i).*((android|bb\\d+|meego).+mobile|avantgo|bada\\/|blackberry|blazer|compal|elaine|fennec|hiptop|iemobile|ip(hone|od)|iris|kindle|lge |maemo|midp|mmp|mobile.+firefox|netfront|opera m(ob|in)i|palm( os)?|phone|p(ixi|re)\\/|plucker|pocket|psp|series(4|6)0|symbian|treo|up\\.(browser|link)|vodafone|wap|windows ce|xda|xiino).*";
	private static final String MOBILE_UA_FIRST4_REG = "(?i)1207|6310|6590|3gso|4thp|50[1-6]i|770s|802s|a wa|abac|ac(er|oo|s\\-)|ai(ko|rn)|al(av|ca|co)|amoi|an(ex|ny|yw)|aptu|ar(ch|go)|as(te|us)|attw|au(di|\\-m|r |s )|avan|be(ck|ll|nq)|bi(lb|rd)|bl(ac|az)|br(e|v)w|bumb|bw\\-(n|u)|c55\\/|capi|ccwa|cdm\\-|cell|chtm|cldc|cmd\\-|co(mp|nd)|craw|da(it|ll|ng)|dbte|dc\\-s|devi|dica|dmob|do(c|p)o|ds(12|\\-d)|el(49|ai)|em(l2|ul)|er(ic|k0)|esl8|ez([4-7]0|os|wa|ze)|fetc|fly(\\-|_)|g1 u|g560|gene|gf\\-5|g\\-mo|go(\\.w|od)|gr(ad|un)|haie|hcit|hd\\-(m|p|t)|hei\\-|hi(pt|ta)|hp( i|ip)|hs\\-c|ht(c(\\-| |_|a|g|p|s|t)|tp)|hu(aw|tc)|i\\-(20|go|ma)|i230|iac( |\\-|\\/)|ibro|idea|ig01|ikom|im1k|inno|ipaq|iris|ja(t|v)a|jbro|jemu|jigs|kddi|keji|kgt( |\\/)|klon|kpt |kwc\\-|kyo(c|k)|le(no|xi)|lg( g|\\/(k|l|u)|50|54|\\-[a-w])|libw|lynx|m1\\-w|m3ga|m50\\/|ma(te|ui|xo)|mc(01|21|ca)|m\\-cr|me(rc|ri)|mi(o8|oa|ts)|mmef|mo(01|02|bi|de|do|t(\\-| |o|v)|zz)|mt(50|p1|v )|mwbp|mywa|n10[0-2]|n20[2-3]|n30(0|2)|n50(0|2|5)|n7(0(0|1)|10)|ne((c|m)\\-|on|tf|wf|wg|wt)|nok(6|i)|nzph|o2im|op(ti|wv)|oran|owg1|p800|pan(a|d|t)|pdxg|pg(13|\\-([1-8]|c))|phil|pire|pl(ay|uc)|pn\\-2|po(ck|rt|se)|prox|psio|pt\\-g|qa\\-a|qc(07|12|21|32|60|\\-[2-7]|i\\-)|qtek|r380|r600|raks|rim9|ro(ve|zo)|s55\\/|sa(ge|ma|mm|ms|ny|va)|sc(01|h\\-|oo|p\\-)|sdk\\/|se(c(\\-|0|1)|47|mc|nd|ri)|sgh\\-|shar|sie(\\-|m)|sk\\-0|sl(45|id)|sm(al|ar|b3|it|t5)|so(ft|ny)|sp(01|h\\-|v\\-|v )|sy(01|mb)|t2(18|50)|t6(00|10|18)|ta(gt|lk)|tcl\\-|tdg\\-|tel(i|m)|tim\\-|t\\-mo|to(pl|sh)|ts(70|m\\-|m3|m5)|tx\\-9|up(\\.b|g1|si)|utst|v400|v750|veri|vi(rg|te)|vk(40|5[0-3]|\\-v)|vm40|voda|vulc|vx(52|53|60|61|70|80|81|83|85|98)|w3c(\\-| )|webc|whit|wi(g |nc|nw)|wmlb|wonu|x700|yas\\-|your|zeto|zte\\-";

	/**
	 * Action框架下的常量
	 * 
	 */
	public static class Constant {
		/**
		 * 分页限制
		 */
		public static Integer PAGE_LIMIT = 20;
	}

	/**
	 * {@link HttpServletRequest#setAttribute(String, Object)}系统内部Keys.
	 * 
	 */
	public static enum Keys {
		/**
		 * 当前路径（类+方法）
		 */
		CUR_URL("_cur_url"),

		/**
		 * 当前action路径
		 */
		ACTION("_action"),

		/**
		 * 当前工程的绝对根路径
		 */
		CP("_cp"),
		/**
		 * CP+ACTION
		 */
		ACP("_acp"),
		/**
		 * 当前页面所处的区域div id.
		 */
		ZONE("_zone"),
		/**
		 * 界面传过来的错误区域div id..
		 */
		ERROR_ZONE("_error_zone"),
		/**
		 * 信息提示页:<br>
		 * 信息提示类型详见{@link Styles}.
		 */
		MSG_TYPE("_msg_type"),
		/**
		 * 信息提示页:<br>
		 * 信息内容.
		 */
		MSG("_msg"),
		/**
		 * HTML内容展示页:<br>
		 * HTML内容.
		 */
		HTML("_html"),
		/**
		 * 分页:<br>
		 * 分页限制
		 */
		LIMIT("_limit"),
		/**
		 * 分页:<br>
		 * 当前页数
		 */
		PAGE("_page"),
		/**
		 * 排序:<br>
		 * 排序字段
		 */
		FIELD("_field"),
		/**
		 * 排序:<br>
		 * 排序方向
		 */
		DIR("_dir"),
		/**
		 * 表单ID :<br>
		 * 表单提交后带到后台的表单ID
		 */
		FORM("_form"),
		/**
		 * 页面样式风格，默认值为"smoothness"
		 */
		STYLE("_style"),
		/**
		 * 客户扩展皮肤样式路径,当客户配置了此项,则默认STYLE无效
		 */
		EXT_STYLE("_ext_style"),
		/**
		 * 底色风格
		 */
		BACKGROUD_STYLE("_backgroud_style"),
		/**
		 * 页面标题,默认值为""
		 */
		TITLE("_title"),
		/**
		 * favicon.ico图标
		 */
		ICO("_ico"),
		/**
		 * CRUD:<br>
		 * table页面行多选框的name
		 */
		KEYS("_keys"),
		/**
		 * CRUD:<br>
		 * table编辑和明细页面的主键name
		 */
		KEY("_key"),
		/**
		 * 当前时间
		 */
		NOW("_now"),
		/**
		 * 每次请求界面上传的唯一标识(当前毫秒级别时间+随机数)
		 */
		RANDOM("_random"),
		/**
		 * 当前调用的方式:<br>
		 * html/json
		 */
		DATA_TYPE("_data_type"),
		/**
		 * 框架传递参数固定字段,用于菜单,控件配置传参
		 */
		PARAMS("_params"),
		/**
		 * 框架来源<br>
		 * 1:menu菜单<br>
		 * 2:home首页标签
		 */
		FRAME_TYPE("_frame_type"),
		/**
		 * 页面类型.xhtml/h5
		 */
		ACTION_MODE("_action_mode"),
		/**
		 * 视图主键
		 */
		VIEW_KEY("_view_key"),
		/**
		 * 当前网址(WXJSSDK与oauth2需要)
		 */
		FULL_URL("_full_url"),
		/**
		 * 是否需要页面头部
		 */
		HEAD("_head"),
		/**
		 * 引入JS LIB类型.可选项:amaze/weui.默认weui.
		 */
		H5_JS("_h5_js");

		private String name;

		private Keys(String name) {
			this.name = name;
		}

		@Override
		public String toString() {
			return name;
		}
	}

	/**
	 * web框架中使用的公共jsp页面路径.
	 * 
	 */
	public static enum Pages {
		/**
		 * 信息提示页:<br>
		 * 信息提示框架页.
		 */
		MSG_PAGE("/common/msg.jsp"),

		/**
		 * HTML内容展示
		 */
		HTML_PAGE("/common/html.jsp"),
		/**
		 * 分页标签内页
		 */
		PAGE_BAR_TAG("/common/page_bar.jsp"),
		/**
		 * javascript配置片段标签内页
		 */
		JAVA_SCRIPT_TAG("/common/javascript.jsp");

		private String page;

		private Pages(String page) {
			this.page = page;
		}

		public String getPage() {
			return page;
		}

	}

	/**
	 * 界面CSS样式class
	 * 
	 */
	public static enum Styles {
		/**
		 * 警告，黄色风格
		 */
		MSG_WARNING("warning"),
		/**
		 * 提示，蓝色风格
		 */
		MSG_INFO("info"),
		/**
		 * 错误，红色风格
		 */
		MSG_ERROR("error");

		private String name;

		private Styles(String name) {
			this.name = name;
		}

		@Override
		public String toString() {
			return name;
		}
	}

	/**
	 * 随机验证码生成
	 * 
	 * @author woden
	 * 
	 */
	private static class RandomValidateCode {

		private String sessionKey = "RANDOMVALIDATECODEKEY";// 放到session中的key
		private Random random = new Random();
		private String randString = "0123456789";// 随机产生的字符串

		private int width = 80;// 图片宽
		private int height = 22;// 图片高
		private int lineSize = 40;// 干扰线数量
		private int stringNum = 4;// 随机产生字符数量

		RandomValidateCode(String key) {
			this.sessionKey = key;
		}

		/*
		 * 获得字体
		 */
		private Font getFont() {
			return new Font("Fixedsys", Font.CENTER_BASELINE, 18);
		}

		/*
		 * 获得颜色
		 */
		private Color getRandColor(int fc, int bc) {
			if (fc > 255)
				fc = 255;
			if (bc > 255)
				bc = 255;
			int r = fc + random.nextInt(bc - fc - 16);
			int g = fc + random.nextInt(bc - fc - 14);
			int b = fc + random.nextInt(bc - fc - 18);
			return new Color(r, g, b);
		}

		/**
		 * 生成随机图片
		 */
		public void create(HttpServletRequest request, HttpServletResponse response) {
			HttpSession session = request.getSession();
			// BufferedImage类是具有缓冲区的Image类,Image类是用于描述图像信息的类
			BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_BGR);
			Graphics g = image.getGraphics();// 产生Image对象的Graphics对象,改对象可以在图像上进行各种绘制操作
			g.fillRect(0, 0, width, height);
			g.setFont(new Font("Times New Roman", Font.ROMAN_BASELINE, 18));
			g.setColor(getRandColor(110, 133));
			// 绘制干扰线
			for (int i = 0; i <= lineSize; i++) {
				drowLine(g);
			}
			// 绘制随机字符
			String randomString = "";
			for (int i = 1; i <= stringNum; i++) {
				randomString = drowString(g, randomString, i);
			}
			session.removeAttribute(sessionKey);
			session.setAttribute(sessionKey, randomString);
			g.dispose();

			try (OutputStream os = response.getOutputStream()) {
				ImageIO.write(image, "JPEG", os);// 将内存中的图片通过流动形式输出到客户端
				os.flush();
				response.flushBuffer();
			} catch (IOException e) {
				logger.error("生成图片出错", e);
			}
		}

		/*
		 * 绘制字符串
		 */
		private String drowString(Graphics g, String randomString, int i) {
			g.setFont(getFont());
			g.setColor(new Color(random.nextInt(101), random.nextInt(111), random.nextInt(121)));
			String rand = String.valueOf(getRandomString(random.nextInt(randString.length())));
			randomString += rand;
			g.translate(random.nextInt(3), random.nextInt(3));
			g.drawString(rand, 13 * i, 16);
			return randomString;
		}

		/*
		 * 绘制干扰线
		 */
		private void drowLine(Graphics g) {
			int x = random.nextInt(width);
			int y = random.nextInt(height);
			int xl = random.nextInt(13);
			int yl = random.nextInt(15);
			g.drawLine(x, y, x + xl, y + yl);
		}

		/*
		 * 获取随机的字符
		 */
		public String getRandomString(int num) {
			return String.valueOf(randString.charAt(num));
		}
	}

	/**
	 * 辅助工具类
	 * 
	 */
	public static final class Util {

		private static final String CLASS_ROOT = "com.riversoft.module";// action路径

		/**
		 * 设置页面标题(移动端有用)
		 * 
		 * @param request
		 * @param title
		 */
		public static void setTitle(HttpServletRequest request, String title) {
			request.setAttribute(Actions.Keys.TITLE.toString(), title);
		}

		/**
		 * 获取查询字符串
		 * 
		 * @param queryMap
		 * @param request
		 * @return
		 */
		public static Map<String, Object> buildQueryMap(Map<String, Object> queryMap, HttpServletRequest request) {
			if (queryMap == null) {
				queryMap = new HashMap<>();
			}

			Map<String, String[]> parameterMap = request.getParameterMap();
			Iterator<Map.Entry<String, String[]>> it = parameterMap.entrySet().iterator();
			while (it.hasNext()) {
				Map.Entry<String, String[]> entry = it.next();
				String key = entry.getKey();
				String value = parameterMap.get(key)[0].trim();
				if (key.matches("[_][a-z]+[_][a-zA-Z0-9_-]+") && value != null && !value.trim().equals("")) {// 判断格式
					queryMap.put(key, value);
				}
			}

			return queryMap;
		}

		/**
		 * 根据调用URL获取对应Action Class
		 * 
		 * @param request
		 * @return
		 */
		public static Class<?> getActionClass(HttpServletRequest request) {
			String servletPath = request.getServletPath();
			return getActionClass(servletPath);
		}

		/**
		 * 根据调用URL获取对应Action Class
		 * 
		 * @param servletPath
		 * @return
		 */
		public static Class<?> getActionClass(String servletPath) {
			if (StringUtils.isEmpty(servletPath) || StringUtils.lastIndexOf(servletPath, ".") < 1) {
				return null;
			}
			String strPathInfo = servletPath;
			strPathInfo = strPathInfo.substring(1, strPathInfo.lastIndexOf("."));
			String classname = CLASS_ROOT + "." + strPathInfo.substring(0, strPathInfo.lastIndexOf("/")).replaceAll("/", ".");

			try {
				Class<?> clazz = Class.forName(classname);
				return clazz;
			} catch (ClassNotFoundException e) {
				logger.warn("类[" + classname + "]不存在.", e);
				return null;
			}
		}

		/**
		 * 根据调用URL获取对应Action Method
		 * 
		 * @param request
		 * @param clazz
		 * @return
		 */
		public static Method getActionMethod(HttpServletRequest request, Class<?> clazz) {
			String strPathInfo = request.getServletPath();
			if (StringUtils.isEmpty(strPathInfo)) {
				return null;
			}

			strPathInfo = strPathInfo.substring(1, strPathInfo.lastIndexOf("."));
			String method = strPathInfo.substring(strPathInfo.lastIndexOf("/") + 1, strPathInfo.length());

			try {
				Method mth = clazz.getMethod(method, new Class[] { HttpServletRequest.class, HttpServletResponse.class });
				return mth;
			} catch (NoSuchMethodException | SecurityException e) {
				logger.warn("方法[" + method + "]不存在.", e);
				return null;
			}
		}

		/**
		 * 获取根网址<br>
		 * 返回例如:http://localhost:8080/aincs
		 * 
		 * @param request
		 * @return
		 */
		public static String getContextPath(HttpServletRequest request) {
			return request.getScheme() + "://" + request.getServerName() + (request.getServerPort() == 80 ? "" : (":" + request.getServerPort())) + request.getContextPath();
		}

		/**
		 * 获取当前完整网址
		 * 
		 * @param request
		 * @return
		 */
		public static String getFullURL(HttpServletRequest request) {
			String url = RequestUtils.getStringValue(request, Keys.FULL_URL.toString());
			if (StringUtils.isNotEmpty(url)) {
				return url;
			}

			StringBuffer requestURL = request.getRequestURL();
			String queryString = request.getQueryString();
			if (StringUtils.isEmpty(queryString)) {
				url = requestURL.toString();
			} else {
				url = requestURL.append('?').append(queryString).toString();
			}

			if (logger.isDebugEnabled()) {
				logger.debug("当前FULL URL:{}", url);
			}
			return url;
		}

		/**
		 * 得到真实的IP地址
		 * 
		 * @param request
		 * @return
		 */
		public static String getRealIpAddr(HttpServletRequest request) {
			String ip = request.getHeader("x-forwarded-for");
			if (StringUtils.isEmpty(ip) || StringUtils.equalsIgnoreCase("unknown", ip)) {
				ip = request.getHeader("Proxy-Client-IP");
			}
			if (StringUtils.isEmpty(ip) || StringUtils.equalsIgnoreCase("unknown", ip)) {
				ip = request.getHeader("WL-Proxy-Client-IP");
			}
			if (StringUtils.isEmpty(ip) || StringUtils.equalsIgnoreCase("unknown", ip)) {
				ip = request.getRemoteAddr();
			}
			if (ip == null) {
				ip = "";
			}
			return ip;
		}

		/**
		 * 是否微信访问
		 * 
		 * @param request
		 * @return
		 */
		public static boolean fromWx(HttpServletRequest request) {
			String userAgent = request.getHeader("user-agent");
			return StringUtils.isNotEmpty(userAgent) && userAgent.toLowerCase().contains("micromessenger");
		}

		/**
		 * 获取当前action路径<br>
		 * 如<b>/some/package/AnyAction</b>.
		 * 
		 * @param request
		 * @return
		 */
		public static String getActionUrl(HttpServletRequest request) {
			return getActionUrl(getActionClass(request));
		}

		/**
		 * 获取当前action路径<br>
		 * 如<b>/some/package/AnyAction</b>.
		 * 
		 * @param clazz
		 * @return
		 */
		public static String getActionUrl(Class<?> clazz) {
			if (clazz == null) {
				return null;
			}

			String className = clazz.getName();
			className = className.substring(CLASS_ROOT.length());// 截掉前面面的包名
			className = className.replaceAll("\\.", "/");
			return className;

		}

		/**
		 * 页面根目录
		 * 
		 * @param request
		 * @return
		 */
		private static String getPageRootPath(HttpServletRequest request) {
			String rootPath = null;
			Class<?> actionClass = getActionClass(request);
			if (actionClass != null) {
				Method actionMethod = getActionMethod(request, actionClass);
				ActionMode mode = null;
				if (actionMethod != null) {
					mode = actionMethod.getAnnotation(ActionMode.class);
				}
				if (mode == null) {
					mode = actionClass.getAnnotation(ActionMode.class);
				}
				Mode m = Mode.XHTML;// 默认
				if (mode != null) {
					m = mode.value();
				}

				switch (m) {
				case XHTML:
					rootPath = "/xhtml";
					break;
				case H5:
					rootPath = "/h5";
					break;
				case FIT:
					if (isMobile(request)) {
						rootPath = "/h5";
					} else {
						rootPath = "/xhtml";
					}
					break;
				case EXT:
					rootPath = mode.ext();
					break;
				default:
					break;
				}
			}

			if (StringUtils.isEmpty(rootPath)) {// 默认页面路径
				if (isMobile(request)) {
					rootPath = "/h5";
				} else {
					rootPath = "/xhtml";
				}
			}

			return rootPath;
		}

		/**
		 * 是否移动设备
		 * 
		 * @param request
		 * @return
		 */
		public static boolean isMobile(HttpServletRequest request) {
			String actionMode = (String) request.getAttribute(Keys.ACTION_MODE.toString());
			if (StringUtils.isEmpty(actionMode)) {
				actionMode = RequestUtils.getStringValue(request, Keys.ACTION_MODE.toString());
			}
			if ("xhtml".equalsIgnoreCase(actionMode)) {
				return false;
			} else if ("h5".equalsIgnoreCase(actionMode)) {
				return true;
			}

			String userAgent = request.getHeader("user-agent");
			return StringUtils.isNotEmpty(userAgent) && (userAgent.toLowerCase().matches(Config.get("mobile.useragent.reg", MOBILE_UA_REG))
					|| userAgent.toLowerCase().substring(0, 4).matches(Config.get("mobile.useragent.reg.04", MOBILE_UA_FIRST4_REG)));
		}

		/**
		 * 获取模块对应jsp页面路径<br>
		 * 自适应,若pageName为'/'开头则表示从/module根目录开始;否则则从action对应目录开始
		 * 
		 * @param request
		 * @param pageName
		 *            jsp文件名
		 * @return
		 */
		public static String getPagePath(HttpServletRequest request, String pageName) {
			if (pageName.startsWith("/")) {
				return getPageRootPath(request) + pageName;
			} else {
				return getPageRootPath(request) + getActionUrl(request) + "/" + pageName;
			}
		}

		/**
		 * 获取排序方向
		 * 
		 * @param request
		 * @return
		 */
		public static String getSortDir(HttpServletRequest request) {
			String dir = RequestUtils.getStringValue(request, Keys.DIR.toString());
			if ("desc".equalsIgnoreCase(dir)) {
				dir = "desc";
			} else {
				dir = "asc";
			}
			return dir;
		}

		/**
		 * 获取排序字段
		 * 
		 * @param request
		 * @return
		 */
		public static String getSortField(HttpServletRequest request) {
			String field = RequestUtils.getStringValue(request, Keys.FIELD.toString());
			return field;
		}

		/**
		 * 获取分页限制
		 * 
		 * @param request
		 * @return
		 */
		public static Integer getLimit(HttpServletRequest request) {
			String strLimit = RequestUtils.getStringValue(request, Keys.LIMIT.toString());
			Integer limit;
			try {
				limit = Integer.parseInt(strLimit);
			} catch (Throwable e) {
				limit = Constant.PAGE_LIMIT;
			}
			return limit;
		}

		/**
		 * 获取分页开始记录
		 * 
		 * @param request
		 * @return
		 */
		public static Integer getStart(HttpServletRequest request) {
			String strPage = RequestUtils.getStringValue(request, Keys.PAGE.toString());
			Integer page;
			try {
				page = Integer.parseInt(strPage);
			} catch (Throwable e) {
				page = 1;
			}
			Integer start = (page - 1) * getLimit(request);
			return start;
		}
	}

	/**
	 * 文件类型辅助
	 * 
	 * @author Woden
	 * 
	 */
	public static final class FileType {

		/**
		 * 文件后缀
		 * 
		 * @author Woden
		 * 
		 */
		enum Pixel {
			xls("application/vnd.ms-excel"),
			xlsx("application/vnd.openxmlformats-offedocument.spreadsheetml.sheet"),
			pdf("application/pdf"),
			doc("application/doc"),
			docx("application/docx"),
			ppt("application/vnd.ms-powerpoint"),
			pptx("application/vnd.openxmlformats-officedocument.presentationml.presentation"),
			json("application/json;charset=utf-8"),
			xml("application/xml;charset=utf-8"),
			plain("text/plain"),
			other("application/x-download"),
			jpeg("image/jpeg"),
			jpg("image/jpeg"),
			png("image/png"),
			gif("image/gif");

			String contentType;

			Pixel(String contentType) {
				this.contentType = contentType;
			}
		}

		private String fileName;
		private Pixel piexe;

		public FileType(String fileName) {
			this.fileName = fileName;
			if (StringUtils.isEmpty(fileName)) {
				this.piexe = Pixel.other;
			}

			try {
				this.piexe = Pixel.valueOf(fileName.substring(fileName.lastIndexOf(".") + 1).toLowerCase());
			} catch (Throwable e) {
				this.piexe = Pixel.other;
			}
		}

		/**
		 * 设置web端文件类型
		 * 
		 * @param request
		 * @param response
		 */
		public void prepareWeb(HttpServletRequest request, HttpServletResponse response) {
			try {
				response.reset();
				request.setCharacterEncoding("utf-8");
				response.setCharacterEncoding("utf-8");
				response.setContentType(this.piexe.contentType);
				if (this.piexe == Pixel.jpeg || this.piexe == Pixel.jpg || this.piexe == Pixel.png || this.piexe == Pixel.gif) {
					// do nothing
				} else {
					response.setHeader("Content-disposition", "attachment;  filename=" + new String(fileName.getBytes("UTF-8"), "ISO8859-1")); // 设定输出文件头
				}

			} catch (UnsupportedEncodingException ignore) {
				// do nothing
			}
		}
	}

	/**
	 * 直接跳转<br>
	 * 跳转到另一action<br>
	 * 与redirectAction不同的是,使用这个方法跳转的时候,request信息不会丢失
	 * 
	 * @param request
	 * @param response
	 * @param actionUrl
	 */
	public static void forwardAction(HttpServletRequest request, HttpServletResponse response, String actionUrl) {
		try {
			request.getRequestDispatcher(actionUrl).forward(request, response);
		} catch (IOException | ServletException e) {
			logger.error("页面报错.", e);
			throw new SystemRuntimeException(ExceptionType.WEB, e);

		}
	}

	/**
	 * action包含页面<br>
	 * action处理业务完成之后，包含一个jsp页面时用此方法
	 * 
	 * @param request
	 * @param response
	 * @param pageURL
	 */
	public static void includePage(HttpServletRequest request, HttpServletResponse response, String pageURL) {
		try {
			request.getRequestDispatcher(pageURL).forward(request, response);
		} catch (IOException | ServletException e) {
			logger.error("页面报错.", e);
			throw new SystemRuntimeException(ExceptionType.WEB, e);
		}
	}

	/**
	 * 跳转到另一地址
	 * 
	 * @param request
	 * @param response
	 * @param actionUrl
	 */
	public static void jump(HttpServletRequest request, HttpServletResponse response, String actionUrl) {
		try {
			String contextPath = Actions.Util.getContextPath(request);
			response.sendRedirect(contextPath + actionUrl);
		} catch (IOException e) {
			logger.error("转向出错.", e);
			throw new SystemRuntimeException(ExceptionType.WEB, e);
		}
	}

	/**
	 * 直接跳转<br>
	 * 跳转到另一action
	 * 
	 * @param request
	 * @param response
	 * @param actionUrl
	 */
	public static void redirectAction(HttpServletRequest request, HttpServletResponse response, String actionUrl) {
		try {
			String contextPath = Actions.Util.getContextPath(request);
			// 补充公共参数
			if (!Util.isMobile(request) && request.getAttribute(Keys.ZONE.toString()) != null) {
				if (actionUrl.indexOf("?") > 0) {
					actionUrl += ("&" + Keys.ZONE.toString() + "=" + request.getAttribute(Keys.ZONE.toString()) + "&" + Keys.FORM.toString() + "=" + request.getAttribute(Keys.FORM.toString()));
				} else {
					actionUrl += ("?" + Keys.ZONE.toString() + "=" + request.getAttribute(Keys.ZONE.toString()) + "&" + Keys.FORM.toString() + "=" + request.getAttribute(Keys.FORM.toString()));
				}
			}

			response.sendRedirect(contextPath + actionUrl);
		} catch (IOException e) {
			logger.error("转向出错.", e);
			throw new SystemRuntimeException(ExceptionType.WEB, e);
		}
	}

	/**
	 * 转向错误页面
	 * 
	 * @param request
	 * @param response
	 * @param msg
	 */
	public static void redirectErrorPage(HttpServletRequest request, HttpServletResponse response, String msg) {
		// 修改当前zone到errorzone
		String zoneId = RequestUtils.getStringValue(request, Actions.Keys.ERROR_ZONE.toString());
		if (!StringUtils.isEmpty(zoneId)) {
			request.setAttribute(Actions.Keys.ZONE.toString(), zoneId);
		}
		redirectMsgPage(request, response, msg, Styles.MSG_ERROR);
	}

	/**
	 * 转向信息提示页面
	 * 
	 * @param request
	 * @param response
	 * @param msg
	 */
	public static void redirectInfoPage(HttpServletRequest request, HttpServletResponse response, String msg) {
		redirectMsgPage(request, response, msg, Styles.MSG_INFO);
	}

	/**
	 * 转向到信息提示页面
	 * 
	 * @param request
	 * @param response
	 * @param msg
	 * @param type
	 */
	public static void redirectMsgPage(HttpServletRequest request, HttpServletResponse response, String msg, Styles type) {

		Util.setTitle(request, "消息提示");
		request.setAttribute(Keys.MSG.toString(), msg);
		request.setAttribute(Keys.MSG_TYPE.toString(), type.toString());

		try {
			request.getRequestDispatcher(Util.getPagePath(request, Pages.MSG_PAGE.getPage())).forward(request, response);
		} catch (IOException | ServletException e) {
			logger.error("转向出错", e);
			throw new SystemRuntimeException(ExceptionType.WEB, e);
		}
	}

	/**
	 * 展示HTML
	 * 
	 * @param request
	 * @param response
	 * @param html
	 */
	public static void showHtml(HttpServletRequest request, HttpServletResponse response, String html) {
		request.setAttribute(Keys.HTML.toString(), html);
		String zoneId = RequestUtils.getStringValue(request, Actions.Keys.ERROR_ZONE.toString());
		if (!StringUtils.isEmpty(zoneId)) {
			request.setAttribute(Actions.Keys.ZONE.toString(), zoneId);
		}
		try {
			request.getRequestDispatcher(Util.getPagePath(request, Pages.HTML_PAGE.getPage())).forward(request, response);
		} catch (IOException | ServletException e) {
			logger.error("转向出错", e);
			throw new SystemRuntimeException(ExceptionType.WEB, e);
		}
	}

	/**
	 * 转向警告页面
	 * 
	 * @param request
	 * @param response
	 * @param msg
	 */
	public static void redirectWarningPage(HttpServletRequest request, HttpServletResponse response, String msg) {
		// 修改当前zone到errorzone
		String zoneId = RequestUtils.getStringValue(request, Actions.Keys.ERROR_ZONE.toString());
		if (!StringUtils.isEmpty(zoneId)) {
			request.setAttribute(Actions.Keys.ZONE.toString(), zoneId);
		}
		redirectMsgPage(request, response, msg, Styles.MSG_WARNING);
	}

	/**
	 * 以json方式展示
	 * 
	 * @param request
	 * @param response
	 * @param jsonObj
	 */
	public static void showJson(HttpServletRequest request, HttpServletResponse response, Object jsonObj) {
		try {
			response.setContentType(FileType.Pixel.json.contentType);
			request.setCharacterEncoding("utf-8");
			response.setCharacterEncoding("utf-8");
			PrintWriter out = response.getWriter();
			out.println(JsonMapper.defaultMapper().toJson(jsonObj));
			out.flush();
			out.close();
		} catch (IOException e) {
			logger.error("转换JSON出错", e);
			throw new SystemRuntimeException(ExceptionType.WEB, "JSON数据转换出错。", e);
		}
	}

	/**
	 * 以文本方式展示
	 *
	 * @param request
	 * @param response
	 * @param text
	 */
	public static void showText(HttpServletRequest request, HttpServletResponse response, String text) {
		try {
			if (isJson(text)) {
				response.setContentType(FileType.Pixel.json.contentType);
			} else if (isXml(text)) {
				response.setContentType(FileType.Pixel.xml.contentType);
			} else {
				response.setContentType(FileType.Pixel.plain.contentType);
			}
			request.setCharacterEncoding("utf-8");
			response.setCharacterEncoding("utf-8");
			PrintWriter out = response.getWriter();
			out.println(text);
			out.flush();
			out.close();
		} catch (IOException e) {
			logger.error("展示文本出错", e);
			throw new SystemRuntimeException(ExceptionType.WEB, "展示文本出错。", e);
		}
	}

	/**
	 * 简单判断是否是xml格式，不一定通用，所以没考虑放到util类里面
	 */
	private static boolean isXml(String text) {
		boolean result = false;
		Pattern pattern;
		Matcher matcher;

		final String XML_PATTERN_STR = "<(\\S+?)(.*?)>(.*?)</\\1>";

		if (text != null && text.trim().length() > 0) {
			if (text.trim().startsWith("<")) {
				pattern = Pattern.compile(XML_PATTERN_STR, Pattern.CASE_INSENSITIVE | Pattern.DOTALL | Pattern.MULTILINE);
				matcher = pattern.matcher(text);
				result = matcher.matches();
			}
		}

		return result;
	}

	/**
	 * 简单判断是否是json格式，不一定通用，所以没考虑放到util类里面
	 */
	private static boolean isJson(String text) {
		try {
			JsonMapper.defaultMapper().getMapper().readTree(text);
			return true;
		} catch (IOException e) {
			return false;
		}
	}

	/**
	 * 文件展示
	 * 
	 * @param request
	 * @param response
	 * @param fileName
	 * @param is
	 */
	public static void showFile(HttpServletRequest request, HttpServletResponse response, String fileName, InputStream is) {
		try (OutputStream os = response.getOutputStream();) {

			FileType type = new FileType(fileName);
			type.prepareWeb(request, response);
			response.setHeader("Content-disposition", " filename=" + new String(fileName.getBytes("UTF-8"), "ISO8859-1")); // 设定输出文件头

			byte[] b = new byte[1024];
			int len = -1;
			while ((len = is.read(b, 0, 1024)) != -1) {
				response.getOutputStream().write(b, 0, len);
			}

			is.close();
			os.flush();
			os.close();
			response.flushBuffer();
		} catch (IOException e) {
			throw new SystemRuntimeException(ExceptionType.WEB, "服务器找不到对应文件.", e);
		}
	}

	/**
	 * 文件下载(提示强制下载)
	 * 
	 * @param request
	 * @param response
	 * @param fileName
	 * @param is
	 */
	public static void download(HttpServletRequest request, HttpServletResponse response, String fileName, InputStream is) {
		try (OutputStream os = response.getOutputStream();) {

			FileType type = new FileType(fileName);
			type.prepareWeb(request, response);
			response.setHeader("Content-disposition", "attachment;  filename=" + new String(fileName.replaceAll("[,//']", "").getBytes("UTF-8"), "ISO8859-1")); // 设定输出文件头

			byte[] b = new byte[1024];
			int len = -1;
			while ((len = is.read(b, 0, 1024)) != -1) {
				response.getOutputStream().write(b, 0, len);
			}

			is.close();
			os.flush();
			os.close();
			response.flushBuffer();
		} catch (IOException e) {
			throw new SystemRuntimeException(ExceptionType.WEB, "服务器找不到对应文件.", e);
		}
	}

	/**
	 * 以excel文件下载
	 * 
	 * @param request
	 * @param response
	 * @param fileName
	 * @param fields
	 * @param titles
	 * @param list
	 */
	public static void downloadExcel(HttpServletRequest request, HttpServletResponse response, String fileName, String[] fields, Map<String, String> titles, List<HashMap<String, Object>> list) {
		try (OutputStream out = response.getOutputStream();) {
			FileType type = new FileType(fileName);
			type.prepareWeb(request, response);

			switch (type.piexe) {
			case xlsx:
				PoiUtils.exportListWithExcel2007(out, fields, titles, list);
				break;
			case xls:
				PoiUtils.exportListWithExcel2003(out, fields, titles, list);
				break;
			default:
				throw new SystemRuntimeException(ExceptionType.WEB, "文件[" + fileName + "]类型不支持.");
			}

			out.flush();
			response.flushBuffer();
		} catch (IOException e) {
			logger.error("生成excel出错", e);
			throw new SystemRuntimeException(ExceptionType.WEB, "excel数据生成出错。", e);
		}
	}

	/**
	 * 生成随机验证码
	 * 
	 * @param request
	 * @param response
	 * @param sessionKey
	 */
	public static void showRandomImage(HttpServletRequest request, HttpServletResponse response, String sessionKey) {
		new RandomValidateCode(sessionKey).create(request, response);
	}
}
