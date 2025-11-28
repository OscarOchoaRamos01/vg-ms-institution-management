package pe.edu.vallegrande.vgmsinstitutionmanagement.domain.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import pe.edu.vallegrande.vgmsinstitutionmanagement.domain.enums.ClassroomStatus;

import java.time.LocalDateTime;

@Data
@Document(collection = "classrooms")
public class Classroom {

    @Id
    private String classroomId;

    // Referencia a la institución a la que pertenece
    private String institutionId;

    private String classroomName;

    private String classroomAge;

    private Integer capacity;

    private String color;

    private ClassroomStatus status;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    private LocalDateTime deletedAt;
}