package pe.edu.vallegrande.vgmsinstitutionmanagement.infrastructure.dto.response;

import lombok.Data;
import pe.edu.vallegrande.vgmsinstitutionmanagement.domain.enums.InstitutionStatus;
import pe.edu.vallegrande.vgmsinstitutionmanagement.domain.model.*;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class InstitutionWithUsersAndClassroomsResponseDto {
    private String institutionId;
    private InstitutionInformation institutionInformation;
    private Address address;
    private List<ContactMethod> contactMethods;
    private String gradingType;
    private String classroomType;
    private List<Schedule> schedules;
    // Aquí traemos la información completa de los classrooms
    private List<Classroom> classrooms;
    // Información completa del director
    private UserResponse director;
    // Información completa de los auxiliares
    private List<UserResponse> auxiliaries;
    private String ugel;
    private String dre;
    private InstitutionStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime deletedAt;
}
