package com.riversoft.util;

import com.aspose.cells.*;
import com.aspose.slides.*;
import com.aspose.slides.Collections.ArrayList;
import com.aspose.slides.PdfCompliance;
import com.aspose.words.Document;
import com.sun.image.codec.jpeg.JPEGCodec;
import com.sun.image.codec.jpeg.JPEGImageEncoder;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.jpedal.PdfDecoder;
import org.jpedal.exception.PdfException;
import org.jpedal.fonts.FontMappings;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

/**
 * 使用OfficeUtils需要做破解
 * 例如：
     InputStream slidesLicenseFile = OfficeUtilsTest.class.getClassLoader().getResourceAsStream("slides-license.xml");
     InputStream wordsLicenseFile = OfficeUtilsTest.class.getClassLoader().getResourceAsStream("words-license.xml");
     InputStream cellsLicenseFile = OfficeUtilsTest.class.getClassLoader().getResourceAsStream("cells-license.xml");
     com.aspose.slides.License slidesLicense = new com.aspose.slides.License();
     slidesLicense.setLicense(slidesLicenseFile);
     com.aspose.words.License wordsLicense = new com.aspose.words.License();
     wordsLicense.setLicense(wordsLicenseFile);
     com.aspose.cells.License cellsLicense = new com.aspose.cells.License();
     cellsLicense.setLicense(cellsLicenseFile);
 *
 * @borball on 3/21/2016.
 */
public class OfficeUtils {
	
	//破译
	static {
		try {
			InputStream slidesLicenseFile = OfficeUtils.class.getClassLoader().getResourceAsStream("aspose/slides-license.xml");
			InputStream wordsLicenseFile = OfficeUtils.class.getClassLoader().getResourceAsStream("aspose/words-license.xml");
			InputStream cellsLicenseFile = OfficeUtils.class.getClassLoader().getResourceAsStream("aspose/cells-license.xml");
			com.aspose.slides.License slidesLicense = new com.aspose.slides.License();
			slidesLicense.setLicense(slidesLicenseFile);
			com.aspose.words.License wordsLicense = new com.aspose.words.License();
			wordsLicense.setLicense(wordsLicenseFile);
			com.aspose.cells.License cellsLicense = new com.aspose.cells.License();
			cellsLicense.setLicense(cellsLicenseFile);
		} catch (Exception ignore) {
			// do nothing
		}
	}

    /**
     * 把ppt转换成图片，默认不放大
     * @param ppt
     * @return
     * @throws IOException
     */
    public static List<File> ppt2jpgs(File ppt) throws IOException {
        return ppt2jpgs(ppt, 1f);
    }

    /**
     * 把ppt转换成图片
     * @param ppt
     * @param scale 放大倍数
     * @return
     * @throws IOException
     */
    public static List<File> ppt2jpgs(File ppt, float scale) throws IOException {
        String fileName = ppt.getName();
        fileName = fileName.replaceFirst(".pptx?", "");
        fileName = fileName.replaceFirst(".ppt?", "");

        return ppt2jpgs(new Presentation(ppt.getAbsolutePath()), fileName, scale);
    }

    /**
     * 把ppt转换成图片
     * @param ppt ppt文件流
     * @param pptName 文件名，需要带扩展名
     * @return
     * @throws IOException
     */
    public static List<File> ppt2jpgs(InputStream ppt, String pptName) throws IOException {
        return ppt2jpgs(ppt, pptName, 1f);
    }

    /**
     * 把ppt转换成图片
     * @param ppt ppt文件流
     * @param pptName 文件名，需要带扩展名
     * @param scale 放大倍数
     * @return
     * @throws IOException
     */
    public static List<File> ppt2jpgs(InputStream ppt, String pptName, float scale) throws IOException {
        String fileName = pptName;
        if(StringUtils.isEmpty(fileName)) {
            fileName = UUID.randomUUID() + ".ppt";
        }

        fileName = fileName.replaceFirst(".pptx?", "");
        fileName = fileName.replaceFirst(".ppt?", "");

        return ppt2jpgs(new Presentation(ppt), fileName, scale);
    }

    private static List<File> ppt2jpgs(Presentation presentation, String fileName, float scale) {
        int size = presentation.getSlides().size();
        List<File> images = new ArrayList();
        File tempFolder = createTempDirectory();
        for (int page = 0; page < size; page++) {
            ISlide slide = presentation.getSlides().get_Item(page);
            BufferedImage image = slide.getThumbnail(scale, scale);

            String name = String.format(Locale.ROOT, "%1$s-%2$04d.%3$s", fileName, page, "jpg");

            File file = new File(tempFolder, name);
            try {
                ImageIO.write(image, "jpeg", file);
                images.add(file);
            } catch (Exception e) {
            }
        }

        return images;
    }

