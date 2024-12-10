package me.klad3.sumapispring.exception;

import me.klad3.sumapispring.controller.TestController;
import me.klad3.sumapispring.security.ApiKeyAuthFilter;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = TestController.class,
        excludeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = ApiKeyAuthFilter.class))
@AutoConfigureMockMvc(addFilters = false)
@ActiveProfiles("test")
@Import(GlobalExceptionHandler.class)
class GlobalExceptionHandlerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    @DisplayName("Handle MethodArgumentNotValidException - Validation Failed")
    void handleValidationExceptions_ShouldReturnBadRequest() throws Exception {
        mockMvc.perform(get("/api/test/validation"))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.success", is(false)))
                .andExpect(jsonPath("$.message", is("Bad Request")))
                .andExpect(jsonPath("$.data.message", is("Required request parameter 'username' for method parameter type String is not present")))
                .andExpect(jsonPath("$.data.error", is("Bad Request")))
        ;
    }

    @Test
    @DisplayName("Handle AuthenticationException - Unauthorized")
    void handleAuthenticationException_ShouldReturnUnauthorized() throws Exception {
        mockMvc.perform(get("/api/test/authentication"))
                .andExpect(status().isUnauthorized())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message", is("Authentication failed")))
                .andExpect(jsonPath("$.data.message", is("Invalid credentials")))
                .andExpect(jsonPath("$.data.error", is("Authentication failed")))
                .andExpect(jsonPath("$.success", is(false)));
    }

    @Test
    @DisplayName("Handle ResourceNotFoundException - Not Found")
    void handleResourceNotFoundException_ShouldReturnNotFound() throws Exception {
        mockMvc.perform(get("/api/test/resource-not-found"))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message", is("Not Found")))
                .andExpect(jsonPath("$.data.message", is("User not found")))
                .andExpect(jsonPath("$.data.error", is("Resource not found")))
                .andExpect(jsonPath("$.success", is(false)));
    }

    @Test
    @DisplayName("Handle BadRequestException - Bad Request")
    void handleBadRequestException_ShouldReturnBadRequest() throws Exception {
        mockMvc.perform(get("/api/test/bad-request"))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message", is("Bad Request")))
                .andExpect(jsonPath("$.data.message", is("Invalid input")))
                .andExpect(jsonPath("$.data.error", is("Bad Request")))
                .andExpect(jsonPath("$.success", is(false)));
    }

    @Test
    @DisplayName("Handle ApiKeyUnauthorizedException - Unauthorized")
    void handleApiKeyUnauthorizedException_ShouldReturnUnauthorized() throws Exception {
        mockMvc.perform(get("/api/test/api-key-unauthorized"))
                .andExpect(status().isUnauthorized())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message", is("Unauthorized")))
                .andExpect(jsonPath("$.data.message", is("API Key is invalid")))
                .andExpect(jsonPath("$.data.error", is("Unauthorized")))
                .andExpect(jsonPath("$.success", is(false)));
    }

    @Test
    @DisplayName("Handle ResourceAlreadyExistsException - Conflict")
    void handleResourceAlreadyExistsException_ShouldReturnConflict() throws Exception {
        mockMvc.perform(get("/api/test/resource-already-exists"))
                .andExpect(status().isConflict())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message", is("Conflict")))
                .andExpect(jsonPath("$.data.message", is("Resource already exists")))
                .andExpect(jsonPath("$.data.error", is("Conflict")))
                .andExpect(jsonPath("$.success", is(false)));
    }

    @Test
    @DisplayName("Handle ExternalApiException - Internal Server Error")
    void handleExternalApiException_ShouldReturnInternalServerError() throws Exception {
        mockMvc.perform(get("/api/test/external-api-error"))
                .andExpect(status().isInternalServerError())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message", is("External API Error")))
                .andExpect(jsonPath("$.data.message", is("External API call failed")))
                .andExpect(jsonPath("$.data.error", is("External API Error")))
                .andExpect(jsonPath("$.success", is(false)));
    }

    @Test
    @DisplayName("Handle General Exception - Internal Server Error")
    void handleAllExceptions_ShouldReturnInternalServerError() throws Exception {
        mockMvc.perform(get("/api/test/general-error"))
                .andExpect(status().isInternalServerError())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message", is("Internal Server Error")))
                .andExpect(jsonPath("$.data.message", is("An unexpected error occurred")))
                .andExpect(jsonPath("$.data.error", is("General error")))
                .andExpect(jsonPath("$.success", is(false)));
    }

    @Test
    @DisplayName("Handle MissingServletRequestParameterException - Bad Request")
    void handleMissingServletRequestParameterException_ShouldReturnBadRequest() throws Exception {
        mockMvc.perform(get("/api/test/missing-parameter"))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message", is("Bad Request")))
                .andExpect(jsonPath("$.data.message", is("Required request parameter 'param' for method parameter type String is not present")))
                .andExpect(jsonPath("$.data.error", is("Bad Request")))
                .andExpect(jsonPath("$.success", is(false)));
    }

    @Test
    @DisplayName("Handle HttpMessageNotReadableException - Bad Request")
    void handleHttpMessageNotReadableException_ShouldReturnBadRequest() throws Exception {
        mockMvc.perform(get("/api/test/malformed-json"))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message", is("Bad Request")))
                .andExpect(jsonPath("$.data.message", is("Malformed JSON request")))
                .andExpect(jsonPath("$.data.error", is("Malformed JSON Request")))
                .andExpect(jsonPath("$.success", is(false)));
    }
}
