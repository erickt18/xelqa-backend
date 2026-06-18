package ec.ucacue.xelqa.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(
        info = @Info(
                title = "API REST - XELQA UCACUE",
                version = "1.0",
                description = "Documentación oficial del backend transaccional de XELQA. Incluye módulos de Wallet, Cafetería, Identidad y Eventos.",
                contact = @Contact(name = "Equipo Backend XELQA")
        ),
        security = @SecurityRequirement(name = "bearerAuth") // Aplica el candado a todas las rutas por defecto
)
@SecurityScheme(
        name = "bearerAuth",
        type = SecuritySchemeType.HTTP,
        scheme = "bearer",
        bearerFormat = "JWT",
        description = "Ingresa el Token JWT generado en el endpoint /api/auth/login. No es necesario escribir la palabra 'Bearer'."
)
public class SwaggerConfig {
    // La configuración se levanta sola gracias a las anotaciones.
}