package cn.bigcoder.plugin.objecthelper.common.util;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.IOException;
import java.io.StringWriter;
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
    DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
    String output = null; //.replaceAll("\n|\r", "");
    try {
      DocumentBuilder documentBuilder= documentBuilderFactory.newDocumentBuilder();
      org.w3c.dom.Document document = documentBuilder.newDocument();
      org.w3c.dom.Element root = document.createElement("xml");
      document.appendChild(root);
      for (String key: data.keySet()) {
        String value = data.get(key).toString();
        value = value.trim();
        org.w3c.dom.Element filed = document.createElement(key);
        filed.appendChild(document.createTextNode(value));
        root.appendChild(filed);
      }
      TransformerFactory tf = TransformerFactory.newInstance();
      Transformer transformer = tf.newTransformer();
      DOMSource source = new DOMSource(document);
      transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
      transformer.setOutputProperty(OutputKeys.INDENT, "yes");
      StringWriter writer = new StringWriter();
      StreamResult result = new StreamResult(writer);
      transformer.transform(source, result);
      output = writer.getBuffer().toString();
      writer.close();
    } catch (ParserConfigurationException e) {
      e.printStackTrace();
    } catch (TransformerException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    }
    return output;
  }
}
