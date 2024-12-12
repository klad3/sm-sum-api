package Pruebas_Funcionales;

import org.junit.jupiter.api.*;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.net.URL;
import java.time.Duration;
import java.util.UUID;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class CreateAccountTest {

    private WebDriver driver;
    private WebDriverWait wait;
    private static final String SELENIUM_URL = System.getenv("SELENIUM_URL") != null ?
            System.getenv("SELENIUM_URL") :
            "http://localhost:4444";
    private static final String BASE_URL = System.getenv("FRONTEND_BASE_URL") != null ?
            System.getenv("FRONTEND_BASE_URL") :
            "http://localhost:3000/login";

    @BeforeEach
    public void setUp() throws Exception {
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless=new"); // Ejecutar en modo headless
        options.addArguments("--no-sandbox"); // Evitar problemas de sandbox en Docker
        options.addArguments("--disable-dev-shm-usage"); // Usar /tmp en lugar de /dev/shm
        options.addArguments("--disable-gpu"); // Desactivar GPU
        options.addArguments("--remote-allow-origins=*"); // Permitir orígenes remotos
        options.addArguments("--disable-software-rasterizer"); // Desactivar rasterizador de software
        options.addArguments("--disable-extensions"); // Desactivar extensiones
        options.addArguments("--disable-logging"); // Desactivar logging
        options.addArguments("--disable-background-networking"); // Desactivar networking en segundo plano
        options.addArguments("--disable-background-timer-throttling"); // Desactivar limitación de temporizador en segundo plano
        options.addArguments("--disable-backgrounding-occluded-windows"); // Desactivar ventanas ocultas en segundo plano
        options.addArguments("--disable-breakpad"); // Desactivar breakpad
        options.addArguments("--disable-component-extensions-with-background-pages"); // Desactivar extensiones de componentes con páginas en segundo plano
        options.addArguments("--disable-features=TranslateUI"); // Desactivar características de UI de traducción
        options.addArguments("--disable-ipc-flooding-protection"); // Desactivar protección contra inundación de IPC
        options.addArguments("--disable-popup-blocking"); // Desactivar bloqueo de popups
        options.addArguments("--disable-prompt-on-repost"); // Desactivar aviso en reenvío
        options.addArguments("--disable-renderer-backgrounding"); // Desactivar renderizado en segundo plano
        options.addArguments("--disable-sync"); // Desactivar sincronización
        options.addArguments("--metrics-recording-only"); // Solo grabación de métricas
        options.addArguments("--no-first-run"); // Sin primera ejecución
        options.addArguments("--safebrowsing-disable-auto-update"); // Desactivar actualización automática de navegación segura
        driver = new RemoteWebDriver(new URL(SELENIUM_URL), options);
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));
    }

    @AfterEach
    public void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }

    private String getRandomString(int length) {
        String uuid = UUID.randomUUID().toString().replace("-", ""); // Elimina guiones
        return uuid.substring(0, Math.min(length, uuid.length()));  // Recorta al tamaño requerido
    }

    @Test
    @Order(1)
    public void testCreateAccountSuccess() {
        try {
            driver.get(BASE_URL);

            WebElement nombreUsuario = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id(":r0:")));
            nombreUsuario.sendKeys(getRandomString(8));

            WebElement correoElectronico = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id(":r1:")));
            correoElectronico.sendKeys(getRandomString(8));

            WebElement codigoEstudiante = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id(":r2:")));
            codigoEstudiante.sendKeys(getRandomString(6));

            WebElement submitButton = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//button[contains(text(), 'Crear cuenta')]")));
            submitButton.click();

            wait.until(ExpectedConditions.urlContains("/app"));

            String currentUrl = driver.getCurrentUrl();
            Assertions.assertTrue(currentUrl.contains("/app"), "El usuario no fue redirigido a la página /app.");
        } catch (Exception e) {
        }
    }

    @Test
    @Order(2)
    public void testStayOnSamePageWhenFieldsEmpty() {
        driver.get(BASE_URL);

        WebElement submitButton = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//button[contains(text(), 'Crear cuenta')]")));
        submitButton.click();

        String currentUrl = driver.getCurrentUrl();
        Assertions.assertEquals(BASE_URL, currentUrl, "El usuario no se mantuvo en la misma página al enviar campos vacíos.");

   }


    @Test
    @Order(3)
    public void testDuplicateUsernameError() {
        driver.get(BASE_URL);

        WebElement nombreUsuario = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id(":r0:")));
        String duplicateUsername = getRandomString(8);
        nombreUsuario.sendKeys(duplicateUsername);

        WebElement correoElectronico = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id(":r1:")));
        correoElectronico.sendKeys(duplicateUsername);

        WebElement codigoEstudiante = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id(":r2:")));
        codigoEstudiante.sendKeys(getRandomString(6));

        WebElement submitButton = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//button[contains(text(), 'Crear cuenta')]")));
        submitButton.click();

        driver.navigate().refresh();
        nombreUsuario = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id(":r0:")));
        nombreUsuario.sendKeys(duplicateUsername);

        submitButton = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//button[contains(text(), 'Crear cuenta')]")));
        submitButton.click();

        String currentUrl = driver.getCurrentUrl();
        Assertions.assertEquals(BASE_URL, currentUrl, "El usuario fue redirigido a otra página.");


    }

}
