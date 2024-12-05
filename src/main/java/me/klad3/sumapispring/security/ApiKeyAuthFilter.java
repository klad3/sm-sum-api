package me.klad3.sumapispring.security;

import io.micrometer.common.lang.NonNull;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import me.klad3.sumapispring.exception.ApiKeyUnauthorizedException;
import me.klad3.sumapispring.model.User;
import me.klad3.sumapispring.service.UserService;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.servlet.HandlerExceptionResolver;

import java.io.IOException;
import java.util.Collections;
import java.util.Optional;

@Component
public class ApiKeyAuthFilter extends OncePerRequestFilter {

    private final UserService apiClientService;

    @Qualifier("handlerExceptionResolver") @NonNull
    private final HandlerExceptionResolver resolver;

    @Value("${api.security.header.name:API-Key}")
    private String apiKeyHeaderName;

    @Value("${api.security.secret.header.name:API-Secret}")
    private String apiSecretHeaderName;

    public ApiKeyAuthFilter(UserService apiClientService, @Qualifier("handlerExceptionResolver") @NonNull HandlerExceptionResolver resolver) {
        this.apiClientService = apiClientService;
        this.resolver = resolver;
    }

    @Override
    protected boolean shouldNotFilter(@NonNull HttpServletRequest request) {
        String path = request.getRequestURI();
        return path.equals("/user/create");
    }

    @Override
    protected void doFilterInternal(@NonNull  HttpServletRequest request,
                                    @NonNull  HttpServletResponse response,
                                    @NonNull  FilterChain filterChain) throws ServletException, IOException {

        try {
            String apiKey = request.getHeader(apiKeyHeaderName);
            String apiSecret = request.getHeader(apiSecretHeaderName);

            if (!StringUtils.hasText(apiKey) || !StringUtils.hasText(apiSecret)) {
                throw new ApiKeyUnauthorizedException("Missing API Key or Secret");
            }

            Optional<User> optionalApiClient = apiClientService.findByApiKey(apiKey);

            if (optionalApiClient.isPresent()) {
                User apiClient = optionalApiClient.get();
                if (apiClient.verifyApiSecret(apiSecret)) {
                    Authentication auth = new UsernamePasswordAuthenticationToken(
                            apiClient.getApiKey(),
                            null,
                            Collections.singletonList(new SimpleGrantedAuthority("ROLE_API_CLIENT"))
                    );
                    SecurityContextHolder.getContext().setAuthentication(auth);
                    filterChain.doFilter(request, response);
                    return;
                }
            }

            throw new ApiKeyUnauthorizedException("Invalid API Key or Secret");
        } catch (ApiKeyUnauthorizedException e) {
            resolver.resolveException(request, response, null, e);
        }
        }
}
