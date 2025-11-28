package pe.edu.vallegrande.vgmsinstitutionmanagement.infrastructure.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ContactMethodDto {
    @NotBlank(message = "El tipo de contacto es requerido")
    private String type;

    @NotBlank(message = "El valor del contacto es requerido")
    private String value;
}