package pe.edu.vallegrande.vgmsinstitutionmanagement.infrastructure.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class AddressDto {
    @NotBlank(message = "La calle es requerida")
    private String street;

    @NotBlank(message = "El distrito es requerido")
    private String district;

    @NotBlank(message = "La provincia es requerida")
    private String province;

    @NotBlank(message = "El departamento es requerido")
    private String department;

    private String postalCode;
}