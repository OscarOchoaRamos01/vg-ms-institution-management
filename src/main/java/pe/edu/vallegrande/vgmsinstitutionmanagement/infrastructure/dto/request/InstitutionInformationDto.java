package pe.edu.vallegrande.vgmsinstitutionmanagement.infrastructure.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class InstitutionInformationDto {
    @NotBlank(message = "El nombre de la institución es requerido")
    private String institutionName;

    @NotBlank(message = "El código de institución es requerido")
    private String codeInstitution;

    @NotBlank(message = "El código modular es requerido")
    private String modularCode;

    @NotBlank(message = "El tipo de institución es requerido")
    private String institutionType;

    @NotBlank(message = "El nivel de institución es requerido")
    private String institutionLevel;

    @NotBlank(message = "El género es requerido")
    private String gender;

    private String slogan;
    private String logoUrl;
}