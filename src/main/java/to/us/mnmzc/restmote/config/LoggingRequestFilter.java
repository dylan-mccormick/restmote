package to.us.mnmzc.restmote.config;

import java.io.IOException;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

import org.jspecify.annotations.NullMarked;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

/** Logs each incoming request. Logs the request method, URI, and query parameters. */
@NullMarked
@Component
@Slf4j
public class LoggingRequestFilter extends OncePerRequestFilter {

  @Override
  protected void doFilterInternal(
      HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
      throws IOException, ServletException {
    String method = request.getMethod();
    String uri = request.getRequestURI();
    String queryString = request.getQueryString();

    if (queryString != null) {
      uri += "?" + queryString;
    }

    log.info("Incoming request: {} {}", method, uri);

    filterChain.doFilter(request, response);
  }
}
