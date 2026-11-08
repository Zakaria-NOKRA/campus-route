package com.campusroute.campus_route_backend.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.servers.Server;

/** General information shown at the top of the Swagger page (/swagger-ui.html). */
@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI campusRouteOpenApi() {
        return new OpenAPI()
                .info(new Info()
                        .title("Campus Route API")
                        .version("0.0.1")
                        .description("""
                                API de navigation indoor pour le campus de l'Université Sorbonne Paris Nord. \
                                Version MVP : un seul bâtiment simulé « B » (3 étages : 0 = RDC, 1 et 2, 27 salles). \
                                Elle permet de rechercher une salle, de calculer l'itinéraire le plus court \
                                (algorithme de Dijkstra, option « sans escaliers ») avec des instructions pas à pas \
                                en français, et de récupérer toutes les données utiles pour dessiner le plan."""))
                .addServersItem(new Server().url("http://localhost:8080").description("Serveur de développement local"));
    }
}
