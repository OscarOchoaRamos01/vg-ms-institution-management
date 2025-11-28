package pe.edu.vallegrande.vgmsinstitutionmanagement.infrastructure.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ScheduleDto {
    @NotBlank(message = "El tipo de horario es requerido")
    private String type;

    @NotBlank(message = "La hora de entrada es requerida")
    private String entryTime;

    @NotBlank(message = "La hora de salida es requerida")
    private String exitTime;
}