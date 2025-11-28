package pe.edu.vallegrande.vgmsinstitutionmanagement.infrastructure.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import pe.edu.vallegrande.vgmsinstitutionmanagement.domain.enums.InstitutionStatus;

import java.util.List;

@Data
public class InstitutionUpdateRequest {

    @Valid
    @NotNull(message = "La información de la institución es requerida")
    private InstitutionInformationDto institutionInformation;

    @Valid
    @NotNull(message = "La dirección es requerida")
    private AddressDto address;

    @Valid
    private List<ContactMethodDto> contactMethods;

    @NotBlank(message = "El tipo de calificación es requerido")
    private String gradingType;

    @NotBlank(message = "El tipo de aula es requerido")
    private String classroomType;

    @Valid
    private List<ScheduleDto> schedules;

    // Solo el ID del director
    @NotBlank(message = "El ID del director es requerido")
    private String directorId;

    // Solo los IDs de los auxiliares
    private List<String> auxiliaryIds;

    @NotBlank(message = "La UGEL es requerida")
    private String ugel;

    @NotBlank(message = "La DRE es requerida")
    private String dre;

}