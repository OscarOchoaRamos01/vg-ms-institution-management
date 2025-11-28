package pe.edu.vallegrande.vgmsinstitutionmanagement.infrastructure.dto.request;

import lombok.Data;

import java.util.List;

@Data
public class InstitutionCreateWithUsersRequest {
    private InstitutionInformationDto institutionInformation;
    private AddressDto address;
    private List<ContactMethodDto> contactMethods;
    private String gradingType;
    private String classroomType;
    private List<ScheduleDto> schedules;
    private List<ClassroomCreateDto> classrooms;

    // Datos del director
    private UserCreateRequest director;

    // Datos de los auxiliares
    private List<UserCreateRequest> auxiliaries;

    private String ugel;
    private String dre;
}
