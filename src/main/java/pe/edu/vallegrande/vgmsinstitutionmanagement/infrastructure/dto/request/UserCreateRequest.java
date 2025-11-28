package pe.edu.vallegrande.vgmsinstitutionmanagement.infrastructure.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class UserCreateRequest {

    @NotBlank(message = "El nombre es requerido")
    private String firstName;

    @NotBlank(message = "El apellido es requerido")
    private String lastName;

    @NotBlank(message = "El tipo de documento es requerido")
    private String documentType;

    @NotBlank(message = "El número de documento es requerido")
    private String documentNumber;

    @NotBlank(message = "El teléfono es requerido")
    private String phone;

    @NotBlank(message = "El email es requerido")
    @Email(message = "El email debe ser válido")
    private String email;

    @NotBlank(message = "El rol es requerido")
    @Pattern(regexp = "PADRE|MADRE|DIRECTOR|AUXILIAR|ADMIN|PROFESOR", message = "El rol debe ser: PADRE, MADRE, DIRECTOR, AUXILIAR, ADMIN o PROFESOR")
    private String role;
}
