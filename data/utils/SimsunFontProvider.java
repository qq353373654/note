package com.alipay.riskplus.dubservice.biz.shared.pdm.utils.utils;


import com.itextpdf.text.BaseColor;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Font;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.tool.xml.XMLWorkerFontProvider;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;

import java.io.IOException;

/**
 * @author Crow
 */
@Slf4j
public class SimsunFontProvider extends XMLWorkerFontProvider {

    private static final BaseFont BASE_FONT;

    static {
        ClassPathResource resource = new ClassPathResource("simsun.ttc");
        try {
            String path = resource.getURL().toString();
            log.info("字体文件路径：{}", path);
            BASE_FONT = BaseFont.createFont(path + ",1", BaseFont.IDENTITY_H, BaseFont.EMBEDDED);
        } catch (IOException | DocumentException e) {
            log.error("simsun.ttc加载失败", e);
            throw new RuntimeException(e);
        }
    }

    @Override
    public Font getFont(String fontname, String encoding, boolean embedded, float size, int style, BaseColor color, boolean cached) {
        Font font = new Font(BASE_FONT, size, style, color);
        font.setColor(color);
        return font;
    }
}
