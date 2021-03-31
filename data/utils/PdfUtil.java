package com.alipay.riskplus.dubservice.biz.shared.pdm.utils.utils;

import com.alipay.riskplus.dubservice.access.common.enums.FileType;
import com.alipay.riskplus.dubservice.biz.shared.access.common.utils.FileTypeUtil;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.tool.xml.XMLWorkerHelper;
import fr.opensagres.poi.xwpf.converter.xhtml.XHTMLConverter;
import fr.opensagres.poi.xwpf.converter.xhtml.XHTMLOptions;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.hwpf.HWPFDocument;
import org.apache.poi.hwpf.converter.WordToHtmlConverter;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.*;
import java.nio.charset.StandardCharsets;

/**
 * @author Crow
 */
@Slf4j
public final class PdfUtil {
    private static final SimsunFontProvider SIMSUN_FONT_PROVIDER = new SimsunFontProvider();

    private PdfUtil() {
    }

    /**
     * word 文件转 pdf
     *
     * @param bytes word字节数组
     * @return pdf字节数组
     */
    public static byte[] wordToPdf(byte[] bytes) {
        if (null == bytes || 0 == bytes.length) {
            throw new IllegalArgumentException("bytes不能为空");
        }

        // doc -> html
        FileType type = FileTypeUtil.getType(bytes);
        boolean clean = false;
        String html;
        if (FileType.XLS_DOC.equals(type)) {
            html = DocProducer.docToHtml(bytes);
        } else if (FileType.XLSX_DOCX.equals(type)) {
            html = DocProducer.docxToHtml(bytes);
            clean = true;
        } else {
            throw new IllegalArgumentException("输入文件格式错误, 非word");
        }

        // 格式化
        String content = formatHtml(html, clean);

        // html -> pdf
        byte[] pdfBytes;
        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            Document document = new Document();
            PdfWriter writer = PdfWriter.getInstance(document, out);
            document.open();
            XMLWorkerHelper.getInstance().parseXHtml(writer, document, new ByteArrayInputStream(content.getBytes(StandardCharsets.UTF_8)),
                    StandardCharsets.UTF_8,
                    SIMSUN_FONT_PROVIDER);

            document.close();

            pdfBytes = out.toByteArray();
            out.flush();
        } catch (IOException e) {
            log.error("pdf写入失败", e);
            throw new RuntimeException(e);
        } catch (DocumentException e) {
            log.error("pdf转换失败", e);
            throw new RuntimeException(e);
        }

        return pdfBytes;
    }

    /**
     * jsoup格式化html
     *
     * @param html  html内容
     * @param clean 是否清理div样式
     * @return 格式化后的html
     */
    private static String formatHtml(String html, boolean clean) {
        if (null == html || 0 == html.length()) {
            return html;
        }
        org.jsoup.nodes.Document document = Jsoup.parse(html);

        Element head = document.head();
        head.append("<style>p{line-height:36px}</style>");

        if (clean) {
            Element body = document.body();
            String style = body.attr("style");
            if (null != style && style.contains("width")) {
                body.attr("style", "");
            }
            body.attr("style", "font-family: SimSun");

            Elements divs = document.select("div");
            for (Element div : divs) {
                style = div.attr("style");
                if (null != style && style.contains("width")) {
                    div.attr("style", "");
                }
            }
        }

        return document.html();
    }

    static class DocProducer {
        /***
         * poi doc转html
         * @param bytes doc内容
         * @return html
         */
        static String docToHtml(byte[] bytes) {
            String html = null;
            try (InputStream in = new ByteArrayInputStream(bytes);
                 ByteArrayOutputStream out = new ByteArrayOutputStream()) {
                HWPFDocument wordDocument = new HWPFDocument(in);
                WordToHtmlConverter wordToHtmlConverter = new WordToHtmlConverter(DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument());

                wordToHtmlConverter.processDocument(wordDocument);
                org.w3c.dom.Document htmlDocument = wordToHtmlConverter.getDocument();
                DOMSource domSource = new DOMSource(htmlDocument);
                StreamResult streamResult = new StreamResult(out);

                TransformerFactory tf = TransformerFactory.newInstance();
                Transformer serializer = tf.newTransformer();
                serializer.setOutputProperty(OutputKeys.ENCODING, StandardCharsets.UTF_8.toString());
                serializer.setOutputProperty(OutputKeys.INDENT, "yes");
                serializer.setOutputProperty(OutputKeys.METHOD, "html");
                serializer.transform(domSource, streamResult);
                html = out.toString();
            } catch (IOException e) {
                log.error("文件读取失败", e);
                throw new RuntimeException(e);
            } catch (ParserConfigurationException e) {
                log.error("Doc文件读取失败", e);
                throw new RuntimeException(e);
            } catch (TransformerException e) {
                log.error("Transformer转化失败", e);
                throw new RuntimeException(e);
            }
            return html;
        }

        /***
         * poi docx转html
         * @param bytes docx内容
         * @return html
         */
        static String docxToHtml(byte[] bytes) {
            String content = null;
            try (InputStream in = new ByteArrayInputStream(bytes);
                 ByteArrayOutputStream out = new ByteArrayOutputStream();) {

                XWPFDocument wordDocument = new XWPFDocument(in);
                // XHTML解析配置
                XHTMLOptions options = XHTMLOptions.create();
//            options.setExtractor(new FileImageExtractor("/xxx.jpg"));
//            options.URIResolver(new BasicURIResolver("/xxx.jpg"));

                // XWPFDocument转换HTML
                XHTMLConverter.getInstance().convert(wordDocument, out, options);
                content = out.toString();
            } catch (IOException e) {
                log.error("文件读取失败", e);
                throw new RuntimeException(e);
            }

            return content;
        }
    }

}
