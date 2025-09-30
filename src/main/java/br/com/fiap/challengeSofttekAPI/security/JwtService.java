package br.com.fiap.challengeSofttekAPI.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class JwtService {


    @Value("${jwt.secret}")
    private String secretKey;

    @Value("${jwt.expiration}")
    private long expirationMs;

    // Construtor padrão (sem injeção direta) para logar a inicialização.
    // As anotações @Value já injetam os valores.
    public JwtService(@Value("${jwt.secret}") String secretKey, @Value("${jwt.expiration}") long expirationMs) {
        this.secretKey = secretKey;
        this.expirationMs = expirationMs;
        log.info("JwtService inicializado. Expiração do token configurada para {} ms.", expirationMs);
        // Logar a secretKey é perigoso em produção, evite. Apenas logamos que ela existe.
        log.debug("SecretKey para JWT carregada (não exibindo o valor por segurança).");
    }

    public String generateAnonymousToken(String anonymousUserId) {
        log.info("Gerando token anônimo para o usuário: {}.", anonymousUserId);
        Map<String, Object> claims = new HashMap<>();
        claims.put("role", "ROLE_ANONYMOUS");
        String token = createToken(claims, anonymousUserId);
        log.info("Token anônimo gerado com sucesso para o usuário: {}. Início do token: {}...", anonymousUserId, token.substring(0, Math.min(token.length(), 20))); // Loga apenas uma parte do token
        return token;
    }

    private String createToken(Map<String, Object> claims, String subject) {
        Date issuedAt = new Date(System.currentTimeMillis());
        Date expiration = new Date(System.currentTimeMillis() + expirationMs);
        log.debug("Criando token JWT para assunto: {}. Claims: {}. Emitido em: {}. Expira em: {}.", subject, claims, issuedAt, expiration);

        String token = Jwts.builder()
                .setClaims(claims)
                .setSubject(subject)
                .setIssuedAt(issuedAt)
                .setExpiration(expiration)
                .signWith(getSignKey(), SignatureAlgorithm.HS256)
                .compact();
        log.debug("Token JWT compactado com sucesso para assunto: {}.", subject);
        return token;
    }

    public Boolean validateToken(String token) {
        log.debug("Validando token JWT recebido. Início do token: {}...", token.substring(0, Math.min(token.length(), 20)));
        try {
            Claims claims = extractAllClaims(token); // Tenta extrair as claims. Isso vai lançar exceção se inválido/expirado.
            // Se chegou aqui, o token não está expirado e é válido.
            log.info("Token JWT validado com sucesso. Assunto (sub): {}.", claims.getSubject());
            return true;
        } catch (ExpiredJwtException e) {
            log.warn("Token JWT expirado. Assunto (sub): {}. Erro: {}", e.getClaims() != null ? e.getClaims().getSubject() : "N/A", e.getMessage());
            return false;
        } catch (SignatureException e) {
            log.warn("Assinatura do token JWT inválida. Erro: {}", e.getMessage());
            return false;
        } catch (MalformedJwtException e) {
            log.warn("Token JWT malformado. Erro: {}", e.getMessage());
            return false;
        } catch (Exception e) { // Captura outras exceções gerais de validação JWT
            log.warn("Erro na validação do JWT (token inválido ou outras causas): {}", e.getMessage(), e);
            return false;
        }
    }

    // Este método agora é redundante, pois a lógica de expiração é tratada por ExpiredJwtException.
    // Pode ser removido ou marcado como deprecated se não houver uso externo.
    @Deprecated
    private Boolean isTokenExpired(String token) {
        log.debug("Verificando expiração do token (método isTokenExpired).");
        try {
            return extractClaim(token, Claims::getExpiration).before(new Date());
        } catch (Exception e) {
            log.warn("Erro ao tentar extrair data de expiração do token para verificação. Assumindo token expirado. Erro: {}", e.getMessage());
            return true; // Força a expiração
        }
    }

    public String extractAnonymousUserId(String token) {
        log.debug("Extraindo ID do usuário anônimo do token JWT.");
        return extractClaim(token, Claims::getSubject);
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        log.debug("Extraindo claim específica do token JWT.");
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    private Claims extractAllClaims(String token) {
        log.debug("Extraindo todas as claims do token JWT.");
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(getSignKey())
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
        } catch (Exception e) {
            // Este catch pega erros como token malformado, assinatura inválida antes do validateToken.
            // O validateToken já possui um catch mais robusto para os tipos específicos.
            log.warn("Falha ao extrair todas as claims do token. Token inválido ou corrompido. Erro: {}", e.getMessage());
            throw e; // Relança para ser tratado pelo validateToken ou chamador.
        }
    }

    private Key getSignKey() {
        log.debug("Obtendo chave de assinatura para JWT.");
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}