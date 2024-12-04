// src/main/java/com/example/loginapp/util/HtmlParserUtil.java

package com.example.loginapp.util;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.stereotype.Component;

@Component
public class HtmlParserUtil {

    public String extractCsrfToken(String html) {
        Document doc = Jsoup.parse(html);
        Element csrfElement = doc.selectFirst("input[name=_csrf]");
        if (csrfElement != null) {
            return csrfElement.attr("value");
        }
        return null;
    }
}
