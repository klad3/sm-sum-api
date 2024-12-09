package me.klad3.sumapispring.util;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class HtmlParserUtilTest {

    private HtmlParserUtil htmlParserUtil;

    @BeforeEach
    void setUp() {
        htmlParserUtil = new HtmlParserUtil();
    }

    @Test
    void extractCsrfToken_ShouldReturnToken_WhenTokenExists() {
        // Arrange
        String html = "<html><body><form><input type='hidden' name='_csrf' value='dummyCsrfToken'></form></body></html>";

        // Act
        String csrfToken = htmlParserUtil.extractCsrfToken(html);

        // Assert
        assertNotNull(csrfToken);
        assertEquals("dummyCsrfToken", csrfToken);
    }

    @Test
    void extractCsrfToken_ShouldReturnNull_WhenTokenDoesNotExist() {
        // Arrange
        String html = "<html><body><form></form></body></html>";

        // Act
        String csrfToken = htmlParserUtil.extractCsrfToken(html);

        // Assert
        assertNull(csrfToken);
    }

    @Test
    void extractCsrfToken_ShouldReturnFirstToken_WhenMultipleTokensExist() {
        // Arrange
        String html = "<html><body>" +
                "<form><input type='hidden' name='_csrf' value='firstToken'></form>" +
                "<form><input type='hidden' name='_csrf' value='secondToken'></form>" +
                "</body></html>";

        // Act
        String csrfToken = htmlParserUtil.extractCsrfToken(html);

        // Assert
        assertNotNull(csrfToken);
        assertEquals("firstToken", csrfToken);
    }
}
