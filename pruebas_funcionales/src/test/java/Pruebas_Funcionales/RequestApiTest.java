package Pruebas_Funcionales;

import org.junit.jupiter.api.*;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

public class RequestApiTest {
    private WebDriver driver;
    private WebDriverWait wait;

    @BeforeEach
    public void setUp() {
        // Configura el driver de Chrome (asegúrate de que el path del chromedriver esté en tu PATH o especifica el path aquí)
        driver = new ChromeDriver();
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));
    }

    @AfterEach
    public void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }

    @Test
    @DisplayName("Test de creación de cuenta y login exitoso")
    public void testAccountCreationAndLogin() {
        try {
            // Paso 1: Abrir página de creación de cuenta
            driver.get("http://localhost:3000/login");

            // Completar el campo de nombre de usuario
            WebElement nombreUsuario = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id(":r0:")));
            nombreUsuario.sendKeys("admin123456");

            // Completar el campo de correo electrónico
            WebElement correoElectronico = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id(":r1:")));
            correoElectronico.sendKeys("admincorreo"); // Asegúrate de usar un correo electrónico válido

            // Completar el campo de código de estudiante
            WebElement codigoEstudiante = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id(":r2:")));
            codigoEstudiante.sendKeys("123456789");

            // Hacer clic en el botón de envío para crear la cuenta
            WebElement submitButton = driver.findElement(By.xpath("//button[contains(text(), 'Crear cuenta')]"));
            submitButton.click();

            // Validar redirección a la nueva página después de crear la cuenta
            wait.until(ExpectedConditions.urlToBe("http://localhost:3000/app"));

            // Paso 2: Rellenar los campos para iniciar sesión
            // Se necesita ingresar una cuenta real de unmsm, modificar para que pueda completarse el test
            WebElement userInput = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id(":r9:")));
            userInput.clear(); // Limpia el campo antes de ingresar texto
            userInput.sendKeys("INGRESAR CORREO REAL DE SU CORREO DE SAN MARCOS");

            WebElement passwordInput = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id(":ra:")));
            passwordInput.clear(); // Limpia el campo antes de ingresar texto
            //modificar por tu contraseña real de san marcos
            passwordInput.sendKeys("INGRESAR CONTRASEÑA REAL DE SU CORREO DE SAN MARCOS");

            // Hacer clic en el botón de login
            WebElement loginButton = driver.findElement(By.xpath("//button[contains(text(), 'Enviar Solicitud')]"));
            loginButton.click();

            // Verificar que aparece el mensaje "HTTP Status: 200" en la pantalla
            WebElement statusMessageElement = wait.until(ExpectedConditions.visibilityOfElementLocated(
                    By.xpath("//*[contains(text(), 'HTTP Status: 200')]")
            ));

            String expectedMessage = "HTTP Status: 200";
            String actualMessage = statusMessageElement.getText();

            Assertions.assertEquals(expectedMessage, actualMessage, "El mensaje de estado HTTP no coincide.");

        } catch (Exception e) {
            Assertions.fail("Se produjo un error durante la prueba: " + e.getMessage());
        }
    }
    @Test
    public void TestRequestApiFailed() {
        try {
            // Paso 1: Abrir página de creación de cuenta
            driver.get("http://localhost:3000/login");

            // Completar el campo de nombre de usuario
            WebElement nombreUsuario = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id(":r0:")));
            nombreUsuario.sendKeys("ADMIN123456789");

            // Completar el campo de correo electrónico
            WebElement correoElectronico = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id(":r1:")));
            correoElectronico.sendKeys("ADMIN_CORREO"); // Asegúrate de usar un correo electrónico válido

            // Completar el campo de código de estudiante
            WebElement codigoEstudiante = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id(":r2:")));
            codigoEstudiante.sendKeys("987654321");

            // Hacer clic en el botón de envío para crear la cuenta
            WebElement submitButton = driver.findElement(By.xpath("//button[contains(text(), 'Crear cuenta')]"));
            submitButton.click();

            // Validar redirección a la nueva página después de crear la cuenta
            wait.until(ExpectedConditions.urlToBe("http://localhost:3000/app"));

            // Paso 2: Rellenar los campos para iniciar sesión
            WebElement userInput = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id(":r9:")));
            userInput.clear(); // Limpia el campo antes de ingresar texto
            userInput.sendKeys("daniel.ames");

            WebElement passwordInput = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id(":ra:")));
            passwordInput.clear(); // Limpia el campo antes de ingresar texto
            passwordInput.sendKeys("contraseña");

            // Hacer clic en el botón de login
            WebElement loginButton = driver.findElement(By.xpath("//button[contains(text(), 'Enviar Solicitud')]"));
            loginButton.click();

            // Verificar que aparece el mensaje "HTTP Status: 401" en la pantalla
            WebElement statusMessageElement = wait.until(ExpectedConditions.visibilityOfElementLocated(
                    By.xpath("//*[contains(text(), 'HTTP Status: 401')]")
            ));

            String expectedMessage = "HTTP Status: 401";
            String actualMessage = statusMessageElement.getText();

            Assertions.assertEquals(expectedMessage, actualMessage, "El mensaje de estado HTTP no coincide.");

        } catch (Exception e) {
            Assertions.fail("Se produjo un error durante la prueba: " + e.getMessage());
        }
    }
}
