package dgtic.core.springweb.repository;

import dgtic.core.springweb.model.ClienteEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ClienteRepository extends JpaRepository<ClienteEntity,Integer> {
}
