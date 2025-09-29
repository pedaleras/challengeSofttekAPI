package br.com.fiap.challengeSofttekAPI.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class JwtService {

    private static final Logger logger = LoggerFactory.getLogger(JwtService.class);

    @Value("${jwt.secret}")
    private String secretKey;

    @Value("${jwt.expiration}")
    private long expirationMs;

    public String generateAnonymousToken(String anonymousUserId) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("role", "ROLE_ANONYMOUS");
        return createToken(claims, anonymousUserId);
    }

    private String createToken(Map<String, Object> claims, String subject) {
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(subject)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + expirationMs))
                .signWith(getSignKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    public Boolean validateToken(String token) {
        try {
            // Tenta extrair as claims. Se o token estiver expirado, ExpiredJwtException será lançada aqui.
            Claims claims = extractAllClaims(token);
            // Se chegou aqui, o token não está expirado e é válido.
            return true;
        } catch (ExpiredJwtException e) {
            logger.warn("Token JWT expirado: {}", e.getMessage());
            return false;
        } catch (Exception e) {
            logger.warn("Erro na validação do JWT (token inválido ou malformado): {}", e.getMessage());
            return false;
        }
    }

    private Boolean isTokenExpired(String token) {
        // Este método não será mais diretamente chamado por validateToken na nova lógica,
        // mas é mantido caso outras partes do código o utilizem.
        // A lógica de expiração agora é tratada pela ExpiredJwtException no validateToken.
        try {
            return extractClaim(token, Claims::getExpiration).before(new Date());
        } catch (Exception e) {
            // Se houver qualquer erro ao extrair a data de expiração, consideramos o token inválido ou expirado
            return true; // Força a expiração
        }
    }

    public String extractAnonymousUserId(String token) {
        // Nota: se o token estiver expirado, extractAllClaims vai lançar ExpiredJwtException.
        // O JwtAuthenticationFilter precisará de um try-catch mais robusto ou chamar
        // validateToken primeiro para evitar essa exceção aqui.
        // Por agora, manteremos assim, mas é um ponto a observar.
        return extractClaim(token, Claims::getSubject);
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSignKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    private Key getSignKey() {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}