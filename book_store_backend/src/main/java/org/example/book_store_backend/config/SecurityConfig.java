package org.example.book_store_backend.config;

import com.nimbusds.jose.jwk.source.ImmutableSecret;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;
import org.springframework.security.web.SecurityFilterChain;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;


@Configuration
public class SecurityConfig {
    private final String jwtSecret;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
        return httpSecurity.csrf(httpSecurityCsrfConfigurer -> httpSecurityCsrfConfigurer.disable())
                .sessionManagement(x -> x.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/users/auth").permitAll()
                        .requestMatchers("/users/register").permitAll()
                        .requestMatchers("/books/allbooks").permitAll()
                        .requestMatchers("/books/{isbn_id}").permitAll()
                        //.requestMatchers("/users/review/").authenticated() //optional, might add later
                        .anyRequest().authenticated())
                .oauth2ResourceServer(oauth -> oauth.jwt())
                .build();
    }

    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }

    @Bean
    public JwtDecoder jwtDecoder(){
        byte[] bytes = jwtSecret.getBytes(); //get the bytes from the jwt secret
        SecretKeySpec originalKey = new SecretKeySpec(bytes, 0, bytes.length, "HmacSHA256");
        return NimbusJwtDecoder.withSecretKey(originalKey).macAlgorithm(MacAlgorithm.HS256).build(); //decodes jwt to see if request is valid
    }

    @Bean
    public JwtEncoder jwtEncoder(){
        //byte[] byes = jwtSecret.getBytes();
        //SecretKeySpec originalKey = new SecretKeySpec(byes, 0, byes.length, "HmacSHA256");
        //return new NimbusJwtEncoder(new ImmutableSecret<>(originalKey));
        return new NimbusJwtEncoder(new ImmutableSecret<>(jwtSecret.getBytes()));
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    public SecurityConfig(@Value("${jwt.secret}") String jwtSecret){
        this.jwtSecret = jwtSecret; //goes to application properties, look and set it to the value of the jwt secret in the private final string. takes application property and set it,
    }

}
