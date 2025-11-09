package com.riversoft.platform.script.function;

import com.riversoft.core.exception.ExceptionType;
import com.riversoft.core.exception.SystemRuntimeException;
import com.riversoft.core.script.annotation.ScriptSupport;
import com.riversoft.platform.web.FileManager;
import com.riversoft.util.PdfUtils;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;

/**
 * @borball on 3/28/2016.
 */
@ScriptSupport("pdf")
public class PdfHelper {

    /**
     * 把pdf文件里面form抽取出来放到map里面
     * @param file
     * @return
     */
    public static Map<String, String> parse(Object file) {
        try {
            InputStream is = null;
            if (file instanceof byte[]) {
                List<FileManager.UploadFile> fileList = FileManager.toFiles((byte[]) file);
                if (fileList == null || fileList.size() < 1) {
                    throw new SystemRuntimeException(ExceptionType.SCRIPT, "解析文件不存在.");
                }
                is = fileList.get(0).getInputStream();
            } else if (file instanceof InputStream) {
                is = (InputStream) file;
            } else if (file instanceof File) {
                is = new FileInputStream((File) file);
            } else {
                throw new SystemRuntimeException(ExceptionType.SCRIPT, "解析文件不符合格式.");
            }

            return PdfUtils.parse(is);
        } catch (IOException e) {
            throw new SystemRuntimeException(e);
        }
    }


}
