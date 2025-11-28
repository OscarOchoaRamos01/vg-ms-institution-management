package pe.edu.vallegrande.vgmsinstitutionmanagement.infrastructure.dto.response;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class UserResponse {

    private String userId;
    private String firstName;
    private String lastName;
    private String documentType;
    private String documentNumber;
    private String phone;
    private String email;
    private String role; // PADRE, MADRE, DIRECTOR, AUXILIAR, ADMIN, PROFESOR
    private char status; // A = Activo, I = Inactivo
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime deletedAt;
}
