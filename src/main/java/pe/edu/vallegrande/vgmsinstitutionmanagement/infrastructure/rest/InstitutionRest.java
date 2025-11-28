package pe.edu.vallegrande.vgmsinstitutionmanagement.infrastructure.rest;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import pe.edu.vallegrande.vgmsinstitutionmanagement.application.service.InstitutionService;
import pe.edu.vallegrande.vgmsinstitutionmanagement.domain.model.Institution;
import pe.edu.vallegrande.vgmsinstitutionmanagement.infrastructure.dto.ApiResponse;
import pe.edu.vallegrande.vgmsinstitutionmanagement.infrastructure.dto.request.InstitutionCreateWithUsersRequest;
import pe.edu.vallegrande.vgmsinstitutionmanagement.infrastructure.dto.request.InstitutionUpdateRequest;
import pe.edu.vallegrande.vgmsinstitutionmanagement.infrastructure.dto.response.InstitutionCompleteResponseDto;
import pe.edu.vallegrande.vgmsinstitutionmanagement.infrastructure.dto.response.InstitutionWithUsersAndClassroomsResponseDto;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import java.util.List;

@RestController
@RequestMapping("/api/v1/institutions")
@RequiredArgsConstructor
@Tag(name = "Instituciones", description = "API para gestión de instituciones educativas")
public class InstitutionRest {

        private final InstitutionService service;

        @GetMapping
        @Operation(summary = "Listar todas las instituciones", description = "Obtiene todas las instituciones con información completa de aulas")
        public Mono<ApiResponse<List<InstitutionCompleteResponseDto>>> listarTodos() {
                return service.listAllComplete().collectList()
                                .map(list -> ApiResponse.success("Instituciones obtenidas correctamente.", list));
        }

        @GetMapping("/active")
        @Operation(summary = "Listar instituciones activas", description = "Obtiene todas las instituciones con estado activo")
        public Mono<ApiResponse<List<InstitutionCompleteResponseDto>>> listarActivos() {
                return service.listActiveComplete().collectList()
                                .map(list -> ApiResponse.success("Instituciones activas obtenidas correctamente.",
                                                list));
        }

        @GetMapping("/inactive")
        @Operation(summary = "Listar instituciones inactivas", description = "Obtiene todas las instituciones con estado inactivo o eliminadas")
        public Mono<ApiResponse<List<InstitutionCompleteResponseDto>>> listarInactivos() {
                return service.listInactiveComplete().collectList()
                                .map(list -> ApiResponse.success("Instituciones inactivas obtenidas correctamente.",
                                                list));
        }

        @GetMapping("/{id}")
        @Operation(summary = "Obtener institución por ID", description = "Obtiene una institución específica con información completa de aulas, director y auxiliares")
        @ApiResponses({
                        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Institución encontrada"),
                        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Institución no encontrada")
        })
        public Mono<ApiResponse<InstitutionWithUsersAndClassroomsResponseDto>> obtenerPorId(
                        @Parameter(description = "ID de la institución") @PathVariable String id) {
                return service.getCompleteWithUsers(id)
                                .map(institution -> ApiResponse.success("Institución obtenida correctamente.",
                                                institution));
        }

        @PostMapping("/with-users")
        @ResponseStatus(HttpStatus.CREATED)
        @Operation(summary = "Crear institución con usuarios", description = "Crea una institución y registra automáticamente director y auxiliares")
        @ApiResponses({
                        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "Institución y usuarios creados exitosamente"),
                        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Datos de entrada inválidos")
        })
        public Mono<ApiResponse<Institution>> crearConUsuarios(
                        @Valid @RequestBody InstitutionCreateWithUsersRequest request) {
                return service.createWithUsers(request)
                                .map(institution -> ApiResponse.success("Institución creada correctamente.",
                                                institution));
        }

        @GetMapping("/with-users-classrooms")
        @Operation(summary = "Listar instituciones completas", description = "Obtiene todas las instituciones con información completa de usuarios (director y auxiliares) y aulas")
        @ApiResponses({
                        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Lista obtenida exitosamente")
        })
        public Mono<ApiResponse<List<InstitutionWithUsersAndClassroomsResponseDto>>> listarTodosConUsuariosYClassrooms() {
                return service.listAllWithUsersAndClassrooms().collectList()
                                .map(list -> ApiResponse.success("Instituciones obtenidas correctamente.", list));
        }

        @PutMapping("/{id}")
        @Operation(summary = "Actualizar institución", description = "Actualiza la información completa de una institución existente")
        @ApiResponses({
                        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Institución actualizada exitosamente"),
                        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Datos de entrada inválidos"),
                        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Institución no encontrada")
        })
        public Mono<ApiResponse<Institution>> actualizar(
                        @Parameter(description = "ID de la institución a actualizar") @PathVariable String id,
                        @Valid @RequestBody InstitutionUpdateRequest request) {
                return service.update(id, request)
                                .map(institution -> ApiResponse.success("Institución actualizada correctamente.",
                                                institution));
        }

        @DeleteMapping("/{id}")
        @Operation(summary = "Eliminar institución (lógico)", description = "Elimina lógicamente una institución marcándola como inactiva y estableciendo fecha de eliminación")
        @ApiResponses({
                        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Institución eliminada lógicamente"),
                        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "La institución ya está eliminada"),
                        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Institución no encontrada")
        })
        public Mono<ApiResponse<Void>> eliminarLogico(
                        @Parameter(description = "ID de la institución a eliminar") @PathVariable String id) {
                return service.deleteLogical(id)
                                .then(Mono.just(ApiResponse.success("Institución eliminada correctamente.", null)));
        }

        @PutMapping("/{id}/restore")
        @Operation(summary = "Restaurar institución", description = "Restaura una institución previamente eliminada lógicamente")
        @ApiResponses({
                        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Institución restaurada exitosamente"),
                        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "La institución no está eliminada"),
                        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Institución no encontrada")
        })
        public Mono<ApiResponse<Institution>> restaurar(
                        @Parameter(description = "ID de la institución a restaurar") @PathVariable String id) {
                return service.restore(id)
                                .map(institution -> ApiResponse.success("Institución restaurada correctamente.",
                                                institution));
        }
}
