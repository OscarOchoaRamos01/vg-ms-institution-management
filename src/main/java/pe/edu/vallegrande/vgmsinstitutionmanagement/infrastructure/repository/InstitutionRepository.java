package pe.edu.vallegrande.vgmsinstitutionmanagement.infrastructure.repository;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import pe.edu.vallegrande.vgmsinstitutionmanagement.domain.model.Institution;

public interface InstitutionRepository extends ReactiveMongoRepository<Institution, String> {
}
