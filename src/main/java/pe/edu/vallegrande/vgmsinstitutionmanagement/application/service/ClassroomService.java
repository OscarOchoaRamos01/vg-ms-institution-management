package pe.edu.vallegrande.vgmsinstitutionmanagement.application.service;

import pe.edu.vallegrande.vgmsinstitutionmanagement.domain.model.Classroom;
import pe.edu.vallegrande.vgmsinstitutionmanagement.infrastructure.dto.request.ClassroomCreateRequest;
import pe.edu.vallegrande.vgmsinstitutionmanagement.infrastructure.dto.request.ClassroomUpdateRequest;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface ClassroomService {
    Flux<Classroom> listAll();

    Mono<Classroom> update(String id, ClassroomUpdateRequest request);

    Mono<Classroom> searchById(String id);

    Mono<Void> delete(String id);

    Mono<Classroom> restore(String id);

    Flux<Classroom> listActive();

    Flux<Classroom> listInactive();

    Mono<Classroom> create(ClassroomCreateRequest request);

}