    /**
     * ppt转换成pdf文件
     * @param ppt
     * @return
     * @throws IOException
     */
    public static File ppt2pdf(File ppt) throws IOException {
        return ppt2pdf(new Presentation(ppt.getAbsolutePath()), ppt.getName());
    }

    /**
     * ppt转换成pdf文件
     * @param ppt ppt文件流
     * @param pptName ppt名字，带扩展名
     * @return
     * @throws IOException
     */
    public static File ppt2pdf(InputStream ppt, String pptName) throws IOException {
        String fileName = pptName;
        if(StringUtils.isEmpty(fileName)) {
            fileName = UUID.randomUUID() + ".ppt";
        }

        return ppt2pdf(new Presentation(ppt), fileName);
    }

    private static File ppt2pdf(Presentation pres, String fileName) {
        fileName = fileName.replaceFirst(".pptx?", ".pdf");
        fileName = fileName.replaceFirst(".ppt?", ".pdf");

        File file = new File(createTempDirectory(), fileName);
        pres.save(file.getAbsolutePath(), com.aspose.slides.SaveFormat.Pdf);
        return file;
    }

    /**
     * ppt转换成html文件
     * @param ppt
     * @return
     * @throws IOException
     */
    public static File ppt2html(File ppt) throws IOException {
        return ppt2html(new Presentation(ppt.getAbsolutePath()), ppt.getName());
    }

    /**
     * ppt转换成html文件
     * @param ppt ppt文件流
     * @param pptName ppt文件名，带扩展名；可为空
     * @return
     * @throws IOException
     */
    public static File ppt2html(InputStream ppt, String pptName) throws IOException {
        String fileName = pptName;
        if(StringUtils.isEmpty(fileName)) {
            fileName = UUID.randomUUID() + ".ppt";
        }
        return ppt2html(new Presentation(ppt), fileName);
    }

    private static File ppt2html(Presentation pres, String fileName) {
        HtmlOptions htmlOpt = new HtmlOptions();
        htmlOpt.setHtmlFormatter(HtmlFormatter.createDocumentFormatter("", false));
        fileName = fileName.replaceFirst(".pptx?", ".html");
        fileName = fileName.replaceFirst(".ppt?", ".html");
        File html = new File(createTempDirectory(), fileName);

        pres.save(html.getAbsolutePath(), com.aspose.slides.SaveFormat.Html, htmlOpt);
        return html;
    }

    /**
     * word文档转换成pdf文件
     * @param word
     * @return
     * @throws Exception
     */
    public static File word2pdf(File word) throws Exception {
        return word2pdf(new Document(word.getAbsolutePath()), word.getName());
    }


    /**
     * word文档转换成pdf文件
     * @param word word文件流
     * @param wordName word文件名
     * @return
     * @throws Exception
     */
    public static File word2pdf(InputStream word, String wordName) throws Exception {
        String fileName = wordName;
        if(StringUtils.isEmpty(fileName)) {
            fileName = UUID.randomUUID() + ".doc";
        }
        return word2pdf(new Document(word), fileName);
    }

    private static File word2pdf(Document doc, String fileName ) throws Exception {
        fileName = fileName.replaceFirst(".docx?", ".pdf");
        fileName = fileName.replaceFirst(".doc?", ".pdf");
        File file = new File(createTempDirectory(), fileName);

        doc.save(file.getAbsolutePath());
        return file;
    }

    /**
     * word转换成html文件,图片使用base64
     * @param word
     * @return
     * @throws IOException
     */
    public static File word2html(File word) throws Exception {
        return word2html(word, true);
    }

    /**
     * word转换成html文件
     * @param word 文件
     * @param imageBase64 图片是否base64
     * @return
     * @throws IOException
     */
    public static File word2html(File word, boolean imageBase64) throws Exception {
        return word2html(new Document(word.getAbsolutePath()), word.getName(), imageBase64);
    }

    /**
     * word转换成html文件
     * @param word word文件流
     * @param wordName word文件名,带扩展名，可为空
     * @return
     * @throws IOException
     */
    public static File word2html(InputStream word, String wordName) throws Exception {
        return word2html(word, wordName, true);
    }

    /**
     * word转换成html文件
     * @param word word文件流
     * @param wordName word文件名,带扩展名，可为空
     * @return
     * @throws IOException
     */
    public static File word2html(InputStream word, String wordName, boolean imageBase64) throws Exception {
        String fileName = wordName;
        if(StringUtils.isEmpty(fileName)) {
            fileName = UUID.randomUUID() + ".doc";
        }

        return word2html(new Document(word), fileName, imageBase64);
    }

