package pe.edu.vallegrande.vgmsinstitutionmanagement.infrastructure.repository;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import pe.edu.vallegrande.vgmsinstitutionmanagement.domain.model.Classroom;

public interface ClassroomRepository extends ReactiveMongoRepository<Classroom, String> {
}
