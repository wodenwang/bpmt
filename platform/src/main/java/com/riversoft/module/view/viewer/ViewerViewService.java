package com.riversoft.module.view.viewer;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.apache.commons.compress.archivers.zip.ZipArchiveEntry;
import org.apache.commons.compress.archivers.zip.ZipFile;
import org.apache.commons.compress.utils.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.tools.zip.ZipEntry;
import org.apache.tools.zip.ZipOutputStream;

import com.riversoft.core.IDGenerator;
import com.riversoft.core.context.RequestContext;
import com.riversoft.core.exception.SystemRuntimeException;
import com.riversoft.core.script.function.FormatterFunction;
import com.riversoft.core.web.Actions.Keys;
import com.riversoft.core.web.FreeMarkerUtils;
import com.riversoft.platform.script.ScriptHelper;
import com.riversoft.platform.script.ScriptTypes;
import com.riversoft.platform.web.FileManager;
import com.riversoft.platform.web.FileManager.UploadFile;

/**
 * @borball on 2/22/2016.
 */
@SuppressWarnings("unchecked")
public class ViewerViewService {

	/**
	 * 执行变量计算操作
	 * 
	 * @param vars
	 * @return
	 */
	private Map<String, Object> executeContext(Set<Map<String, Object>> vars) {
		// 准备变量
		Map<String, Object> context = new HashMap<>();
		context.put("uuid", IDGenerator.uuid());// uuid,标记生成.后续考虑保存

		for (Map<String, Object> var : vars) {
			Integer type = (Integer) var.get("execType");
			String script = (String) var.get("execScript");
			Object value = ScriptHelper.evel(ScriptTypes.forCode(type), script, context);
			context.put((String) var.get("var"), value);
		}

		return context;
	}

	/**
	 * 页面模板
	 * 
	 * @param config
	 * @return
	 * @throws FileNotFoundException
	 */
	public String executePage(Map<String, Object> config) {
		// 准备变量
		Map<String, Object> context = executeContext((Set<Map<String, Object>>) config.get("vars"));
		context.put("zone", RequestContext.getCurrent().getString(Keys.ZONE.toString()));// 当前区域

		// 判断是否文件路径
		if ((config.get("tempFileType") != null) && ((int) config.get("tempFileType") == 1)) {
			String path = config.get("tempFilePath").toString();
			String fileName = path;
			if (fileName.indexOf("\\") > 0) {
				fileName = fileName.substring(fileName.lastIndexOf("\\") + 1);
			} else if (fileName.indexOf("/") > 0) {
				fileName = fileName.substring(fileName.lastIndexOf("/") + 1);
			}
			if (StringUtils.startsWith(path, "classpath:")) {
				return FreeMarkerUtils.process(path, context);
			} else {
				try (InputStream is = new FileInputStream(path)) {
					return FreeMarkerUtils.process((String) config.get("viewKey"), is, context);
				} catch (IOException e) {
					throw new SystemRuntimeException(e);
				}
			}
		} else {
			// 数据库存储
			UploadFile file = FileManager.toFiles((byte[]) config.get("templateFile")).get(0);// 附件
			try (InputStream is = file.getInputStream()) {
				return FreeMarkerUtils.process((String) config.get("viewKey"), is, context);
			} catch (IOException e) {
				throw new SystemRuntimeException(e);
			}
		}
	}

	/**
	 * excel模板
	 * 
	 * @param config
	 * @return
	 */
	public ByteArrayOutputStream getExcel(Map<String, Object> config) {
		UploadFile file = FileManager.toFiles((byte[]) config.get("templateFile")).get(0);// 附件
		String fileName = file.getName();// 文件名
		String pixel = fileName.substring(fileName.lastIndexOf(".") + 1).toLowerCase();// 后缀
		Map<String, Object> context = executeContext((Set<Map<String, Object>>) config.get("vars"));

		if ("zip".equalsIgnoreCase(pixel)) {
			try (ByteArrayOutputStream bos = new ByteArrayOutputStream(); ZipOutputStream zos = new ZipOutputStream(bos)) {
				File tmpFile = File.createTempFile("A" + UUID.randomUUID().toString(), pixel);
				try (FileOutputStream zipFileOs = new FileOutputStream(tmpFile);) {
					IOUtils.copy(file.getInputStream(), zipFileOs);
				}

				zos.setEncoding(System.getProperty("sun.jnu.encoding"));
				try (ZipFile zipFile = new ZipFile(tmpFile, System.getProperty("sun.jnu.encoding"));) {
					Enumeration<ZipArchiveEntry> e = zipFile.getEntries();
					while (e.hasMoreElements()) {
						ZipArchiveEntry entry = e.nextElement();
						if (entry.isDirectory()) {// 文件夹
							continue;
						}

						ZipEntry outEntry = new ZipEntry(entry.getName());
						InputStream ins = zipFile.getInputStream(entry);
						outEntry.setSize(ins.available());
						zos.putNextEntry(outEntry);

						try (ByteArrayOutputStream tmp = new ByteArrayOutputStream()) {
							ScriptHelper.export(tmp, ins, context);
							zos.write(tmp.toByteArray());
						}
					}
				}

				return bos;
			} catch (IOException e) {
				throw new SystemRuntimeException(e);
			}
		} else {
			try (ByteArrayOutputStream tmpOut = new ByteArrayOutputStream()) {
				ScriptHelper.export(tmpOut, file.getInputStream(), context);
				return tmpOut;
			} catch (IOException e) {
				throw new SystemRuntimeException(e);
			}
		}
	}

	/**
	 * 处理文本
	 * 
	 * @param config
	 * @return
	 */
	public String executeText(Map<String, Object> config) {
		Integer textType = (Integer) config.get("textType");
		String textScript = (String) config.get("textScript");
		Map<String, Object> context = executeContext((Set<Map<String, Object>>) config.get("vars"));
		Object text = ScriptHelper.evel(ScriptTypes.forCode(textType), textScript, context);
		if (text instanceof String) {
			return (String) text;
		} else {
			return FormatterFunction.formatJson(text);
		}
	}

	/**
	 * 处理跳转url
	 * 
	 * @param config
	 * @return
	 */
	public Object executeRedirectUrl(Map<String, Object> config) {
		Integer urlType = (Integer) config.get("urlType");
		String urlScript = (String) config.get("urlScript");
		Map<String, Object> context = executeContext((Set<Map<String, Object>>) config.get("vars"));
		Object url = ScriptHelper.evel(ScriptTypes.forCode(urlType), urlScript, context);
		return url;
	}

	/**
	 * 处理消息页面
	 * 
	 * @param config
	 * @return
	 */
	public Object executeMsgPage(Map<String, Object> config) {
		Integer msgType = (Integer) config.get("msgType");
		String msgScript = (String) config.get("msgScript");
		Map<String, Object> context = executeContext((Set<Map<String, Object>>) config.get("vars"));
		Object msg = ScriptHelper.evel(ScriptTypes.forCode(msgType), msgScript, context);
		return msg;
	}

	/**
	 * 处理文件下载
	 * 
	 * @param config
	 * @return
	 */
	public Object getFile(Map<String, Object> config) {
		Integer fileType = (Integer) config.get("fileType");
		String fileScript = (String) config.get("fileScript");
		Map<String, Object> context = executeContext((Set<Map<String, Object>>) config.get("vars"));
		Object obj = ScriptHelper.evel(ScriptTypes.forCode(fileType), fileScript, context);
		return obj;
	}
}