    private static File word2html(Document doc, String fileName, boolean imageBase64) throws Exception {
        fileName = fileName.replaceFirst(".docx?", ".html");
        fileName = fileName.replaceFirst(".doc?", ".html");
        File html = new File(createTempDirectory(), fileName);

        com.aspose.words.HtmlSaveOptions options = new com.aspose.words.HtmlSaveOptions();
        options.setExportImagesAsBase64(imageBase64);
        doc.save(html.getAbsolutePath(), options);
        return html;
    }

    /**
     * excel文档转换成pdf
     * @param excel
     * @return
     * @throws Exception
     */
    public static File excel2pdf(File excel) throws Exception {
        return excel2pdf(new Workbook(excel.getAbsolutePath()), excel.getName());
    }

    /**
     * excel文档转换成pdf
     * @param excel excel文件流
     * @param excelName excel文件名,带扩展名，可为空
     * @return
     * @throws Exception
     */
    public static File excel2pdf(InputStream excel, String excelName) throws Exception {
        String fileName = excelName;
        if(StringUtils.isEmpty(fileName)) {
            fileName = UUID.randomUUID() + ".xls";
        }

        return excel2pdf(new Workbook(excel), fileName);
    }

    private static File excel2pdf(Workbook workbook, String fileName) throws Exception {
        fileName = fileName.replaceFirst(".xlsx?", ".pdf");
        fileName = fileName.replaceFirst(".xls?", ".pdf");
        File file = new File(createTempDirectory(), fileName);

        PdfSaveOptions saveOptions = new PdfSaveOptions();
        saveOptions.setCompliance(PdfCompliance.PdfA1b);
        saveOptions.setAllColumnsInOnePagePerSheet(true);
        workbook.save(file.getAbsolutePath(), saveOptions);
        return file;
    }

    /**
     * excel文档转换成JPG
     * @param excel
     * @return
     * @throws Exception
     */
    public static List<File> excel2jpgs(File excel) throws Exception {
        com.aspose.cells.ImageOrPrintOptions imgOptions = new com.aspose.cells.ImageOrPrintOptions();
        imgOptions.setImageFormat(ImageFormat.getPng());

        return excel2picture(new Workbook(excel.getAbsolutePath()), imgOptions, excel.getName(), "jpg");
    }

    /**
     * excel文档转换成JPG
     * @param excel excel文件流
     * @param excelName excel文件名
     * @return
     * @throws Exception
     */
    public static List<File> excel2jpgs(InputStream excel, String excelName) throws Exception {
        String fileName = excelName;
        if(StringUtils.isEmpty(fileName)) {
            fileName = UUID.randomUUID() + ".xls";
        }

        com.aspose.cells.ImageOrPrintOptions imgOptions = new com.aspose.cells.ImageOrPrintOptions();
        imgOptions.setImageFormat(ImageFormat.getPng());

        return excel2picture(new Workbook(excel), imgOptions, fileName, "jpg");
    }

    /**
     * excel文档转换成SVG
     * @param excel
     * @return
     * @throws Exception
     */
    public static List<File> excel2svgs(File excel) throws Exception {
        ImageOrPrintOptions imgOptions = new ImageOrPrintOptions();
        imgOptions.setSaveFormat(com.aspose.cells.SaveFormat.SVG);
        imgOptions.setOnePagePerSheet(true);

        return excel2picture(new Workbook(excel.getAbsolutePath()), imgOptions, excel.getName(), "svg");
    }

    /**
     * excel文档转换成SVG
     * @param excel excel文件流
     * @param excelName 文件名
     * @return
     * @throws Exception
     */
    public static List<File> excel2svgs(InputStream excel, String excelName) throws Exception {
        String fileName = excelName;
        if(StringUtils.isEmpty(fileName)) {
            fileName = UUID.randomUUID() + ".xls";
        }

        ImageOrPrintOptions imgOptions = new ImageOrPrintOptions();
        imgOptions.setSaveFormat(com.aspose.cells.SaveFormat.SVG);
        imgOptions.setOnePagePerSheet(true);

        return excel2picture(new Workbook(excel), imgOptions, fileName, "svg");
    }

