package io.bootify.saven.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.oauth2.core.DelegatingOAuth2TokenValidator;
import org.springframework.security.oauth2.core.OAuth2TokenValidator;
import org.springframework.security.oauth2.jwt.*;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.security.web.SecurityFilterChain;


@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityConfig {

    
   //APIs that don't need authentication
   private static final String[] WHITE_LIST_URLS = {
        "/api/leaderboards",
        "/api/userLeaderboards/{\\d+}",
        "/api/leaderboards/getLeaderboards/{\\d}/{\\d}"
   };
   
   @Value("${auth0.audience}")
   private String audience;
    
   @Value("${spring.security.oauth2.resourceserver.jwt.issuer-uri}")
   private String issuer;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
    
        http    .authorizeRequests()
                .antMatchers(HttpMethod.GET, WHITE_LIST_URLS).permitAll()
                .and()
                .authorizeRequests()
                .antMatchers("/actuator/**").permitAll()
                .and()
                .authorizeRequests()
                .anyRequest()
                .authenticated()
                .and().cors()
                .and().oauth2ResourceServer().jwt().decoder(jwtDecoder())
                .jwtAuthenticationConverter(jwtAuthenticationConverter());
        return http.build();
    }
    
    @Bean
    @ConditionalOnMissingBean
    JwtDecoder jwtDecoder() {
        
        NimbusJwtDecoder jwtDecoder = (NimbusJwtDecoder)
                JwtDecoders.fromOidcIssuerLocation(issuer);

        OAuth2TokenValidator<Jwt> audienceValidator = new AudienceValidator(audience);
        OAuth2TokenValidator<Jwt> withIssuer = JwtValidators.createDefaultWithIssuer(issuer);
        OAuth2TokenValidator<Jwt> withAudience = new DelegatingOAuth2TokenValidator<>(withIssuer, audienceValidator);
        jwtDecoder.setJwtValidator(withAudience);

        return jwtDecoder;
    }

    JwtAuthenticationConverter jwtAuthenticationConverter() {
        JwtGrantedAuthoritiesConverter converter = new JwtGrantedAuthoritiesConverter();
        converter.setAuthoritiesClaimName("permissions");
        converter.setAuthorityPrefix("");

        JwtAuthenticationConverter jwtConverter = new JwtAuthenticationConverter();
        jwtConverter.setJwtGrantedAuthoritiesConverter(converter);
        return jwtConverter;
    }
}
