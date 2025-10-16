package com.sprint.mission.discodeit.security.jwt;

import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.JWSSigner;
import com.nimbusds.jose.JWSVerifier;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Component
public class JwtTokenProvider {
    @Getter
    @Value("${jwt.key}")
    private String secretKey;

    @Getter
    @Value("${jwt.access-token-expiration-minutes}")
    private int accessTokenExpirationMinutes;

    @Getter
    @Value("${jwt.refresh-token-expiration-minutes}")
    private int refreshTokenExpirationMinutes;

    // 액세스 토큰 발급
    public String generateAccessToken(Map<String, Object> claims, String subject) {
        return generateToken(claims, subject, accessTokenExpirationMinutes);
    }
    
    // 리프레시 토큰 발급
    public String generateRefreshToken(String subject) {
        return generateToken(Map.of(), subject, refreshTokenExpirationMinutes);
    }

    public String generateToken(Map<String, Object> claims, String subject, int expirationMinutes) {
        try {
            JWSSigner signer = new MACSigner(secretKey.getBytes(StandardCharsets.UTF_8));
            Date expiration = new Date(System.currentTimeMillis() + expirationMinutes * 60 * 1000);
            JWTClaimsSet claimSet = new JWTClaimsSet.Builder()
                    .subject(subject)
                    .claim("role", claims.get("role"))
                    .expirationTime(expiration)
                    .issueTime(new Date())
                    .issuer("discodeit")
                    .build();
            SignedJWT signedJWT = new SignedJWT(
                    new JWSHeader(JWSAlgorithm.HS256),
                    claimSet
            );
            signedJWT.sign(signer);
            return signedJWT.serialize();
        } catch (Exception e) {
            throw new RuntimeException("JWT 발급 실패", e);
        }
    }

    public Map<String, Object> getClaims(String token) {
        try {
            SignedJWT signedJWT = SignedJWT.parse(token);
            JWSVerifier verifier = new MACVerifier(secretKey.getBytes(StandardCharsets.UTF_8));

            if (!signedJWT.verify(verifier)) {
                throw new RuntimeException("JWT 검증 실패");
            }

            JWTClaimsSet claimsSet = signedJWT.getJWTClaimsSet();
            return claimsSet.getClaims();
        } catch (Exception e) {
            throw new RuntimeException("JWT 파싱 실패", e);
        }
    }

    public String refreshAccessToken(String refreshToken) {
        try {
            SignedJWT signedJWT = SignedJWT.parse(refreshToken);
            JWSVerifier verifier = new MACVerifier(secretKey.getBytes(StandardCharsets.UTF_8));

            if (!signedJWT.verify(verifier)) {
                throw new RuntimeException("Refresh Token 검증 실패");
            }

            JWTClaimsSet claimsSet = signedJWT.getJWTClaimsSet();

            Date now = new Date();
            if (claimsSet.getExpirationTime().before(now)) {
                throw new RuntimeException("만료된 Refresh Token");
            }

            String subject = claimsSet.getSubject();
            Map<String, Object> newClaims = new HashMap<>();
            newClaims.put("roles", claimsSet.getClaim("roles"));

            return generateToken(newClaims, subject, refreshTokenExpirationMinutes);
        } catch (Exception e) {
            throw new RuntimeException("Access Token 갱신 실패");
        }
    }
}
