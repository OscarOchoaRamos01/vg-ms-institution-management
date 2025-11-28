package pe.edu.vallegrande.vgmsinstitutionmanagement.application.service.impl;

import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import pe.edu.vallegrande.vgmsinstitutionmanagement.application.service.InstitutionService;
import pe.edu.vallegrande.vgmsinstitutionmanagement.infrastructure.client.UserClient;
import pe.edu.vallegrande.vgmsinstitutionmanagement.infrastructure.client.dto.UserRequest;
import pe.edu.vallegrande.vgmsinstitutionmanagement.infrastructure.client.dto.UserResponse;
import pe.edu.vallegrande.vgmsinstitutionmanagement.domain.enums.ClassroomStatus;
import pe.edu.vallegrande.vgmsinstitutionmanagement.domain.enums.InstitutionStatus;
import pe.edu.vallegrande.vgmsinstitutionmanagement.domain.model.*;
import pe.edu.vallegrande.vgmsinstitutionmanagement.infrastructure.dto.request.InstitutionCreateWithUsersRequest;
import pe.edu.vallegrande.vgmsinstitutionmanagement.infrastructure.dto.request.InstitutionUpdateRequest;
import pe.edu.vallegrande.vgmsinstitutionmanagement.infrastructure.dto.response.InstitutionWithUsersAndClassroomsResponseDto;
import pe.edu.vallegrande.vgmsinstitutionmanagement.infrastructure.dto.response.InstitutionCompleteResponseDto;
import pe.edu.vallegrande.vgmsinstitutionmanagement.infrastructure.repository.ClassroomRepository;
import pe.edu.vallegrande.vgmsinstitutionmanagement.infrastructure.repository.InstitutionRepository;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Flux;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class InstitutionServiceImpl implements InstitutionService {

    private final InstitutionRepository repository;
    private final ClassroomRepository classroomRepository;
    private final UserClient userClient;

    @Override
    public Flux<Institution> listAll() {
        return repository.findAll();
    }

    @Override
    public Flux<Institution> listActive() {
        return repository.findAll()
                .filter(institution -> institution.getStatus() == InstitutionStatus.ACTIVE
                        && institution.getDeletedAt() == null);
    }

    @Override
    public Flux<Institution> listInactive() {
        return repository.findAll()
                .filter(institution -> institution.getStatus() == InstitutionStatus.INACTIVE
                        || institution.getDeletedAt() != null);
    }

    @Override
    public Mono<Institution> createWithUsers(InstitutionCreateWithUsersRequest request) {
        // 1. Crear primero la institución sin usuarios
        Institution institution = new Institution();
        setBasicInstitutionData(institution,
                request.getInstitutionInformation(),
                request.getAddress(),
                request.getContactMethods(),
                request.getSchedules(),
                request.getGradingType(),
                request.getClassroomType(),
                request.getUgel(),
                request.getDre());

        return repository.save(institution)
                .flatMap(savedInstitution -> {
                    // 2. Crear el director usando UserClient con el institutionId
                    UserRequest directorRequest = convertToUserRequest(request.getDirector(),
                            savedInstitution.getInstitutionId());
                    return userClient.createUser(directorRequest)
                            .flatMap(directorResponse -> {
                                // 3. Actualizar la institución con el ID del director
                                // Los auxiliares NO se crean aquí, el microservicio de usuarios
                                // se encargará de crearlos y agregarlos posteriormente
                                savedInstitution.setDirectorId(directorResponse.getUserId());
                                savedInstitution.setAuxiliaryIds(new ArrayList<>()); // Inicializar vacío
                                return repository.save(savedInstitution)
                                        .flatMap(updatedInstitution -> createClassroomsAndUpdateInstitution(
                                                updatedInstitution, request.getClassrooms()));
                            });
                });
    }

    @Override
    public Flux<InstitutionCompleteResponseDto> listAllComplete() {
        return repository.findAll()
                .flatMap(this::convertToCompleteDto);
    }

    @Override
    public Flux<InstitutionCompleteResponseDto> listActiveComplete() {
        return repository.findAll()
                .filter(institution -> institution.getStatus() == InstitutionStatus.ACTIVE
                        && institution.getDeletedAt() == null)
                .flatMap(this::convertToCompleteDto);
    }

    @Override
    public Flux<InstitutionCompleteResponseDto> listInactiveComplete() {
        return repository.findAll()
                .filter(institution -> institution.getStatus() == InstitutionStatus.INACTIVE
                        || institution.getDeletedAt() != null)
                .flatMap(this::convertToCompleteDto);
    }

    @Override
    public Mono<InstitutionCompleteResponseDto> getComplete(String institutionId) {
        return repository.findById(institutionId)
                .flatMap(this::convertToCompleteDto);
    }

    private Mono<InstitutionCompleteResponseDto> convertToCompleteDto(Institution institution) {
        InstitutionCompleteResponseDto response = new InstitutionCompleteResponseDto();

        // Copiar datos básicos de la institución
        response.setInstitutionId(institution.getInstitutionId());
        response.setInstitutionInformation(institution.getInstitutionInformation());
        response.setAddress(institution.getAddress());
        response.setContactMethods(institution.getContactMethods());
        response.setGradingType(institution.getGradingType());
        response.setClassroomType(institution.getClassroomType());
        response.setSchedules(institution.getSchedules());
        response.setDirectorId(institution.getDirectorId());
        response.setAuxiliaryIds(institution.getAuxiliaryIds());
        response.setUgel(institution.getUgel());
        response.setDre(institution.getDre());
        response.setStatus(institution.getStatus());
        response.setCreatedAt(institution.getCreatedAt());
        response.setUpdatedAt(institution.getUpdatedAt());
        response.setDeletedAt(institution.getDeletedAt());

        // Obtener información completa de los classrooms
        if (institution.getClassroomIds() != null && !institution.getClassroomIds().isEmpty()) {
            return classroomRepository.findAllById(institution.getClassroomIds())
                    .collectList()
                    .map(classrooms -> {
                        response.setClassrooms(classrooms);
                        return response;
                    });
        } else {
            response.setClassrooms(new ArrayList<>());
            return Mono.just(response);
        }
    }

    @Override
    public Flux<InstitutionWithUsersAndClassroomsResponseDto> listAllWithUsersAndClassrooms() {
        return repository.findAll()
                .flatMap(institution -> {
                    InstitutionWithUsersAndClassroomsResponseDto response = new InstitutionWithUsersAndClassroomsResponseDto();

                    // Copiar datos básicos de la institución
                    response.setInstitutionId(institution.getInstitutionId());
                    response.setInstitutionInformation(institution.getInstitutionInformation());
                    response.setAddress(institution.getAddress());
                    response.setContactMethods(institution.getContactMethods());
                    response.setGradingType(institution.getGradingType());
                    response.setClassroomType(institution.getClassroomType());
                    response.setSchedules(institution.getSchedules());
                    response.setUgel(institution.getUgel());
                    response.setDre(institution.getDre());
                    response.setStatus(institution.getStatus());
                    response.setCreatedAt(institution.getCreatedAt());
                    response.setUpdatedAt(institution.getUpdatedAt());
                    response.setDeletedAt(institution.getDeletedAt());

                    // Obtener información completa de los classrooms
                    Mono<List<Classroom>> classroomsMono;
                    if (institution.getClassroomIds() != null && !institution.getClassroomIds().isEmpty()) {
                        classroomsMono = Flux.fromIterable(institution.getClassroomIds())
                                .flatMap(classroomId -> classroomRepository.findById(classroomId))
                                .collectList();
                    } else {
                        classroomsMono = Mono.just(new ArrayList<>());
                    }

                    // Obtener información del director
                    Mono<pe.edu.vallegrande.vgmsinstitutionmanagement.infrastructure.dto.response.UserResponse> directorMono = userClient
                            .getUserById(institution.getDirectorId())
                            .map(this::convertToResponseUserResponse);

                    // Obtener información de auxiliares
                    Mono<List<pe.edu.vallegrande.vgmsinstitutionmanagement.infrastructure.dto.response.UserResponse>> auxiliariesMono;
                    if (institution.getAuxiliaryIds() != null && !institution.getAuxiliaryIds().isEmpty()) {
                        auxiliariesMono = Flux.fromIterable(institution.getAuxiliaryIds())
                                .flatMap(auxiliaryId -> userClient.getUserById(auxiliaryId))
                                .map(this::convertToResponseUserResponse)
                                .collectList();
                    } else {
                        auxiliariesMono = Mono.just(new ArrayList<>());
                    }

                    return Mono.zip(classroomsMono, directorMono, auxiliariesMono)
                            .map(tuple -> {
                                response.setClassrooms(tuple.getT1());
                                response.setDirector(tuple.getT2());
                                response.setAuxiliaries(tuple.getT3());
                                return response;
                            });
                });
    }

    @Override
    public Mono<InstitutionWithUsersAndClassroomsResponseDto> getCompleteWithUsers(String institutionId) {
        return repository.findById(institutionId)
                .flatMap(institution -> {
                    InstitutionWithUsersAndClassroomsResponseDto response = new InstitutionWithUsersAndClassroomsResponseDto();

                    // Copiar datos básicos de la institución
                    response.setInstitutionId(institution.getInstitutionId());
                    response.setInstitutionInformation(institution.getInstitutionInformation());
                    response.setAddress(institution.getAddress());
                    response.setContactMethods(institution.getContactMethods());
                    response.setGradingType(institution.getGradingType());
                    response.setClassroomType(institution.getClassroomType());
                    response.setSchedules(institution.getSchedules());
                    response.setUgel(institution.getUgel());
                    response.setDre(institution.getDre());
                    response.setStatus(institution.getStatus());
                    response.setCreatedAt(institution.getCreatedAt());
                    response.setUpdatedAt(institution.getUpdatedAt());
                    response.setDeletedAt(institution.getDeletedAt());

                    // Obtener información completa de los classrooms
                    Mono<List<Classroom>> classroomsMono;
                    if (institution.getClassroomIds() != null && !institution.getClassroomIds().isEmpty()) {
                        classroomsMono = Flux.fromIterable(institution.getClassroomIds())
                                .flatMap(classroomId -> classroomRepository.findById(classroomId))
                                .collectList();
                    } else {
                        classroomsMono = Mono.just(new ArrayList<>());
                    }

                    // Obtener información del director
                    Mono<pe.edu.vallegrande.vgmsinstitutionmanagement.infrastructure.dto.response.UserResponse> directorMono = userClient
                            .getUserById(institution.getDirectorId())
                            .map(this::convertToResponseUserResponse);

                    // Obtener información de auxiliares
                    Mono<List<pe.edu.vallegrande.vgmsinstitutionmanagement.infrastructure.dto.response.UserResponse>> auxiliariesMono;
                    if (institution.getAuxiliaryIds() != null && !institution.getAuxiliaryIds().isEmpty()) {
                        auxiliariesMono = Flux.fromIterable(institution.getAuxiliaryIds())
                                .flatMap(auxiliaryId -> userClient.getUserById(auxiliaryId))
                                .map(this::convertToResponseUserResponse)
                                .collectList();
                    } else {
                        auxiliariesMono = Mono.just(new ArrayList<>());
                    }

                    return Mono.zip(classroomsMono, directorMono, auxiliariesMono)
                            .map(tuple -> {
                                response.setClassrooms(tuple.getT1());
                                response.setDirector(tuple.getT2());
                                response.setAuxiliaries(tuple.getT3());
                                return response;
                            });
                });
    }

    // Métodos helper para reducir duplicación de código
    private InstitutionInformation mapToInstitutionInformation(
            pe.edu.vallegrande.vgmsinstitutionmanagement.infrastructure.dto.request.InstitutionInformationDto dto) {
        InstitutionInformation info = new InstitutionInformation();
        info.setInstitutionName(dto.getInstitutionName());
        info.setCodeInstitution(dto.getCodeInstitution());
        info.setModularCode(dto.getModularCode());
        info.setInstitutionType(dto.getInstitutionType());
        info.setInstitutionLevel(dto.getInstitutionLevel());
        info.setGender(dto.getGender());
        info.setSlogan(dto.getSlogan());
        info.setLogoUrl(dto.getLogoUrl());
        return info;
    }

    private Address mapToAddress(
            pe.edu.vallegrande.vgmsinstitutionmanagement.infrastructure.dto.request.AddressDto dto) {
        Address address = new Address();
        address.setStreet(dto.getStreet());
        address.setDistrict(dto.getDistrict());
        address.setProvince(dto.getProvince());
        address.setDepartment(dto.getDepartment());
        address.setPostalCode(dto.getPostalCode());
        return address;
    }

    private List<ContactMethod> mapToContactMethods(
            List<pe.edu.vallegrande.vgmsinstitutionmanagement.infrastructure.dto.request.ContactMethodDto> dtos) {
        if (dtos == null)
            return new ArrayList<>();
        return dtos.stream()
                .map(dto -> {
                    ContactMethod cm = new ContactMethod();
                    cm.setType(dto.getType());
                    cm.setValue(dto.getValue());
                    return cm;
                })
                .collect(Collectors.toList());
    }

    private List<Schedule> mapToSchedules(
            List<pe.edu.vallegrande.vgmsinstitutionmanagement.infrastructure.dto.request.ScheduleDto> dtos) {
        if (dtos == null)
            return new ArrayList<>();
        return dtos.stream()
                .map(dto -> {
                    Schedule s = new Schedule();
                    s.setType(dto.getType());
                    s.setEntryTime(dto.getEntryTime());
                    s.setExitTime(dto.getExitTime());
                    return s;
                })
                .collect(Collectors.toList());
    }

    private void setBasicInstitutionData(Institution institution,
            pe.edu.vallegrande.vgmsinstitutionmanagement.infrastructure.dto.request.InstitutionInformationDto institutionInfo,
            pe.edu.vallegrande.vgmsinstitutionmanagement.infrastructure.dto.request.AddressDto addressDto,
            List<pe.edu.vallegrande.vgmsinstitutionmanagement.infrastructure.dto.request.ContactMethodDto> contactMethods,
            List<pe.edu.vallegrande.vgmsinstitutionmanagement.infrastructure.dto.request.ScheduleDto> schedules,
            String gradingType, String classroomType, String ugel, String dre) {
        institution.setInstitutionInformation(mapToInstitutionInformation(institutionInfo));
        institution.setAddress(mapToAddress(addressDto));
        institution.setContactMethods(mapToContactMethods(contactMethods));
        institution.setSchedules(mapToSchedules(schedules));
        institution.setGradingType(gradingType);
        institution.setClassroomType(classroomType);
        institution.setUgel(ugel);
        institution.setDre(dre);
        institution.setStatus(InstitutionStatus.ACTIVE);
        institution.setCreatedAt(LocalDateTime.now());
        institution.setUpdatedAt(LocalDateTime.now());
        institution.setClassroomIds(new ArrayList<>());
    }

    private Mono<Institution> createClassroomsAndUpdateInstitution(Institution savedInstitution,
            List<pe.edu.vallegrande.vgmsinstitutionmanagement.infrastructure.dto.request.ClassroomCreateDto> classroomDtos) {
        if (classroomDtos == null || classroomDtos.isEmpty()) {
            return Mono.just(savedInstitution);
        }

        List<Mono<Classroom>> classroomMonos = classroomDtos.stream()
                .map(dto -> {
                    Classroom classroom = new Classroom();
                    classroom.setInstitutionId(savedInstitution.getInstitutionId());
                    classroom.setClassroomName(dto.getClassroomName());
                    classroom.setClassroomAge(dto.getClassroomAge());
                    classroom.setCapacity(dto.getCapacity());
                    classroom.setColor(dto.getColor());
                    classroom.setStatus(ClassroomStatus.ACTIVE);
                    classroom.setCreatedAt(LocalDateTime.now());
                    classroom.setUpdatedAt(LocalDateTime.now());
                    return classroomRepository.save(classroom);
                })
                .collect(Collectors.toList());

        return Flux.concat(classroomMonos)
                .collectList()
                .flatMap(classrooms -> {
                    List<String> classroomIds = classrooms.stream()
                            .map(Classroom::getClassroomId)
                            .collect(Collectors.toList());
                    savedInstitution.setClassroomIds(classroomIds);
                    return repository.save(savedInstitution);
                });
    }

    @Override
    public Mono<Institution> update(String institutionId, InstitutionUpdateRequest request) {
        return repository.findById(institutionId)
                .switchIfEmpty(Mono.error(new RuntimeException("Institución no encontrada")))
                .flatMap(existingInstitution -> {
                    String oldDirectorId = existingInstitution.getDirectorId();
                    String newDirectorId = request.getDirectorId();

                    // Actualizar información básica de la institución
                    existingInstitution.setInstitutionInformation(
                            mapToInstitutionInformation(request.getInstitutionInformation()));
                    existingInstitution.setAddress(mapToAddress(request.getAddress()));
                    existingInstitution.setContactMethods(mapToContactMethods(request.getContactMethods()));
                    existingInstitution.setGradingType(request.getGradingType());
                    existingInstitution.setClassroomType(request.getClassroomType());
                    existingInstitution.setSchedules(mapToSchedules(request.getSchedules()));

                    // Actualizar información de usuarios
                    existingInstitution.setDirectorId(newDirectorId);
                    existingInstitution.setAuxiliaryIds(
                            request.getAuxiliaryIds() != null ? request.getAuxiliaryIds() : new ArrayList<>());

                    // Actualizar información administrativa
                    existingInstitution.setUgel(request.getUgel());
                    existingInstitution.setDre(request.getDre());
                    existingInstitution.setUpdatedAt(LocalDateTime.now());

                    // Si el director cambió, actualizar los institutionId en el microservicio de
                    // usuarios
                    if (oldDirectorId != null && !oldDirectorId.equals(newDirectorId)) {
                        // 1. Desvincular al director anterior
                        Mono<UserResponse> unlinkOldDirector = userClient.getUserById(oldDirectorId)
                                .flatMap(oldDirector -> {
                                    UserRequest updateRequest = UserRequest.builder()
                                            .institutionId("") // Quitar la institución
                                            .firstName(oldDirector.getFirstName())
                                            .lastName(oldDirector.getLastName())
                                            .documentType(oldDirector.getDocumentType())
                                            .documentNumber(oldDirector.getDocumentNumber())
                                            .phone(oldDirector.getPhone())
                                            .email(oldDirector.getEmail())
                                            .role(oldDirector.getRole())
                                            .status(oldDirector.getStatus())
                                            .build();
                                    return userClient.updateUser(oldDirectorId, updateRequest);
                                })
                                .doOnSuccess(updated -> log.info("Director anterior desvinculado: {}", oldDirectorId))
                                .onErrorResume(error -> {
                                    log.error("Error al desvincular director anterior: {}", error.getMessage());
                                    return Mono.empty(); // Continuar aunque falle
                                });

                        // 2. Vincular al nuevo director
                        Mono<UserResponse> linkNewDirector = userClient.getUserById(newDirectorId)
                                .flatMap(newDirector -> {
                                    UserRequest updateRequest = UserRequest.builder()
                                            .institutionId(institutionId) // Asignar la institución
                                            .firstName(newDirector.getFirstName())
                                            .lastName(newDirector.getLastName())
                                            .documentType(newDirector.getDocumentType())
                                            .documentNumber(newDirector.getDocumentNumber())
                                            .phone(newDirector.getPhone())
                                            .email(newDirector.getEmail())
                                            .role(newDirector.getRole())
                                            .status(newDirector.getStatus())
                                            .build();
                                    return userClient.updateUser(newDirectorId, updateRequest);
                                })
                                .doOnSuccess(updated -> log.info("Nuevo director vinculado: {}", newDirectorId))
                                .onErrorResume(error -> {
                                    log.error("Error al vincular nuevo director: {}", error.getMessage());
                                    return Mono.empty(); // Continuar aunque falle
                                });

                        // Ejecutar ambas operaciones en paralelo y luego guardar la institución
                        return Mono.zip(unlinkOldDirector, linkNewDirector)
                                .then(repository.save(existingInstitution))
                                .doOnSuccess(
                                        saved -> log.info("Institución actualizada con cambio de director exitoso"));
                    }

                    // Si no cambió el director, solo guardar la institución
                    return repository.save(existingInstitution);
                });
    }

    @Override
    public Mono<Institution> deleteLogical(String institutionId) {
        return repository.findById(institutionId)
                .switchIfEmpty(Mono.error(new RuntimeException("Institución no encontrada")))
                .flatMap(institution -> {
                    if (institution.getDeletedAt() != null) {
                        return Mono.error(new RuntimeException("La institución ya está eliminada"));
                    }

                    // Marcar como eliminada lógicamente
                    institution.setStatus(InstitutionStatus.INACTIVE);
                    institution.setDeletedAt(LocalDateTime.now());
                    institution.setUpdatedAt(LocalDateTime.now());

                    return repository.save(institution);
                });
    }

    @Override
    public Mono<Institution> restore(String institutionId) {
        return repository.findById(institutionId)
                .switchIfEmpty(Mono.error(new RuntimeException("Institución no encontrada")))
                .flatMap(institution -> {
                    if (institution.getDeletedAt() == null) {
                        return Mono.error(new RuntimeException("La institución no está eliminada"));
                    }

                    // Restaurar la institución
                    institution.setStatus(InstitutionStatus.ACTIVE);
                    institution.setDeletedAt(null);
                    institution.setUpdatedAt(LocalDateTime.now());

                    return repository.save(institution);
                });
    }

    /**
     * Convierte UserCreateRequest a UserRequest para el microservicio User
     */
    private UserRequest convertToUserRequest(
            pe.edu.vallegrande.vgmsinstitutionmanagement.infrastructure.dto.request.UserCreateRequest request,
            String institutionId) {
        return UserRequest.builder()
                .institutionId(institutionId)
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .documentType(request.getDocumentType())
                .documentNumber(request.getDocumentNumber())
                .phone(request.getPhone())
                .email(request.getEmail())
                .role(request.getRole())
                .status("ACTIVE") // Activo por defecto
                .build();
    }

    /**
     * Convierte UserResponse del cliente a UserResponse del DTO de respuesta
     */
    private pe.edu.vallegrande.vgmsinstitutionmanagement.infrastructure.dto.response.UserResponse convertToResponseUserResponse(
            pe.edu.vallegrande.vgmsinstitutionmanagement.infrastructure.client.dto.UserResponse clientResponse) {
        pe.edu.vallegrande.vgmsinstitutionmanagement.infrastructure.dto.response.UserResponse response = new pe.edu.vallegrande.vgmsinstitutionmanagement.infrastructure.dto.response.UserResponse();
        response.setUserId(clientResponse.getUserId());
        response.setFirstName(clientResponse.getFirstName());
        response.setLastName(clientResponse.getLastName());
        response.setDocumentType(clientResponse.getDocumentType());
        response.setDocumentNumber(clientResponse.getDocumentNumber());
        response.setPhone(clientResponse.getPhone());
        response.setEmail(clientResponse.getEmail());
        response.setRole(clientResponse.getRole());
        response.setStatus(clientResponse.getStatus().charAt(0));
        response.setCreatedAt(clientResponse.getCreatedAt());
        response.setUpdatedAt(clientResponse.getUpdatedAt());
        response.setDeletedAt(clientResponse.getDeletedAt());
        return response;
    }
}
