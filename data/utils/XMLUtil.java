package com.sunyard.fintech.zhba.house.common.utils;

import com.sunyard.fintech.zhba.house.common.dic.ErrorInfo;
import com.sunyard.fintech.zhba.house.common.exception.BaseException;
import lombok.extern.slf4j.Slf4j;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamWriter;
import java.io.*;
import java.nio.charset.StandardCharsets;

/**
 * bean与xml互转
 */
@Slf4j
public class XMLUtil {
    /**
     * bean转为xml
     *
     * @param obj
     * @return
     */
    public static String convertToXml(Object obj) {
        String result = null;
        try {
            JAXBContext context = JAXBContext.newInstance(obj.getClass());
            Marshaller marshaller = context.createMarshaller();
            marshaller.setProperty(Marshaller.JAXB_ENCODING, "utf-8");
            marshaller.setProperty(Marshaller.JAXB_FRAGMENT, true);

            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            XMLStreamWriter xmlStreamWriter = XMLOutputFactory.newInstance().createXMLStreamWriter(byteArrayOutputStream, "utf-8");
            xmlStreamWriter.writeStartDocument("utf-8", "1.0");
            marshaller.marshal(obj, xmlStreamWriter);
            xmlStreamWriter.writeEndDocument();
            xmlStreamWriter.close();
            return new String(byteArrayOutputStream.toByteArray(), StandardCharsets.UTF_8);
        } catch (Exception e) {
            log.error("bean传xml异常：",e);
        }
        return result;
    }

    /**
     * 将String类型的xml转换成javaBean
     */
    public static <T> T convertXmlStrToObject(Class<T> clazz, String xmlStr) {
        T t = null;
        try {
            JAXBContext context = JAXBContext.newInstance(clazz);
            // 进行将Xml转成对象的核心接口
            Unmarshaller unmarshaller = context.createUnmarshaller();
            StringReader sr = new StringReader(xmlStr);
            t = (T)unmarshaller.unmarshal(sr);
        } catch (Exception e) {
            log.error("string转javaBean失败：",e);
            throw new BaseException(ErrorInfo.FAIL.getCode(),e.getMessage());
        }
        return t;
    }
}
