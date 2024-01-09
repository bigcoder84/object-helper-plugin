package cn.bigcoder.plugin.objecthelper.common.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import java.util.Map;

/**
 * @author: Jindong.Tian
 * @date: 2022-08-26
 **/
public class XMLUtils {

    /**
     * 将Map转换为XML格式的字符串
     *
     * @param data Map类型数据
     * @return XML格式的字符串
     * @throws Exception
     */
    public static String mapToXml(Map<String, Object> data) {
        ObjectMapper objectMapper = new XmlMapper();
        // 修正根元素名称
        objectMapper.addMixIn(Map.class, MapMixin.class);
        objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
        String xmlString = "";
        try {
            xmlString = objectMapper.writeValueAsString(data);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        return xmlString;
    }

    /**
     * 用于修正根元素名称
     */
    @JacksonXmlRootElement(localName = "root")
    public static abstract class MapMixin {

    }
}
