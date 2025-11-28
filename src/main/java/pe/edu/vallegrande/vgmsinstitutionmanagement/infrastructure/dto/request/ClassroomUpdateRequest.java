package pe.edu.vallegrande.vgmsinstitutionmanagement.infrastructure.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import lombok.Data;

@Data
public class ClassroomUpdateRequest {
    // SIN institutionId - no se puede cambiar

    @NotBlank(message = "El nombre del aula es requerido")
    private String classroomName;

    @NotBlank(message = "La edad del aula es requerida")
    private String classroomAge;

    @Positive(message = "La capacidad debe ser mayor a 0")
    private Integer capacity;

    private String color;
}