package org.example.book_store_backend.service;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
public class JwtServiceImpl implements JwtService{
    private JwtEncoder jwtEncoder;

    //bean annotation for jwtEncoder in config, registers the bean into the inversion of control container, so it can be injected into the service,
    //this is how the jwt encoder is injected into the service
    public JwtServiceImpl(JwtEncoder jwtEncoder) {
        this.jwtEncoder = jwtEncoder;
    }

    @Override
    public String generateToken(UserDetails userDetails) {
        Instant now = Instant.now();
        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuer("self")
                .issuedAt(now)
                .subject(userDetails.getUsername())
                .expiresAt(now.plusSeconds(3600))
                .build();
        return jwtEncoder.encode(JwtEncoderParameters.from(claims)).getTokenValue();
    }

    //write dto that takes username and password
    //write controller to contact generateToken
    //write controller to register user
    //write controller to login user
}
