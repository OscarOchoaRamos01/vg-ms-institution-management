package pe.edu.vallegrande.vgmsinstitutionmanagement.application.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("API de Gestión de Instituciones - Valle Grande")
                        .version("1.0.0")
                        .description("API REST para la gestión de instituciones educativas, aulas y usuarios. " +
                                "Incluye operaciones CRUD para instituciones, aulas y gestión de usuarios (directores, auxiliares, etc.)")
                        .contact(new Contact()
                                .name("Soporte Valle Grande")
                                .email("soporte@vallegrande.edu.pe")
                                .url("https://www.vallegrande.edu.pe"))
                        .license(new License()
                                .name("Apache 2.0")
                                .url("https://www.apache.org/licenses/LICENSE-2.0.html")));
    }
}
