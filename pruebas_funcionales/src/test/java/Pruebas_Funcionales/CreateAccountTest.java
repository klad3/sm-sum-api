package Pruebas_Funcionales;

import org.junit.jupiter.api.*;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class CreateAccountTest {

    private WebDriver driver;
    private WebDriverWait wait;

    @BeforeEach
    public void setUp() {
        driver = new ChromeDriver();
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));
    }

    @AfterEach
    public void tearDown() {
        driver.quit();
    }

    @Test
    @Order(1)
    public void testCreateAccountSuccess() {
        driver.get("http://localhost:3000/login");

        // Localizar los campos de entrada y rellenarlos
        WebElement nombreUsuario = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id(":r0:")));
        nombreUsuario.sendKeys("adm");

        WebElement correoElectronico = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id(":r1:")));
        correoElectronico.sendKeys("admin");

        WebElement codigoEstudiante = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id(":r2:")));
        codigoEstudiante.sendKeys("112024");

        // Hacer clic en el botón de envío
        WebElement submitButton = driver.findElement(By.xpath("//button[contains(text(), 'Crear cuenta')]"));
        submitButton.click();

        // Validar que el mensaje "Error al crear el usuario" no aparezca
        boolean isErrorMessagePresent = driver.findElements(By.xpath("//*[text()='Error al crear el usuario']")).size() > 0;
        Assertions.assertFalse(isErrorMessagePresent, "El mensaje 'Error al crear el usuario' apareció en un caso exitoso");
    }


    @Test
    public void testStayOnSamePageWhenFieldsEmpty() {
        driver.get("http://localhost:3000/login");

        // Hacer clic en el botón de envío sin rellenar ningún campo
        WebElement submitButton = driver.findElement(By.xpath("//button[contains(text(), 'Crear cuenta')]"));
        submitButton.click();

        // Verificar que la URL no cambió (se mantiene en la misma página)
        String currentUrl = driver.getCurrentUrl();
        Assertions.assertEquals("http://localhost:3000/login", currentUrl, "El usuario no se mantuvo en la misma página después de enviar el formulario vacío");

    }


    @Test
    public void testDuplicateUsernameError() {
        driver.get("http://localhost:3000/login");

        WebElement nombreUsuario = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id(":r0:")));
        nombreUsuario.sendKeys("adm");

        WebElement correoElectronico = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id(":r1:")));
        correoElectronico.sendKeys("correo");

        WebElement codigoEstudiante = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id(":r2:")));
        codigoEstudiante.sendKeys("12345");

        WebElement submitButton = driver.findElement(By.xpath("//button[contains(text(), 'Crear cuenta')]"));
        submitButton.click();

        WebElement errorMessage = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//*[text()='Error al crear el usuario']")));
        Assertions.assertTrue(errorMessage.isDisplayed(), "El mensaje 'Error al crear el usuario' no está visible");
    }
    @Test
    public void testLetraporCodigo() {
        driver.get("http://localhost:3000/login");

        WebElement nombreUsuario = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id(":r0:")));
        nombreUsuario.sendKeys("nuevousuario");

        WebElement correoElectronico = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id(":r1:")));
        correoElectronico.sendKeys("correo542");

        WebElement codigoEstudiante = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id(":r2:")));
        codigoEstudiante.sendKeys("asdasd");

        WebElement submitButton = driver.findElement(By.xpath("//button[contains(text(), 'Crear cuenta')]"));
        submitButton.click();

        WebElement errorMessage = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//*[text()='Error al crear el usuario']")));
        Assertions.assertTrue(errorMessage.isDisplayed(), "El mensaje 'Error al crear el usuario' no está visible");
    }


}
