package ec.ucacue.xelqa.config;

import java.security.Key;
import java.util.Date;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;

@Component
public class JwtUtil {

    // Spring Boot inyecta automáticamente el valor de application.properties
    @Value("${jwt.secret}")
    private String secretKey;

    private Key key;

    // El token durará 24 horas (en milisegundos)
    private final long TIEMPO_EXPIRACION = 86400000;

    // Este método se ejecuta automáticamente justo después de que Spring inyecta el secretKey
    @PostConstruct
    public void init() {
        this.key = Keys.hmacShaKeyFor(secretKey.getBytes());
    }

    public String generarToken(String correo, String rol) {
        return Jwts.builder()
                .setSubject(correo)
                .claim("rol", rol)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + TIEMPO_EXPIRACION))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();

    }
    // Añade esto en tu JwtUtil.java

    public String extraerCorreo(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }

    public boolean validarToken(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
            return true;
        } catch (Exception e) {
            return false; // Si el token expiró, está mal escrito o es falso, retorna false
        }

    }

    // Agrega esto al final de tu JwtUtil.java
    public String extraerRol(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .get("rol", String.class);
    }
}
