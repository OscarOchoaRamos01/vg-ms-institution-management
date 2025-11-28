package pe.edu.vallegrande.vgmsinstitutionmanagement.domain.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import pe.edu.vallegrande.vgmsinstitutionmanagement.domain.enums.InstitutionStatus;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Document(collection = "institutions")
public class Institution {

    @Id
    private String institutionId;

    private InstitutionStatus status;

    private InstitutionInformation institutionInformation;

    private Address address;

    private List<ContactMethod> contactMethods;

    private String gradingType;

    private String classroomType;

    private List<Schedule> schedules;

    // Referencia a las aulas (solo IDs)
    private List<String> classroomIds;

    private String directorId;

    private List<String> auxiliaryIds;

    private String ugel;

    private String dre;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    private LocalDateTime deletedAt;
}
