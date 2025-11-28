package pe.edu.vallegrande.vgmsinstitutionmanagement.domain.model;

import lombok.Data;

@Data
public class Address {
    private String street;
    private String district;
    private String province;
    private String department;
    private String postalCode;
}
