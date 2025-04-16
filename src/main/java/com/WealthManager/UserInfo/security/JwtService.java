package com.WealthManager.UserInfo.security;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.stream.Collectors;

@Service
@Slf4j
public class JwtService {

    private final JwtEncoder jwtEncoder;

    public JwtService(JwtEncoder jwtEncoder) {
        this.jwtEncoder = jwtEncoder;
    }


    public String generateToken(Authentication authentication) {
        Instant now = Instant.now();

        String scope = authentication.getAuthorities()
                .stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(" "));

        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuer("self")
                .issuedAt(now)
                .expiresAt(now.plus(10, ChronoUnit.HOURS))
                .subject(authentication.getName())
                .claim("scope", scope)
                .build();

        return jwtEncoder.encode(JwtEncoderParameters.from(claims)).getTokenValue();
    }
/*
    @Value("${app.jwt-secret}")
    private String Secret;


//    @Value("${app.jwt-expiration-milliseconds}")
//    private Long ExpirationDate;

    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    private Claims extractAllClaims(String token) {
        return Jwts
                .parser()
                .setSigningKey(getSignkey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    private Boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    public Boolean validateToken(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }


    public String generateToken(String username, Role role) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("role", role.name());
        log.info("Generating token: {}", username);
        return createToken(claims, username);

    }

    private String createToken(Map<String, Object> claims, String username) {
        try {
            long nowMillis = System.currentTimeMillis();
            long expMillis = nowMillis + 1000 * 60 * 1; // 1 minute

            Date now = new Date(nowMillis);
            Date exp = new Date(expMillis);

            log.info("Creating JWT for {}", username);

            return Jwts.builder()
                    .setClaims(claims)
                    .setSubject(username)
                    .setIssuedAt(now)
                    .setExpiration(exp)
                    .signWith(getSignkey(), SignatureAlgorithm.HS256)
                    .compact();

        } catch (Exception e) {
            log.error("Error while creating JWT token: {}", e.getMessage(), e);
            throw new RuntimeException("Token creation failed", e);
        }
    }


    private Key getSignkey() {
        log.info("Create signkey");
        byte[] keyBytes = Decoders.BASE64.decode(Secret);
        log.info("Signing key bytes: {}", new String(keyBytes));
        return Keys.hmacShaKeyFor(keyBytes);
    }

 */
}
