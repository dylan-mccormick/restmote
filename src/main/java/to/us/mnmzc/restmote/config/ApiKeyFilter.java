package to.us.mnmzc.restmote.config;

import java.io.IOException;
import java.util.ArrayList;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

/**
 * Filter that checks for the presence of an API key in the request header and validates it. Very
 * basic single API key for authentication, planned to be replaced with a more robust authentication
 * mechanism in the future. The API key is expected to be passed in the "X-API-Key" header.
 */
@NullMarked
@Component
public class ApiKeyFilter extends OncePerRequestFilter {

  @Value("${security.api-key}")
  @Nullable
  private String apiKey;

  public boolean apiKeyMatches(String key) {
    return key.equals(apiKey);
  }

  @Override
  protected void doFilterInternal(
          HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
          throws IOException, ServletException {
    // if the API key is not set, throw error
    if (apiKey == null || apiKey.isBlank()) {
      response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
      response.getWriter().write("Server error: API key is not configured");
      return;
    }

    // we need to allow all websocket requests to pass, since many WS clients can't set custom
    // headers
    // authorization will be handled in the WebSocketReceiverHandler by checking the query
    // parameters for the API key
    if (request.getHeader("Upgrade") == null
            || !request.getHeader("Upgrade").equalsIgnoreCase("websocket")) {
      String passedKey = request.getHeader("X-API-Key");

      if (passedKey == null || !apiKeyMatches(passedKey)) {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.getWriter().write("Unauthorized: Invalid API key");
        return;
      }
    }

    // Set authentication in the security context
    UsernamePasswordAuthenticationToken authentication =
            new UsernamePasswordAuthenticationToken("api-key-user", null, new ArrayList<>());
    SecurityContextHolder.getContext().setAuthentication(authentication);

    filterChain.doFilter(request, response);
  }
}