    private static List<File> excel2picture(Workbook book, ImageOrPrintOptions imgOptions, String fileName, String format) throws Exception {
        fileName = fileName.replaceFirst(".xlsx?", "");
        fileName = fileName.replaceFirst(".xls?", "");

        File tempFolder = createTempDirectory();

        List<File> images = new ArrayList();
        int count = book.getWorksheets().getCount();
        for(int i = 0; i < count; i++) {
            Worksheet sheet = book.getWorksheets().get(i);
            SheetRender sheetRender = new SheetRender(sheet, imgOptions);
            for (int j = 0; j < sheetRender.getPageCount(); j++) {
                String name = String.format(Locale.ROOT, "%1$s-%2$03d-%3$02d.%4$s", fileName, i, j, format);
                File file = new File(tempFolder, name);
                sheetRender.toImage(j, file.getAbsolutePath());
                images.add(file);
            }
        }

        return images;
    }

    /**
     * excel转换成html文件
     * @param excel
     * @return
     * @throws IOException
     */
    public static File excel2html(File excel) throws Exception {
        return excel2html(new Workbook(excel.getAbsolutePath()), excel.getName());
    }

    /**
     * excel转换成html文件
     * @param excel
     * @param excelName
     * @return
     * @throws IOException
     */
    public static File excel2html(InputStream excel, String excelName) throws Exception {
        String fileName = excelName;
        if(StringUtils.isEmpty(fileName)) {
            fileName = UUID.randomUUID() + ".xls";
        }

        return excel2html(new Workbook(excel), fileName);
    }

    private static File excel2html(Workbook book, String fileName) throws Exception {
        fileName = fileName.replaceFirst(".xlsx?", ".html");
        fileName = fileName.replaceFirst(".xls?", ".html");
        File file = new File(createTempDirectory(), fileName);

        HtmlSaveOptions save = new HtmlSaveOptions(com.aspose.cells.SaveFormat.HTML);
        book.save(file.getAbsolutePath(), save);
        return file;
    }

    /**
     * pdf转成图片
     * @param pdf pdf文件
     * @return
     * @throws PdfException
     * @throws IOException
     */
    public static List<File> pdf2jpgs(File pdf) throws PdfException, IOException {
        return pdf2jpgs(pdf, 1f);
    }

    /**
     * pdf转成图片
     * @param pdf pdf文件
     * @param scaling 放大倍数
     * @return
     * @throws PdfException
     * @throws IOException
     */
    public static List<File> pdf2jpgs(File pdf, float scaling) throws PdfException, IOException {
        PdfDecoder pdfDecoder = new PdfDecoder(true);
        FontMappings.setFontReplacements();
        pdfDecoder.openPdfFile(pdf.getAbsolutePath());

        return pdf2jpgs(pdfDecoder, pdf.getName(), scaling);
    }

    /**
     * pdf转成图片
     * @param pdf pdf文件
     * @param pdfName pdf文档名
     * @return
     * @throws PdfException
     * @throws IOException
     */
    public static List<File> pdf2jpgs(InputStream pdf, String pdfName) throws PdfException, IOException {
        return pdf2jpgs(pdf, pdfName, 1f);
    }

    /**
     * pdf转成图片
     * @param pdf pdf文件
     * @param scaling 放大倍数
     * @return
     * @throws PdfException
     * @throws IOException
     */
    public static List<File> pdf2jpgs(InputStream pdf, String pdfName, float scaling) throws PdfException, IOException {
        PdfDecoder pdfDecoder = new PdfDecoder(true);
        FontMappings.setFontReplacements();
        pdfDecoder.openPdfFileFromInputStream(pdf, false);

        String fileName = pdfName;
        if(StringUtils.isEmpty(fileName)) {
            fileName = UUID.randomUUID() + ".xls";
        }

        return pdf2jpgs(pdfDecoder, fileName, scaling);
    }

    private static List<File> pdf2jpgs(PdfDecoder pdfDecoder, String fileName, float scaling) throws PdfException, IOException {
        fileName = fileName.replaceFirst(".pdf?", "");

        File tempFolder = createTempDirectory();

        List<File> images = new ArrayList();

        int pageCount = pdfDecoder.getPageCount();
        for(int page = 1; page <= pageCount; page++) {
            pdfDecoder.setPageParameters(scaling, page);
            BufferedImage img = pdfDecoder.getPageAsImage(page);

            String name = String.format(Locale.ROOT, "%1$s-%2$04d.%3$s", fileName, page, "jpg");
            File file = new File(tempFolder, name);
            JPEGImageEncoder encoder = JPEGCodec.createJPEGEncoder(new FileOutputStream(file));
            encoder.encode(img);
            images.add(file);
        }
        return images;
    }

    private static File createTempDirectory() {
        File office = new File(FileUtils.getTempDirectory(), "office-tools");
        if (!office.exists()) {
            office.mkdir();
        }
        File tempFolder = new File(office, UUID.randomUUID().toString());
        if (!tempFolder.exists()) {
            tempFolder.mkdir();
        }
        return tempFolder;
    }

}
