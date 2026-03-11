package to.us.mnmzc.restmote.config;

import jakarta.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfigurationSource;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

  @Autowired private ApiKeyFilter apiKeyFilter;
  @Autowired private LoggingRequestFilter loggingRequestFilter;
  @Autowired private CorsConfigurationSource corsConfigurationSource;

  @Bean
  public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
    http
        .cors(cors -> cors.configurationSource(corsConfigurationSource))
        .csrf(AbstractHttpConfigurer::disable)
        .sessionManagement(
            session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
        .authorizeHttpRequests(
            auth -> auth.requestMatchers("/error").permitAll().anyRequest().authenticated())
        .anonymous(AbstractHttpConfigurer::disable)
        .addFilterBefore(apiKeyFilter, UsernamePasswordAuthenticationFilter.class)
        .addFilterBefore(loggingRequestFilter, ApiKeyFilter.class);
    http.exceptionHandling(
        ex ->
            ex.authenticationEntryPoint(
                (request, response, authException) -> {
                  response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                }));

    return http.build();
  }
}
