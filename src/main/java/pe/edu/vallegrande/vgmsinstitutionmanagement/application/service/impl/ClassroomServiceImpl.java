package pe.edu.vallegrande.vgmsinstitutionmanagement.application.service.impl;

import org.springframework.stereotype.Service;
import pe.edu.vallegrande.vgmsinstitutionmanagement.domain.enums.ClassroomStatus;
import pe.edu.vallegrande.vgmsinstitutionmanagement.domain.model.Classroom;
import pe.edu.vallegrande.vgmsinstitutionmanagement.application.service.ClassroomService;
import pe.edu.vallegrande.vgmsinstitutionmanagement.infrastructure.dto.request.ClassroomCreateRequest;
import pe.edu.vallegrande.vgmsinstitutionmanagement.infrastructure.dto.request.ClassroomUpdateRequest;
import pe.edu.vallegrande.vgmsinstitutionmanagement.infrastructure.repository.ClassroomRepository;
import pe.edu.vallegrande.vgmsinstitutionmanagement.infrastructure.repository.InstitutionRepository;
import reactor.core.publisher.Flux;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.ArrayList;

@Service
@RequiredArgsConstructor
public class ClassroomServiceImpl implements ClassroomService {

    private final ClassroomRepository repository;
    private final InstitutionRepository institutionRepository;

    @Override
    public Flux<Classroom> listActive() {
        return repository.findAll()
                .filter(classroom -> classroom.getStatus() == ClassroomStatus.ACTIVE
                        && classroom.getDeletedAt() == null);
    }

    @Override
    public Flux<Classroom> listInactive() {
        return repository.findAll()
                .filter(classroom -> classroom.getStatus() == ClassroomStatus.INACTIVE
                        || classroom.getDeletedAt() != null);
    }

    @Override
    public Flux<Classroom> listAll() {
        return repository.findAll();
    }

    @Override
    public Mono<Classroom> update(String id, ClassroomUpdateRequest request) {
        return repository.findById(id)
                .filter(classroom -> classroom.getDeletedAt() == null)
                .flatMap(existing -> {
                    // NO se actualiza institutionId - se mantiene el original
                    existing.setClassroomName(request.getClassroomName());
                    existing.setClassroomAge(request.getClassroomAge());
                    existing.setCapacity(request.getCapacity());
                    existing.setColor(request.getColor());
                    existing.setUpdatedAt(LocalDateTime.now());

                    return repository.save(existing);
                })
                .switchIfEmpty(Mono.error(new RuntimeException("Classroom no encontrado o eliminado")));
    }

    @Override
    public Mono<Classroom> searchById(String id) {
        return repository.findById(id)
                .filter(classroom -> classroom.getDeletedAt() == null)
                .switchIfEmpty(Mono.error(new RuntimeException("Classroom no encontrado o eliminado")));
    }

    @Override
    public Mono<Void> delete(String id) {
        // Eliminación lógica - Solo marca como INACTIVE, NO remueve de la institución
        return repository.findById(id)
                .flatMap(classroom -> {
                    classroom.setDeletedAt(LocalDateTime.now());
                    classroom.setStatus(ClassroomStatus.INACTIVE); // Inactivo
                    classroom.setUpdatedAt(LocalDateTime.now());
                    return repository.save(classroom);
                })
                .then();
    }

    @Override
    public Mono<Classroom> restore(String id) {
        return repository.findById(id)
                .filter(classroom -> classroom.getDeletedAt() != null)
                .flatMap(classroom -> {
                    classroom.setDeletedAt(null);
                    classroom.setStatus(ClassroomStatus.ACTIVE); // Activo
                    classroom.setUpdatedAt(LocalDateTime.now());
                    return repository.save(classroom);
                })
                .switchIfEmpty(Mono.error(new RuntimeException("Classroom no encontrado o ya está activo")));
    }

    @Override
    public Mono<Classroom> create(ClassroomCreateRequest request) {
        // Convertir el request al modelo
        Classroom classroom = new Classroom();
        classroom.setInstitutionId(request.getInstitutionId());
        classroom.setClassroomName(request.getClassroomName());
        classroom.setClassroomAge(request.getClassroomAge());
        classroom.setCapacity(request.getCapacity());
        classroom.setColor(request.getColor());
        classroom.setStatus(ClassroomStatus.ACTIVE); // Establecer como activo por defecto

        // Establecer fechas automáticamente
        classroom.setCreatedAt(LocalDateTime.now());
        classroom.setUpdatedAt(LocalDateTime.now());

        // Guardar el aula en MongoDB
        return repository.save(classroom)
                .flatMap(savedClassroom ->
                // Actualizar la institución agregando el ID del aula
                institutionRepository.findById(request.getInstitutionId())
                        .flatMap(institution -> {
                            // Inicializar lista de classroomIds si es null
                            if (institution.getClassroomIds() == null) {
                                institution.setClassroomIds(new ArrayList<>());
                            }

                            // Agregar el ID del aula si no existe
                            if (!institution.getClassroomIds().contains(savedClassroom.getClassroomId())) {
                                institution.getClassroomIds().add(savedClassroom.getClassroomId());
                                institution.setUpdatedAt(LocalDateTime.now());

                                // Guardar la institución actualizada
                                return institutionRepository.save(institution)
                                        .thenReturn(savedClassroom);
                            }

                            return Mono.just(savedClassroom);
                        })
                        .switchIfEmpty(Mono.error(new RuntimeException(
                                "Institución no encontrada con ID: " + request.getInstitutionId()))));
    }

}