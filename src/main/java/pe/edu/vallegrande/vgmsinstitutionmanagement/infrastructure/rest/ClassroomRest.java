package pe.edu.vallegrande.vgmsinstitutionmanagement.infrastructure.rest;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import pe.edu.vallegrande.vgmsinstitutionmanagement.application.service.ClassroomService;
import pe.edu.vallegrande.vgmsinstitutionmanagement.domain.model.Classroom;
import pe.edu.vallegrande.vgmsinstitutionmanagement.infrastructure.dto.ApiResponse;
import pe.edu.vallegrande.vgmsinstitutionmanagement.infrastructure.dto.request.ClassroomCreateRequest;
import pe.edu.vallegrande.vgmsinstitutionmanagement.infrastructure.dto.request.ClassroomUpdateRequest;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import jakarta.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/v1/classrooms")
@RequiredArgsConstructor
public class ClassroomRest {

    private final ClassroomService service;

    @GetMapping("/active")
    @Operation(summary = "Listar aulas activas", description = "Obtiene todas las aulas con estado activo")
    public Mono<ApiResponse<List<Classroom>>> listActive() {
        return service.listActive().collectList()
                .map(list -> ApiResponse.success("Aulas activas obtenidas correctamente.", list));
    }

    @GetMapping("/inactive")
    @Operation(summary = "Listar aulas inactivas", description = "Obtiene todas las aulas con estado inactivo")
    public Mono<ApiResponse<List<Classroom>>> listInactive() {
        return service.listInactive().collectList()
                .map(list -> ApiResponse.success("Aulas inactivas obtenidas correctamente.", list));
    }

    @GetMapping
    @Operation(summary = "Listar todas las aulas", description = "Obtiene todas las aulas con información completa")
    public Mono<ApiResponse<List<Classroom>>> listAll() {
        return service.listAll().collectList()
                .map(list -> ApiResponse.success("Aulas obtenidas correctamente.", list));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Listar aula por ID", description = "Obtiene una aula específica por su ID")
    public Mono<ApiResponse<Classroom>> searchById(@PathVariable String id) {
        return service.searchById(id)
                .map(classroom -> ApiResponse.success("Aula obtenida correctamente.", classroom));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar aula", description = "Actualiza la información de una aula existente")
    public Mono<ApiResponse<Classroom>> update(
            @PathVariable String id,
            @Valid @RequestBody ClassroomUpdateRequest request) {
        return service.update(id, request)
                .map(classroom -> ApiResponse.success("Aula actualizada correctamente.", classroom));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar aula", description = "Elimina una aula")
    @ResponseStatus(HttpStatus.OK)
    public Mono<ApiResponse<Void>> delete(@PathVariable String id) {
        return service.delete(id)
                .then(Mono.just(ApiResponse.success("Aula eliminada correctamente.", null)));
    }

    @PatchMapping("/{id}/restore")
    @Operation(summary = "Restaurar aula", description = "Restaura una aula previamente eliminada")
    public Mono<ApiResponse<Classroom>> restore(@PathVariable String id) {
        return service.restore(id)
                .map(classroom -> ApiResponse.success("Aula restaurada correctamente.", classroom));
    }

    @PostMapping
    @Operation(summary = "Crear aula", description = "Crea una nueva aula")
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<ApiResponse<Classroom>> create(@Valid @RequestBody ClassroomCreateRequest request) {
        return service.create(request)
                .map(classroom -> ApiResponse.success("Aula creada correctamente.", classroom));
    }
}
