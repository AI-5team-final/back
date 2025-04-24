package com.example.backend.utiles;

import org.w3c.dom.*;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;
import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class MarkupChange {

    public static Map<String, Object> parseXmlResult(String xmlString) {
        Map<String, Object> result = new HashMap<>();

        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();

            Document doc = builder.parse(new ByteArrayInputStream(xmlString.getBytes(StandardCharsets.UTF_8)));
            NodeList children = doc.getDocumentElement().getChildNodes();

            for (int i = 0; i < children.getLength(); i++) {
                Node node = children.item(i);
                if (node.getNodeType() == Node.ELEMENT_NODE) {
                    String tag = node.getNodeName();
                    String text = node.getTextContent().trim();

                    // 숫자형 점수 필드는 int로 파싱
                    if (tag.endsWith("_score") || tag.equals("total_score")) {
                        try {
                            result.put(tag, Integer.parseInt(text));
                        } catch (NumberFormatException e) {
                            result.put(tag, text); // 숫자가 아닐 경우 문자열로 저장
                        }
                    } else {
                        result.put(tag, text);
                    }
                }
            }
        } catch (Exception e) {
            throw new RuntimeException("XML 파싱 실패", e);
        }
        return result;
    }
}
