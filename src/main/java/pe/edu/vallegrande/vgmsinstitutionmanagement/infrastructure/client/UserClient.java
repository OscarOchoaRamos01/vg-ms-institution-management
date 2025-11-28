package pe.edu.vallegrande.vgmsinstitutionmanagement.infrastructure.client;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import pe.edu.vallegrande.vgmsinstitutionmanagement.infrastructure.client.dto.UserRequest;
import pe.edu.vallegrande.vgmsinstitutionmanagement.infrastructure.client.dto.UserResponse;
import pe.edu.vallegrande.vgmsinstitutionmanagement.infrastructure.dto.ApiResponse;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
@Slf4j
public class UserClient {

    private final WebClient userServiceWebClient;
    private final ObjectMapper objectMapper;

    /**
     * Crear un nuevo usuario en el microservicio User
     */
    public Mono<UserResponse> createUser(UserRequest request) {
        log.info("Creando usuario en microservicio User: {}", request.getEmail());

        return userServiceWebClient
                .post()
                .uri("")
                .bodyValue(request)
                .retrieve()
                .bodyToMono(ApiResponse.class)
                .map(apiResponse -> {
                    if (apiResponse.isSuccess() && apiResponse.getData() != null) {
                        // El data contiene el User completo
                        return objectMapper.convertValue(apiResponse.getData(), UserResponse.class);
                    } else {
                        throw new RuntimeException("Error al crear usuario: " + apiResponse.getMessage());
                    }
                })
                .onErrorResume(WebClientResponseException.class, ex -> {
                    log.error("Error HTTP al crear usuario: {} - {}", ex.getStatusCode(), ex.getResponseBodyAsString());
                    return Mono.error(new RuntimeException("Error al crear usuario: " + ex.getMessage()));
                })
                .onErrorResume(Exception.class, ex -> {
                    log.error("Error general al crear usuario: {}", ex.getMessage());
                    return Mono.error(new RuntimeException("Error al crear usuario: " + ex.getMessage()));
                });
    }

    /**
     * Obtener un usuario por ID desde el microservicio User
     */
    public Mono<UserResponse> getUserById(String userId) {
        log.info("Obteniendo usuario por ID desde microservicio User: {}", userId);

        return userServiceWebClient
                .get()
                .uri("/{userId}", userId)
                .retrieve()
                .bodyToMono(ApiResponse.class)
                .map(apiResponse -> {
                    if (apiResponse.isSuccess() && apiResponse.getData() != null) {
                        return objectMapper.convertValue(apiResponse.getData(), UserResponse.class);
                    } else {
                        throw new RuntimeException("Usuario no encontrado: " + apiResponse.getMessage());
                    }
                })
                .onErrorResume(WebClientResponseException.class, ex -> {
                    if (ex.getStatusCode() == HttpStatus.NOT_FOUND) {
                        log.warn("Usuario no encontrado: {}", userId);
                        return Mono.empty();
                    }
                    log.error("Error HTTP al obtener usuario: {} - {}", ex.getStatusCode(),
                            ex.getResponseBodyAsString());
                    return Mono.error(new RuntimeException("Error al obtener usuario: " + ex.getMessage()));
                })
                .onErrorResume(Exception.class, ex -> {
                    log.error("Error general al obtener usuario: {}", ex.getMessage());
                    return Mono.error(new RuntimeException("Error al obtener usuario: " + ex.getMessage()));
                });
    }

    /**
     * Actualizar un usuario en el microservicio User
     */
    public Mono<UserResponse> updateUser(String userId, UserRequest request) {
        log.info("Actualizando usuario en microservicio User: {}", userId);

        return userServiceWebClient
                .put()
                .uri("/{userId}", userId)
                .bodyValue(request)
                .retrieve()
                .bodyToMono(ApiResponse.class)
                .map(apiResponse -> {
                    if (apiResponse.isSuccess() && apiResponse.getData() != null) {
                        return objectMapper.convertValue(apiResponse.getData(), UserResponse.class);
                    } else {
                        throw new RuntimeException("Error al actualizar usuario: " + apiResponse.getMessage());
                    }
                })
                .onErrorResume(WebClientResponseException.class, ex -> {
                    if (ex.getStatusCode() == HttpStatus.NOT_FOUND) {
                        log.warn("Usuario no encontrado para actualizar: {}", userId);
                        return Mono.empty();
                    }
                    log.error("Error HTTP al actualizar usuario: {} - {}", ex.getStatusCode(),
                            ex.getResponseBodyAsString());
                    return Mono.error(new RuntimeException("Error al actualizar usuario: " + ex.getMessage()));
                })
                .onErrorResume(Exception.class, ex -> {
                    log.error("Error general al actualizar usuario: {}", ex.getMessage());
                    return Mono.error(new RuntimeException("Error al actualizar usuario: " + ex.getMessage()));
                });
    }

    /**
     * Eliminar un usuario lógicamente en el microservicio User
     */
    public Mono<UserResponse> deleteUser(String userId) {
        log.info("Eliminando usuario lógicamente en microservicio User: {}", userId);

        return userServiceWebClient
                .delete()
                .uri("/{userId}", userId)
                .retrieve()
                .bodyToMono(ApiResponse.class)
                .map(apiResponse -> {
                    if (apiResponse.isSuccess() && apiResponse.getData() != null) {
                        return objectMapper.convertValue(apiResponse.getData(), UserResponse.class);
                    } else {
                        throw new RuntimeException("Error al eliminar usuario: " + apiResponse.getMessage());
                    }
                })
                .onErrorResume(WebClientResponseException.class, ex -> {
                    if (ex.getStatusCode() == HttpStatus.NOT_FOUND) {
                        log.warn("Usuario no encontrado para eliminar: {}", userId);
                        return Mono.empty();
                    }
                    log.error("Error HTTP al eliminar usuario: {} - {}", ex.getStatusCode(),
                            ex.getResponseBodyAsString());
                    return Mono.error(new RuntimeException("Error al eliminar usuario: " + ex.getMessage()));
                })
                .onErrorResume(Exception.class, ex -> {
                    log.error("Error general al eliminar usuario: {}", ex.getMessage());
                    return Mono.error(new RuntimeException("Error al eliminar usuario: " + ex.getMessage()));
                });
    }

    /**
     * Restaurar un usuario en el microservicio User
     */
    public Mono<UserResponse> restoreUser(String userId) {
        log.info("Restaurando usuario en microservicio User: {}", userId);

        return userServiceWebClient
                .patch()
                .uri("/{userId}/restore", userId)
                .retrieve()
                .bodyToMono(ApiResponse.class)
                .map(apiResponse -> {
                    if (apiResponse.isSuccess() && apiResponse.getData() != null) {
                        return objectMapper.convertValue(apiResponse.getData(), UserResponse.class);
                    } else {
                        throw new RuntimeException("Error al restaurar usuario: " + apiResponse.getMessage());
                    }
                })
                .onErrorResume(WebClientResponseException.class, ex -> {
                    if (ex.getStatusCode() == HttpStatus.NOT_FOUND) {
                        log.warn("Usuario no encontrado para restaurar: {}", userId);
                        return Mono.empty();
                    }
                    log.error("Error HTTP al restaurar usuario: {} - {}", ex.getStatusCode(),
                            ex.getResponseBodyAsString());
                    return Mono.error(new RuntimeException("Error al restaurar usuario: " + ex.getMessage()));
                })
                .onErrorResume(Exception.class, ex -> {
                    log.error("Error general al restaurar usuario: {}", ex.getMessage());
                    return Mono.error(new RuntimeException("Error al restaurar usuario: " + ex.getMessage()));
                });
    }
}
