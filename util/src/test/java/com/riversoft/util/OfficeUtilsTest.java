package com.riversoft.util;

import org.jpedal.exception.PdfException;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

/**
 * @borball on 3/23/2016.
 */
@Ignore
public class OfficeUtilsTest {

    @BeforeClass
    public static void beforeClass() throws Exception {
        InputStream slidesLicenseFile = OfficeUtilsTest.class.getClassLoader().getResourceAsStream("slides-license.xml");
        InputStream wordsLicenseFile = OfficeUtilsTest.class.getClassLoader().getResourceAsStream("words-license.xml");
        InputStream cellsLicenseFile = OfficeUtilsTest.class.getClassLoader().getResourceAsStream("cells-license.xml");
        com.aspose.slides.License slidesLicense = new com.aspose.slides.License();
        slidesLicense.setLicense(slidesLicenseFile);
        com.aspose.words.License wordsLicense = new com.aspose.words.License();
        wordsLicense.setLicense(wordsLicenseFile);
        com.aspose.cells.License cellsLicense = new com.aspose.cells.License();
        cellsLicense.setLicense(cellsLicenseFile);
    }

    @Test
    public void testPpt2Jpgs() throws IOException {
        Path ppt = Paths.get("src/test/resources/高中物理第二册电场总复习3.ppt");
        List<File> images = OfficeUtils.ppt2jpgs(ppt.toFile(), 2);
        Assert.assertNotNull(images);
    }

    @Test
    public void testPpt2Pdf() throws IOException {
        Path ppt = Paths.get("src/test/resources/高中物理第二册电场总复习3.ppt");
        File pdf = OfficeUtils.ppt2pdf(ppt.toFile());
        Assert.assertNotNull(pdf);
    }

    @Test
    public void testPpt2Html() throws IOException {
        Path ppt = Paths.get("src/test/resources/高中物理第二册电场总复习3.ppt");
        File html = OfficeUtils.ppt2html(ppt.toFile());
        Assert.assertNotNull(html);
    }

    @Test
    public void testWord2Pdf() throws Exception {
        Path word = Paths.get("src/test/resources/BPM-Table用户手册-动态脚本.doc");
        File file = OfficeUtils.word2pdf(word.toFile());
        Assert.assertNotNull(file);
    }

    @Test
    public void testWord2Html() throws Exception {
        Path word = Paths.get("src/test/resources/BPM-Table用户手册-动态脚本.doc");
        File html = OfficeUtils.word2html(word.toFile(), false);
        Assert.assertNotNull(html);
    }

    @Test
    public void testWord2HtmlJpgsBase64() throws Exception {
        Path word = Paths.get("src/test/resources/BPM-Table用户手册-动态脚本.doc");
        File html = OfficeUtils.word2html(word.toFile());
        Assert.assertNotNull(html);
    }

    @Test
    public void testExcel2Pdf() throws Exception {
        Path excel = Paths.get("src/test/resources/双11爆款精选商品集合.xls");
        File file = OfficeUtils.excel2pdf(excel.toFile());
        Assert.assertNotNull(file);
    }

    @Test
     public void testExcel2Jpgs() throws Exception {
        Path excel = Paths.get("src/test/resources/双11爆款精选商品集合.xls");
        List<File> files = OfficeUtils.excel2jpgs(excel.toFile());
        Assert.assertNotNull(files);
    }

    @Test
    public void testExcel2Svgs() throws Exception {
        Path excel = Paths.get("src/test/resources/双11爆款精选商品集合.xls");
        List<File> files = OfficeUtils.excel2svgs(excel.toFile());
        Assert.assertNotNull(files);
    }

    @Test
    public void testExcel2Html() throws Exception {
        Path excel = Paths.get("src/test/resources/双11爆款精选商品集合.xls");
        File file = OfficeUtils.excel2html(excel.toFile());
        Assert.assertNotNull(file);
    }

    @Test
    public void testPdf2Jpgs() throws IOException, PdfException {
        Path pdf = Paths.get("src/test/resources/一步到位实现MySQL优化20141024.pdf");
        List<File> images = OfficeUtils.pdf2jpgs(pdf.toFile(), 2f);
        Assert.assertNotNull(images);
    }
}

