package pe.edu.vallegrande.vgmsinstitutionmanagement.application.service;

import pe.edu.vallegrande.vgmsinstitutionmanagement.domain.model.Institution;
import pe.edu.vallegrande.vgmsinstitutionmanagement.infrastructure.dto.request.InstitutionCreateWithUsersRequest;
import pe.edu.vallegrande.vgmsinstitutionmanagement.infrastructure.dto.request.InstitutionUpdateRequest;
import pe.edu.vallegrande.vgmsinstitutionmanagement.infrastructure.dto.response.InstitutionCompleteResponseDto;
import pe.edu.vallegrande.vgmsinstitutionmanagement.infrastructure.dto.response.InstitutionWithUsersAndClassroomsResponseDto;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface InstitutionService {

    Flux<Institution> listAll();

    Flux<Institution> listActive();

    Flux<Institution> listInactive();

    // Método para crear institución con usuarios
    Mono<Institution> createWithUsers(InstitutionCreateWithUsersRequest request);

    // Método para listar todas las instituciones con usuarios y classrooms
    // completos
    Flux<InstitutionWithUsersAndClassroomsResponseDto> listAllWithUsersAndClassrooms();

    // Métodos para obtener instituciones con información completa de classrooms
    Flux<InstitutionCompleteResponseDto> listAllComplete();

    Flux<InstitutionCompleteResponseDto> listActiveComplete();

    Flux<InstitutionCompleteResponseDto> listInactiveComplete();

    Mono<InstitutionCompleteResponseDto> getComplete(String institutionId);

    Mono<InstitutionWithUsersAndClassroomsResponseDto> getCompleteWithUsers(String institutionId);

    Mono<Institution> update(String institutionId, InstitutionUpdateRequest request);

    Mono<Institution> deleteLogical(String institutionId);

    Mono<Institution> restore(String institutionId);

}
