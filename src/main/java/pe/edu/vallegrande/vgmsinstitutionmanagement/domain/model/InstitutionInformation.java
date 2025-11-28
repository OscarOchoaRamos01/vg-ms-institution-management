package pe.edu.vallegrande.vgmsinstitutionmanagement.domain.model;

import lombok.Data;

@Data
public class InstitutionInformation {
    private String institutionName;
    private String codeInstitution;
    private String modularCode;
    private String institutionType;
    private String institutionLevel;
    private String gender;
    private String slogan;
    private String logoUrl;
}