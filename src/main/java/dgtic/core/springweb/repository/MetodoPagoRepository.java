package dgtic.core.springweb.repository;

import dgtic.core.springweb.model.MetodoPagoEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MetodoPagoRepository extends JpaRepository<MetodoPagoEntity,Integer> {
}
