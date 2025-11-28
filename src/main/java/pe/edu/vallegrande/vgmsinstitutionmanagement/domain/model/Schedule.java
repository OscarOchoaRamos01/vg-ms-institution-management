package pe.edu.vallegrande.vgmsinstitutionmanagement.domain.model;

import lombok.Data;

@Data
public class Schedule {
    private String type;
    private String entryTime;
    private String exitTime;
}
